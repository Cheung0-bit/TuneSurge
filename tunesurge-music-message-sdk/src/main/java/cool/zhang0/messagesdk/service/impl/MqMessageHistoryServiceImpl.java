package cool.zhang0.messagesdk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cool.zhang0.messagesdk.mapper.MqMessageHistoryMapper;
import cool.zhang0.messagesdk.model.po.MqMessageHistory;
import cool.zhang0.messagesdk.service.MqMessageHistoryService;
import org.springframework.stereotype.Service;

/**
* @author zhanglin
* @description 针对表【mq_message_history】的数据库操作Service实现
* @createDate 2023-03-28 19:25:05
*/
@Service
public class MqMessageHistoryServiceImpl extends ServiceImpl<MqMessageHistoryMapper, MqMessageHistory>
    implements MqMessageHistoryService {

}




