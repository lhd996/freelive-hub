package com.lhd.aspect;

import com.lhd.annotation.RecordUserMessage;
import com.lhd.component.RedisComponent;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.MessageTypeEnum;
import com.lhd.entity.enums.UserActionTypeEnum;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.redis.RedisUtils;
import com.lhd.service.UserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Author: liuhd
 * @Date: 2025/1/14 16:28
 * @Description: 发布信息切面
 */
@Aspect
@Component
@Slf4j
public class UserMessageOperationAspect {
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private UserMessageService userMessageService;

    /**
     * @description: 给用户发送消息
     * @param point
     * @return com.lhd.entity.vo.ResponseVO
     * @author liuhd
     * 2025/1/14 16:46
     */
    @Around("@annotation(com.lhd.annotation.RecordUserMessage)")
    public ResponseVO interceptorDo(ProceedingJoinPoint point) throws Throwable {
        // 执行方法
        ResponseVO responseVO = (ResponseVO) point.proceed();
        // 获取方法
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        // 获取注解
        RecordUserMessage recordUserMessage = method.getAnnotation(RecordUserMessage.class);
        // 保存信息
        saveMessage(recordUserMessage,point.getArgs(),method.getParameters());

        return responseVO;
    }
    /**
     * @description: 将消息存到消息表中(点赞,收藏,评论,系统消息)
     * 不同的方法参数是不一样的
     * @param recordUserMessage
     * @param args 代理的方法的参数的值
     * @param parameters 代理的方法的参数
     * @return
     * @author liuhd
     * 2025/1/14 16:47
     */
    private void saveMessage(RecordUserMessage recordUserMessage, Object[] args, Parameter[] parameters) {
        String videoId = null;
        Integer actionType = null;
        Integer replyCommentId =null;
        String content = null;
        for (int i = 0; i < parameters.length; i++) {
            if ("videoId".equals(parameters[i].getName())){
                videoId = (String) args[i];
            }else if ("actionType".equals(parameters[i].getName())){
                actionType = (Integer) args[i];
            }else if ("replyCommentId".equals(parameters[i].getName())){
                replyCommentId = (Integer) args[i];
            }else if ("content".equals(parameters[i].getName())){
                content = (String) args[i];
            }else if ("reason".equals(parameters[i].getName())){
                content = (String) args[i];
            }
            MessageTypeEnum messageTypeEnum = recordUserMessage.messageType();
            // 对于收藏与点赞,使用的是同一个接口，我们加的是@recordUserMessage(messageType = like)
            // 因此有必要判断一下当前到底是哪一种信息
            if (UserActionTypeEnum.VIDEO_COLLECT.getType().equals(actionType)){
                messageTypeEnum = MessageTypeEnum.COLLECTION;
            }
            TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
            userMessageService.saveUserMessage(videoId,tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId(),messageTypeEnum,content,replyCommentId);
        }
    }
    public TokenUserInfoDto getTokenUserInfoDto(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(Constants.TOKEN_WEB);
        return redisComponent.getTokenInfo(token);
    }
}
