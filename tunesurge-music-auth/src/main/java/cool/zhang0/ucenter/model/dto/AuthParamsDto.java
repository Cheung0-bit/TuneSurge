package cool.zhang0.ucenter.model.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <身份信息封装（通用载体）>
 *
 * @Author zhanglin
 * @createTime 2023/3/16 20:46
 */
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
