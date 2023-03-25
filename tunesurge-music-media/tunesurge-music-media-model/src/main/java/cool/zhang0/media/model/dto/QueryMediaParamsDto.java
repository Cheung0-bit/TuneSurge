package cool.zhang0.media.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <媒资查询参数表>
 *
 * @Author zhanglin
 * @createTime 2023/3/24 14:38
 */
@Data
@ApiModel(value = "QueryMediaParamsDto", description = "Media查询参数表")
public class QueryMediaParamsDto {

    @ApiModelProperty("媒资文件名称")
    private String fileName;
    @ApiModelProperty("媒资类型")
    private String fileType;
    @ApiModelProperty("审核状态")
    private String auditStatus;

}
