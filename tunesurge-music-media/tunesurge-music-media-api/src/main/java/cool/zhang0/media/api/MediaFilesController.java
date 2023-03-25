package cool.zhang0.media.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.zhang0.exception.TuneSurgeException;
import cool.zhang0.media.model.dto.QueryMediaParamsDto;
import cool.zhang0.media.model.dto.UploadFileParamsDto;
import cool.zhang0.media.model.po.MediaFiles;
import cool.zhang0.media.service.MediaFileService;
import cool.zhang0.media.util.SecurityUtil;
import cool.zhang0.model.PageParams;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author zhanglin
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    private static final String IMAGE = "image";

    private static final String OTHER = "other";

    @Resource
    MediaFileService mediaFileService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public RestResponse<Page<MediaFiles>> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {

        return mediaFileService.queryMediaFiles(pageParams, queryMediaParamsDto);

    }

    @ApiOperation("上传文件通用方法")
    @RequestMapping(value = "/upload/mvFile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    public RestResponse<MediaFiles> upload(@RequestPart("filedata") MultipartFile filedata,
                                           @RequestParam(value = "folder", required = false) String folder,
                                           @RequestParam(value = "objectName", required = false) String objectName) {
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        String contentType = filedata.getContentType();
        uploadFileParamsDto.setContentType(contentType);
        uploadFileParamsDto.setFileSize(filedata.getSize());
        assert contentType != null;
        if (contentType.contains(IMAGE)) {
            uploadFileParamsDto.setFileType(IMAGE);
        } else {
            uploadFileParamsDto.setFileType(OTHER);
        }
        // 设置文件名称
        uploadFileParamsDto.setFileName(filedata.getOriginalFilename());
        // 当前登录用户
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        // 设置文件创建人
        assert tsUser != null;
        uploadFileParamsDto.setUserId(tsUser.getId());
        RestResponse<MediaFiles> mediaFilesRestResponse = null;
        try {
            mediaFilesRestResponse = mediaFileService.uploadFile(uploadFileParamsDto, filedata.getBytes(), folder, objectName);
        } catch (IOException e) {
            TuneSurgeException.cast("上文文件过程中出错");
        }
        return mediaFilesRestResponse;
    }

}
