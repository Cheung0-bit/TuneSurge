package cool.zhang0.content.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cool.zhang0.content.model.dto.*;
import cool.zhang0.content.model.po.MvBase;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;

/**
 * <MV基本信息>
 *
 * @Author zhanglin
 * @createTime 2023/3/21 20:01
 */
public interface MvBaseService extends IService<MvBase> {

    /**
     * 根据用户ID查询对应的发布作品
     *
     * @param pageParams
     * @param queryMvBaseParams
     * @return
     */
    RestResponse<Page<MvBase>> queryMvBaseList(PageParams pageParams, QueryMvBaseParams queryMvBaseParams);

    /**
     * 添加MV作品基本信息
     * @param addMvDto
     * @return
     */
    RestResponse<MvBase> createMvBase(AddMvDto addMvDto);

    /**
     * 修稿作品信息
     * @param updateMvDto
     * @return
     */
    RestResponse<MvBase> updateMvBase(UpdateMvDto updateMvDto);

    /**
     * 逻辑删除MV作品
     * @param logicDeleteMvDto
     * @return
     */
    RestResponse<String> logicDeleteMvBase(LogicDeleteMvDto logicDeleteMvDto);

    /**
     * 回收站恢复MV作品
     * @param recoverMvDto
     * @return
     */
    RestResponse<String> recoverMvBase(RecoverMvDto recoverMvDto);
}
