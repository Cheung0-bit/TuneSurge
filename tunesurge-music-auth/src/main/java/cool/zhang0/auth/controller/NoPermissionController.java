package cool.zhang0.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <无权限测试接口>
 *
 * @Author zhanglin
 * @createTime 2023/4/4 14:56
 */
@RestController
@Api(value = "无权限测试接口", tags = "无权限测试接口")
public class NoPermissionController {

    @GetMapping("/power-test")
    @ApiOperation("查看是否具备权限")
    @PreAuthorize("hasAuthority('power-test')")
    public String powerTest() {
        return "ok";
    }

}
