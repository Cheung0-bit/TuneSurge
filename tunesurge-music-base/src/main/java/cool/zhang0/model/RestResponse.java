package cool.zhang0.model;

import lombok.Data;

/**
 * <统一全局返回体>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 20:50
 */
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
