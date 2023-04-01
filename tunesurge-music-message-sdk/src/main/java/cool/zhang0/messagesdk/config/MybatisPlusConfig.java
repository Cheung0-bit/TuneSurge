package cool.zhang0.messagesdk.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/3/31 16:10
 */
@Configuration("message-sdk")
@MapperScan("cool.zhang0.messagesdk.mapper")
public class MybatisPlusConfig {
}
