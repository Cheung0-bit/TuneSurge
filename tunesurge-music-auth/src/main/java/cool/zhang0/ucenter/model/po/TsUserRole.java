package cool.zhang0.ucenter.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @TableName ts_user_role
 */
@TableName(value ="ts_user_role")
@Data
@Alias("TsUserRole")
public class TsUserRole implements Serializable {
    /**
     * 用户角色中间表ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String creator;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}