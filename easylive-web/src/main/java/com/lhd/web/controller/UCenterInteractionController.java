package com.lhd.web.controller;
import com.lhd.web.annotation.GlobalInterceptor;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.po.*;
import com.lhd.entity.query.*;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: liuhd
 * @Date: 2024/12/8 16:02
 * @Description: 视频发布
 */
@RestController
@RequestMapping("/ucenter")
@Validated
@Slf4j
public class UCenterInteractionController extends ABaseController{

    @Resource
    private VideoDanmuService videoDanmuService;
    @Resource
    private VideoCommentService videoCommentService;
    @Resource
    private VideoInfoService videoInfoService;


    /**
     * @description: 获取所有发布的视频
     * @param request
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 17:19
     */

    @RequestMapping("/loadAllVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO saveVideoInteraction(HttpServletRequest request){
        // 获取视频信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoQuery.setOrderBy("create_time desc");
        List<VideoInfo> videoInfoList = videoInfoService.findListByParam(videoInfoQuery);
        return getSuccessResponseVO(videoInfoList);
    }
    /**
     * @description: 加载评论（联表）
     * @param request
     * @param pageNo
     * @param videoId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 17:49
     */

    @RequestMapping("/loadComment")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadComment(HttpServletRequest request,Integer pageNo,String videoId){
        // 获取视频信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
        videoCommentQuery.setVideoId(videoId);
        videoCommentQuery.setVideoUserId(tokenUserInfoDto.getUserId());
        videoCommentQuery.setOrderBy("comment_id desc");
        videoCommentQuery.setPageSize(pageNo);
        // 联查videoInfo
        videoCommentQuery.setQueryVideoInfo(true);

        PaginationResultVO<VideoComment> resultVO = videoCommentService.findListByPage(videoCommentQuery);
        return getSuccessResponseVO(resultVO);
    }


    @RequestMapping("/delComment")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delComment(HttpServletRequest request,@NotNull Integer commentId){
        // 获取视频信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        videoCommentService.deleteComment(commentId,tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * @description: 获取自己的视频的弹幕（联表）
     * @param request
     * @param pageNo
     * @param videoId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 17:57
     */

    @RequestMapping("/loadDanmu")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadDanmu(HttpServletRequest request,Integer pageNo,String videoId){
        // 获取视频弹幕信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        VideoDanmuQuery videoDanmuQuery = new VideoDanmuQuery();
        videoDanmuQuery.setVideoId(videoId);
        videoDanmuQuery.setVideoUserId(tokenUserInfoDto.getUserId());
        videoDanmuQuery.setOrderBy("danmu_id desc");
        videoDanmuQuery.setPageNo(pageNo);
        // 是否联查视频表
        videoDanmuQuery.setQueryVideoInfo(true);

        PaginationResultVO<VideoDanmu> resultVO = videoDanmuService.findListByPage(videoDanmuQuery);
        return getSuccessResponseVO(resultVO);
    }


    @RequestMapping("/delDanmu")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delDanmu(HttpServletRequest request,@NotNull Integer danmuId){
        // 获取视频信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        videoDanmuService.deleteDanmu(danmuId,tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
