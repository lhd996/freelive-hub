package com.lhd.web.task;

import com.lhd.component.EsSearchComponent;
import com.lhd.component.RedisComponent;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.VideoPlayInfoDto;
import com.lhd.entity.enums.SearchOrderTypeEnum;
import com.lhd.entity.po.VideoInfoFilePost;
import com.lhd.service.VideoInfoPostService;
import com.lhd.service.VideoInfoService;
import com.lhd.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: liuhd
 * @Date: 2024/12/8 22:27
 * @Description: 异步消费任务
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
    @Resource
    private VideoInfoService videoInfoService;
    @Resource
    private EsSearchComponent esSearchComponent;

    /**
     * @description: 消费视频转码队列
     * @param
     * @return
     * @author liuhd
     * 2025/1/14 12:37
     */
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

    /**
     * @description: 消费视频的播放信息队列
     * @param
     * @return
     * @author liuhd
     * 2025/1/14 13:01
     */

    @PostConstruct
    public void consumeVideoPlayQueue(){
        executorService.execute(()->{
            while (true){
                VideoPlayInfoDto videoPlayInfoDto = null;
                try {
                    // 从消息队列取出
                    videoPlayInfoDto = redisComponent.getVideoPlayFromVideoPlayQueue();
                    // 消息队列没有数据了
                    if (videoPlayInfoDto == null){
                        // 休息一下
                        Thread.sleep(2000);
                        continue;
                    }
                    // 有数据 消费
                    // 增加视频播放量
                    videoInfoService.addReadCount(videoPlayInfoDto.getVideoId());
                    // 记录用户历史播放记录
                    if (!StringTools.isEmpty(videoPlayInfoDto.getUserId())){
                        // TODO 记录历史播放
                    }
                    // 按天记录视频播放量
                    redisComponent.recordVideoPlayCount(videoPlayInfoDto.getVideoId());
                    // 更新ES播放数量
                    esSearchComponent.updateDocCount(videoPlayInfoDto.getVideoId(), SearchOrderTypeEnum.VIDEO_PLAY.getField(), 1);
                }catch (Exception e){
                    log.error("获取视频播放文件队列消息失败",e);
                }
            }
        });
    }

}
