package cool.zhang0.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.zhang0.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhanglin
 * @description 针对表【media_process】的数据库操作Mapper
 * @createDate 2023-03-24 11:34:50
 * @Entity generator.domain.MediaProcess
 */
@Mapper
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    /**
     * 根据分片参数获取待处理任务
     *
     * @param shardTotal 分片总数
     * @param shardIndex 分片序号
     * @param count      任务数
     * @return
     */
    @Select("SELECT t.* FROM media_process t WHERE t.id % #{shardTotal} = #{shardIndex} and t.status='1' limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal,@Param("shardIndex") int shardIndex,@Param("count") int count);

}




