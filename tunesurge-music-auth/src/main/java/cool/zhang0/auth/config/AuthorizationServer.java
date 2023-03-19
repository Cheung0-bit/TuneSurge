package cool.zhang0.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import javax.annotation.Resource;

/**
 * <OAuth2服务器配置>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 17:04
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Resource(name = "authorizationServerTokenServicesCustom")
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    private AuthenticationManager authenticationManager;


    //客户端详情服务
    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.inMemory()
                .withClient("TuneSurgeApp")
//                .secret("TuneSurge")
                .secret(new BCryptPasswordEncoder().encode("TuneSurge"))
                .resourceIds("tunesurge")
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
                .scopes("all")
                .autoApprove(false)
                .redirectUris("http://localhost:8848/nacos")
        ;
    }

    /**
     * 用于配置授权服务器的类，用于定义授权服务器的API端点和安全约束。授权服务器的API端点确定了OAuth 2.0协议中定义的授权和令牌相关的对外公开的接口
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                //认证管理器
                .authenticationManager(authenticationManager)
                //令牌管理服务
                .tokenServices(authorizationServerTokenServices)
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    /**
     * 令牌端点的安全配置
     * @param security
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients()
        ;
    }

}
