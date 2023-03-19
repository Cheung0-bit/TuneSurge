package cool.zhang0.checknode.model;

import lombok.Data;

/**
 * @author zhanglin
 */
@Data
public class CheckCodeParamsDto {

    /**
     * 验证码类型:pic、sms、email等
     */
    private String checkCodeType;

    /**
     * 业务携带参数
     */
    private String param1;
    private String param2;
    private String param3;
}
