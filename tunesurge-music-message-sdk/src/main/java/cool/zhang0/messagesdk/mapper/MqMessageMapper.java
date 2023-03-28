package cool.zhang0.messagesdk.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.zhang0.messagesdk.model.po.MqMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author zhanglin
* @description 针对表【mq_message】的数据库操作Mapper
* @createDate 2023-03-28 19:25:05
* @Entity generator.domain.MqMessage
*/
@Mapper
public interface MqMessageMapper extends BaseMapper<MqMessage> {

    /**
     * 消息广播 选取任务 保证幂等性
     * @param shardTotal
     * @param shardIndex
     * @param messageType
     * @param count
     * @return
     */
    @Select("SELECT t.* FROM mq_message t WHERE t.id % #{shardTotal} = #{shardIndex} and t.state='0' and t.message_type=#{messageType} limit #{count}")
    List<MqMessage> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("messageType") String messageType, @Param("count") int count);


}




