package cool.zhang0.content.api;

import cool.zhang0.content.model.dto.MvCategoryDto;
import cool.zhang0.content.service.MvCategoryService;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <MV分类>
 *
 * @Author zhanglin
 * @createTime 2023/3/21 15:02
 */
@Api(value = "MV作品分类查询接口", tags = "MV作品分类查询接口")
@RestController
public class MvCategoryController {

    @Resource
    MvCategoryService mvCategoryService;

    @GetMapping("/mv-category/tree-nodes")
    public RestResponse<MvCategoryDto> queryTreeNodes() {
        return mvCategoryService.queryTreeNodes();
    }

}
