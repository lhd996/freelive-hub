package com.lhd.admin.controller;

import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.StatisticsTypeEnum;
import com.lhd.entity.po.StatisticsInfo;
import com.lhd.entity.query.StatisticsInfoQuery;
import com.lhd.entity.query.UserInfoQuery;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.StatisticsInfoService;
import com.lhd.service.UserInfoService;
import com.lhd.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/index")
@Validated
@Slf4j
public class IndexController extends ABaseController {
    @Resource
    private StatisticsInfoService statisticsInfoService;
    @Resource
    private UserInfoService userInfoService;

    /**
     * @param
     * @return com.lhd.entity.vo.ResponseVO
     * @description: 获取系统中所有的统计数据
     * @author liuhd
     * 2025/1/15 23:16
     */
    @RequestMapping("/getActualTimeStatisticsInfo")
    public ResponseVO getActualTimeStatisticsInfo() {

        String preDate = DateUtil.getBeforeDayDate(Constants.ONE);
        StatisticsInfoQuery statisticsInfoQuery = new StatisticsInfoQuery();
        statisticsInfoQuery.setStatisticsDate(preDate);

        // 查出前一天的所有数据
        List<StatisticsInfo> preDayData = statisticsInfoService.findListTotalInfoByParam(statisticsInfoQuery);

        // 将粉丝总数替换成用户数 （每个UP的粉丝数之和 >= 用户数）
        Integer count = userInfoService.findCountByParam(new UserInfoQuery());
        preDayData.forEach(item -> {
            if (StatisticsTypeEnum.FANS.getType().equals(item.getDataType())) {
                item.setStatisticsCount(count);
            }
        });

        // 把前一天的数据根据类型分开
        Map<Integer, Integer> preDayDataMap = preDayData.stream().collect(Collectors.toMap(StatisticsInfo::getDataType, StatisticsInfo::getStatisticsCount, (item1, item2) -> item2));

        // 查实时数据
        Map<String, Integer> totalCountInfo = statisticsInfoService.getStatisticsInfoActualTime(null);

        Map<String, Object> resultVO = new HashMap<>();
        resultVO.put("preDayData", preDayDataMap);
        resultVO.put("totalCountInfo", totalCountInfo);

        return getSuccessResponseVO(resultVO);
    }

    /**
     * @param dataType
     * @return com.lhd.entity.vo.ResponseVO
     * @description: 获取一周内某种类型的统计数据
     * @author liuhd
     * 2025/1/15 23:43
     */
    @RequestMapping("/getWeekStatisticsInfo")
    public ResponseVO getWeekStatisticsInfo(Integer dataType) {

        // 获取一周内的数据
        List<String> dataList = DateUtil.getBeforeDates(7);
        StatisticsInfoQuery statisticsInfoQuery = new StatisticsInfoQuery();
        statisticsInfoQuery.setDataType(dataType);
        statisticsInfoQuery.setStatisticsDateStart(dataList.get(0));
        statisticsInfoQuery.setStatisticsDateEnd(dataList.get(dataList.size() - 1));
        statisticsInfoQuery.setOrderBy("statistics_date asc");
        List<StatisticsInfo> statisticsInfoList = new ArrayList<>();
        if (!StatisticsTypeEnum.FANS.getType().equals(dataType)) {
            statisticsInfoList = statisticsInfoService.findListTotalInfoByParam(statisticsInfoQuery);
        } else {
            statisticsInfoList = statisticsInfoService.findUserCountTotalInfoByParam(statisticsInfoQuery);
        }



        // 以天为单位分开
        Map<String, StatisticsInfo> dataMap = statisticsInfoList.stream().collect(Collectors.toMap(item -> item.getStatisticsDate(), Function.identity(),
                (d1, d2) -> d2));

        List<StatisticsInfo> resultDataList = new ArrayList<>();
        for (String date : dataList) {
            StatisticsInfo statisticsInfo = dataMap.get(date);
            if (statisticsInfo == null) {
                statisticsInfo = new StatisticsInfo();
                statisticsInfo.setStatisticsCount(0);
                statisticsInfo.setStatisticsDate(date);
                statisticsInfo.setDataType(dataType);
            }
            resultDataList.add(statisticsInfo);
        }
        return getSuccessResponseVO(resultDataList);
    }


}
