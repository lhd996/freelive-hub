package com.lhd.web.task;

import com.lhd.component.RedisComponent;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.po.VideoInfoFilePost;
import com.lhd.service.VideoInfoPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: liuhd
 * @Date: 2024/12/8 22:27
 * @Description:
 */

@Component
@Slf4j
public class ExecuteQueueTask {
    // 创建线程池 长度为10
    private ExecutorService executorService = Executors.newFixedThreadPool(Constants.LENGTH_10);
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private VideoInfoPostService videoInfoPostService;
    @PostConstruct
    public void consumeTransferFileQueue(){
        executorService.execute(()->{
            while (true){
                VideoInfoFilePost videoInfoFilePost = null;
                try {
                    // 从消息队列取出
                    videoInfoFilePost = redisComponent.getFileFromTransferQueue();
                    // 消息队列没有数据了
                    if (videoInfoFilePost == null){
                        // 休息一下
                        Thread.sleep(2000);
                        continue;
                    }
                    // 有数据 消费
                    videoInfoPostService.transferVideoFile(videoInfoFilePost);
                }catch (Exception e){
                    log.error("消息转码文件队列信息失败",e);
                }
            }
        });
    }

}
