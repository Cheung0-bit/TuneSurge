package cool.zhang0.content.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.zhang0.content.model.dto.*;
import cool.zhang0.content.model.po.MvAudit;
import cool.zhang0.content.model.po.MvBase;
import cool.zhang0.content.service.MvBaseService;
import cool.zhang0.content.util.SecurityUtil;
import cool.zhang0.exception.ValidationGroups;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <MV基本信息接口>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 12:28
 */
@RestController
@RequestMapping("mv-base")
@Api(value = "MV作品基本信息处理", tags = "MV作品基本信息处理")
public class MvBaseController {

    @Resource
    MvBaseService mvBaseService;

    @ApiOperation("我的MV作品列表接口")
    @PostMapping("/list")
    public RestResponse<Page<MvBase>> list(PageParams params, @RequestParam(value = "mvName", required = false) String mvName) {

        // 当前登录用户
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();

        // 组装查询参数
        QueryMvBaseParams queryMvBaseParams = new QueryMvBaseParams();
        queryMvBaseParams.setMvName(mvName);
        assert tsUser != null;
        queryMvBaseParams.setUserId(tsUser.getId());

        //调用service获取数据
        return mvBaseService.queryMvBaseList(params, queryMvBaseParams);
    }

    @ApiOperation("添加MV作品接口，并提交审核")
    @PostMapping
    public RestResponse<MvBase> createMvBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddMvDto addMvDto) {
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        assert tsUser != null;
        addMvDto.setCreateUser(tsUser.getId());
        return mvBaseService.createMvBase(addMvDto);
    }

    @ApiOperation("修改MV作品接口，并提交审核")
    @PutMapping
    public RestResponse<MvBase> updateMvBase(@RequestBody @Validated(ValidationGroups.Update.class) UpdateMvDto updateMvDto) {
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        assert tsUser != null;
        updateMvDto.setCreateUser(tsUser.getId());
        return mvBaseService.updateMvBase(updateMvDto);
    }

    @ApiOperation("逻辑删除MV作品接口")
    @DeleteMapping("/{mvId}")
    public RestResponse<String> logicDeleteMvBase(@PathVariable("mvId") Long mvId) {
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        assert tsUser != null;
        LogicDeleteMvDto logicDeleteMvDto = new LogicDeleteMvDto();
        logicDeleteMvDto.setUserId(tsUser.getId());
        logicDeleteMvDto.setMvId(mvId);
        return mvBaseService.logicDeleteMvBase(logicDeleteMvDto);
    }

    @ApiOperation("恢复MV作品接口")
    @PutMapping("/recover/{mvId}")
    public RestResponse<String> recoverMvBase(@PathVariable("mvId") Long mvId) {
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        assert tsUser != null;
        RecoverMvDto recoverMvDto = new RecoverMvDto();
        recoverMvDto.setUserId(tsUser.getId());
        recoverMvDto.setMvId(mvId);
        return mvBaseService.recoverMvBase(recoverMvDto);
    }

    @ApiOperation("通过ID获取MV作品")
    @GetMapping("/{mvId}")
    public RestResponse<MvBase> getById(@PathVariable("mvId") Long mvId) {
        MvBase mvBase = mvBaseService.getById(mvId);
        return RestResponse.success(mvBase);
    }

    @ApiOperation("审核MV作品")
    @PostMapping("/audit")
    @PreAuthorize("hasAuthority('ts_sys_mv_audit')")
    public RestResponse<MvAudit> doAudit(@RequestBody @Validated AuditDto auditDto) {
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        assert tsUser != null;
        auditDto.setAuditPeople(tsUser.getId());
        return mvBaseService.auditMv(auditDto);
    }


}
