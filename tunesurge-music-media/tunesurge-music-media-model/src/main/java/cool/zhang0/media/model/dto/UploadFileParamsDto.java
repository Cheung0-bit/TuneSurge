package cool.zhang0.media.model.dto;

import lombok.Data;

/**
 * @author zhanglin
 */
@Data
public class UploadFileParamsDto {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 资源的媒体类型 mimeType
     */
    private String contentType;

    /**
     * 文件类型（文档，音频，视频）
     */
    private String fileType;
    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 标签
     */
    private String tags;

    /**
     * 上传人
     */
    private Long userId;

    /**
     * 备注
     */
    private String remark;


}
