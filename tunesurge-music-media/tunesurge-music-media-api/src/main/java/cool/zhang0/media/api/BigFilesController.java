package cool.zhang0.media.api;

import cool.zhang0.media.model.dto.UploadFileParamsDto;
import cool.zhang0.media.model.po.MediaFiles;
import cool.zhang0.media.service.MediaFileService;
import cool.zhang0.media.util.SecurityUtil;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * <大文件处理接口>
 *
 * @Author zhanglin
 * @createTime 2023/3/24 15:04
 */
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {


    @Resource
    MediaFileService mediaFileService;


    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<String> checkfile(
            @RequestParam("fileMd5") String fileMd5
    ) throws Exception {

        return mediaFileService.checkFile(fileMd5);

    }


    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<String> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.checkChunk(fileMd5, chunk);
    }

    @ApiOperation(value = "上传分块文件")
    @RequestMapping(value = "/upload/uploadchunk", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, method = RequestMethod.POST)
    public RestResponse<String> uploadChunk(@RequestPart("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.uploadChunk(fileMd5, chunk, file.getBytes());

    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse<MediaFiles> mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                                @RequestParam("fileName") String fileName,
                                                @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileName(fileName);
        uploadFileParamsDto.setFileType("video");
        uploadFileParamsDto.setTags("MV作品");
        // 当前登录用户
        SecurityUtil.TsUser tsUser = SecurityUtil.getUser();
        // 设置文件创建人
        assert tsUser != null;
        uploadFileParamsDto.setUserId(tsUser.getId());
        return mediaFileService.mergeChunks(fileMd5, chunkTotal, uploadFileParamsDto);

    }


}
