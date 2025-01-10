package com.lhd.web.controller;

import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.CommentTopTypeEnum;
import com.lhd.entity.enums.PageSize;
import com.lhd.entity.enums.UserActionTypeEnum;
import com.lhd.entity.po.UserAction;
import com.lhd.entity.po.VideoComment;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.query.UserActionQuery;
import com.lhd.entity.query.VideoCommentQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.entity.vo.VideoCommentResultVO;
import com.lhd.entity.vo.VideoInfoVO;
import com.lhd.exception.BusinessException;
import com.lhd.service.UserActionService;
import com.lhd.service.VideoCommentService;
import com.lhd.service.VideoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
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
@RequestMapping("/comment")
@Validated
@Slf4j
public class VideoCommentController extends ABaseController{
    @Resource
    private VideoCommentService videoCommentService;
    @Resource
    private UserActionService userActionService;
    @Resource
    private VideoInfoService videoInfoService;

    /**
     * @description: 发表评论
     * @param request
     * @param videoId 哪个视频
     * @param content 评论内容
     * @param imgPath 评论可以是图像
     * @param replyCommentId 回复了谁
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/8 14:32
     */
    @RequestMapping("/postComment")
    public ResponseVO postComment(HttpServletRequest request,
                                  @NotEmpty String videoId,
                                  @NotEmpty @Size(max = 500) String content,
                                  @Size(max = 50) String imgPath,
                                  Integer replyCommentId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        VideoComment videoComment = new VideoComment();
        videoComment.setUserId(tokenUserInfoDto.getUserId());
        videoComment.setAvatar(tokenUserInfoDto.getAvatar());
        videoComment.setNickName(tokenUserInfoDto.getNickName());
        videoComment.setVideoId(videoId);
        videoComment.setContent(content);
        videoComment.setImgPath(imgPath);
        videoCommentService.postComment(videoComment,replyCommentId);

        // 发布评论无需再查 直接返回给前端
        return getSuccessResponseVO(videoComment);
    }

    /**
     * @description: 加载评论区
     * @param request
     * @param videoId 视频id
     * @param pageNo 页号
     * @param orderType 根据什么排序
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/8 15:55
     */

    @RequestMapping("/loadComment")
    public ResponseVO loadComment(HttpServletRequest request,
                                  @NotEmpty String videoId,
                                  Integer pageNo,
                                  Integer orderType){

        VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
        // 如果关闭互动
        if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains(Constants.ZERO.toString())){
            return getSuccessResponseVO(new ArrayList<>());
        }
        // 获取评论
        VideoCommentQuery commentQuery = new VideoCommentQuery();
        commentQuery.setLoadChildren(true);
        commentQuery.setVideoId(videoId);
        commentQuery.setPageNo(pageNo);
        commentQuery.setPageSize(PageSize.SIZE15.getSize());
        commentQuery.setpCommentId(0);
        String orderBy = orderType == null || orderType == 0 ? "like_count desc,comment_id desc" : "comment_id desc";
        commentQuery.setOrderBy(orderBy);

        PaginationResultVO<VideoComment> commentData = videoCommentService.findListByPage(commentQuery);

        if (pageNo == null){
            List<VideoComment> topCommentList = topComment(videoId);
            // 如果存在置顶评论
            if (!topCommentList.isEmpty()){
                // 获取除了置顶评论的所有评论（此时topType = 1的评论还没被真正置顶）
                List<VideoComment> commentList = commentData.getList().stream().filter(item -> !item.getCommentId().equals(topCommentList.get(0).getCommentId())).collect(Collectors.toList());
                // 然后将置顶评论放到最前面（置顶）
                commentList.addAll(0,topCommentList);
                commentData.setList(commentList);
            }
        }


        // 获取用户对评论的行为
        List<UserAction> userActionList = new ArrayList<>();
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        // 如果登录了
        if (tokenUserInfoDto != null){
            // 取出来
            UserActionQuery userActionQuery = new UserActionQuery();
            userActionQuery.setUserId(tokenUserInfoDto.getUserId());
            userActionQuery.setVideoId(videoId);
            userActionQuery.setActionTypeArray(new Integer[]{
                    UserActionTypeEnum.COMMENT_LIKE.getType(),
                    UserActionTypeEnum.COMMENT_HATE.getType()
            });
            userActionList = userActionService.findListByParam(userActionQuery);
        }
        // 组装成VO
        VideoCommentResultVO resultVO = new VideoCommentResultVO();
        resultVO.setCommentData(commentData);
        resultVO.setUserActionList(userActionList);

        return getSuccessResponseVO(resultVO);
    }
    /**
     * @description: 查询置顶评论
     * @param videoId 视频id
     * @return java.util.List<com.lhd.entity.po.VideoComment>
     * @author liuhd
     * 2025/1/10 20:37
     */

    private List<VideoComment> topComment(String videoId){
        VideoCommentQuery commentQuery = new VideoCommentQuery();
        commentQuery.setVideoId(videoId);
        // 查询置顶评论
        commentQuery.setTopType(CommentTopTypeEnum.TOP.getType());
        commentQuery.setLoadChildren(true);

        List<VideoComment> videoCommentList = videoCommentService.findListByParam(commentQuery);
        return videoCommentList;
    }
}
