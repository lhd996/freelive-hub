package com.lhd.admin.controller;

import com.lhd.annotation.RecordUserMessage;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.CommentTopTypeEnum;
import com.lhd.entity.enums.MessageTypeEnum;
import com.lhd.entity.enums.PageSize;
import com.lhd.entity.enums.UserActionTypeEnum;
import com.lhd.entity.po.UserAction;
import com.lhd.entity.po.VideoComment;
import com.lhd.entity.po.VideoDanmu;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.query.UserActionQuery;
import com.lhd.entity.query.VideoCommentQuery;
import com.lhd.entity.query.VideoDanmuQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.entity.vo.VideoCommentResultVO;
import com.lhd.service.UserActionService;
import com.lhd.service.VideoCommentService;
import com.lhd.service.VideoDanmuService;
import com.lhd.service.VideoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liuhd
 * @Date: 2025/1/8 13:55
 * @Description:
 */
@RestController
@RequestMapping("/interact")
@Validated
@Slf4j
public class InteractController extends ABaseController{
    @Resource
    private VideoCommentService videoCommentService;
    @Resource
    private VideoDanmuService videoDanmuService;

    /**
     * @description: 加载评论
     * @param pageNo
     * @param videNameFuzzy
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/16 11:13
     */
    @RequestMapping("/loadComment")
    public ResponseVO loadComment(Integer pageNo,String videNameFuzzy){
        VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
        videoCommentQuery.setOrderBy("comment_id");
        videoCommentQuery.setPageNo(pageNo);
        videoCommentQuery.setVideoNameFuzzy(videNameFuzzy);
        // 联查视频信息
        videoCommentQuery.setQueryVideoInfo(true);
        PaginationResultVO<VideoComment> resultVO = videoCommentService.findListByPage(videoCommentQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/delComment")
    public ResponseVO delComment(@NotNull Integer commentId){
        videoCommentService.deleteComment(commentId,null);
        return getSuccessResponseVO(null);
    }


    @RequestMapping("/loadDanmu")
    public ResponseVO loadDanmu(Integer pageNo,String videoNameFuzzy){
        VideoDanmuQuery videoDanmuQuery = new VideoDanmuQuery();
        videoDanmuQuery.setPageNo(pageNo);
        videoDanmuQuery.setVideoNameFuzzy(videoNameFuzzy);
        videoDanmuQuery.setOrderBy("danmu_id desc");
        videoDanmuQuery.setQueryVideoInfo(true);

        PaginationResultVO<VideoDanmu> resultVO = videoDanmuService.findListByPage(videoDanmuQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/delDanmu")
    public ResponseVO delDanmu(@NotNull Integer danmuId){
        videoDanmuService.deleteDanmu(danmuId,null);
        return getSuccessResponseVO(null);
    }
}
