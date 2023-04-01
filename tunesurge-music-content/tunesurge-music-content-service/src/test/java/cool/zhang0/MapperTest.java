package cool.zhang0;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.zhang0.content.mapper.MvCategoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/3/21 13:48
 */
@SpringBootTest
public class MapperTest {

    @Resource
    MvCategoryMapper mvCategoryMapper;

    @Test
    void categoryTest() {
        System.out.println(mvCategoryMapper.selectPage(new Page<>(1L, 5L), null));
    }

}
