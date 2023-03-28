package cool.zhang0.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * <审核参数表>
 *
 * @Author zhanglin
 * @createTime 2023/3/28 11:30
 */
@Data
@ApiModel(value = "AuditDto", description = "进行审核提交的参数表")
public class AuditDto {

    @ApiModelProperty(value = "MV编号", required = true)
    private Long mvId;

    @ApiModelProperty(value = "审核意见")
    private String auditMind;

    @ApiModelProperty(value = "审核状态", required = true)
    @NotEmpty
    private String auditStatus;

    private Long auditPeople;

}
