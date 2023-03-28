package cool.zhang0.media.service;

import cool.zhang0.media.model.po.MediaProcess;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <视频任务处理>
 *
 * @Author zhanglin
 * @createTime 2023/3/25 11:53
 */
public interface MediaFileProcessService {

    /**
     * 获取待处理列表
     *
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count      任务数
     * @return
     */
    List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * 记录处理后状态
     *
     * @param taskId   任务ID
     * @param status   任务状态
     * @param fileId   文件ID
     * @param url      url
     * @param errorMsg 错误信息
     */
    @Transactional(rollbackFor = Exception.class)
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);


}
