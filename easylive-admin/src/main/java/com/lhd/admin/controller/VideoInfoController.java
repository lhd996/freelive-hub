package com.lhd.admin.controller;

import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.VideoStatusEnum;
import com.lhd.entity.po.VideoInfoPost;
import com.lhd.entity.query.VideoInfoPostQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.VideoInfoFilePostService;
import com.lhd.service.VideoInfoPostService;
import com.lhd.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

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
        videoInfoPostQuery.setOrderBy("v.last_update_time desc");
        // 需要连表查询
        videoInfoPostQuery.setQueryCountInfo(true);
        videoInfoPostQuery.setQueryUserInfo(true);
        PaginationResultVO<VideoInfoPost> resultVO = videoInfoPostService.findListByPage(videoInfoPostQuery);
        return getSuccessResponseVO(resultVO);
    }
    
    /**
     *
     * @param status 是否通过 reason 不通过原因
     * @return 
     * @author liuhd
     * 2024/12/9 19:53
     */
    
    @RequestMapping("/auditVideo")
    public ResponseVO auditVideo(@NotEmpty String videoId,@NotEmpty Integer status,String reason){
        videoInfoPostService.auditVideo(videoId,status,reason);
        return getSuccessResponseVO(null);
    }
}
