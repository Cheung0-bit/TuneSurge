package cool.zhang0.exception;

import cool.zhang0.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <全局异常增强>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 21:49
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody//将信息返回为 json格式
    @ExceptionHandler(Exception.class)//此方法捕获Exception异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//状态码返回500
    public RestResponse<String> doException(Exception e) {
        log.error("捕获异常：{}", e.getMessage());
        return RestResponse.validFail(e.getMessage());
    }

}
