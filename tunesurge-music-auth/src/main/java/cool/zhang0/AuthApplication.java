package cool.zhang0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <Auth模块启动类>
 *
 * @Author zhanglin
 * @createTime 2023/3/15 16:04
 */
@SpringBootApplication
@EnableFeignClients
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }


}
