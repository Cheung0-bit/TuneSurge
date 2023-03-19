package cool.zhang0.ucenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.zhang0.ucenter.model.po.TsMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author zhanglin
* @description 针对表【ts_menu】的数据库操作Mapper
* @createDate 2023-03-16 18:32:00
* @Entity generator.domain.TsMenu
*/
@Mapper
public interface TsMenuMapper extends BaseMapper<TsMenu> {

    /**
     * 通过用户ID查询权限集合
     * @param userId
     * @return
     */
    List<TsMenu> selectPermissionByUserId(@Param("userId") Long userId);

}




