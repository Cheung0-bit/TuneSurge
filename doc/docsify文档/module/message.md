# 消息通知

使用RabbitMQ异步发送Eamil提醒粉丝用户新的MV简讯

## 封装消息内容

由于邮件发送的内容会根据业务动态调整，于是笔者在这里使用原型模式建立邮件消息类：

~~~java
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
~~~

以上是一个抽象类，本项目中需要通知粉丝MV内容，于是进行了进一步的设计：

~~~java
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
~~~

## 构建邮件发送服务类

~~~java
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String sender;

    @Resource
    private JavaMailSender mailSender;

    @Override
    public void sendNotifyMail(String[] to, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(title);
        message.setText(text);
        mailSender.send(message);
    }
}
~~~

## RabbitMQ监听

本项目采用消息订阅模型中的Topic交换机，原因是该交换机更加灵活，可扩展性强

~~~java
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "tunesurge.mv-publish-email-queue"),
        exchange = @Exchange(name = "tunesurge.mv-publish-topic", type = ExchangeTypes.TOPIC),
        key = "email.mv-publish"
))
public void sendPublishMessage(MvPublishEmailMessage mvPublishEmailMessage) {
    StringBuilder builder = new StringBuilder();
    builder.append("您关注的UP主：").append(mvPublishEmailMessage.getUpName())
            .append("\n发布了新的MV作品:").append(mvPublishEmailMessage.getMvName())
            .append("\n作品简介：").append(mvPublishEmailMessage.getDescription());
    emailService.sendNotifyMail(mvPublishEmailMessage.getTo(), mvPublishEmailMessage.getTitle(), builder.toString());
}
~~~

## MV发布时生产信息

![image-20230404211936725](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404211936725.png)

## 消息确认与可靠性

## 消息发布测试

![image-20230404212346865](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404212346865.png)

![image-20230404212336061](https://0-bit.oss-cn-beijing.aliyuncs.com/image-20230404212336061.png)