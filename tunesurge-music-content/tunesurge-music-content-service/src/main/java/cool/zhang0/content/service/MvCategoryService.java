package cool.zhang0.content.service;

import cool.zhang0.content.model.dto.MvCategoryDto;
import cool.zhang0.model.RestResponse;

/**
 * <MV分类树形列表处理>
 *
 * @Author zhanglin
 * @createTime 2023/3/21 14:15
 */
public interface MvCategoryService {

    /**
     * 查询作品树形列表
     * @return
     */
    RestResponse<MvCategoryDto> queryTreeNodes();

}
