package cool.zhang0.content.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.zhang0.content.model.dto.QueryMvBaseParams;
import cool.zhang0.content.model.po.MvBase;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;

/**
 * <MV基本信息>
 *
 * @Author zhanglin
 * @createTime 2023/3/21 20:01
 */
public interface MvBaseService {

    /**
     * 根据用户ID查询对应的发布作品
     *
     * @param pageParams
     * @param queryMvBaseParams
     * @return
     */
    RestResponse<Page<MvBase>> queryMvBaseList(PageParams pageParams, QueryMvBaseParams queryMvBaseParams);

}
