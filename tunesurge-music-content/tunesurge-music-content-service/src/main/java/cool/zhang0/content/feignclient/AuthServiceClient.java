package cool.zhang0.content.feignclient;

import cool.zhang0.content.model.dto.CommonFollowUserDto;
import cool.zhang0.content.model.dto.LikedUserDto;
import cool.zhang0.content.model.dto.MvLikesUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <用户点赞排行查询>
 *
 * @Author zhanglin
 * @createTime 2023/4/2 11:41
 */
@FeignClient(value = "auth-service", fallbackFactory = AuthServiceFallbackFactory.class)
@RequestMapping("/auth")
public interface AuthServiceClient {

    /**
     * 点赞排行查询
     * @param mvLikesUserDto
     * @return
     */
    @PostMapping("/likedSorted")
    List<LikedUserDto> likedSorted(@RequestBody MvLikesUserDto mvLikesUserDto);

    /**
     * 求共同关注用户列表
     * @param userIds
     * @return
     */
    @PostMapping("/getUserList")
    List<CommonFollowUserDto> getUserList(@RequestParam("userIds") String userIds);

    /**
     * 获取待发送邮件列表列表
     * @param userIdList
     * @return
     */
    @PostMapping("/getEmailList")
    String[] getEmailList(@RequestParam("userIdList") List<Long> userIdList);
}
