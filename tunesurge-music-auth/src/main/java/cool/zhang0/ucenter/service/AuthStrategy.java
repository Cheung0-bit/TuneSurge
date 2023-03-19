package cool.zhang0.ucenter.service;

import cool.zhang0.ucenter.model.dto.AuthParamsDto;
import cool.zhang0.ucenter.model.dto.TsUserExt;

/**
 * <Auth流程策略接口>
 *
 * @Author zhanglin
 * @createTime 2023/3/16 18:11
 */
public interface AuthStrategy {

    /**
     * 按照不同的策略执行身份验证
     * @param authParamsDto
     * @return
     */
    TsUserExt execute(AuthParamsDto authParamsDto);

}
