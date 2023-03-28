package cool.zhang0.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cool.zhang0.content.mapper.MvAuditMapper;
import cool.zhang0.content.mapper.MvBaseMapper;
import cool.zhang0.content.mapper.MvCategoryMapper;
import cool.zhang0.content.mapper.MvPublishPreMapper;
import cool.zhang0.content.model.dto.*;
import cool.zhang0.content.model.po.MvAudit;
import cool.zhang0.content.model.po.MvBase;
import cool.zhang0.content.model.po.MvPublishPre;
import cool.zhang0.content.service.MvBaseService;
import cool.zhang0.exception.TuneSurgeException;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    MvCategoryMapper mvCategoryMapper;

    @Resource
    MvAuditMapper mvAuditMapper;

    @Resource
    MvPublishPreMapper mvPublishPreMapper;

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
        // 审核状态：已提交
        mvBase.setAuditStatus("1");
        // 发布状态：未发布
        mvBase.setStatus("0");
        // 事务1 将MV信息插入基本信息表
        int insert = mvBaseMapper.insert(mvBase);

        // 提交审核信息到预发布表
        MvPublishPre mvPublishPre = new MvPublishPre();
        // 拷贝
        BeanUtils.copyProperties(mvBase, mvPublishPre);
        mvPublishPre.setMvName(mvBase.getName());
        mvPublishPre.setMvTags(mvBase.getTags());
        // 置换分类名称
        replaceCategory(mvBase, mvPublishPre);
        // 添加提交审核人ID
        mvPublishPre.setPushUser(mvBase.getCreateUser());
        // 事务2
        int insert1 = mvPublishPreMapper.insert(mvPublishPre);

        // 判断是否插入成功
        if (insert < 1 || insert1 < 1) {
            log.error("插入过程出错：{}", addMvDto);
            TuneSurgeException.cast("数据库插入出现错误");
        }
        return RestResponse.success();
    }

    private void replaceCategory(MvBase mvBase, MvPublishPre mvPublishPre) {
        mvPublishPre.setTypeOneName(mvCategoryMapper.selectCategoryName(mvBase.getTypeOne()));
        mvPublishPre.setTypeTwoName(mvCategoryMapper.selectCategoryName(mvBase.getTypeTwo()));
        if (StringUtils.isNotEmpty(mvBase.getTypeThree())) {
            mvPublishPre.setTypeThreeName(mvCategoryMapper.selectCategoryName(mvBase.getTypeThree()));
        }
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
        mvBase.setAuditStatus("1");
        mvBase.setStatus("0");
        // 事务1 更新MV信息表
        int tpOne = mvBaseMapper.updateById(mvBase);

        // 重新提交审核信息到预发布表
        MvPublishPre getMvPublishPre = mvPublishPreMapper.selectById(mvId);
        if (getMvPublishPre == null) {
            MvPublishPre mvPublishPre = new MvPublishPre();
            // 拷贝
            BeanUtils.copyProperties(mvBase, mvPublishPre);
            mvPublishPre.setMvName(mvBase.getName());
            mvPublishPre.setMvTags(mvBase.getTags());
            // 置换分类名称
            replaceCategory(mvBase, mvPublishPre);
            // 添加提交审核人ID
            mvPublishPre.setPushUser(mvBase.getCreateUser());
            // 事务2
            int tpTwo = mvPublishPreMapper.insert(mvPublishPre);
            if (tpOne < 1 || tpTwo < 1) {
                TuneSurgeException.cast("更新MV信息发生错误");
            }
        } else {
            BeanUtils.copyProperties(mvBase, getMvPublishPre);
            getMvPublishPre.setMvName(mvBase.getName());
            getMvPublishPre.setMvTags(mvBase.getTags());
            // 置换分类名称
            replaceCategory(mvBase, getMvPublishPre);
            // 事务2
            int tpTwo = mvPublishPreMapper.updateById(getMvPublishPre);
            if (tpOne < 1 || tpTwo < 1) {
                TuneSurgeException.cast("更新MV信息发生错误");
            }
        }
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

    @Override
    public RestResponse<MvAudit> auditMv(AuditDto auditDto) {

        MvAudit mvAudit = new MvAudit();
        BeanUtils.copyProperties(auditDto, mvAudit);
        int insert = mvAuditMapper.insert(mvAudit);

        LambdaUpdateWrapper<MvBase> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(MvBase::getId, mvAudit.getMvId());
        if ("0".equals(mvAudit.getAuditStatus())) {
            // 审核不通过
            lambdaUpdateWrapper.set(MvBase::getAuditStatus, "2");
        } else {
            // 审核通过
            lambdaUpdateWrapper.set(MvBase::getAuditStatus, "3");
        }
        int update = mvBaseMapper.update(null, lambdaUpdateWrapper);

        if (insert < 1 || update < 1) {
            TuneSurgeException.cast("修改审核信息时，事务异常");
        }
        return RestResponse.success(mvAudit);
    }
}
