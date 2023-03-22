package cool.zhang0.exception;

import cool.zhang0.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * <全局异常增强>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 21:49
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse<String> doException(Exception e) {
        log.error("捕获异常：{}", e.getMessage());
        return RestResponse.validFail(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(TuneSurgeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse<String> doXueChengPlusException(TuneSurgeException e) {

        log.error("捕获异常：{}", e.getErrMsg());
        e.printStackTrace();
        // 编写异常处理逻辑 日志处理等等
        return RestResponse.validFail(e.getErrMsg());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse<String> doMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        //校验的错误信息
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        //收集错误
        StringBuffer errors = new StringBuffer();
        fieldErrors.forEach(error -> {
            errors.append("[").append(error.getField()).append("]").append(error.getDefaultMessage()).append(",");
        });

        return RestResponse.validFail(errors.toString());
    }

}
