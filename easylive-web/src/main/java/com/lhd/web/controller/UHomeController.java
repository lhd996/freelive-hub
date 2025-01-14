package com.lhd.web.controller;

import com.lhd.web.annotation.GlobalInterceptor;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.PageSize;
import com.lhd.entity.enums.UserActionTypeEnum;
import com.lhd.entity.enums.VideoOrderTypeEnum;
import com.lhd.entity.po.UserAction;
import com.lhd.entity.po.UserFocus;
import com.lhd.entity.po.UserInfo;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.query.UserActionQuery;
import com.lhd.entity.query.UserFocusQuery;
import com.lhd.entity.query.VideoInfoQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.entity.vo.UserInfoVO;
import com.lhd.service.UserActionService;
import com.lhd.service.UserFocusService;
import com.lhd.service.UserInfoService;
import com.lhd.service.VideoInfoService;
import com.lhd.utils.CopyTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.*;

/**
 * @Author: liuhd
 * @Date: 2025/1/11 13:24
 * @Description:
 */
@RestController
@RequestMapping("/uhome")
@Validated
@Slf4j
public class UHomeController extends ABaseController {
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private VideoInfoService videoInfoService;
    @Resource
    private UserFocusService userFocusService;
    @Resource
    private UserActionService userActionService;

    /**
     * @description: 获取主页用户信息
     * @param request
     * @param userId 访问该用户
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/11 13:34
     */

    @RequestMapping("/getUserInfo")
    public ResponseVO getUserInfo(HttpServletRequest request,@NotEmpty String userId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserInfo userInfo = userInfoService.getUserDetailInfo(tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId(), userId);

        UserInfoVO userInfoVO = CopyTools.copy(userInfo, UserInfoVO.class);

        return getSuccessResponseVO(userInfoVO);
    }
    /**
     * @description: 更新主页信息
     * @param request
     * @param nickName
     * @param avatar
     * @param sex
     * @param birthday
     * @param school
     * @param personIntroduction 个人介绍
     * @param noticeInfo 公告
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/11 14:00
     */

    @RequestMapping("/updateUserInfo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO updateUserInfo(HttpServletRequest request,
                                     @NotEmpty @Size(max = 20) String nickName,
                                     @NotEmpty @Size(max = 100) String avatar,
                                     @NotNull Integer sex,
                                     @Size(max = 10) String birthday,
                                     @Size(max = 150) String school,
                                     @Size(max = 80) String personIntroduction,
                                     @Size(max = 300) String noticeInfo){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(tokenUserInfoDto.getUserId());
        userInfo.setNickName(nickName);
        userInfo.setAvatar(avatar);
        userInfo.setSex(sex);
        userInfo.setBirthday(birthday);
        userInfo.setSchool(school);
        userInfo.setPersonIntroduction(personIntroduction);
        userInfo.setNoticeInfo(noticeInfo);

        userInfoService.updateUserInfo(userInfo, tokenUserInfoDto);
        return getSuccessResponseVO(null);
    }

    /**
     * @description: 更换主题
     * @param request
     * @param theme
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/11 15:01
     */

    @RequestMapping("/saveTheme")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO saveTheme(HttpServletRequest request,
                                @Min(1) @Max(10) @NotNull Integer theme){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserInfo userInfo = new UserInfo();
        userInfo.setTheme(theme);
        userInfoService.updateUserInfoByUserId(userInfo,tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * @description: 关注
     * @param request
     * @param focusUserId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/11 15:01
     */

    @RequestMapping("/focus")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO focus(HttpServletRequest request,String focusUserId){
        userFocusService.focusUser(getTokenUserInfoDto(request).getUserId(),focusUserId);
        return getSuccessResponseVO(null);
    }

    /**
     * @description: 取消关注
     * @param request
     * @param focusUserId
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/11 15:02
     */

    @RequestMapping("/cancelFocus")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO cancelFocus(HttpServletRequest request,String focusUserId){
        userFocusService.cancelFocus(getTokenUserInfoDto(request).getUserId(),focusUserId);
        return getSuccessResponseVO(null);
    }

    /**
     * @description: 查询关注列表
     * @param request
     * @param pageNo
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/11 15:33
     */
    @RequestMapping("/loadFocusList")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadFocusList(HttpServletRequest request,Integer pageNo){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        UserFocusQuery userFocusQuery = new UserFocusQuery();
        userFocusQuery.setUserId(tokenUserInfoDto.getUserId());
        userFocusQuery.setPageNo(pageNo);
        userFocusQuery.setOrderBy("focus_time desc");
        userFocusQuery.setQueryType(Constants.ZERO);
        PaginationResultVO<UserFocus> resultVO = userFocusService.findListByPage(userFocusQuery);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * @description: 查询粉丝列表
     * @param request
     * @param pageNo
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/11 16:58
     */

    @RequestMapping("/loadFansList")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadFansList(HttpServletRequest request,Integer pageNo){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        UserFocusQuery userFocusQuery = new UserFocusQuery();
        userFocusQuery.setFocusUserId(tokenUserInfoDto.getUserId());
        userFocusQuery.setPageNo(pageNo);
        userFocusQuery.setOrderBy("focus_time desc");
        userFocusQuery.setQueryType(Constants.ONE);
        PaginationResultVO<UserFocus> resultVO = userFocusService.findListByPage(userFocusQuery);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * @description:
     * @param userId 谁的主页
     * @param pageNo 可选参数 页号
     * @param type
     * @param videoName 视频名称（可根据该字段搜索视频）
     * @param orderType 排序字段（最新发布，最多播放，最多收藏）
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/12 19:40
     */

    @RequestMapping("/loadVideoList")
    public ResponseVO loadFansList(@NotEmpty String userId,
                                   Integer pageNo,
                                   Integer type,
                                   String videoName,
                                   Integer orderType){
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        if (type != null){
            videoInfoQuery.setPageSize(PageSize.SIZE10.getSize());
        }
        VideoOrderTypeEnum videoOrderTypeEnum = VideoOrderTypeEnum.getByType(orderType);
        if (videoOrderTypeEnum == null){
            videoOrderTypeEnum = VideoOrderTypeEnum.CREATE_TIME;
        }
        videoInfoQuery.setOrderBy(videoOrderTypeEnum.getField() + " desc");
        videoInfoQuery.setVideoNameFuzzy(videoName);
        videoInfoQuery.setPageNo(pageNo);
        videoInfoQuery.setUserId(userId);
        PaginationResultVO<VideoInfo> resultVO = videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }
    /**
     * @description: 查询收藏列表
     * @param userId 谁的主页
     * @param pageNo 页号
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/12 20:00
     */

    @RequestMapping("/loadUserCollection")
    public ResponseVO loadFansList(@NotEmpty String userId, Integer pageNo){
        UserActionQuery actionQuery = new UserActionQuery();
        actionQuery.setActionType(UserActionTypeEnum.VIDEO_COLLECT.getType());
        actionQuery.setUserId(userId);
        actionQuery.setPageNo(pageNo);
        actionQuery.setOrderBy("action_time desc");
        actionQuery.setQueryVideoInfo(true);
        PaginationResultVO<UserAction> resultVO = userActionService.findListByPage(actionQuery);

        return getSuccessResponseVO(resultVO);
    }
}
