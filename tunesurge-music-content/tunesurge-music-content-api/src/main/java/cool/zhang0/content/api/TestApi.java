package cool.zhang0.content.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/3/19 22:28
 */
@RestController
public class TestApi {

    @GetMapping("/test")
    public String test() {
        return null;
    }

}
