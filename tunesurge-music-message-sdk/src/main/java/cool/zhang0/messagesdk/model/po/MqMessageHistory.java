package cool.zhang0.messagesdk.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * 
 * @author zhanglin
 * @TableName mq_message_history
 */
@TableName(value ="mq_message_history")
@Data
@Alias("MqMessageHistory")
public class MqMessageHistory implements Serializable {
    /**
     * 消息id
     */
    @TableId
    private Long id;

    /**
     * 消息类型代码
     */
    private String messageType;

    /**
     * 关联业务信息
     */
    private String businessKey1;

    /**
     * 关联业务信息
     */
    private String businessKey2;

    /**
     * 关联业务信息
     */
    private String businessKey3;

    /**
     * 通知次数
     */
    private Object executeNum;

    /**
     * 处理状态，0:初始，1:成功，2:失败
     */
    private Integer state;

    /**
     * 回复失败时间
     */
    private LocalDateTime returnfailureDate;

    /**
     * 回复成功时间
     */
    private LocalDateTime returnsuccessDate;

    /**
     * 回复失败内容
     */
    private String returnfailureMsg;

    /**
     * 最近通知时间
     */
    private LocalDateTime executeDate;

    /**
     * 阶段一处理状态
     */
    private String stageState1;

    /**
     * 阶段二处理状态
     */
    private String stageState2;

    /**
     * 阶段三处理状态
     */
    private String stageState3;

    /**
     * 阶段四处理状态
     */
    private String stageState4;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}