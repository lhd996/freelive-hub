package com.lhd.admin.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Author: liuhd
 * @Date: 2024/12/6 00:37
 * @Description: 注册拦截器
 */
@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {
    @Resource
    private AppInterceptor appInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appInterceptor);
    }
}
