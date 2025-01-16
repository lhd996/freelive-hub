package com.lhd.admin.controller;

import com.lhd.annotation.RecordUserMessage;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.MessageTypeEnum;
import com.lhd.entity.enums.VideoStatusEnum;
import com.lhd.entity.po.VideoInfoFile;
import com.lhd.entity.po.VideoInfoFilePost;
import com.lhd.entity.po.VideoInfoPost;
import com.lhd.entity.query.VideoInfoFilePostQuery;
import com.lhd.entity.query.VideoInfoFileQuery;
import com.lhd.entity.query.VideoInfoPostQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.VideoInfoFilePostService;
import com.lhd.service.VideoInfoFileService;
import com.lhd.service.VideoInfoPostService;
import com.lhd.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author: liuhd
 * @Date: 2024/12/9 16:05
 * @Description:
 */
@RestController
@RequestMapping("/videoInfo")
public class VideoInfoController extends ABaseController {
    @Resource
    private VideoInfoPostService videoInfoPostService;
    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;
    @Resource
    private VideoInfoService videoInfoService;

    /**
     * 加载视频
     * @param 
     * @return 
     * @author liuhd
     * 2024/12/9 19:53
     */
    
    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(VideoInfoPostQuery videoInfoPostQuery){
        // 构建查询条件
        videoInfoPostQuery.setOrderBy("v.last_update_time desc");
        // 需要连表查询

        videoInfoPostQuery.setQueryCountInfo(true);
        videoInfoPostQuery.setQueryUserInfo(true);
        PaginationResultVO<VideoInfoPost> resultVO = videoInfoPostService.findListByPage(videoInfoPostQuery);
        return getSuccessResponseVO(resultVO);
    }
    
    /**
     * @description: 审核视频
     * @param videoId 视频id
     * @param status 审核通过 不通过
     * @param reason 原因
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2024/12/10 17:00
     */

    @RequestMapping("/auditVideo")
    @RecordUserMessage(messageType = MessageTypeEnum.SYS)
    public ResponseVO auditVideo(@NotEmpty String videoId,@NotEmpty Integer status,String reason){
        videoInfoPostService.auditVideo(videoId,status,reason);
        return getSuccessResponseVO(null);
    }
    /**
     * @description: 推荐视频
     * @param videoId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/16 10:41
     */
    @RequestMapping("/recommendVideo")
    public ResponseVO recommendVideo(@NotEmpty String videoId){
        videoInfoService.recommendVideo(videoId);
        return getSuccessResponseVO(null);
    }
    /**
     * @description: 删除视频
     * @param videoId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/16 10:42
     */
    @RequestMapping("/deleteVideo")
    public ResponseVO deleteVideo(@NotEmpty String videoId){
        videoInfoService.deleteVideo(videoId,null);
        return getSuccessResponseVO(null);
    }

    /**
     * @description: 加载分P信息
     * @param videoId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/16 10:56
     */
    @RequestMapping("/loadVideoPList")
    public ResponseVO loadVideoPList(@NotEmpty String videoId){
        VideoInfoFilePostQuery postQuery = new VideoInfoFilePostQuery();
        postQuery.setVideoId(videoId);
        postQuery.setOrderBy("file_index asc");
        List<VideoInfoFilePost> videoInfoFilePostList = videoInfoFilePostService.findListByParam(postQuery);
        return getSuccessResponseVO(videoInfoFilePostList);
    }

}
