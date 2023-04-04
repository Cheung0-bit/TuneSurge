package cool.zhang0.content.listener;

import cool.zhang0.content.model.message.MvPublishEmailMessage;
import cool.zhang0.content.model.message.prototype.EmailMessage;
import cool.zhang0.content.service.EmailService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <邮件发送监听器>
 *
 * @Author zhanglin
 * @createTime 2023/4/3 21:05
 */
@Component
public class EmailPublishMessageListener {

    @Resource
    EmailService emailService;

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

}
