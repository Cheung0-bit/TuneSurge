package cool.zhang0.ucenter.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * <OAuth2返回体构造>
 *
 * @Author zhanglin
 * @createTime 2023/3/16 15:19
 */
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
