package cool.zhang0.messagesdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cool.zhang0.messagesdk.model.po.MqMessageHistory;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zhanglin
* @description 针对表【mq_message_history】的数据库操作Mapper
* @createDate 2023-03-28 19:25:05
* @Entity generator.domain.MqMessageHistory
*/
@Mapper
public interface MqMessageHistoryMapper extends BaseMapper<MqMessageHistory> {

}




