package cool.zhang0.media.model.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @TableName media_process
 */
@TableName(value ="media_process")
@Data
@Alias("MediaProcess")
public class MediaProcess implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件标识
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 存储桶
     */
    private String bucket;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 状态 1：未处理 2：处理成功 3：处理失败
     */
    private String status;

    /**
     * 上传时间
     */
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 媒资文件访问地址
     */
    private String url;

    /**
     * 失败原因
     */
    private String errorMsg;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}