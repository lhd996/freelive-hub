package com.lhd.web.controller;

import com.lhd.entity.constants.Constants;
import com.lhd.entity.po.UserAction;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.UserActionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;

/**
 * @Author: liuhd
 * @Date: 2024/12/12 21:50
 * @Description:
 */
@RestController
@RequestMapping("/userAction")
public class UserActionController extends ABaseController{
    @Resource
    private UserActionService userActionService;

    /**
     * @description:
     * @param videoId 视频id
     * @param actionType 行为类型 点赞/收藏/投币/评论点赞/评论点踩
     * @param actionCount 行为数量
     * @param commentId 评论id
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2024/12/12 21:56
     */

    @RequestMapping("/doAction")
    public ResponseVO doAction(HttpServletRequest request,@NotEmpty String videoId,
                               @NotNull Integer actionType,
                               @Max(2) @Min(1) Integer actionCount,
                               Integer commentId){
        UserAction userAction = new UserAction();
        userAction.setUserId(getTokenUserInfoDto(request).getUserId());
        userAction.setVideoId(videoId);
        userAction.setActionType(actionType);
        Integer count = actionCount == null ? Constants.ONE : actionCount;
        userAction.setActionCount(count);
        commentId = commentId == null ? 0 : commentId;
        userAction.setCommentId(commentId);
        userActionService.saveAction(userAction);
        return getSuccessResponseVO(null);
    }
}
