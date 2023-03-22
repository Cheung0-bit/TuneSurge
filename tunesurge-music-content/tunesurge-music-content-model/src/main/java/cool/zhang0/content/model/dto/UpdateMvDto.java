package cool.zhang0.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <修改作品参数>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 16:19
 */
@Data
@ApiModel(value="UpdateMvDto", description="修改MV作品基本信息")
public class UpdateMvDto extends AddMvDto{
    @ApiModelProperty(value = "MV作品ID", required = true)
    private Long mvId;
}
