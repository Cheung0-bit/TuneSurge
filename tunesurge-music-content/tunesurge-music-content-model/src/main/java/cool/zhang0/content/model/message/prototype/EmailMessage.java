package cool.zhang0.content.model.message.prototype;

import lombok.Data;

import java.io.Serializable;

/**
 * <邮件消息抽象类>
 *
 * @Author zhanglin
 * @createTime 2023/4/3 13:54
 */
@Data
public abstract class EmailMessage implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 收件人
     */
    private String[] to;

}
