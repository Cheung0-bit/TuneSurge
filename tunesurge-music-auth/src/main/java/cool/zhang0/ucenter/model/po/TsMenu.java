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
 * @TableName ts_menu
 */
@TableName(value ="ts_menu")
@Data
@Alias("TsMenu")
public class TsMenu implements Serializable {
    /**
     * 菜单ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 菜单编码
     */
    private String code;

    /**
     * 父菜单ID
     */
    private String pId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 是否是菜单
     */
    private String isMenu;

    /**
     * 菜单层级
     */
    private Integer level;

    /**
     * 菜单排序
     */
    private Integer sort;

    /**
     * 菜单状态
     */
    private String status;

    /**
     * 菜单图表
     */
    private String icon;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}