package cool.zhang0.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <接口测试类>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 16:25
 */
@RestController
@Api(value = "Auth测试接口",tags = "Auth测试接口")
public class TestController {

    @GetMapping("/doLogin")
    @ApiOperation("登录")
    @PreAuthorize("hasAuthority('test')")
    public String doLogin() {
        return "Login Success";
    }


}
