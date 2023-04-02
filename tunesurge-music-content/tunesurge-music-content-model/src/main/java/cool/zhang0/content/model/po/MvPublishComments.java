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
 * @TableName mv_publish_comments
 */
@TableName(value ="mv_publish_comments")
@Data
@Alias("MvPublishComments")
public class MvPublishComments implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 作品ID
     */
    private Long mvId;

    /**
     * 关联的1级评论 如果是1级评论 则为0
     */
    private Long parentId;

    /**
     * 回复的内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer liked;

    /**
     * 状态 0：正常 1：被折叠，无法查看
     */
    private String status;

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