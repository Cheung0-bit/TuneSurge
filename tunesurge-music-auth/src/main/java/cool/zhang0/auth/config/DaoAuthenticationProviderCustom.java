package cool.zhang0.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <自定义身份验证逻辑>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 20:20
 */
@Component
public class DaoAuthenticationProviderCustom extends DaoAuthenticationProvider {

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    /**
     * 这里可以添加额外的处理逻辑，针对用户身份认证，例如字段中用户是否被封禁之类的
     * @param userDetails
     * @param authentication
     * @throws AuthenticationException
     */
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

}
