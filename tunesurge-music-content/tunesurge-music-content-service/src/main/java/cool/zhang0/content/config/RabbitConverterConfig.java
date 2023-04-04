package cool.zhang0.content.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

/**
 * <MQ消息转换器配置>
 *
 * @Author zhanglin
 * @createTime 2023/4/3 15:23
 */
@Configuration
public class RabbitConverterConfig {

    /**
     * 配置JSON序列化转换器
     */
    @Bean
    public MessageConverter JSONConverter() {
        ObjectMapper mapper = new ObjectMapper();
        // 注册Java 8时间支持模块
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

}
