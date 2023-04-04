package cool.zhang0.content.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cool.zhang0.content.feignclient.SearchServiceClient;
import cool.zhang0.content.feignclient.AuthServiceClient;
import cool.zhang0.content.feignclient.model.MvIndex;
import cool.zhang0.content.listener.EmailPublishMessageListener;
import cool.zhang0.content.mapper.MvBaseMapper;
import cool.zhang0.content.mapper.MvPublishMapper;
import cool.zhang0.content.mapper.MvPublishPreMapper;
import cool.zhang0.content.mapper.TsFollowMapper;
import cool.zhang0.content.model.dto.LikedUserDto;
import cool.zhang0.content.model.dto.MvLikesUserDto;
import cool.zhang0.content.model.dto.ScrollResult;
import cool.zhang0.content.model.message.MvPublishEmailMessage;
import cool.zhang0.content.model.message.prototype.EmailMessage;
import cool.zhang0.content.model.po.MvBase;
import cool.zhang0.content.model.po.MvPublish;
import cool.zhang0.content.model.po.MvPublishPre;
import cool.zhang0.content.model.po.TsFollow;
import cool.zhang0.content.service.MvPublishService;
import cool.zhang0.exception.TuneSurgeException;
import cool.zhang0.messagesdk.model.po.MqMessage;
import cool.zhang0.messagesdk.service.MqMessageService;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <MV发布>
 *
 * @Author zhanglin
 * @createTime 2023/3/31 14:05
 */
@Slf4j
@Service
public class MvPublishServiceImpl extends ServiceImpl<MvPublishMapper, MvPublish> implements MvPublishService {

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

    @Resource
    TsFollowMapper tsFollowMapper;

    @Resource
    AuthServiceClient authServiceClient;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Override
    public RestResponse<String> publish(Long userId, String nickname, Long mvId) {
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
        // 查询MV作者的所有粉丝 执行Feed流推送
        LambdaQueryWrapper<TsFollow> tsFollowLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tsFollowLambdaQueryWrapper.eq(TsFollow::getFollowUserId, userId);
        tsFollowLambdaQueryWrapper.select(TsFollow::getUserId);
        List<TsFollow> tsFollows = tsFollowMapper.selectList(tsFollowLambdaQueryWrapper);
        List<Long> userIdList = new ArrayList<>();
        tsFollows.forEach(tsFollow -> {
            // 获取粉丝ID
            Long id = tsFollow.getUserId();
            userIdList.add(id);
            final String key = "mv:feed:" + id;
            stringRedisTemplate.opsForZSet().add(key, mvId.toString(), System.currentTimeMillis());
        });
        String[] emailList = authServiceClient.getEmailList(userIdList);
        // 消息异步执行 Email通知粉丝
        final String exchange = "tunesurge.mv-publish-topic";
        final String routeKey = "email.mv-publish";
        MvPublishEmailMessage mvPublishEmailMessage = new MvPublishEmailMessage();
        mvPublishEmailMessage.setTitle("您关注的UP主发布了新的MV");
        mvPublishEmailMessage.setTo(emailList);
        mvPublishEmailMessage.setUpName(nickname);
        mvPublishEmailMessage.setMvName(mvBase.getName());
        mvPublishEmailMessage.setDescription(mvBase.getDescription());
        rabbitTemplate.convertAndSend(exchange, routeKey, mvPublishEmailMessage);
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
        Page<MvPublish> page = query().orderByDesc("liked").page(new Page<>(pageParams.getPageNo(), pageParams.getPageSize()));
        return RestResponse.success(page);
    }

    @Override
    public RestResponse<String> likeMv(Long userId, Long mvId) {
        // 判断当前用户是否已经点赞
        String key = "mv:liked:" + mvId;
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if (score == null) {
            // 未点赞 执行点赞
            // 数据库点赞数+1
            boolean update = update().setSql("liked = liked + 1").eq("id", mvId).update();
            // 保存用户到redis的set集合 ZADD KEY VALUE SCORE
            if (update) {
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
            }
        } else {
            // 已经点赞 则取消点赞
            // 数据库点赞数-1
            boolean update = update().setSql("liked = liked - 1").eq("id", mvId).update();
            if (update) {
                // 将用户从set中移除
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            }
        }
        return RestResponse.success();
    }

    @Override
    public RestResponse<List<LikedUserDto>> queryMvLikes(Long mvId) {

        String key = "mv:liked:" + mvId;
        // 查询top5的点赞用户 ZRANGE KEY MIN MAX
        Set<String> range = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if (range == null || range.isEmpty()) {
            return RestResponse.success(Collections.emptyList());
        }
        // 解析用户ID
        List<Long> userIds = range.stream().map(Long::valueOf).collect(Collectors.toList());
        String userIdStr = StrUtil.join(",", userIds);
        // 根据用户ID查询用户
        List<LikedUserDto> likedUserDtos = authServiceClient.likedSorted(new MvLikesUserDto(userIds, userIdStr));
        return RestResponse.success(likedUserDtos);
    }

    @Override
    public RestResponse<ScrollResult> queryMvOfFollow(Long userId, Long max, Integer offset) {

        // 先从Redis中查看收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
        final String key = "mv:feed:" + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        // 3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return RestResponse.success();
        }
        // 4.解析数据
        List<Long> ids = new ArrayList<>(typedTuples.size());
        // 最小时间戳的记录
        long minTime = 0;
        // offset 偏移量
        int os = 1;
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            // 4.1.获取id
            ids.add(Long.valueOf(Objects.requireNonNull(tuple.getValue())));
            // 4.2.获取分数(时间戳）
            long time = Objects.requireNonNull(tuple.getScore()).longValue();
            if (time == minTime) {
                os++;
            } else {
                minTime = time;
                os = 1;
            }
        }

        // 根据ID查出MV
        final String idStr = StrUtil.join(",", ids);
        List<MvPublish> mvPublishList = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();

        // 构建返回内容
        ScrollResult scrollResult = new ScrollResult();
        scrollResult.setList(mvPublishList);
        scrollResult.setOffset(os);
        scrollResult.setMinTime(minTime);
        return RestResponse.success(scrollResult);

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
