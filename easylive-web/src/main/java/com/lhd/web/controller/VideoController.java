package com.lhd.web.controller;

import com.lhd.component.EsSearchComponent;
import com.lhd.component.RedisComponent;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.*;
import com.lhd.entity.po.UserAction;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.po.VideoInfoFile;
import com.lhd.entity.po.VideoInfoPost;
import com.lhd.entity.query.UserActionQuery;
import com.lhd.entity.query.VideoInfoFileQuery;
import com.lhd.entity.query.VideoInfoPostQuery;
import com.lhd.entity.query.VideoInfoQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.entity.vo.VideoInfoVO;
import com.lhd.entity.vo.VideoStatusCountInfoVO;
import com.lhd.exception.BusinessException;
import com.lhd.service.UserActionService;
import com.lhd.service.VideoInfoFileService;
import com.lhd.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liuhd
 * @Date: 2024/12/9 21:54
 * @Description:
 */
@RestController
@RequestMapping("/video")
public class VideoController extends ABaseController {
    @Resource
    private VideoInfoService videoInfoService;
    @Resource
    private VideoInfoFileService videoInfoFileService;
    @Resource
    private UserActionService userActionService;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private EsSearchComponent esSearchComponent;
    /**
     * 加载已推荐视频
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/9 22:02
     */

    @RequestMapping("/loadRecommendVideo")
    public ResponseVO loadRecommendVideo() {
        // 构建查询条件
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setOrderBy("create_time desc");
        // 是否添加这个参数
        videoInfoQuery.setQueryUserInfo(true);
        videoInfoQuery.setRecommendType(VideoRecommendTypeEnum.RECOMMEND.getType());
        List<VideoInfo> recommendVideoList = this.videoInfoService.findListByParam(videoInfoQuery);
        return getSuccessResponseVO(recommendVideoList);
    }

    /**
     * 加载未推荐的视频
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/9 22:48
     */




    @RequestMapping("/loadVideo")
    public ResponseVO loadVideo(Integer pCategoryId, Integer categoryId, Integer pageNo) {
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setpCategoryId(pCategoryId);
        videoInfoQuery.setCategoryId(categoryId);
        videoInfoQuery.setPageNo(pageNo);
        videoInfoQuery.setQueryUserInfo(true);
        videoInfoQuery.setOrderBy("create_time desc");
        videoInfoQuery.setRecommendType(VideoRecommendTypeEnum.NO_RECOMMEND.getType());
        // 分页查询
        PaginationResultVO<VideoInfo> resultVO = this.videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * @description: 获取视频详细信息
     * @param videoId 视频id
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2024/12/10 21:43
     */

    @RequestMapping("/getVideoInfo")
    public ResponseVO getVideoInfo(HttpServletRequest request,@NotEmpty String videoId) {
        // 查出视频详细信息
        VideoInfo videoInfo = this.videoInfoService.getVideoInfoByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        // 获取用户所有的行为:是否点赞 投币 收藏
        List<UserAction> userActionList = new ArrayList<>();
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        // 如果登录了
        if (tokenUserInfoDto != null){
            // 取出来
            UserActionQuery userActionQuery = new UserActionQuery();
            userActionQuery.setUserId(tokenUserInfoDto.getUserId());
            userActionQuery.setVideoId(videoId);
            userActionQuery.setActionTypeArray(new Integer[]{
                    UserActionTypeEnum.VIDEO_LIKE.getType(),
                    UserActionTypeEnum.VIDEO_COLLECT.getType(),
                    UserActionTypeEnum.VIDEO_COIN.getType()
            });
            userActionList = userActionService.findListByParam(userActionQuery);
        }
        VideoInfoVO videoInfoVO = new VideoInfoVO(videoInfo,userActionList);
        return getSuccessResponseVO(videoInfoVO);
    }

    /**
     * @description:加载视频分P
     * @param videoId 视频ID
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2024/12/10 17:29
     */
    @RequestMapping("/loadVideoPList")
    public ResponseVO loadVideoPList(@NotEmpty String videoId){
        VideoInfoFileQuery videoInfoFileQuery = new VideoInfoFileQuery();
        videoInfoFileQuery.setVideoId(videoId);
        videoInfoFileQuery.setOrderBy("file_index asc");
        List<VideoInfoFile> resultVO  = videoInfoFileService.findListByParam(videoInfoFileQuery);
        return getSuccessResponseVO(resultVO);
    }
    /**
     * @description:
     * @param fileId 分p的id
     * @param deviceId 设备id
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2024/12/10 17:46
     */

    @RequestMapping("/reportVideoPlayOnline")
    public ResponseVO reportVideoPlayOnline(@NotEmpty String fileId,@NotEmpty String deviceId){
        return getSuccessResponseVO(redisComponent.reportVideoOnline(fileId,deviceId));
    }

    /**
     * @description: 根据关键字搜索视频
     * @param keyword
     * @param orderType 根据什么排序
     * @param pageNo
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/13 22:11
     */
    @RequestMapping("/search")
    public ResponseVO search(@NotEmpty String keyword,Integer orderType,Integer pageNo){
        // 记录搜索热词
        redisComponent.addKeywordCount(keyword);
        // 用ES搜索
        PaginationResultVO<VideoInfo> resultVO = esSearchComponent.search(true, keyword, orderType, pageNo, PageSize.SIZE30.getSize());
        return getSuccessResponseVO(resultVO);
    }

    /**
     * @description:
     * @param keyword 获取推荐视频
     * @param videoId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/14 21:59
     */
    @RequestMapping("/getVideoRecommend")
    public ResponseVO search(@NotEmpty String keyword,@NotEmpty String videoId){
        List<VideoInfo> videoInfoList = esSearchComponent.search(false, keyword, SearchOrderTypeEnum.VIDEO_PLAY.getType(), 1, PageSize.SIZE10.getSize()).getList();
        videoInfoList = videoInfoList.stream().filter(i -> !i.getVideoId().equals(videoId)).collect(Collectors.toList());
        return getSuccessResponseVO(videoInfoList);
    }

    /**
     * @description: 获取搜索热词列表
     * @param
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/14 11:22
     */
    @RequestMapping("/getSearchKeywordTop")
    public ResponseVO getSearchKeywordTop(){
        List<String> keywordTop = redisComponent.getKeywordTop(Constants.LENGTH_10);
        return getSuccessResponseVO(keywordTop);
    }

    /**
     * @description: 加载播放量高的视频
     * @param pageNo
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/14 11:45
     */

    @RequestMapping("/loadHotVideoList")
    public ResponseVO loadHotVideoList(Integer pageNo){
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setPageNo(pageNo);
        videoInfoQuery.setOrderBy("play_count asc");
        videoInfoQuery.setQueryUserInfo(true);
        // 只要最近24个小时的
        videoInfoQuery.setLastPlayHour(Constants.HOUR_24);
        PaginationResultVO<VideoInfo> resultVO = videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }
}
