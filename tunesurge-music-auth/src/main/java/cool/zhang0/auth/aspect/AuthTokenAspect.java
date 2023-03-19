package cool.zhang0.auth.aspect;

import cool.zhang0.model.RestResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <AOP拦截器 使OAuth2返回信息统一>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 21:16
 */
@Component
@Aspect
@Deprecated
public class AuthTokenAspect {

//    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
//    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
//        RestResponse<Object> response = new RestResponse<>();
//        Object proceed = pjp.proceed();
//        if (proceed != null) {
//            ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>) proceed;
//            OAuth2AccessToken body = responseEntity.getBody();
//            if (responseEntity.getStatusCode().is2xxSuccessful()) {
//                response.setCode(0);
//                Map<String, Object> map = new HashMap<>();
//                assert body != null;
//                map.put("access_token", body.getValue());
//                map.put("token_type", body.getTokenType());
//                map.put("refresh_token", body.getRefreshToken().getValue());
//                map.put("expires_in", body.getExpiresIn());
//                map.put("scope", body.getScope());
//                map.put("jti", body.getAdditionalInformation().get("jti"));
//                response.setData(map);
//                response.setMsg("登录成功");
//            } else {
//                response.setCode(-1);
//                response.setMsg("登录失败");
//            }
//        }
//        return ResponseEntity
//                .status(200)
//                .body(response);
//    }
//
//    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.handleException(..))")
//    public Object handleException(ProceedingJoinPoint pjp) throws Throwable {
//        Object proceed = pjp.proceed();
//        ResponseEntity<OAuth2Exception> response = (ResponseEntity<OAuth2Exception>) proceed;
//        return ResponseEntity
//                .status(200)
//                .body(RestResponse.validFail(Objects.requireNonNull(response.getBody()).getMessage()));
//    }

}