package com.lhd.web.controller;

import com.lhd.annotation.GlobalInterceptor;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.PageSize;
import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.entity.enums.UserActionTypeEnum;
import com.lhd.entity.enums.VideoOrderTypeEnum;
import com.lhd.entity.po.*;
import com.lhd.entity.query.*;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.entity.vo.UserInfoVO;
import com.lhd.entity.vo.UserVideoSeriesDetailVO;
import com.lhd.exception.BusinessException;
import com.lhd.service.*;
import com.lhd.utils.CopyTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liuhd
 * @Date: 2025/1/11 13:24
 * @Description:
 */
@RestController
@RequestMapping("/uhome/series")
@Validated
@Slf4j
public class UHomeVideoSeriesController extends ABaseController {
    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private UserVideoSeriesService userVideoSeriesService;
    @Resource
    private  UserVideoSeriesVideoService userVideoSeriesVideoService;

    /**
     * @description: 加载视频合集
     * @param userId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/12 20:47
     */

    @RequestMapping("/loadVideoSeries")
    public ResponseVO loadVideoSeries(@NotEmpty String userId){
        List<UserVideoSeries> userAllSeries = userVideoSeriesService.getUserAllSeries(userId);
        return getSuccessResponseVO(userAllSeries);
    }
    /**
     * @description: 新增或修改分类
     * @param request
     * @param seriesId 有值则为修改 否则是新增
     * @param seriesName 新增的分类名称
     * @param seriesDescription 分类描述
     * @param videoIds 新增的分类中的视频id
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/12 20:49
     */

    @RequestMapping("/saveVideoSeries")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO saveVideoSeries(HttpServletRequest request,
                                      Integer seriesId,
                                      @NotEmpty @Size(max = 100) String seriesName,
                                      @Size(max = 200) String seriesDescription,
                                      String videoIds){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserVideoSeries videoSeries = new UserVideoSeries();
        videoSeries.setUserId(tokenUserInfoDto.getUserId());
        videoSeries.setSeriesId(seriesId);
        videoSeries.setSeriesName(seriesName);
        videoSeries.setSeriesDescription(seriesDescription);

        this.userVideoSeriesService.saveUserVideoSeries(videoSeries,videoIds);

        return getSuccessResponseVO(null);
    }

    /**
     * @description: 加载所有视频
     * @param request
     * @param seriesId 可选 有则是加载分类下的所有视频 否则就是加载自己的所有视频
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/12 21:54
     */

    @RequestMapping("/loadAllVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadAllVideo(HttpServletRequest request,Integer seriesId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        if (seriesId != null){
            UserVideoSeriesVideoQuery videoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
            // 以下的目的是排除当前分类的视频 因为你要添加的肯定是不是该分类的视频
            videoSeriesVideoQuery.setUserId(tokenUserInfoDto.getUserId());
            videoSeriesVideoQuery.setSeriesId(seriesId);
            List<UserVideoSeriesVideo> seriesVideoList = userVideoSeriesVideoService.findListByParam(videoSeriesVideoQuery);
            List<String> seriesVideoIdList = seriesVideoList.stream().map(i -> i.getVideoId()).collect(Collectors.toList());
            videoInfoQuery.setExcludeVideoIdArray(seriesVideoIdList.toArray(new String[seriesVideoIdList.size()]));
        }
        videoInfoQuery.setUserId(tokenUserInfoDto.getUserId());
        List<VideoInfo> videoInfoList = videoInfoService.findListByParam(videoInfoQuery);

        return getSuccessResponseVO(videoInfoList);
    }
    /**
     * @description: 获取分类中的视频（进入某一个分类中）
     * @param seriesId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 12:05
     */

    @RequestMapping("/getVideoSeriesDetail")
    public ResponseVO getVideoSeriesDetail(@NotNull Integer seriesId){
        UserVideoSeries videoSeries = userVideoSeriesService.getUserVideoSeriesBySeriesId(seriesId);
        if (videoSeries == null){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        UserVideoSeriesVideoQuery seriesVideoQuery = new UserVideoSeriesVideoQuery();
        seriesVideoQuery.setOrderBy("sort asc");
        // 是否联查视频信息
        seriesVideoQuery.setQueryVideoInfo(true);
        seriesVideoQuery.setSeriesId(seriesId);

        List<UserVideoSeriesVideo> seriesVideoList = userVideoSeriesVideoService.findListByParam(seriesVideoQuery);

        UserVideoSeriesDetailVO resultVO = new UserVideoSeriesDetailVO(videoSeries,seriesVideoList);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * @description: 新增分类中的视频
     * @param request
     * @param seriesId
     * @param videoIds
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 13:09
     */

    @RequestMapping("/saveSeriesVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO saveSeriesDetail(HttpServletRequest request,@NotNull Integer seriesId,@NotEmpty String videoIds){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        this.userVideoSeriesService.saveSeriesVideo(tokenUserInfoDto.getUserId(),seriesId,videoIds);

        return getSuccessResponseVO(null);
    }

    /**
     * @description: 删除分类中的视频
     * @param request
     * @param seriesId
     * @param videoId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 13:10
     */

    @RequestMapping("/delSeriesVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delSeriesVideo(HttpServletRequest request,@NotNull Integer seriesId,@NotEmpty String videoId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        this.userVideoSeriesService.delSeriesVideo(tokenUserInfoDto.getUserId(),seriesId,videoId);
        return getSuccessResponseVO(null);
    }
    /**
     * @description: 删除视频集合
     * @param request
     * @param seriesId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 13:20
     */

    @RequestMapping("/delVideoSeries")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delVideoSeries(HttpServletRequest request,@NotNull Integer seriesId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        this.userVideoSeriesService.delVideoSeries(tokenUserInfoDto.getUserId(),seriesId);
        return getSuccessResponseVO(null);
    }
    /**
     * @description: 改变分类的顺序
     * @param request
     * @param seriesIds 目前的分类顺序
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 13:53
     */

    @RequestMapping("/changeVideoSeriesSort")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO changeVideoSeriesSort(HttpServletRequest request,@NotEmpty String seriesIds){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        this.userVideoSeriesService.changeVideoSeriesSort(tokenUserInfoDto.getUserId(),seriesIds);
        return getSuccessResponseVO(null);
    }

    /**
     * @description: 加载分类的时候随便加载出该分类的前几个视频
     * @param userId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 13:58
     */

    @RequestMapping("/loadVideoSeriesWithVideo")
    public ResponseVO loadVideoSeriesWithVideo(@NotEmpty String userId){
        UserVideoSeriesQuery videoSeriesQuery = new UserVideoSeriesQuery();
        videoSeriesQuery.setUserId(userId);
        videoSeriesQuery.setOrderBy("sort asc");
        List<UserVideoSeries> videoSeriesList = userVideoSeriesService.findListWithVideoList(videoSeriesQuery);

        return getSuccessResponseVO(videoSeriesList);
    }
}
