package com.lhd.web.aspect;

import com.lhd.web.annotation.GlobalInterceptor;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.exception.BusinessException;
import com.lhd.redis.RedisUtils;
import com.lhd.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @Author: liuhd
 * @Date: 2025/1/14 14:16
 * @Description: 切面
 */
@Aspect
@Component
@Slf4j
public class GlobalOperationAspect {

    @Resource
    private RedisUtils redisUtils;
    /**
     * @description: 检验登录
     * @param point
     * @return
     * @author liuhd
     * 2025/1/14 15:24
     */
    @Before("@annotation(com.lhd.web.annotation.GlobalInterceptor)")
    public void interceptor(JoinPoint point){
        // 获取方法
        Method method =((MethodSignature) point.getSignature()).getMethod();
        // 拿到注解
        GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
        // 如果是true,则校验登录
        if (interceptor.checkLogin()){
            checkLogin();
        }
    }
    // 校验登录
    private void checkLogin(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(Constants.TOKEN_WEB);
        // 如果没有token
        if (StringTools.isEmpty(token)){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        // 如果在redis中没有这个token 说明过期了或者说是恶意token
        TokenUserInfoDto tokenUserInfoDto = (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_TOKEN_WEB + token);
        if (tokenUserInfoDto == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
    }
}

