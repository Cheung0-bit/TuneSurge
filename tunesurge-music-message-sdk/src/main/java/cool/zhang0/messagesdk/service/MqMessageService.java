package cool.zhang0.messagesdk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cool.zhang0.messagesdk.model.po.MqMessage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhanglin
 * @description 针对表【mq_message】的数据库操作Service
 * @createDate 2023-03-28 19:25:05
 */
public interface MqMessageService extends IService<MqMessage> {

    /**
     * 扫描消息记录表
     *
     * @param shardIndex
     * @param shardTotal
     * @param messageType
     * @param count
     * @return
     */
    List<MqMessage> getMessageList(int shardIndex, int shardTotal, String messageType, int count);

    /**
     * 添加消息
     *
     * @param messageType
     * @param businessKey1
     * @param businessKey2
     * @param businessKey3
     * @return
     */
    MqMessage addMessage(String messageType, String businessKey1, String businessKey2, String businessKey3);

    /**
     * 完成任务
     *
     * @param id 消息ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    int completed(long id);

    /**
     * 完成阶段一的任务
     *
     * @param id
     * @return
     */
    int completedStageOne(long id);

    /**
     * 完成阶段二的任务
     *
     * @param id
     * @return
     */
    int completedStageTwo(long id);

    /**
     * 完成阶段三的任务
     *
     * @param id
     * @return
     */
    int completedStageThree(long id);

    /**
     * 完成阶段四的任务
     *
     * @param id
     * @return
     */
    int completedStageFour(long id);

    /**
     * 查询阶段一的状态
     *
     * @param id
     * @return
     */
    int getStageOne(long id);

    /**
     * 查询阶段二的状态
     *
     * @param id
     * @return
     */
    int getStageTwo(long id);

    /**
     * 查询阶段三的状态
     *
     * @param id
     * @return
     */
    int getStageThree(long id);

    /**
     * 查询阶段四的状态
     *
     * @param id
     * @return
     */
    int getStageFour(long id);


}
