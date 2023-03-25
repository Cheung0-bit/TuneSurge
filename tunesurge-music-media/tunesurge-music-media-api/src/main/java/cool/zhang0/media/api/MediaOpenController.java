package cool.zhang0.media.api;

import cool.zhang0.media.model.po.MediaFiles;
import cool.zhang0.media.service.MediaFileService;
import cool.zhang0.model.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <媒资开放接口>
 *
 * @Author zhanglin
 * @createTime 2023/3/24 15:03
 */
@Api(value = "媒资文件开放接口",tags = "媒资文件开放接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {

    @Resource
    MediaFileService mediaFileService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){

        //调用service查询文件的url
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        return RestResponse.success(mediaFiles.getUrl());

    }


}