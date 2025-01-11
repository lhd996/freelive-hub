package com.lhd.web.controller;

import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.po.UserAction;
import com.lhd.entity.po.UserInfo;
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


    @RequestMapping("/saveTheme")
    public ResponseVO saveTheme(HttpServletRequest request,
                                @Min(1) @Max(10) @NotNull Integer theme){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserInfo userInfo = new UserInfo();
        userInfo.setTheme(theme);
        userInfoService.updateUserInfoByUserId(userInfo,tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
