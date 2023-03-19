package cool.zhang0.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <验证码回退工厂>
 *
 * @Author zhanglin
 * @createTime 2023/3/19 19:20
 */
@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable throwable) {
        return new CheckCodeClient() {
            @Override
            public Boolean verify(String key, String code) {
                log.error("调用验证码服务熔断异常：{}", throwable.getMessage());
                return null;
            }
        };
    }
}
