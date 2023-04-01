package cool.zhang0.content.api;

import cool.zhang0.content.model.po.MvPublish;
import cool.zhang0.content.service.MvPublishService;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <MV发布>
 *
 * @Author zhanglin
 * @createTime 2023/3/28 19:10
 */
@Api(value = "MV发布接口", tags = "MV发布接口")
@RestController
public class MvPublishController {

    @Resource
    MvPublishService mvPublishService;

    @ApiOperation("MV发布")
    @ResponseBody
    @PostMapping("/mv-publish/{mvId}")
    public RestResponse<String> mvPublish(@PathVariable("mvId") Long mvId) {
        return mvPublishService.publish(mvId);
    }

    @ApiOperation("通过ID查询MV")
    @ResponseBody
    @GetMapping("/mv-publish/{mvId}")
    public RestResponse<MvPublish> queryById(@PathVariable("mvId") Long mvId) {
        return mvPublishService.queryMvById(mvId);
    }

    @ApiOperation("通过ID查询MV缓存")
    @ResponseBody
    @GetMapping("/mv-publish/cache/{mvId}")
    public RestResponse<MvPublish> queryCacheById(@PathVariable("mvId") Long mvId) {
        return mvPublishService.queryMvCacheById(mvId);
    }


}
