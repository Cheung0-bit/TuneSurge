package cool.zhang0.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.zhang0.content.feignclient.SearchServiceClient;
import cool.zhang0.content.feignclient.model.MvIndex;
import cool.zhang0.content.mapper.MvBaseMapper;
import cool.zhang0.content.mapper.MvPublishMapper;
import cool.zhang0.content.mapper.MvPublishPreMapper;
import cool.zhang0.content.model.po.MvBase;
import cool.zhang0.content.model.po.MvPublish;
import cool.zhang0.content.model.po.MvPublishPre;
import cool.zhang0.content.service.MvPublishService;
import cool.zhang0.exception.TuneSurgeException;
import cool.zhang0.messagesdk.model.po.MqMessage;
import cool.zhang0.messagesdk.service.MqMessageService;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import groovy.lang.GString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <MV发布>
 *
 * @Author zhanglin
 * @createTime 2023/3/31 14:05
 */
@Slf4j
@Service
public class MvPublishServiceImpl implements MvPublishService {

    @Resource
    MvBaseMapper mvBaseMapper;

    @Resource
    MvPublishPreMapper mvPublishPreMapper;

    @Resource
    MvPublishMapper mvPublishMapper;

    @Resource
    MqMessageService mqMessageService;

    @Resource
    SearchServiceClient searchServiceClient;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedissonClient redissonClient;

    @Override
    public RestResponse<String> publish(Long mvId) {
        // 约束校验
        // 是否提交审核或审核通过
        MvBase mvBase = mvBaseMapper.selectById(mvId);
        if (mvBase == null) {
            TuneSurgeException.cast("该作品不存在");
        }
        // 审核状态
        String auditStatus = mvBase.getAuditStatus();
        if (!"3".equals(auditStatus)) {
            TuneSurgeException.cast("作品未审核或审核未通过，通过审核方可提交");
        }
        // 保存MV发布信息
        saveMvPublish(mvId);
        // 保存消息表
        saveMvPublishMessage(mvId);
        // 删除MV预发布表记录
        mvPublishPreMapper.deleteById(mvId);
        return RestResponse.success();
    }

    @Override
    public Boolean saveMvIndex(Long mvId) {
        //查询MV发布表的数据
        MvPublish mvPublish = mvPublishMapper.selectById(mvId);
        //组装数据
        MvIndex mvIndex = new MvIndex();
        BeanUtils.copyProperties(mvPublish, mvIndex);

        //远程调用搜索服务创建索引
        Boolean result = searchServiceClient.add(mvIndex);
        if (!Boolean.TRUE.equals(result)) {
            TuneSurgeException.cast("创建课程索引失败");
        }
        return true;
    }

    @Override
    public Boolean saveMvCache(Long mvId) {
        // 查询MV发布表的数据
        MvPublish mvPublish = mvPublishMapper.selectById(mvId);
        String jsonString = JSON.toJSONString(mvPublish);
//        Map<String, Object> mvMap = BeanUtil.beanToMap(mvPublish, new HashMap<>(),
//                CopyOptions.create()
//                        .setIgnoreNullValue(true)
//                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        final String key = "mv:publish:" + mvId;
        // 以json string存入 并设置随机过期时间 防止缓存雪崩
        // 获取10-30之间的一个随机值
        int random = RandomUtil.randomInt(10, 30);
        stringRedisTemplate.opsForValue().set(key, jsonString, 30 + random, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public RestResponse<MvPublish> queryMvById(Long mvId) {
        MvPublish mvPublish = mvPublishMapper.selectById(mvId);
        return RestResponse.success(mvPublish);
    }

    @Override
    public RestResponse<MvPublish> queryMvCacheById(Long mvId) {

        // 先从缓存中查询
        final String key = "mv:publish" + mvId;
        String jsonString = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(jsonString)) {
            // 解决缓存穿透 空数据直接返回
            if ("null".equals(jsonString)) {
                return RestResponse.validFail("数据不存在");
            }
            // 缓存中有数据，处理完成返回
            MvPublish mvPublish = JSON.parseObject(jsonString, MvPublish.class);
            return RestResponse.success(mvPublish);
        } else {
            // 申请分布式锁
            String lockName = "mv-query:" + mvId;
            RLock lock = redissonClient.getLock(lockName);
            // 获取锁  ###这里使用互斥锁，防止缓存击穿
            lock.lock();
            try {
                // 再次从缓存中查询 提升性能
                jsonString = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.isNotEmpty(jsonString)) {
                    // 缓存中有数据，处理完成返回
                    MvPublish mvPublish = JSON.parseObject(jsonString, MvPublish.class);
                    return RestResponse.success(mvPublish);
                }
                System.out.println("==============执行数据库查询===============");
                MvPublish mvPublish = mvPublishMapper.selectById(mvId);
                int random = RandomUtil.randomInt(10, 30);
                if (mvPublish == null) {
                    // 课程不存在 ###为防止缓存穿透 缓存空数据
                    stringRedisTemplate.opsForValue().set(key, "null", 30 + random, TimeUnit.SECONDS);
                    return RestResponse.validFail("数据不存在");
                }
                jsonString = JSON.toJSONString(mvPublish);
                // 以json string存入 并设置随机过期时间 防止缓存雪崩
                // 获取10-30之间的一个随机值
                stringRedisTemplate.opsForValue().set(key, jsonString, 30 + random, TimeUnit.SECONDS);
                return RestResponse.success(mvPublish);
            } finally {
                // 释放锁
                lock.unlock();
            }
        }
    }

    @Override
    public RestResponse<Page<MvPublish>> queryHotMv(PageParams pageParams) {
        return null;
    }

    private void saveMvPublishMessage(Long mvId) {
        MqMessage mvPublish = mqMessageService.addMessage("mv_publish", String.valueOf(mvId), null, null);
        if (mvPublish == null) {
            TuneSurgeException.cast("添加消息记录失败");
        }
    }

    private void saveMvPublish(Long mvId) {

        // MV信息来源于预发布表
        MvPublishPre mvPublishPre = mvPublishPreMapper.selectById(mvId);
        MvPublish mvPublish = new MvPublish();
        // 拷贝信息
        BeanUtils.copyProperties(mvPublishPre, mvPublish);
        mvPublish.setStatus("0");
        mvPublish.setPublishUser(mvPublishPre.getPushUser());
        MvPublish mvPublishUpdate = mvPublishMapper.selectById(mvId);
        if (mvPublishUpdate == null) {
            mvPublishMapper.insert(mvPublish);
        } else {
            mvPublishMapper.updateById(mvPublish);
        }
        // 更新基本信息表课程发布状态
        MvBase mvBase = mvBaseMapper.selectById(mvId);
        mvBase.setStatus("1");
        mvBaseMapper.updateById(mvBase);

    }


}
