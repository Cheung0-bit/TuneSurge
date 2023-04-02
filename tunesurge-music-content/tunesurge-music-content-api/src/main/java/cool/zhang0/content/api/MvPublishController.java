package cool.zhang0.content.api;

import cool.zhang0.content.model.dto.LikedUserDto;
import cool.zhang0.content.model.po.MvPublish;
import cool.zhang0.content.service.MvPublishService;
import cool.zhang0.content.util.SecurityUtil;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    @PostMapping("/mv-publish/{mvId}")
    public RestResponse<String> mvPublish(@PathVariable("mvId") Long mvId) {
        return mvPublishService.publish(mvId);
    }

    @ApiOperation("通过ID查询MV")
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

    @ApiOperation("用户点赞MV")
    @GetMapping("/mv-publish/like/{mvId}")
    public RestResponse<String> likeMv(@PathVariable("mvId") Long mvId) {
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        assert tsUser != null;
        return mvPublishService.likeMv(tsUser.getId(), mvId);
    }

    @ApiOperation("查看MV点赞用户前5名")
    @GetMapping("/mv-publish/queryMvLikes/{mvId}")
    public RestResponse<List<LikedUserDto>> queryMvLikes(@PathVariable("mvId") Long mvId) {
        return mvPublishService.queryMvLikes(mvId);
    }


}
