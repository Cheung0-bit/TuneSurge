package cool.zhang0.media.service;

import cool.zhang0.media.model.po.MediaProcess;

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
     * @param shardIndex
     * @param shardTotal
     * @param count
     * @return
     */
    List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * 记录处理后状态
     * @param taskId
     * @param status
     * @param fileId
     * @param url
     * @param errorMsg
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);


}
