package cool.zhang0.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cool.zhang0.content.mapper.MvBaseMapper;
import cool.zhang0.content.model.dto.*;
import cool.zhang0.content.model.po.MvBase;
import cool.zhang0.content.service.MvBaseService;
import cool.zhang0.exception.TuneSurgeException;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <实现>
 *
 * @Author zhanglin
 * @createTime 2023/3/21 20:56
 */
@Service
@Slf4j
public class MvBaseServiceImpl extends ServiceImpl<MvBaseMapper, MvBase> implements MvBaseService {

    @Resource
    MvBaseMapper mvBaseMapper;

    @Override
    public RestResponse<Page<MvBase>> queryMvBaseList(PageParams pageParams, QueryMvBaseParams queryMvBaseParams) {
        LambdaQueryWrapper<MvBase> queryWrapper = new LambdaQueryWrapper<>();
        // 根据用户ID筛选
        queryWrapper.eq(MvBase::getCreateUser, queryMvBaseParams.getUserId());
        queryWrapper.like(MvBase::getName, queryMvBaseParams.getMvName());
        Page<MvBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<MvBase> mvBasePage = mvBaseMapper.selectPage(page, queryWrapper);
        return RestResponse.success(mvBasePage);
    }

    @Override
    public RestResponse<MvBase> createMvBase(AddMvDto addMvDto) {
        // 参数校验 Valid注解已经实现
        MvBase mvBase = new MvBase();
        // 拷贝
        BeanUtils.copyProperties(addMvDto, mvBase);
        // 设置审核状态和发布状态
        mvBase.setAuditStatus("0");
        mvBase.setStatus("0");
        int insert = mvBaseMapper.insert(mvBase);
        // 判断是否插入成功
        if (insert < 1) {
            log.error("插入过程出错：{}", addMvDto);
            TuneSurgeException.cast("数据库插入出现错误");
        }
        Long mvBaseId = mvBase.getId();
        return RestResponse.success(this.getById(mvBaseId));
    }

    @Override
    public RestResponse<MvBase> updateMvBase(UpdateMvDto updateMvDto) {
        Long mvId = updateMvDto.getMvId();
        MvBase mvBase = mvBaseMapper.selectById(mvId);
        if (mvBase == null) {
            TuneSurgeException.cast("待更改的课程不存在");
        }

        // 校验用户只能更改自己的课程
        if (!mvBase.getCreateUser().equals(updateMvDto.getCreateUser())) {
            TuneSurgeException.cast("系统异常");
        }
        BeanUtils.copyProperties(updateMvDto, mvBase);
        // 重新进行审核
        mvBase.setAuditStatus("0");
        mvBase.setStatus("0");
        mvBaseMapper.updateById(mvBase);
        return RestResponse.success(this.getById(mvBase.getId()));
    }

    @Override
    public RestResponse<String> logicDeleteMvBase(LogicDeleteMvDto logicDeleteMvDto) {
        Long mvId = logicDeleteMvDto.getMvId();
        MvBase mvBase = mvBaseMapper.selectById(mvId);
        if (mvBase == null) {
            TuneSurgeException.cast("待逻辑删除的课程不存在");
        }
        // 校验用户只能更改自己的课程
        if (!mvBase.getCreateUser().equals(logicDeleteMvDto.getUserId())) {
            TuneSurgeException.cast("系统异常");
        }
        mvBase.setIsDelete("1");
        mvBaseMapper.deleteById(mvBase.getId());
        return RestResponse.success();
    }

    @Override
    public RestResponse<String> recoverMvBase(RecoverMvDto recoverMvDto) {
        Long mvId = recoverMvDto.getMvId();
        MvBase mvBase = mvBaseMapper.queryLogicDelMvById(mvId);
        if (mvBase == null) {
            TuneSurgeException.cast("待逻辑删除的课程不存在");
        }
        mvBaseMapper.recoverMv(mvBase.getId());
        return RestResponse.success();
    }
}
