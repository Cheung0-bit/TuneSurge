package cool.zhang0.content.service;

/**
 * <邮件接口类>
 *
 * @Author zhanglin
 * @createTime 2023/4/3 15:17
 */
public interface EmailService {

    /**
     * 发送文本通知邮件
     *
     * @param to 目的邮件地址(列表)
     * @param title 邮件标题
     * @param text  邮件内容
     */
    void sendNotifyMail(String[] to, String title, String text);

}
