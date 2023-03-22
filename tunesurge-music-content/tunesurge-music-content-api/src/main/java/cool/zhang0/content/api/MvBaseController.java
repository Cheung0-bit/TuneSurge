package cool.zhang0.content.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.zhang0.content.model.dto.QueryMvBaseParams;
import cool.zhang0.content.model.po.MvBase;
import cool.zhang0.content.service.MvBaseService;
import cool.zhang0.content.util.SecurityUtil;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <MV基本信息接口>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 12:28
 */
@RestController
@Api(value = "MV作品基本信息处理", tags = "MV作品基本信息处理")
public class MvBaseController {

    @Resource
    MvBaseService mvBaseService;

    @ApiOperation("我的MV作品列表接口")
    @PostMapping("/mv/list")
    public RestResponse<Page<MvBase>> list(PageParams params, @RequestParam("mvName") String mvName) {

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

}
