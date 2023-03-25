package cool.zhang0.media.service.impl;

import cool.zhang0.media.mapper.MediaProcessMapper;
import cool.zhang0.media.model.po.MediaProcess;
import cool.zhang0.media.service.MediaFileProcessService;

import javax.annotation.Resource;
import java.util.List;

/**
 * <实现>
 *
 * @Author zhanglin
 * @createTime 2023/3/25 11:55
 */
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Resource
    MediaProcessMapper mediaProcessMapper;


    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        return null;
    }

    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {

    }
}
