package cool.zhang0.content.feignclient;

import cool.zhang0.content.model.dto.CommonFollowUserDto;
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
public class AuthServiceFallbackFactory implements FallbackFactory<AuthServiceClient> {
    @Override
    public AuthServiceClient create(Throwable throwable) {
        return new AuthServiceClient() {
            @Override
            public List<LikedUserDto> likedSorted(MvLikesUserDto mvLikesUserDto) {
                throwable.printStackTrace();
                log.error("AuthServiceFallbackFactory");
                return Collections.emptyList();
            }

            @Override
            public List<CommonFollowUserDto> getUserList(String userIds) {
                throwable.printStackTrace();
                log.error("AuthServiceFallbackFactory");
                return Collections.emptyList();
            }
        };

    }
}
