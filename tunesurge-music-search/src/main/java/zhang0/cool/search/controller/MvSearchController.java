package zhang0.cool.search.controller;

import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhang0.cool.search.model.dto.SearchMvParamDto;
import zhang0.cool.search.model.dto.SearchPageResultDto;
import zhang0.cool.search.model.po.MvIndex;
import zhang0.cool.search.service.MvSearchService;

import javax.annotation.Resource;

/**
 * <MV作品全文检索>
 *
 * @Author zhanglin
 * @createTime 2023/3/31 13:07
 */
@Api(value = "MV全文检索接口",tags = "MV全文检索接口")
@RestController
@RequestMapping("/mv")
public class MvSearchController {

    @Resource
    MvSearchService mvSearchService;

    @ApiOperation("MV全文检索列表")
    @GetMapping("/list")
    public RestResponse<SearchPageResultDto<MvIndex>> list(PageParams pageParams, SearchMvParamDto searchMvParamDto){
        return mvSearchService.queryMvPubIndex(pageParams, searchMvParamDto);
    }


}
