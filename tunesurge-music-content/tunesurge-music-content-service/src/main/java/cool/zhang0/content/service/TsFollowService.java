package cool.zhang0.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import cool.zhang0.content.model.dto.CommonFollowUserDto;
import cool.zhang0.content.model.po.TsFollow;
import cool.zhang0.model.RestResponse;

import java.util.List;

/**
 * @author zhanglin
 * @description 针对表【ts_follow】的数据库操作Service
 * @createDate 2023-04-01 21:47:03
 */
public interface TsFollowService extends IService<TsFollow> {

    /**
     * 关注/取关 用户
     *
     * @param userId
     * @param followUserId
     * @param isFollow
     * @return
     */
    RestResponse<String> follow(Long userId, Long followUserId, Boolean isFollow);

    /**
     * 是否关注
     * @param userId
     * @param followUserId
     * @return
     */
    RestResponse<String> isFollow(Long userId, Long followUserId);

    /**
     * 求两个用户的共同关注
     * @param userId
     * @param anotherUserId
     * @return
     */
    RestResponse<List<CommonFollowUserDto>> followCommons(Long userId, Long anotherUserId);

}
