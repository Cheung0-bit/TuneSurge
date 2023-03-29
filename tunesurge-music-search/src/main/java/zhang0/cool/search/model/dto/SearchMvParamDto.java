package zhang0.cool.search.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * <查询参数>
 *
 * @Author zhanglin
 * @createTime 2023/3/29 10:57
 */
@Data
@ApiModel(value = "SearchMvParamDto", description = "elasticsearch模糊查询的参数表")
public class SearchMvParamDto {

    @ApiModelProperty(value = "查询关键字", required = true)
    @NotEmpty
    private String keyWords;

    @ApiModelProperty(value = "一级分类")
    private String typeOneName;

    @ApiModelProperty(value = "二级分类")
    private String typeTwoName;

    @ApiModelProperty(value = "三级分类")
    private String typeThreeName;


}
