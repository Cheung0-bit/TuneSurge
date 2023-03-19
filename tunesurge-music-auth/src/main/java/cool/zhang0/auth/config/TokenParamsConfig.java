package cool.zhang0.auth.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <Token参数的配置>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 19:20
 */
@Configuration
@Data
public class TokenParamsConfig {

    @Value("${token.key}")
    private String tokenKey;

    @Value("${token.access-token-valid-time}")
    private int accessTokenValidTime;

    @Value("${token.refresh-token-valid-time}")
    private int refreshTokenValidTime;

}
