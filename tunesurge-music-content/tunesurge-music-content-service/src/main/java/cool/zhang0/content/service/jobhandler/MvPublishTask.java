package cool.zhang0.content.service.jobhandler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import cool.zhang0.content.service.MvPublishService;
import cool.zhang0.messagesdk.model.po.MqMessage;
import cool.zhang0.messagesdk.service.MessageProcessAbstract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <MV发布任务>
 *
 * @Author zhanglin
 * @createTime 2023/3/31 16:22
 */
@Component
@Slf4j
public class MvPublishTask extends MessageProcessAbstract {

    @Resource
    MvPublishService mvPublishService;

    @XxlJob("MvPublishJobHandler")
    public void mvPublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex=" + shardIndex + ",shardTotal=" + shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex, shardTotal, "mv_publish", 5, 60);
    }

    /**
     * MV发布过程
     *
     * @param mqMessage 执行任务内容
     * @return
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        log.info("开始执行MV发布任务，MV：{}", mqMessage.getBusinessKey1());

        // MV编号
        Long mvId = Long.parseLong(mqMessage.getBusinessKey1());

        // MV信息执行索引
        saveMvPublishIndex(mqMessage, mvId);

        // MV缓存到Redis
        saveMvPublishCache(mqMessage, mvId);

        // MV作品预览页面静态化

        return true;
    }

    /**
     * @param mqMessage
     * @param mvId
     */
    private void saveMvPublishCache(MqMessage mqMessage, Long mvId) {

        // 任务id
        Long id = mqMessage.getId();

        // 消息幂等性处理
        int stageTwo = this.getMqMessageService().getStageTwo(id);
        if (stageTwo > 0) {
            log.info("当前阶段是MV缓存到Redis，已经完成不再处理，任务信息");
            return;
        }

        Boolean saveMvCache = mvPublishService.saveMvCache(mvId);
        if (Boolean.TRUE.equals(saveMvCache)) {
            this.getMqMessageService().completedStageTwo(id);
        }

    }

    /**
     * 发布索引存储
     *
     * @param mqMessage
     * @param mvId
     */
    private void saveMvPublishIndex(MqMessage mqMessage, Long mvId) {

        //任务id
        Long id = mqMessage.getId();

        //消息幂等性处理
        int stageOne = this.getMqMessageService().getStageOne(id);
        if (stageOne > 0) {
            log.info("当前阶段是创建课程索引,已经完成不再处理");
            return;
        }

        //调用service创建索引
        Boolean saveMvIndex = mvPublishService.saveMvIndex(mvId);
//        int a = 1/0;

        //给该阶段任务打上完成标记
        if (Boolean.TRUE.equals(saveMvIndex)) {
            this.getMqMessageService().completedStageOne(id);
        }

    }
}
