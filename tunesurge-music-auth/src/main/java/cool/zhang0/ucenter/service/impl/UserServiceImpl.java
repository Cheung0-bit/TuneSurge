package cool.zhang0.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import cool.zhang0.ucenter.mapper.TsMenuMapper;
import cool.zhang0.ucenter.mapper.TsUserMapper;
import cool.zhang0.ucenter.model.dto.AuthParamsDto;
import cool.zhang0.ucenter.model.dto.TsUserExt;
import cool.zhang0.ucenter.model.po.TsMenu;
import cool.zhang0.ucenter.service.AuthStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <用户密码校验逻辑>
 *
 * @Author zhanglin
 * @createTime 2023/3/16 18:14
 */
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
