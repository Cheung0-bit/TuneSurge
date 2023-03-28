package cool.zhang0.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.zhang0.content.model.po.MvCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author zhanglin
* @description 针对表【mv_category】的数据库操作Mapper
* @createDate 2023-03-21 13:34:04
* @Entity generator.domain.MvCategory
*/
@Mapper
public interface MvCategoryMapper extends BaseMapper<MvCategory> {

    /**
     * 根据ID查询分类名称
     * @param id
     * @return
     */
    @Select("SELECT mc.`name` from mv_category mc where id = #{id}")
    String selectCategoryName(@Param("id") String id);

}




