package cool.zhang0.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.zhang0.content.mapper.MvBaseMapper;
import cool.zhang0.content.model.dto.QueryMvBaseParams;
import cool.zhang0.content.model.po.MvBase;
import cool.zhang0.content.service.MvBaseService;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <实现>
 *
 * @Author zhanglin
 * @createTime 2023/3/21 20:56
 */
@Service
public class MvBaseServiceImpl implements MvBaseService {

    @Resource
    MvBaseMapper mvBaseMapper;

    @Override
    public RestResponse<Page<MvBase>> queryMvBaseList(PageParams pageParams, QueryMvBaseParams queryMvBaseParams) {
        LambdaQueryWrapper<MvBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MvBase::getId, queryMvBaseParams.getUserId());
        queryWrapper.like(MvBase::getName, queryMvBaseParams.getMvName());
        Page<MvBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<MvBase> mvBasePage = mvBaseMapper.selectPage(page, queryWrapper);
        return RestResponse.success(mvBasePage);
    }
}
