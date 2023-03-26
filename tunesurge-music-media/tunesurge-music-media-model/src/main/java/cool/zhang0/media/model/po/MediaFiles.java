package cool.zhang0.media.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @author zhanglin
 * @TableName media_files
 */
@TableName(value ="media_files")
@Data
@Alias("MediaFiles")
public class MediaFiles implements Serializable {
    /**
     * 文件ID，MD5值
     */
    @TableId
    private String id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型（图片，文档，视频 ）
     */
    private String fileType;

    /**
     * 标签
     */
    private String tags;

    /**
     * 存储目录
     */
    private String bucket;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 媒资文件访问路径
     */
    private String url;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 状态 1：正常 2：不展示
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 审核意见
     */
    private String auditMind;

    /**
     * 文件大小
     */
    private Long fileSize;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}