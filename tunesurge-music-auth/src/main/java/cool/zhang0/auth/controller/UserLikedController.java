package cool.zhang0.auth.controller;

import cn.hutool.core.bean.BeanUtil;
import cool.zhang0.ucenter.model.dto.LikedUserDto;
import cool.zhang0.ucenter.model.dto.MvLikesUserDto;
import cool.zhang0.ucenter.service.TsUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <接口测试类>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 16:25
 */
@RestController
@Api(value = "用户点赞排序查看接口", tags = "用户点赞排序查看接口")
public class UserLikedController {

    @Resource
    TsUserService tsUserService;

    @PostMapping("/likedSorted")
    @ApiOperation("点赞排行")
    public List<LikedUserDto> likedSorted(@RequestBody MvLikesUserDto mvLikesUserDto) {

        return tsUserService.query()
                .in("id", mvLikesUserDto.getUserIds()).last("ORDER BY FIELD(id," + mvLikesUserDto.getUserIdStr() + ")").list()
                .stream()
                .map(tsUser -> BeanUtil.copyProperties(tsUser, LikedUserDto.class))
                .collect(Collectors.toList());

    }


}
