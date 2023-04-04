# Auth模块

本项目搭建了一套基于SpringSecurityOAuth2的身份认证与鉴权中心，所有的模块通过AuthServer来做**统一**身份认证和鉴权，并实现SSO（单点登录）

![image-20230319194158307](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230319194158307.png)

用户通过统一认证服务器，拿到相关令牌后，即可访问网站底下的其他服务，无需再次认证授权，即实现了我们所说的SSO单点登录。这里Security OAuth2默认实现的是使用UUID生成令牌，我将其改成了JWT。JWT（JSON WEB TOKEN）实现令牌无状态话，避免了因存储令牌和多个机器之间的同步session数据带来的空间损失 。

集成Security OAuth2，可实现搭建OAuth2认证服务器，开启授权码模式，密码模式等多种认证方式。

下面进行详细分析

## 数据模型设计

经典的RBAC数据库设计：

![tunesurge_users](https://0-bit.oss-cn-beijing.aliyuncs.com/tunesurge_users.png)

权限集合存放在`ts_menu`中，该表的结构按照菜单的树形结构进行构建，方便后期与后台管理系统结合，实现角色权限的分配

## 模块搭建

![image-20230319195749261](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230319195749261.png)

**导入依赖**

~~~xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-oauth2</artifactId>
</dependency>
~~~

配置**AuthorizationServer**

~~~java
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
~~~

该类继承**AuthorizationServerConfigurerAdapter接口**，进行了客户端详情服务配置（配置了client_id,client_secret,resourseIds, authorizedGrantTypes,scopes,redirectUri等参数），授权服务器配置（用于定义授权服务器的API端点和安全约束。授权服务器的API端点确定了OAuth 2.0协议中定义的授权和令牌相关的对外公开的接口），令牌端点的安全配置。

## 授权码模式与密码模式

Spring Security支持OAuth2认证，OAuth2提供授权码模式、密码模式、简化模式、客户端模式等四种授权模式

### 授权码模式

![image-20230319203307625](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230319203307625.png)

第一步：访问`/oauth/authorize?client_id=TuneSurgeApp&response_type=code&scope=all&redirect_uri=http://localhoust:63070`第二步：此时会出现授权页面，资源拥有着（终端用户点击授权和授权域），便会重定向`localhoust:63070?code=xxxxx`，第三步：使用HTTP工具请求`/oauth/token?client_id=TuneSurgeApp&client_secret=TuneSurge&grant_type=authorization_code&code=授权码&redirect_uri=http://localhost:63070/`第四步：可以拿到返回的`access_token`去请求资源。

### 密码模式

![image-20230319203956624](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230319203956624.png)

![image-20230319210030307](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230319210030307.png)

~~~json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidHVuZXN1cmdlIl0sInVzZXJfbmFtZSI6IntcImNlbGxQaG9uZVwiOlwiMTFcIixcImNyZWF0ZVRpbWVcIjpcIjIwMjMtMDMtMTdUMDM6Mjg6MzhcIixcImVtYWlsXCI6XCJ4eFwiLFwiaWRcIjoxLFwibmlja25hbWVcIjpcIuaXoOaDheeahOW4heWTpVwiLFwicGVybWlzc2lvbnNcIjpbXSxcInNleFwiOlwiMVwiLFwic3RhdHVzXCI6XCLmraPluLhcIixcInVwZGF0ZVRpbWVcIjpcIjIwMjMtMDMtMTlUMDQ6MzA6NDBcIixcInVzZXJBdmF0YXJcIjpcInh4XCIsXCJ1c2VyQmFja1wiOlwieHhcIixcInVzZXJuYW1lXCI6XCJ6aGFuZ2xpblwifSIsInNjb3BlIjpbImFsbCJdLCJleHAiOjE2NzkyMzc0NjksImF1dGhvcml0aWVzIjpbInRzX3N5cyIsInJvb3QiXSwianRpIjoiYTA4ZGM3ZDYtMTQxOC00YzIwLWI3YjktZGJjZjdhNmFhMzQ0IiwiY2xpZW50X2lkIjoiVHVuZVN1cmdlQXBwIn0.VLQj7FWtBkpvhsrbHplJgaj3x0XW5Bmv7ogBOk9W_SY",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidHVuZXN1cmdlIl0sInVzZXJfbmFtZSI6IntcImNlbGxQaG9uZVwiOlwiMTFcIixcImNyZWF0ZVRpbWVcIjpcIjIwMjMtMDMtMTdUMDM6Mjg6MzhcIixcImVtYWlsXCI6XCJ4eFwiLFwiaWRcIjoxLFwibmlja25hbWVcIjpcIuaXoOaDheeahOW4heWTpVwiLFwicGVybWlzc2lvbnNcIjpbXSxcInNleFwiOlwiMVwiLFwic3RhdHVzXCI6XCLmraPluLhcIixcInVwZGF0ZVRpbWVcIjpcIjIwMjMtMDMtMTlUMDQ6MzA6NDBcIixcInVzZXJBdmF0YXJcIjpcInh4XCIsXCJ1c2VyQmFja1wiOlwieHhcIixcInVzZXJuYW1lXCI6XCJ6aGFuZ2xpblwifSIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiJhMDhkYzdkNi0xNDE4LTRjMjAtYjdiOS1kYmNmN2E2YWEzNDQiLCJleHAiOjE2Nzk0ODk0NjksImF1dGhvcml0aWVzIjpbInRzX3N5cyIsInJvb3QiXSwianRpIjoiYzJlOGVmNzQtODQ2OS00YTU1LTg4YzgtOGQyNGYzNDA0NDcxIiwiY2xpZW50X2lkIjoiVHVuZVN1cmdlQXBwIn0.yekVtYLFwNu3vXccgbExtMwskSJXG86CUUTmTfnSmQQ",
    "tokenHead": "Bearer ",
    "expiresIn": 7199,
    "scope": [
        "all"
    ],
    "jti": "a08dc7d6-1418-4c20-b7b9-dbcf7a6aa344"
}
~~~

