package cool.zhang0.checknode.controller;

import cool.zhang0.checknode.model.CheckCodeParamsDto;
import cool.zhang0.checknode.model.CheckCodeResultDto;
import cool.zhang0.checknode.service.CheckCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <验证码服务接口>
 *
 * @Author zhanglin
 * @createTime 2023/3/19 13:35
 */
@Api(value = "验证码服务接口",tags = "验证码服务接口")
@RestController
public class CheckCodeController {

    @Resource(name = "PicCheckCodeService")
    private CheckCodeService picCheckCodeService;


    @ApiOperation(value="生成验证信息", notes="生成验证信息")
    @PostMapping(value = "/pic")
    public CheckCodeResultDto generatePicCheckCode(CheckCodeParamsDto checkCodeParamsDto){
        return picCheckCodeService.generate(checkCodeParamsDto);
    }

    @ApiOperation(value="校验", notes="校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "业务名称", required = true, dataType = "String", paramType="query"),
            @ApiImplicitParam(name = "key", value = "验证key", required = true, dataType = "String", paramType="query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType="query")
    })
    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code){
        Boolean isSuccess = picCheckCodeService.verify(key,code);
        return isSuccess;
    }
}
