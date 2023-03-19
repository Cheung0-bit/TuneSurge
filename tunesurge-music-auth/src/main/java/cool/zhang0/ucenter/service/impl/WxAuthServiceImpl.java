package cool.zhang0.ucenter.service.impl;

import cool.zhang0.ucenter.model.dto.AuthParamsDto;
import cool.zhang0.ucenter.model.dto.TsUserExt;
import cool.zhang0.ucenter.service.AuthStrategy;
import org.springframework.stereotype.Service;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/3/19 12:43
 */
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthStrategy {
    @Override
    public TsUserExt execute(AuthParamsDto authParamsDto) {
        return null;
    }
}