## refresh_token刷新令牌

返回结果中不仅有`access_token`,还有`refresh_token`，一般而言，前者的有效期较短，而后者较长。当前者失效后，可以用后者来刷新令牌。

`refresh_token`在有效期内：

![image-20230320133108581](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230320133108581.png)

失效后：

![image-20230320133008799](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230320133008799.png)

### 思考：为什么要这样设计

1. 提高安全性：access_token的有效期通常较短，这样可以减少access_token被盗用的风险。同时，refresh_token的有效期较长，只有在access_token快过期时才会使用，这样可以减少refresh_token被盗用的风险。
2. 提高用户体验：当access_token过期时，如果没有refresh_token，用户需要重新登录并重新授权，这会影响用户体验。有了refresh_token，用户只需要使用refresh_token刷新access_token即可，无需重新登录和授权。
3. 简化开发：使用access_token和refresh_token可以简化开发，减少代码复杂度和开发成本。开发人员无需自己实现认证和授权功能，只需要调用OAuth2.0协议提供的API即可。

## Token配置

~~~java
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
~~~

## 自定义用户名密码校验逻辑

~~~java
@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService {

    @Resource
    ApplicationContext applicationContext;

    @Resource
    TsMenuMapper tsMenuMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.error("认证信息不合法：{}", e.getMessage());
            throw new RuntimeException(e);
        }
        //认证方式,
        String authType = authParamsDto.getAuthType();
        //从spring容器中拿具体的认证bean实例
        AuthStrategy authStrategy = applicationContext.getBean(authType + "_authservice", AuthStrategy.class);
        //开始认证,认证成功拿到用户信息
        TsUserExt tsUserExt = authStrategy.execute(authParamsDto);
        return getUserPrincipal(tsUserExt);
    }

    public UserDetails getUserPrincipal(TsUserExt user) {

        //权限列表，存放的用户权限
        List<String> permissionList = new ArrayList<>();
        //根据用户id查询数据库中他的权限
        List<TsMenu> xcMenus = tsMenuMapper.selectPermissionByUserId(user.getId());
        xcMenus.forEach(menu -> {
            permissionList.add(menu.getCode());
        });
        if (permissionList.size() == 0) {
            //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
            permissionList.add("test");
        }

        String[] authorities = permissionList.toArray(new String[0]);
        //原来存的是账号，现在扩展为用户的全部信息(密码不要放)
        user.setPassword(null);
        String jsonString = JSON.toJSONString(user);
        UserDetails userDetails = User.withUsername(jsonString).password("").authorities(authorities).build();
        return userDetails;
    }

}
~~~

## 通过策略设计模式，多渠道登录实现

一个完整成熟的系统，都会兼容各种三方登录，包括不限于微信登录、QQ登录、Github登录、手机号验证码登录、邮箱验证码登录......

![image-20230319201239701](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230319201239701.png)

首先，编写策略接口：

~~~java
public interface AuthStrategy {

    /**
     * 按照不同的策略执行身份验证
     * @param authParamsDto
     * @return
     */
    TsUserExt execute(AuthParamsDto authParamsDto);

}
~~~

其中，**AuthParamsDto**类用于传输登录相关参数：

