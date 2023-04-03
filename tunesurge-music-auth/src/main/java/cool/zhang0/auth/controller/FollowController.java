package cool.zhang0.auth.controller;

import cn.hutool.core.bean.BeanUtil;
import cool.zhang0.ucenter.model.dto.CommonFollowUserDto;
import cool.zhang0.ucenter.service.TsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/4/2 15:09
 */
@RestController
@Api(value = "用户共同关注查看接口", tags = "用户共同关注查看接口")
public class FollowController {

    @Resource
    TsUserService tsUserService;

    @PostMapping("/getUserList")
    @ApiOperation("获取用户列表")
    public List<CommonFollowUserDto> getUserList(@RequestParam("userIds") String userIds) {
        String[] split = userIds.split(",");
        List<Long> collect = Arrays.stream(split).map(Long::valueOf).collect(Collectors.toList());
        return tsUserService.listByIds(collect).stream().map(tsUser -> BeanUtil.copyProperties(tsUser, CommonFollowUserDto.class)).collect(Collectors.toList());

    }

}
