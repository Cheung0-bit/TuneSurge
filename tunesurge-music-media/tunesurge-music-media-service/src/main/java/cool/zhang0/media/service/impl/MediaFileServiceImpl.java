package cool.zhang0.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import cool.zhang0.exception.TuneSurgeException;
import cool.zhang0.media.mapper.MediaFilesMapper;
import cool.zhang0.media.mapper.MediaProcessMapper;
import cool.zhang0.media.model.dto.QueryMediaParamsDto;
import cool.zhang0.media.model.dto.UploadFileParamsDto;
import cool.zhang0.media.model.po.MediaFiles;
import cool.zhang0.media.model.po.MediaProcess;
import cool.zhang0.media.service.MediaFileService;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <实现>
 *
 * @Author zhanglin
 * @createTime 2023/3/23 13:10
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Resource
    MinioClient minioClient;

    @Resource
    MediaFilesMapper mediaFilesMapper;

    @Resource
    MediaProcessMapper mediaProcessMapper;

    /**
     * 普通文件存储的桶
     */
    @Value("${minio.bucket.files}")
    private String bucket_files;

    /**
     * 视频文件存储的桶
     */
    @Value("${minio.bucket.videofiles}")
    private String bucket_videofiles;

    @Resource
    MediaFileService currentProxy;

    @Override
    public RestResponse<MediaFiles> uploadFile(UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {
        //得到文件的md5值
        String fileMd5 = DigestUtils.md5Hex(bytes);

        if (StringUtils.isEmpty(folder)) {
            //自动生成目录的路径 按年月日生成，
            folder = getFileFolder(new Date(), true, true, true);
        } else if (!folder.contains("/")) {
            folder = folder + "/";
        }
        //文件名称
        String filename = uploadFileParamsDto.getFileName();
        if (StringUtils.isEmpty(objectName)) {
            //如果objectName为空，使用文件的md5值为objectName
            objectName = fileMd5 + filename.substring(filename.lastIndexOf("."));
        }
        objectName = folder + objectName;
        try {
            // 将文件上传到分布式文件系统
            addMediaFilesToMinIo(bytes, bucket_files, objectName);
            MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(fileMd5, uploadFileParamsDto, bucket_files, objectName);
            return RestResponse.success(mediaFilesMapper.selectById(mediaFiles.getId()));

        } catch (Exception e) {
            log.debug("上传文件失败：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public MediaFiles addMediaFilesToDb(String fileId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();

            // 封装数据
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);

            // 获取拓展名
            String extension = null;
            String filename = uploadFileParamsDto.getFileName();
            if (StringUtils.isNotEmpty(filename) && filename.contains(".")) {
                extension = filename.substring(filename.lastIndexOf("."));
            }

            // 媒体类型
            String mimeType = getMimeTypeByextension(extension);

            // 图片，MP4视频可以设置URL
            if (mimeType.contains("image") || mimeType.contains("mp4")) {
                mediaFiles.setUrl("/" + bucket + "/" + objectName);
            }
            // todo 事务1
            mediaFilesMapper.insert(mediaFiles);

            // 对AVI视频添加到待处理任务表
            if (mimeType.equals("video/x-msvideo")) {
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles, mediaProcess);
                mediaProcess.setFileId(mediaFiles.getId());
                // 设置处理状态
                mediaProcess.setStatus("1");
                // todo 事务2
                mediaProcessMapper.insert(mediaProcess);
            }

        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Page<MediaFiles>> queryMediaFiles(PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        // TODO 拼接查询条件

        // 分页
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<MediaFiles> mediaFilesPage = mediaFilesMapper.selectPage(page, queryWrapper);
        return RestResponse.success(mediaFilesPage);
    }

    @Override
    public MediaFiles getFileById(String mediaId) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaId);
        if (mediaFiles == null) {
            TuneSurgeException.cast("文件不存在");
        }
        String mediaFilesUrl = mediaFiles.getUrl();
        if (StringUtils.isEmpty(mediaFilesUrl)) {
            TuneSurgeException.cast("该文件未被处理，请稍后预览");
        }
        return mediaFiles;
    }

    @Override
    public RestResponse<String> checkFile(String fileMd5) {

        // 同时在文件表、文件系统存在，此文件才存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            return RestResponse.validFail("此文件不存在");
        }
        // 查看是否在文件系统存在
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(mediaFiles.getBucket()).object(mediaFiles.getFilePath()).build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream == null) {
                //文件不存在
                return RestResponse.validFail("文件不存在");
            }
        } catch (Exception e) {
            //文件不存在
            return RestResponse.validFail("文件不存在");
        }
        // 文件存在
        return RestResponse.success();
    }

    @Override
    public RestResponse<String> checkChunk(String fileMd5, int chunk) {

        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        //查询文件系统分块文件是否存在
        //查看是否在文件系统存在
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucket_videofiles).object(chunkFilePath).build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream == null) {
                //文件不存在
                return RestResponse.validFail("文件不存在");
            }
        } catch (Exception e) {
            //文件不存在
            return RestResponse.validFail("文件不存在");

        }
        return RestResponse.success("文件存在");

    }

    @Override
    public RestResponse<String> uploadChunk(String fileMd5, int chunk, byte[] bytes) {

        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            //将分块上传到文件系统
            addMediaFilesToMinIo(bytes, bucket_videofiles, chunkFilePath);
            //上传成功
            return RestResponse.success("上传成功");
        } catch (Exception e) {
            log.debug("上传分块文件失败：{}", e.getMessage());
            return RestResponse.validFail("上传分块失败");
        }

    }

    /**
     * 根据桶和文件路径从minio下载文件
     *
     * @param file
     * @param bucket
     * @param objectName
     * @return
     */
    public File downloadFileFromMinIO(File file, String bucket, String objectName) {

        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucket).object(objectName).build();
        try (
                InputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream = new FileOutputStream(file);
        ) {
            IOUtils.copy(inputStream, outputStream);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            TuneSurgeException.cast("查询分块文件出错");
            return null;
        }
    }

    /**
     * 下载分块
     *
     * @param fileMd5
     * @param chunkTotal
     * @return
     */
    private File[] checkChunkStatus(String fileMd5, int chunkTotal) {

        //得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //分块文件数组
        File[] chunkFiles = new File[chunkTotal];
        //开始下载
        for (int i = 0; i < chunkTotal; i++) {
            //分块文件的路径
            String chunkFilePath = chunkFileFolderPath + i;
            //分块文件
            File chunkFile = null;
            try {
                chunkFile = File.createTempFile("chunk", null);
            } catch (IOException e) {
                e.printStackTrace();
                TuneSurgeException.cast("创建分块临时文件出错" + e.getMessage());
            }

            //下载分块文件
            downloadFileFromMinIO(chunkFile, bucket_videofiles, chunkFilePath);
            chunkFiles[i] = chunkFile;
        }
        return chunkFiles;

    }

    //合并分块
    @Override
    public RestResponse<MediaFiles> mergeChunks(String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //下载分块
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);

        //得到合并后文件的扩展名
        String filename = uploadFileParamsDto.getFileName();
        //扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        File tempMergeFile = null;
        try {
            try {
                //创建一个临时文件作为合并文件
                tempMergeFile = File.createTempFile("'merge'", extension);
            } catch (IOException e) {
                TuneSurgeException.cast("创建临时合并文件出错");
            }

            //创建合并文件的流对象
            try (RandomAccessFile raf_write = new RandomAccessFile(tempMergeFile, "rw")) {
                byte[] b = new byte[1024];
                for (File file : chunkFiles) {
                    //读取分块文件的流对象
                    try (RandomAccessFile raf_read = new RandomAccessFile(file, "r");) {
                        int len = -1;
                        while ((len = raf_read.read(b)) != -1) {
                            //向合并文件写数据
                            raf_write.write(b, 0, len);
                        }
                    }

                }
            } catch (IOException e) {
                TuneSurgeException.cast("合并文件过程出错");
            }


            //校验合并后的文件是否正确
            try {
                FileInputStream mergeFileStream = new FileInputStream(tempMergeFile);
                String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
                if (!fileMd5.equals(mergeMd5Hex)) {
                    log.error("合并文件校验不通过,文件路径:{},原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
                    TuneSurgeException.cast("合并文件校验不通过");
                }
            } catch (IOException e) {
                log.error("合并文件校验出错,文件路径:{},原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
                TuneSurgeException.cast("合并文件校验出错");
            }


            //拿到合并文件在minio的存储路径
            String mergeFilePath = getFilePathByMd5(fileMd5, extension);
            //将合并后的文件上传到文件系统
            addMediaFilesToMinIo(tempMergeFile.getAbsolutePath(), bucket_videofiles, mergeFilePath);

            //将文件信息入库保存
            uploadFileParamsDto.setFileSize(tempMergeFile.length());//合并文件的大小
            MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(fileMd5, uploadFileParamsDto, bucket_videofiles, mergeFilePath);
            return RestResponse.success(mediaFiles);
        } finally {
            //删除临时分块文件
            if (chunkFiles != null) {
                for (File chunkFile : chunkFiles) {
                    if (chunkFile.exists()) {
                        chunkFile.delete();
                    }
                }
            }
            //删除合并的临时文件
            if (tempMergeFile != null) {
                tempMergeFile.delete();
            }


        }
    }



    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    /**
     * 得到分块文件的目录
     *
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
    }


    /**
     * 将文件上传到分布式文件系统
     * 通用上传方法
     *
     * @param filePath
     * @param bucket
     * @param objectName
     */
    public void addMediaFilesToMinIo(String filePath, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(filePath)
                    .build();
            //上传
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("文件上传成功:{}", filePath);
        } catch (Exception e) {
            TuneSurgeException.cast("文件上传到文件系统失败");
        }
    }

    /**
     * 将文件上传到分布式文件系统
     * 分块上传 二进制流
     *
     * @param bytes
     * @param bucket
     * @param objectName
     */
    private void addMediaFilesToMinIo(byte[] bytes, String bucket, String objectName) {

        // 资源的媒体类型
        // 默认未知二进制流
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if (objectName.contains(".")) {
            //取objectName中的扩展名
            String extension = objectName.substring(objectName.lastIndexOf("."));
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    //InputStream stream, long objectSize 对象大小, long partSize 分片大小(-1表示5M,最大不要超过5T，最多10000)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            //上传到minio
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("上传文件到文件系统出错:{}", e.getMessage());
            TuneSurgeException.cast("上传文件到文件系统出错");
        }
    }

    //根据扩展名拿匹配的媒体类型
    private String getMimeTypeByextension(String extension) {
        //资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流
        if (StringUtils.isNotEmpty(extension)) {
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }

    /**
     * 根据日期拼接目录
     *
     * @param date
     * @param year
     * @param month
     * @param day
     * @return
     */
    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuilder folderString = new StringBuilder();
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }
}
