package cool.zhang0.content.feignclient;

import cool.zhang0.content.model.dto.LikedUserDto;
import cool.zhang0.content.model.dto.MvLikesUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * <用户点赞排行查询>
 *
 * @Author zhanglin
 * @createTime 2023/4/2 11:41
 */
@FeignClient(value = "auth-service", fallbackFactory = UserLikedClientFallbackFactory.class)
@RequestMapping("/auth")
public interface UserLikedClient {

    /**
     * 点赞排行查询
     * @param mvLikesUserDto
     * @return
     */
    @PostMapping("/likedSorted")
    List<LikedUserDto> likedSorted(@RequestBody MvLikesUserDto mvLikesUserDto);
}
