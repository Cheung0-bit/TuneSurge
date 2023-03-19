package cool.zhang0.ucenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.zhang0.ucenter.model.po.TsMenu;
import cool.zhang0.ucenter.model.po.TsUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhanglin
 * @description 针对表【ts_user】的数据库操作Mapper
 * @createDate 2023-03-16 18:32:00
 * @Entity generator.domain.TsUser
 */
@Mapper
public interface TsUserMapper extends BaseMapper<TsUser> {



}




