package cool.zhang0.content.model.dto;

import cool.zhang0.content.model.po.MvCategory;
import lombok.Data;

import java.util.List;

/**
 * <用于处理树形结构>
 *
 * @Author zhanglin
 * @createTime 2023/3/21 14:16
 */
@Data
public class MvCategoryDto extends MvCategory {

    List<MvCategoryDto> children;

}
