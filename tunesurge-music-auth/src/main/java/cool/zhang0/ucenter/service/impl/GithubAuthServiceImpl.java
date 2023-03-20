package cool.zhang0.ucenter.service.impl;

import cool.zhang0.ucenter.model.dto.AuthParamsDto;
import cool.zhang0.ucenter.model.dto.TsUserExt;
import cool.zhang0.ucenter.service.AuthStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <Github三方登录>
 *
 * @Author zhanglin
 * @createTime 2023/3/19 20:10
 */
@Slf4j
@Service("github_authservice")
public class GithubAuthServiceImpl implements AuthStrategy {
    @Override
    public TsUserExt execute(AuthParamsDto authParamsDto) {
        return null;
    }
}
