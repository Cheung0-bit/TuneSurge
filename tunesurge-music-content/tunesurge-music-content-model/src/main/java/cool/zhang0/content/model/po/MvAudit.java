package cool.zhang0.content.model.po;

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
 * @TableName mv_audit
 */
@TableName(value ="mv_audit")
@Data
@Alias("MvAudit")
public class MvAudit implements Serializable {
    /**
     * 审核表ID
     */
    @TableId
    private Long id;

    /**
     * 审核对象（MV）ID
     */
    private Long mvId;

    /**
     * 审核意见
     */
    private String auditMind;

    /**
     * 审核状态 0：不通过 1：通过
     */
    private String auditStatus;

    /**
     * 审核人
     */
    private Long auditPeople;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}