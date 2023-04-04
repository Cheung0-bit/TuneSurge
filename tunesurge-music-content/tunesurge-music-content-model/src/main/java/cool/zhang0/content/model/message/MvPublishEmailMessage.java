package cool.zhang0.content.model.message;

import cool.zhang0.content.model.message.prototype.EmailMessage;
import lombok.Data;

/**
 * <关注用户发布MV时邮件提醒>
 *
 * @Author zhanglin
 * @createTime 2023/4/3 14:02
 */
@Data
public class MvPublishEmailMessage extends EmailMessage {

    /**
     * UP主昵称
     */
    private String upName;

    /**
     * MV名称
     */
    private String mvName;

    /**
     * MV描述
     */
    private String description;

}