~~~java
@Data
public class AuthParamsDto {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private String cellphone;
    /**
     * 验证码
     */
    private String checkcode;
    /**
     * 验证码key
     */
    private String checkcodekey;
    /**
     * 认证的类型   password:用户名密码模式类型    sms:短信模式类型
     */
    private String authType;
    /**
     * 附加数据，作为扩展，不同认证类型可拥有不同的附加数据。如认证类型为短信时包含smsKey
     */
    private Map<String, Object> payload = new HashMap<>();

}
~~~

以密码登录为例，看看如何实现了一个接口：

~~~java
@Slf4j
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthStrategy {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Resource
    TsUserMapper tsUserMapper;

    @Override
    public TsUserExt execute(AuthParamsDto authParamsDto) {

//        //得到验证码
//        String checkcode = authParamsDto.getCheckcode();
//        String checkcodekey = authParamsDto.getCheckcodekey();
//        if(StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)){
//            throw new RuntimeException("验证码为空");
//
//        }
//
//        //校验验证码,请求验证码服务进行校验
//        Boolean result = checkCodeClient.verify(checkcodekey, checkcode);
//        if(result==null || !result){
//            throw new RuntimeException("验证码错误");
//        }

        //账号
        String username = authParamsDto.getUsername();
        //从数据库查询用户信息
        TsUser tsUser = tsUserMapper.selectOne(new LambdaQueryWrapper<TsUser>().eq(TsUser::getUsername, username));
        if (tsUser == null) {
            //账号不存在
            throw new RuntimeException("账号不存在");
        }
        //比对密码
        //正确的密码(加密后)
        String passwordFromDb = tsUser.getPassword();
        //输入的密码
        String passwordFromInput = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordFromInput, passwordFromDb);
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }
        TsUserExt tsUserExt = new TsUserExt();
        BeanUtils.copyProperties(tsUser, tsUserExt);
        return tsUserExt;
    }
}
~~~

一般情况下，策略模式会有一个调用类，从容器中取出对应的策略实现：

~~~java
public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    AuthParamsDto authParamsDto = null;
    try {
        authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
    } catch (Exception e) {
        log.error("认证信息不合法：{}", e.getMessage());
        throw new RuntimeException(e);
    }
    //认证方式,
    String authType = authParamsDto.getAuthType();
    //从spring容器中拿具体的认证bean实例
    AuthStrategy authStrategy = applicationContext.getBean(authType + "_authservice", AuthStrategy.class);
    //开始认证,认证成功拿到用户信息
    TsUserExt tsUserExt = authStrategy.execute(authParamsDto);
    return getUserPrincipal(tsUserExt);
}
~~~

## SpringSecurityOAuth2的自定义返回方案

使用Spring Security OAuth2时，源码对于返回消息没有做一个统一的封装。为了统一规范，我们可以采取一些措施。

下图是原初访问**/oauth/token**获取token时，成功和失败（源码抛出异常时的响应示例）

![image-20230316145452918](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230316145452918.png)

![image-20230316145519096](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230316145519096.png)

### 基于AOP的切点表示式

观察控制台日志：

~~~bash
2023-03-16 14:55:08.940  WARN 25476 --- [io-63070-exec-5] o.s.s.o.p.e.TokenEndpoint                : Handling error: InvalidGrantException, 用户名或密码错误
~~~

可以得到程序是在**TokenEndpoint**类中接受了处理，并返回了结果

阅读源码发现![image-20230316145920029](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230316145920029.png)

**postAccessToken**和**handleException**方法处理了信息的返回

那么思路很暴力很简单，切点表达式，直接对这两个方法做AOP处理

~~~java
@Component
@Aspect
public class AuthTokenAspect {

    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        RestResponse<Object> response = new RestResponse<>();
        Object proceed = pjp.proceed();
        if (proceed != null) {
            ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>) proceed;
            OAuth2AccessToken body = responseEntity.getBody();
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                response.setCode(0);
                Map<String, Object> map = new HashMap<>();
                assert body != null;
                map.put("access_token", body.getValue());
                map.put("token_type", body.getTokenType());
                map.put("refresh_token", body.getRefreshToken().getValue());
                map.put("expires_in", body.getExpiresIn());
                map.put("scope", body.getScope());
                map.put("jti", body.getAdditionalInformation().get("jti"));
                response.setData(map);
                response.setMsg("登录成功");
            } else {
                response.setCode(-1);
                response.setMsg("登录失败");
            }
        }
        return ResponseEntity
                .status(200)
                .body(response);
    }

    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.handleException(..))")
    public Object handleException(ProceedingJoinPoint pjp) throws Throwable {
        Object proceed = pjp.proceed();
        ResponseEntity<OAuth2Exception> response = (ResponseEntity<OAuth2Exception>) proceed;
        return ResponseEntity
                .status(200)
                .body(RestResponse.validFail(Objects.requireNonNull(response.getBody()).getMessage()));
    }

}
~~~

