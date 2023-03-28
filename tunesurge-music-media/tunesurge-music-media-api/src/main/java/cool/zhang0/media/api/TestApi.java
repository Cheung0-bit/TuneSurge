package cool.zhang0.media.api;

import cool.zhang0.media.service.jobhandler.VideoTask;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2023/3/27 13:07
 */
@Api(tags = "视频处理任务测试")
@RestController
public class TestApi {

    @Resource
    VideoTask videoTask;

    @GetMapping("video-test")
    public String videoTest() throws Exception {
        videoTask.videoJobHandler();
        return "ok";
    }

}
