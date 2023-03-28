package cool.zhang0.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cool.zhang0.media.mapper.MediaFilesMapper;
import cool.zhang0.media.mapper.MediaProcessHistoryMapper;
import cool.zhang0.media.mapper.MediaProcessMapper;
import cool.zhang0.media.model.po.MediaFiles;
import cool.zhang0.media.model.po.MediaProcess;
import cool.zhang0.media.model.po.MediaProcessHistory;
import cool.zhang0.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <实现>
 *
 * @Author zhanglin
 * @createTime 2023/3/25 11:55
 */
@Slf4j
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Resource
    MediaProcessMapper mediaProcessMapper;

    @Resource
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Resource
    MediaFilesMapper mediaFilesMapper;


    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        return  mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {

        // 查询这个任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(mediaProcess==null){
            log.debug("更新任务状态时此任务:{}为空",taskId);
            return ;
        }
        LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
        if("3".equals(status)){
            //任务失败
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("3");
            mediaProcess_u.setErrorMsg(errorMsg);
            mediaProcess_u.setFinishTime(LocalDateTime.now());
            mediaProcessMapper.update(mediaProcess_u,queryWrapperById);
            return ;
        }

        //处理成功，更新状态
        if("2".equals(status)){
            mediaProcess.setStatus("2");
            mediaProcess.setUrl(url);
            mediaProcess.setFinishTime(LocalDateTime.now());
            mediaProcessMapper.updateById(mediaProcess);
            //更新文件表中的url
            MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }

        //如果处理成功将任务添加到历史记录表
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
        // todo 事务
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        // todo 事务
        //如果处理成功将待处理表的记录删除
        mediaProcessMapper.deleteById(taskId);
    }
}
