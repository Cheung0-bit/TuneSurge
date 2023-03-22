package cool.zhang0.content.model.dto;

import cool.zhang0.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * <添加MV作品>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 14:21
 */
@Data
@ApiModel(value="AddMvDto", description="新增MV作品基本信息")
public class AddMvDto {

    @NotEmpty(message = "添加MV名称不能为空",groups={ValidationGroups.Insert.class})
    @NotEmpty(message = "修改MV名称不能为空",groups={ValidationGroups.Update.class})
    @ApiModelProperty(value = "MV作品名称", required = true)
    private String name;

    @NotEmpty(message = "请至少添加一条标签")
    @Size(message = "字数控制在10以内", max = 10)
    @ApiModelProperty(value = "MV作品标签", required = true)
    private String tags;

    /**
     * 一级分类
     */
    @NotEmpty(message = "一级分类不可为空")
    private String typeOne;

    /**
     * 二级分类
     */
    @NotEmpty(message = "二级分类不可为空")
    private String typeTwo;

    /**
     * 三级分类
     */
    private String typeThree;

    /**
     * 课程介绍
     */
    @NotEmpty(message = "MV描述不可为空")
    @Size(message = "长度控制在200以内", max = 200)
    private String description;

    /**
     * 封面图片
     */
    @NotEmpty(message = "封面图不可为空")
    private String pic;

    /**
     * 创建人
     */
    private Long createUser;

}
