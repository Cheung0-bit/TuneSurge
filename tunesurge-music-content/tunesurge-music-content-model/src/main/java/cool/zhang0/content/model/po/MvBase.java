package cool.zhang0.content.model.po;

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
 * @TableName mv_base
 */
@TableName(value ="mv_base")
@Data
@Alias("MvBase")
public class MvBase implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * MV名称
     */
    private String name;

    /**
     * MV标签
     */
    private String tags;

    /**
     * 一级分类
     */
    private String typeOne;

    /**
     * 二级分类
     */
    private String typeTwo;

    /**
     * 三级分类
     */
    private String typeThree;

    /**
     * 课程介绍
     */
    private String description;

    /**
     * 封面图片
     */
    private String pic;

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
    private Long createUser;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 发布状态
     */
    private String status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}