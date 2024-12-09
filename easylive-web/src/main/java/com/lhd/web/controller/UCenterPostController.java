package com.lhd.web.controller;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.VideoStatusEnum;
import com.lhd.entity.po.VideoInfoFilePost;
import com.lhd.entity.po.VideoInfoPost;
import com.lhd.entity.query.VideoInfoPostQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.entity.vo.VideoStatusCountInfoVO;
import com.lhd.service.VideoInfoFilePostService;
import com.lhd.service.VideoInfoPostService;
import com.lhd.service.VideoInfoService;
import com.lhd.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
public class UCenterPostController extends ABaseController{
    @Resource
    private VideoInfoPostService videoInfoPostService;
    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;
    @Resource
    private VideoInfoService videoInfoService;

    @RequestMapping("/postVideo")
    public ResponseVO postVideo(HttpServletRequest request, String videoId,
                                @NotEmpty String videoCover,
                                @NotEmpty @Size(max = 100) String videoName,
                                @NotNull Integer pCategoryId,
                                Integer categoryId,
                                @NotNull Integer postType,
                                @NotEmpty @Size(max = 300) String tags,
                                @Size(max = 2000) String introduction,
                                @Size(max = 3) String interaction,
                                @NotEmpty String uploadFileList){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        List<VideoInfoFilePost> uploadFileList1 = JsonUtils.convertJsonArray2List(uploadFileList, VideoInfoFilePost.class);

        VideoInfoPost videoInfoPost = new VideoInfoPost();
        videoInfoPost.setVideoId(videoId);
        videoInfoPost.setVideoName(videoName);
        videoInfoPost.setVideoCover(videoCover);
        videoInfoPost.setpCategoryId(pCategoryId);
        videoInfoPost.setCategoryId(categoryId);
        videoInfoPost.setPostType(postType);
        videoInfoPost.setTags(tags);
        videoInfoPost.setIntroduction(introduction);
        videoInfoPost.setInteraction(interaction);

        videoInfoPost.setUserId(tokenUserInfoDto.getUserId());
        videoInfoPostService.saveVideoInfo(videoInfoPost,uploadFileList1);
        return getSuccessResponseVO(null);
    }
    /**
     * 加载已发布视频
     * @param
     * @return
     * @author liuhd
     * 2024/12/9 16:45
     */

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoPost(HttpServletRequest request,Integer status,Integer pageNo,String videoNameFuzzy){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
        videoInfoPostQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoPostQuery.setPageNo(pageNo);
        videoInfoPostQuery.setOrderBy("v.create_time asc");
        if (status!=null){
            if (status == -1){ // 进行中
                // 排除审核成功与失败
                videoInfoPostQuery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(),VideoStatusEnum.STATUS4.getStatus()});
            }else {
                videoInfoPostQuery.setStatus(status);
            }
        }
        videoInfoPostQuery.setVideoNameFuzzy(videoNameFuzzy);
        videoInfoPostQuery.setQueryCountInfo(true);
        PaginationResultVO<VideoInfoPost> resultVO  = videoInfoPostService.findListByPage(videoInfoPostQuery);
        return getSuccessResponseVO(resultVO);
    }
    
    /**
     * 获取视频总数
     * @param 
     * @return 
     * @author liuhd
     * 2024/12/9 17:35
     */
    

    @RequestMapping("/getVideoCountInfo")
    public ResponseVO getVideoCountInfo(HttpServletRequest request){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
        videoInfoPostQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoPostQuery.setStatus(VideoStatusEnum.STATUS3.getStatus());
        Integer auditPassCount = videoInfoPostService.findCountByParam(videoInfoPostQuery);

        videoInfoPostQuery.setStatus(VideoStatusEnum.STATUS4.getStatus());
        Integer auditFailCount = videoInfoPostService.findCountByParam(videoInfoPostQuery);

        videoInfoPostQuery.setStatus(null);
        videoInfoPostQuery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(),VideoStatusEnum.STATUS4.getStatus()});
        Integer inProgress = videoInfoPostService.findCountByParam(videoInfoPostQuery);

        VideoStatusCountInfoVO countInfoVO = new VideoStatusCountInfoVO();
        countInfoVO.setAuditFailCount(auditFailCount);
        countInfoVO.setAuditPassCount(auditPassCount);
        countInfoVO.setInProgress(inProgress);
        return getSuccessResponseVO(countInfoVO);
    }

}
