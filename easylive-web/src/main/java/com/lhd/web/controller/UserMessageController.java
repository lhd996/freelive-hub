package com.lhd.web.controller;

import com.lhd.annotation.RecordUserMessage;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.dto.UserMessageCountDto;
import com.lhd.entity.enums.MessageReadTypeEnum;
import com.lhd.entity.enums.MessageTypeEnum;
import com.lhd.entity.po.UserAction;
import com.lhd.entity.po.UserMessage;
import com.lhd.entity.query.UserMessageQuery;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.UserActionService;
import com.lhd.service.UserMessageService;
import com.lhd.web.annotation.GlobalInterceptor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: liuhd
 * @Date: 2024/12/12 21:50
 * @Description:
 */
@RestController
@RequestMapping("/message")
@Validated
public class UserMessageController extends ABaseController{
    @Resource
    private UserMessageService userMessageService;

    /**
     * @description: 获取未读消息
     * @param request
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/14 18:47
     */
    @RequestMapping("/getNoReadCount")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getNoReadCount(HttpServletRequest request){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        UserMessageQuery userMessageQuery = new UserMessageQuery();
        userMessageQuery.setUserId(tokenUserInfoDto.getUserId());
        userMessageQuery.setReadType(MessageReadTypeEnum.NO_READ.getType());
        Integer count = userMessageService.findCountByParam(userMessageQuery);

        return getSuccessResponseVO(count);
    }

    /**
     * @description: 获取各种类型的未读消息
     * @param request
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/14 20:40
     */
    @RequestMapping("/getNoReadCountGroup")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getNoReadCountGroup(HttpServletRequest request){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        List<UserMessageCountDto> dtoList = userMessageService.getMessageTypeNoReadCount(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(dtoList);
    }


    /**
     * @description: 读取消息/标记消息为已读
     * @param request
     * @param messageType 消息类型
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/14 20:48
     */

    @RequestMapping("/readAll")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO readAll(HttpServletRequest request,@NotNull Integer messageType){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserMessageQuery userMessageQuery = new UserMessageQuery();
        userMessageQuery.setUserId(tokenUserInfoDto.getUserId());
        userMessageQuery.setMessageType(messageType);

        UserMessage userMessage = new UserMessage();
        userMessage.setReadType(MessageReadTypeEnum.READ.getType());

        userMessageService.updateByParam(userMessage,userMessageQuery);

        return getSuccessResponseVO(null);
    }
    /**
     * @description: 获取消息
     * @param request
     * @param pageNo
     * @param messageType 消息的类型
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/14 20:46
     */
    @RequestMapping("/loadMessage")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadMessage(HttpServletRequest request,Integer pageNo,@NotNull Integer messageType){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserMessageQuery userMessageQuery = new UserMessageQuery();
        userMessageQuery.setUserId(tokenUserInfoDto.getUserId());
        userMessageQuery.setPageNo(pageNo);
        userMessageQuery.setMessageType(messageType);
        userMessageQuery.setOrderBy("message_id desc");
        PaginationResultVO<UserMessage> resultVO = userMessageService.findListByPage(userMessageQuery);
        return getSuccessResponseVO(resultVO);
    }


    @RequestMapping("/delMessage")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delMessage(HttpServletRequest request,@NotNull Integer messageId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserMessageQuery userMessageQuery = new UserMessageQuery();
        userMessageQuery.setUserId(tokenUserInfoDto.getUserId());
        userMessageQuery.setMessageId(messageId);

        userMessageService.deleteByParam(userMessageQuery);
        return getSuccessResponseVO(null);
    }
}