其中，`RestResponse`是我自己封装的同意返回体

得到返回结果：

![image-20230316150151554](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230316150151554.png)

![image-20230316150205344](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230316150205344.png)

### 基于全局MVC增强

本到这里就结束了，可笔者认为此方法不过完美，在网上得到了重写`/oauth/token`等MVC端点，再使用@ControllerAdvice增强来实现，十分优雅。故做此纪录：

~~~java
@RestController
@RequestMapping("/oauth")
public class AuthorityController {

    @Resource
    private TokenEndpoint tokenEndpoint;

    /**
     * Oauth2登录认证
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public RestResponse<Oauth2TokenDto> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .accessToken(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead("Bearer ")
                .scope(oAuth2AccessToken.getScope())
                .jti(oAuth2AccessToken.getAdditionalInformation().get("jti").toString()).build();
        return RestResponse.success(oauth2TokenDto);
    }
}
~~~

这里新建一个Controller类重写了postAccessToken的处理逻辑，并新建了OAuth2AccessToken类返回结果信息

~~~java
@Data
@Builder
public class Oauth2TokenDto {
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 访问令牌头前缀
     */
    private String tokenHead;
    /**
     * 有效时间（秒）
     */
    private int expiresIn;

    /**
     * 请求域
     */
    private Set<String> scope;

    /**
     * jti
     * JWT的唯一标识
     * 可避免重放攻击
     */
    private String jti;

}
~~~

对于异常信息，我直接定义一个全局异常来处理：

~~~java
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody//将信息返回为 json格式
    @ExceptionHandler(Exception.class)//此方法捕获Exception异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500
    public RestResponse<String> doException(Exception e) {
        log.error("捕获异常：{}", e.getMessage());
        return RestResponse.validFail(e.getMessage());
    }

}
~~~

![image-20230316154512487](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230316154512487.png)

![image-20230316154522929](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230316154522929.png)

为了”统一规范“，这里造成了不少的牺牲，让代码侵入性变的很强，我认为不如和前端约定好，关于OAuth2的模块的返回结果进行针对性的处理。

## 巧妙利用username，传入更多信息

在编写业务时，经常会取用当前终端用户的信息。你可能会想到，将接口HTTP请求头中JWT中信息取出来，但这样过于繁琐，不够优雅。Security OAuth2为我们提供了安全上下文SecurityContextHolder,当我们通过UserDetailService的身份验证后，用户信息会被存放到该上下文中。我们只要每次取用户信息时，只要从安全上下文中获取就可以了。那么使用过SpringSecurity的朋友都清楚，上下文中存储的内容都是内置类User中的信息，而内置User类信息如下：

~~~java
private static final long serialVersionUID = 530L;
private static final Log logger = LogFactory.getLog(User.class);
private String password;
private final String username;
private final Set<GrantedAuthority> authorities;
private final boolean accountNonExpired;
private final boolean accountNonLocked;
private final boolean credentialsNonExpired;
private final boolean enabled;
~~~

其提供的字段根本不够存放业务所需的信息（用户昵称、头像等等）。这里我们可以选择继承重写内置User类，在我的项目中，我选择将相关信息转成JSON字符串统一存放在username字段中。

~~~java
@Override
public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    AuthParamsDto authParamsDto = null;
    try {
        authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
    } catch (Exception e) {
        log.error("认证信息不合法：{}", e.getMessage());
        throw new RuntimeException(e);
    }
    //认证方式,
    String authType = authParamsDto.getAuthType();
    //从spring容器中拿具体的认证bean实例
    AuthStrategy authStrategy = applicationContext.getBean(authType + "_authservice", AuthStrategy.class);
    //开始认证,认证成功拿到用户信息
    TsUserExt tsUserExt = authStrategy.execute(authParamsDto);
    return getUserPrincipal(tsUserExt);
}
~~~

## 拓展用户信息

项目中默认的TsUser类不满足后面的细粒度的鉴权要求，于是选择继承TsUser类建立TsUserExt类

~~~java
@Data
public class TsUserExt extends TsUser {

    List<String> permissions = new ArrayList<>();

}
~~~

用于存储用户对应角色的权限集合，以降低对TsUser类的代码侵入性

## 细粒度鉴权

SpringSecurityOAuth2提供了@PreAuthorize注解用于进行细粒度鉴权。这里鉴权的依据就是我们在重写UserDetailService中存储到User中的authorities集合

![image-20230404150204249](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404150204249.png)

当该用户权限列表不存在所要求的权限时，便会抛出异常

![image-20230404150245980](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404150245980.png)

从JWT的解析结果中可以看出：![image-20230404151144837](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404151144837.png)

权限集合中并不包含“power-test”，故抛出异常

