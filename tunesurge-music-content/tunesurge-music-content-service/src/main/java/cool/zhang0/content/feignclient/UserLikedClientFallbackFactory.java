package cool.zhang0.content.feignclient;

import cool.zhang0.content.model.dto.LikedUserDto;
import cool.zhang0.content.model.dto.MvLikesUserDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/4/2 11:42
 */
@Slf4j
@Component
public class UserLikedClientFallbackFactory implements FallbackFactory<UserLikedClient> {
    @Override
    public UserLikedClient create(Throwable throwable) {
        return new UserLikedClient() {
            @Override
            public List<LikedUserDto> likedSorted(MvLikesUserDto mvLikesUserDto) {
                log.error("UserLikedClientFallbackFactory");
                return Collections.emptyList();
            }
        };

    }
}
