# Base模块

此模块为公共依赖模块，引入了大量的SDK依赖。

值得一提的是，我将一些全局共享的参数类、自定义异常类、工具类放入其中

## 自定义系统异常

~~~java
public class TuneSurgeException extends RuntimeException{

    private String errMsg;

    public TuneSurgeException() {
        super();
    }

    public TuneSurgeException(String message) {
        super(message);
        this.errMsg = message;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public static void cast(String errMsg) {
        throw new TuneSurgeException(errMsg);
    }

}
~~~

此类继承RuntimeException类，自定义静态方法，供直接调用。

### 思考：为什么要自定义系统异常

是为了装逼吗，不全是。自定义系统异常可以帮助我们在系统出现问题，查看日志时快速分析出是哪一个模块的错误。如果日志系统打印的是TuneSurge的异常，那说明我的业务哪里出问题了！而不是依赖的SDK包中某个地方出问题了。

## 全局异常捕获

~~~java
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
~~~

得益于@ControllerAdvice全局增强注解，可在每次的MVC请求中监听捕获异常，减少代码中自己不断的try-catch。这里，我简单的监听了以下TuneSurgeException、MethodArgumentNotValidException、Exception。分别是自定义系统异常、JSR303校验异常、所有异常。

## 分页参数、消息响应参数

~~~java
@Data
public class PageParams {


    /**
     * 当前页码默认值
     */
    public static final long DEFAULT_PAGE_CURRENT = 1L;

    /**
     * 每页记录数默认值
     */
    public static final long DEFAULT_PAGE_SIZE = 10L;

    /**
     * 当前页码
     */
    @ApiModelProperty("当前页码")
    private Long pageNo = DEFAULT_PAGE_CURRENT;

    /**
     * 每页记录数默认值
     */
    @ApiModelProperty("每页大小")
    private Long pageSize = DEFAULT_PAGE_SIZE;

    public PageParams(){

    }

    public PageParams(long pageNo,long pageSize){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

}

~~~

~~~java
@Data
public class RestResponse<T> {

    /**
     * 响应编码,0为正常,-1错误
     */
    private int code;

    /**
     * 响应提示信息
     */
    private String msg;

    /**
     * 响应内容
     */
    private T data;


    public RestResponse() {
        this(0, "success");
    }

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 错误信息的封装
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> RestResponse<T> validFail(String msg) {
        RestResponse<T> response = new RestResponse<T>();
        response.setCode(-1);
        response.setMsg(msg);
        return response;
    }
    public static <T> RestResponse<T> validFail(T data,String msg) {
        RestResponse<T> response = new RestResponse<T>();
        response.setCode(-1);
        response.setData(data);
        response.setMsg(msg);
        return response;
    }

    /**
     * 添加正常响应数据（包含响应内容）
     *
     * @return RestResponse Rest服务封装相应数据
     */
    public static <T> RestResponse<T> success(T data) {
        RestResponse<T> response = new RestResponse<T>();
        response.setData(data);
        return response;
    }
    public static <T> RestResponse<T> success(T data,String msg) {
        RestResponse<T> response = new RestResponse<T>();
        response.setData(data);
        response.setMsg(msg);
        return response;
    }

    /**
     * 添加正常响应数据（不包含响应内容）
     *
     * @return RestResponse Rest服务封装相应数据
     */
    public static <T> RestResponse<T> success() {
        return new RestResponse<T>();
    }

}
~~~

这样，整个系统在前后端对接中显得更加规范、高效。

