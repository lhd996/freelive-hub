package com.lhd.web.controller;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.entity.enums.VideoStatusEnum;
import com.lhd.entity.po.VideoInfoFilePost;
import com.lhd.entity.po.VideoInfoPost;
import com.lhd.entity.query.VideoInfoFilePostQuery;
import com.lhd.entity.query.VideoInfoPostQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.entity.vo.VideoPostEditInfoVo;
import com.lhd.entity.vo.VideoStatusCountInfoVO;
import com.lhd.exception.BusinessException;
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

    /**
     * @description: 发布视频接口
     * @param request HttpServletRequest
     * @param videoId 视频id
     * @param videoCover 视频封面
     * @param videoName 视频标题
     * @param pCategoryId 父级分类
     * @param categoryId 子级分类
     * @param postType 发布类型（转载or自制）
     * @param tags 所属标签
     * @param introduction 介绍
     * @param interaction  互动设置
     * @param uploadFileList 分p列表
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2024/12/10 14:06
     */

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

    /**
     * @description: 获取视频的信息
     * @param request
     * @param videoId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 16:04
     */

    @RequestMapping("/getVideoByVideoId")
    public ResponseVO getVideoByVideoId(HttpServletRequest request,@NotEmpty String videoId){
        // 获取视频信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        VideoInfoPost videoInfoPost = videoInfoPostService.getVideoInfoPostByVideoId(videoId);
        if (videoInfoPost == null || !videoInfoPost.getUserId().equals(tokenUserInfoDto.getUserId())){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        // 获取分p信息
        VideoInfoFilePostQuery videoInfoFilePostQuery = new VideoInfoFilePostQuery();
        videoInfoFilePostQuery.setVideoId(videoId);
        videoInfoFilePostQuery.setOrderBy("file_index asc");
        List<VideoInfoFilePost> videoInfoFilePostList = videoInfoFilePostService.findListByParam(videoInfoFilePostQuery);
        // 组装成vo返回
        VideoPostEditInfoVo vo = new VideoPostEditInfoVo();
        vo.setVideoInfo(videoInfoPost);
        vo.setVideoInfoFileList(videoInfoFilePostList);
        return getSuccessResponseVO(vo);
    }

    /**
     * @description: 保存视频的交互信息
     * @param request
     * @param videoId
     * @param interaction
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 16:23
     */

    @RequestMapping("/saveVideoInteraction")
    public ResponseVO saveVideoInteraction(HttpServletRequest request,@NotEmpty String videoId,String interaction){
        // 获取视频信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        videoInfoService.changeInteraction(videoId,tokenUserInfoDto.getUserId(),interaction);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/deleteVideo")
    public ResponseVO saveVideoInteraction(HttpServletRequest request,@NotEmpty String videoId){
        // 获取视频信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        videoInfoService.deleteVideo(videoId,tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

}
