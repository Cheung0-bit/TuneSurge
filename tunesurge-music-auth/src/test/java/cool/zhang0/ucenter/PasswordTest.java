package cool.zhang0.ucenter;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/3/19 12:26
 */
public class PasswordTest {

    @Test
    void run() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "123";
        System.out.println(passwordEncoder.encode(password));
    }

}
