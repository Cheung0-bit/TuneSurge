package cool.zhang0.content.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @author zhanglin
 * @TableName mv_publish_pre
 */
@TableName(value ="mv_publish_pre")
@Data
@Alias("MvPublishPre")
public class MvPublishPre implements Serializable {
    /**
     * 发布表ID 也是MV作品的ID号
     */
    @TableId
    private Long id;

    /**
     * MV名称
     */
    private String mvName;

    /**
     * NV标签
     */
    private String mvTags;

    /**
     * 一级分类（名称）
     */
    private String typeOneName;

    /**
     * 二级分类（名称）
     */
    private String typeTwoName;

    /**
     * 三级分类（名称）
     */
    private String typeThreeName;

    /**
     * MV描述
     */
    private String description;

    /**
     * 封面图URL
     */
    private String pic;

    /**
     * 视频URL
     */
    private String videoId;

    /**
     * 提交时间
     */
    private LocalDateTime pushTime;

    /**
     * 提交人
     */
    private Long pushUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}