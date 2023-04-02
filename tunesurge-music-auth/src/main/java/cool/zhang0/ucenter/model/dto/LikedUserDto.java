package cool.zhang0.ucenter.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * <点赞用户信息>
 *
 * @Author zhanglin
 * @createTime 2023/4/1 21:35
 */
@Data
@ApiModel(value = "LikedUserDto", description = "点赞用户信息")
public class LikedUserDto {

    private Long id;

    private String nickname;

    private String userAvatar;

}
