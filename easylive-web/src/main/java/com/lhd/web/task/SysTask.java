package com.lhd.web.task;

import com.lhd.service.StatisticsInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: liuhd
 * @Date: 2025/1/15 10:10
 * @Description:
 */
@Component
public class SysTask {

    @Resource
    private StatisticsInfoService statisticsInfoService;


    @Scheduled(cron = "0 0 0  * * ?")
    public void statisticData(){
        statisticsInfoService.statisticsData();
    }
}
