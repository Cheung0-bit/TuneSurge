package cool.zhang0.ucenter.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/4/2 12:23
 */
@Data
@ApiModel(value = "MvLikesUserDto", description = "用户点赞排行传输参数包装")
public class MvLikesUserDto {

    private List<Long> userIds;

    private String userIdStr;

}
