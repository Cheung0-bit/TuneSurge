package cool.zhang0.content.service.impl;

import cool.zhang0.content.service.MvCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MvCategoryServiceImplTest {

    @Resource
    MvCategoryService mvCategoryService;

    @Test
    void queryTreeNodes() {
        System.out.println(mvCategoryService.queryTreeNodes());
    }
}