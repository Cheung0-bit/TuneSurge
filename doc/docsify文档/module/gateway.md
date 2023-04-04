# Gateway网关模块

## Oauth2配置

主要进行了安全配置和Token令牌策略配置

~~~java
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    /**
     * 安全拦截配置
     *
     * @param http
     * @return
     */
    @Bean
    public SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http) {

        return http.authorizeExchange()
                .pathMatchers("/**").permitAll()
                .anyExchange().authenticated()
                .and().csrf().disable().build();
    }


}
~~~

~~~java
@Configuration
public class TokenConfig {

    @Value("${token.key}")
    private String tokenKey;

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(tokenKey);
        return converter;
    }

}
~~~

## 自定义全局过滤器

### 设定白名单

某些路径可以放开，就不需要全局执行拦截，例如登录认证接口，必须要放开，于是可列一个白名单，存在文件中

在自定义的全局过滤器中进行读取执行：

~~~java
private static List<String> whitelist = null;

static {
    //加载白名单
    try (
            InputStream resourceAsStream = GatewayAuthFilter.class.getResourceAsStream("/security-whitelist.properties");
    ) {
        Properties properties = new Properties();
        properties.load(resourceAsStream);
        Set<String> strings = properties.stringPropertyNames();
        whitelist = new ArrayList<>(strings);

    } catch (Exception e) {
        log.error("加载/security-whitelist.properties出错:{}", e.getMessage());
        e.printStackTrace();
    }


}
~~~

### 过滤器主要逻辑

~~~java
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String requestUrl = exchange.getRequest().getPath().value();
    AntPathMatcher pathMatcher = new AntPathMatcher();
    //白名单放行
    for (String url : whitelist) {
        if (pathMatcher.match(url, requestUrl)) {
            return chain.filter(exchange);
        }
    }

    //检查token是否存在
    String token = getToken(exchange);
    if (StringUtils.isBlank(token)) {
        return buildReturnMono("没有认证", exchange);
    }
    //判断是否是有效的token
    OAuth2AccessToken oAuth2AccessToken;
    try {
        oAuth2AccessToken = tokenStore.readAccessToken(token);

        boolean expired = oAuth2AccessToken.isExpired();
        if (expired) {
            return buildReturnMono("认证令牌已过期", exchange);
        }
        return chain.filter(exchange);
    } catch (InvalidTokenException e) {
        log.info("认证令牌无效: {}", token);
        return buildReturnMono("认证令牌无效", exchange);
    }
}
~~~

将HTTP请求中header中KEY为Authorization的值取出来，使用配置的TokenStore中的函数进行解析，进行认证

### 网关作用

通过配置网关，实现了以下功能：

1. 对路由作统一转发处理，对前端友好
2. 自定义全局过滤器，实现统一身份认证。具体业务模块只要负责做细粒度鉴权即可
3. 聚合Swagger文档