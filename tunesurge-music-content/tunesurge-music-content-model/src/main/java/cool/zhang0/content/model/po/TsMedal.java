package cool.zhang0.content.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @author zhanglin
 * @TableName ts_medal
 */
@TableName(value ="ts_medal")
@Data
@Alias("TsMedal")
public class TsMedal implements Serializable {
    /**
     * 勋章ID
     */
    @TableId
    private Integer id;

    /**
     * 勋章名称
     */
    private String name;

    /**
     * 勋章描述
     */
    private String description;

    /**
     * 勋章库存
     */
    private Long store;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}