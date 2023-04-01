package cool.zhang0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.security.auth.login.Configuration;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/3/19 22:31
 */
@EnableFeignClients
@SpringBootApplication
public class ContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
