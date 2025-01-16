package com.lhd.admin.controller;

import com.lhd.entity.po.VideoComment;
import com.lhd.entity.po.VideoDanmu;
import com.lhd.entity.query.UserInfoQuery;
import com.lhd.entity.query.VideoCommentQuery;
import com.lhd.entity.query.VideoDanmuQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.UserInfoService;
import com.lhd.service.VideoCommentService;
import com.lhd.service.VideoDanmuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @Author: liuhd
 * @Date: 2025/1/8 13:55
 * @Description:
 */
@RestController
@RequestMapping("/user")
@Validated
@Slf4j
public class UserController extends ABaseController{
    @Resource
    private UserInfoService userInfoService;

    @RequestMapping("/loadUser")
    public ResponseVO loadUser(UserInfoQuery userInfoQuery){
       userInfoQuery.setOrderBy("join_time desc");
       return getSuccessResponseVO(userInfoService.findListByPage(userInfoQuery));
    }

    @RequestMapping("/changeStatus")
    public ResponseVO loadUser(String userId,Integer status){
        userInfoService.changeUserStatus(userId,status);
        return getSuccessResponseVO(null);
    }

}
