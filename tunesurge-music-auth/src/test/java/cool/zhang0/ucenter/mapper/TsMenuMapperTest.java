package cool.zhang0.ucenter.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TsMenuMapperTest {

    @Resource
    TsMenuMapper tsMenuMapper;

    @Test
    void selectPermissionByUserId() {
        System.out.println(tsMenuMapper.selectPermissionByUserId(1L));
    }
}