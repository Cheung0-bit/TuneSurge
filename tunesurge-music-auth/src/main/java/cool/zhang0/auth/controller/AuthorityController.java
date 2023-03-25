package cool.zhang0.auth.controller;

import cool.zhang0.model.RestResponse;
import cool.zhang0.ucenter.model.dto.Oauth2TokenDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.Map;

/**
 * <重写OAuth2端点>
 *
 * @Author zhanglin
 * @createTime 2023/3/16 15:06
 */
@RestController
@RequestMapping("/oauth")
@Api(value = "OAuth2模块",tags = "OAuth2模块")
public class AuthorityController {

    @Resource
    private TokenEndpoint tokenEndpoint;

    /**
     * Oauth2登录认证
     */
    @ApiOperation("密码模式获取Token")
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
