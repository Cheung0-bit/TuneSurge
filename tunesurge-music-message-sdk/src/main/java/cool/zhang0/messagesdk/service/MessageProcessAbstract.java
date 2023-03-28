package cool.zhang0.messagesdk.service;

import cool.zhang0.messagesdk.model.po.MqMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <抽象类 供具体实现>
 *
 * @Author zhanglin
 * @createTime 2023/3/28 19:48
 */
@Data
@Slf4j
public abstract class MessageProcessAbstract {

    @Resource
    MqMessageService mqMessageService;

    /**
     * 任务处理
     *
     * @param mqMessage 执行任务内容
     * @return
     */
    public abstract boolean execute(MqMessage mqMessage);

    /**
     * 扫描消息表多线程执行任务
     *
     * @param shardIndex  分片序号
     * @param shardTotal  分片总数
     * @param messageType 消息类型
     * @param count       一次取出任务总数
     * @param timeout     预估任务执行时间,到此时间如果任务还没有结束则强制结束 单位秒
     */
    public void process(int shardIndex, int shardTotal, String messageType, int count, long timeout) {

        try {
            //扫描消息表获取任务清单
            List<MqMessage> messageList = mqMessageService.getMessageList(shardIndex, shardTotal, messageType, count);
            //任务个数
            int size = messageList.size();
            if (size == 0) {
                return;
            }
            //创建线程池
            ExecutorService threadPool = Executors.newFixedThreadPool(size);
            //计数器
            CountDownLatch countDownLatch = new CountDownLatch(size);
            messageList.forEach(message -> {
                threadPool.execute(() -> {
                    log.info("开始任务:{}", message);
                    //处理任务
                    try {
                        boolean result = execute(message);
                        if (result) {
                            log.debug("任务执行成功:{})", message);
                            //更新任务状态,删除消息表记录,添加到历史表
                            int completed = mqMessageService.completed(message.getId());
                            if (completed > 0) {
                                log.info("任务执行成功:{}", message);
                            } else {
                                log.info("任务执行失败:{}", message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("任务出现异常:{},任务:{}", e.getMessage(), message);
                    }
                    //计数
                    countDownLatch.countDown();
                    log.info("结束任务:{}", message);
                });
            });

            //等待,给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
            countDownLatch.await(timeout, TimeUnit.SECONDS);
            System.out.println("结束....");
        } catch (InterruptedException e) {
            e.printStackTrace();

        }


    }

}
