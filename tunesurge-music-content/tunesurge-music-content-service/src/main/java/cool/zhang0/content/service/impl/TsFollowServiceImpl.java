package cool.zhang0.content.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cool.zhang0.content.feignclient.AuthServiceClient;
import cool.zhang0.content.mapper.TsFollowMapper;
import cool.zhang0.content.model.dto.CommonFollowUserDto;
import cool.zhang0.content.model.po.TsFollow;
import cool.zhang0.content.service.TsFollowService;
import cool.zhang0.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <实现>
 *
 * @Author zhanglin
 * @createTime 2023/4/2 14:34
 */
@Service
@Slf4j
public class TsFollowServiceImpl extends ServiceImpl<TsFollowMapper, TsFollow> implements TsFollowService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    AuthServiceClient tsFollowClient;

    @Override
    public RestResponse<String> follow(Long userId, Long followUserId, Boolean isFollow) {

        final String key = "ts:follows:" + userId;
        // 关注 or 取关
        if (isFollow) {
            // 关注，新增数据
            TsFollow tsFollow = new TsFollow();
            tsFollow.setUserId(userId);
            tsFollow.setFollowUserId(followUserId);
            boolean save = save(tsFollow);
            if (save) {
                // 把关注的用户ID，放入Redis的SET集合 SADD KEY VALUE
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }
        } else {
            // 取关 先更新数据库 再删除Redis数据 Cache Aside 数据一致性较强
            LambdaQueryWrapper<TsFollow> tsFollowLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tsFollowLambdaQueryWrapper.eq(TsFollow::getUserId, userId);
            tsFollowLambdaQueryWrapper.eq(TsFollow::getFollowUserId, followUserId);
            boolean remove = remove(tsFollowLambdaQueryWrapper);
            if (remove) {
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            }
        }

        return RestResponse.success();

    }

    @Override
    public RestResponse<String> isFollow(Long userId, Long followUserId) {
        return null;
    }

    @Override
    public RestResponse<List<CommonFollowUserDto>> followCommons(Long userId, Long anotherUserId) {

        final String key = "ts:follows:" + userId;
        final String anotherKey = "ts:follows:" + anotherUserId;

        // 求交集
        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, anotherKey);
        if (intersect == null || intersect.isEmpty()) {
            return RestResponse.success(Collections.emptyList());
        }

        // 解析集合
        String userIds = StrUtil.join(",", intersect);
        return RestResponse.success(tsFollowClient.getUserList(userIds));

    }
}
