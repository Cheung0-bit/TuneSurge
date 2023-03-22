package cool.zhang0.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.zhang0.content.model.po.MvBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author zhanglin
* @description 针对表【mv_base】的数据库操作Mapper
* @createDate 2023-03-21 19:56:33
* @Entity generator.domain.MvBase
*/
@Mapper
public interface MvBaseMapper extends BaseMapper<MvBase> {

    /**
     * 查询被逻辑删除的记录
     * @param mvId
     * @return
     */
    @Select("select mb.id from mv_base mb where mb.is_delete = '1' and mb.id = #{mvId}")
    MvBase queryLogicDelMvById(@Param("mvId") Long mvId);

    /**
     * 恢复
     * @param mvId
     * @return
     */
    @Update("update mv_base set is_delete = '0' where id = #{mvId}")
    int recoverMv(@Param("mvId") Long mvId);


}




