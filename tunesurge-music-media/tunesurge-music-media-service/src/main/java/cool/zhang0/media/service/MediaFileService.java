package cool.zhang0.media.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.zhang0.media.model.dto.QueryMediaParamsDto;
import cool.zhang0.media.model.dto.UploadFileParamsDto;
import cool.zhang0.media.model.po.MediaFiles;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * <媒资接口实现>
 *
 * @Author zhanglin
 * @createTime 2023/3/23 13:08
 */
public interface MediaFileService {

    /**
     * 上传文件的通用接口
     *
     * @param uploadFileParamsDto
     * @param bytes
     * @param folder
     * @param objectName
     * @return
     */
    RestResponse<MediaFiles> uploadFile(UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName);

    /**
     * 将媒资文件信息添加到数据库
     *
     * @param fileId
     * @param uploadFileParamsDto
     * @param bucket
     * @param objectName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    MediaFiles addMediaFilesToDb(String fileId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    /**
     * 媒资文件查询方法
     *
     * @param pageParams
     * @param queryMediaParamsDto
     * @return
     */
    RestResponse<Page<MediaFiles>> queryMediaFiles(PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 根据id查询文件信息
     *
     * @param mediaId
     * @return
     */
    MediaFiles getFileById(String mediaId);

    /**
     * 上传前检查文件
     *
     * @param fileMd5
     * @return
     */
    RestResponse<String> checkFile(String fileMd5);

    /**
     * 上传前检查分块
     *
     * @param fileMd5
     * @param chunk
     * @return
     */
    RestResponse<String> checkChunk(String fileMd5, int chunk);

    /**
     * 检查后上传分块
     *
     * @param fileMd5
     * @param chunk
     * @param bytes
     * @return
     */
    RestResponse<String> uploadChunk(String fileMd5, int chunk, byte[] bytes);

    /**
     * 从文件系统中下载文件
     *
     * @param file
     * @param bucket
     * @param objectName
     * @return
     */
    File downloadFileFromMinIO(File file, String bucket, String objectName);

    /**
     * 合并分块文件
     *
     * @param fileMd5
     * @param chunkTotal
     * @param uploadFileParamsDto
     * @return
     */
    RestResponse<MediaFiles> mergeChunks(String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 将文件上传到文件系统中
     *
     * @param filePath
     * @param bucket
     * @param objectName
     */
    void addMediaFilesToMinIo(String filePath, String bucket, String objectName);
}
