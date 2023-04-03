package cool.zhang0.content.api;

import cool.zhang0.content.model.dto.CommonFollowUserDto;
import cool.zhang0.content.service.TsFollowService;
import cool.zhang0.content.util.SecurityUtil;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <关注相关接口>
 *
 * @Author zhanglin
 * @createTime 2023/4/2 15:15
 */
@RestController
@Api(value = "关注相关接口", tags = "关注相关接口")
public class FollowController {

    @Resource
    TsFollowService tsFollowService;

    @PostMapping("/follow")
    @ApiOperation("关注用户")
    RestResponse<String> follow(@RequestParam("followUserId") Long followUserId, @RequestParam("isFollow") Boolean isFollow) {
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        assert tsUser != null;
        if (tsUser.getId().equals(followUserId)) {
            return RestResponse.validFail("不能关注/取关自己");
        }
        return tsFollowService.follow(tsUser.getId(), followUserId, isFollow);
    }

    @PostMapping("/isFollow")
    @ApiOperation("是否关注")
    RestResponse<String> isFollow(@RequestParam("followUserId") Long followUserId) {
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        assert tsUser != null;
        return tsFollowService.isFollow(tsUser.getId(), followUserId);
    }

    @PostMapping("/followCommons")
    @ApiOperation("求取共同关注")
    RestResponse<List<CommonFollowUserDto>> followCommons(@RequestParam("anotherUserId") Long anotherUserId) {
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        assert tsUser != null;
        return tsFollowService.followCommons(tsUser.getId(), anotherUserId);
    }


}
