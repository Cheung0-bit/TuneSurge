package cool.zhang0.content.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * 
 * @author zhanglin
 * @TableName mv_category
 */
@TableName(value ="mv_category")
@Data
@Alias("MvCategory")
public class MvCategory implements Serializable {
    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类标签默认和名称一样
     */
    private String label;

    /**
     * 父节点ID（第一级的父节点是0）
     */
    private String parentId;

    /**
     * 是否显示
     */
    private Integer isShow;

    /**
     * 排序字段
     */
    private Integer orderBy;

    /**
     * 是否叶子
     */
    private Integer isLeaf;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}