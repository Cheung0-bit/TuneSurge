package cool.zhang0.exception;

/**
 * <系统异常>
 *
 * @Author zhanglin
 * @createTime 2023/3/22 15:20
 */
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
