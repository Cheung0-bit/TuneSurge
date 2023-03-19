package cool.zhang0.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * <Token令牌配置>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 18:38
 */
@Configuration
public class TokenConfig {

    @Resource
    TokenParamsConfig tokenParamsConfig;

    @Resource
    TokenStore tokenStore;

    @Resource
    private JwtAccessTokenConverter accessTokenConverter;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(tokenParamsConfig.getTokenKey());
        return converter;
    }

    /**
     * 令牌管理服务
     *
     * @return
     */
    @Bean(name = "authorizationServerTokenServicesCustom")
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service = new DefaultTokenServices();
        service.setSupportRefreshToken(true);
        service.setTokenStore(tokenStore);
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);
        service.setAccessTokenValiditySeconds(tokenParamsConfig.getAccessTokenValidTime());
        service.setRefreshTokenValiditySeconds(tokenParamsConfig.getRefreshTokenValidTime());
        return service;
    }

//    @Bean
//    public TokenStore tokenStore() {
//        //使用内存存储令牌（普通令牌）
//        return new InMemoryTokenStore();
//    }
//
//    @Bean(name="authorizationServerTokenServicesCustom")
//    public AuthorizationServerTokenServices tokenService() {
//        DefaultTokenServices service=new DefaultTokenServices();
//        service.setSupportRefreshToken(true);//支持刷新令牌
//        service.setTokenStore(tokenStore);//令牌存储策略
//        service.setAccessTokenValiditySeconds(7200); // 令牌默认有效期2小时
//        service.setRefreshTokenValiditySeconds(259200); // 刷新令牌默认有效期3天
//        System.out.println(tokenKey);
//        System.out.println(accessTokenValidTime);
//        System.out.println(refreshTokenValidTime);
//        return service;
//    }

}
