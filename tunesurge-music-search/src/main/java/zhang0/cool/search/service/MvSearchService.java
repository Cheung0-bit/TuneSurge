package zhang0.cool.search.service;

import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import zhang0.cool.search.model.dto.SearchMvParamDto;
import zhang0.cool.search.model.dto.SearchPageResultDto;
import zhang0.cool.search.model.po.MvIndex;

/**
 * <MV搜索>
 *
 * @Author zhanglin
 * @createTime 2023/3/28 20:59
 */
public interface MvSearchService {

    /**
     * 全文检索并返回分页
     * @param pageParams 分页参数
     * @param searchMvParamDto 查询参数
     * @return
     */
    RestResponse<SearchPageResultDto<MvIndex>> queryMvPubIndex(PageParams pageParams, SearchMvParamDto searchMvParamDto);

}
