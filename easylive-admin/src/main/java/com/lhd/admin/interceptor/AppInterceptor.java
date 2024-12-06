package com.lhd.admin.interceptor;

import com.lhd.component.RedisComponent;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.exception.BusinessException;
import com.lhd.utils.StringTools;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: liuhd
 * @Date: 2024/12/6 00:35
 * @Description: 拦截器
 */
@Component
public class AppInterceptor implements HandlerInterceptor {
    private static final String URL_ACCOUNT = "/account";
    private static final String URL_FILE = "/file";
    @Resource
    private RedisComponent redisComponent;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // true放行 false拦截
        // 如果是/account下的（checkCode login ...） 全部放行
        if (request.getRequestURI().contains(URL_ACCOUNT)){
            return true;
        }
        // 登录之后 前端会把admin server的token放在请求头中,因为浏览器中的cookies太多了
        // 咱们不能每次都遍历cookies然后去找admin server的token吧！
        String token = request.getHeader(Constants.TOKEN_ADMIN);

        // 图片相关的请求的token不能从head中获取，它不会被我们前端的请求拦截器拦截
        // 所以不会被加上head 需要去Cookie中找
        if (request.getRequestURI().contains(URL_FILE)){
            token = getTokenFromCookies(request);
        }
        // token不存在  说明没登录或者token过期
        if (StringTools.isEmpty(token)){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        // reid中token信息为空
          // 要么是redis中token过期 tokenKey没了 但是前端忘了将请求头token删除
          // tokenKey还有 但是用户信息被删除了
        if (StringTools.isEmpty(redisComponent.getTokenInfoForAdmin(token))){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        // 登录了 放行
        return true;
    }
    /**
     * 从cookie中取token
     * @param 
     * @return 
     * @author liuhd
     * 2024/12/6 1:29
     */
    
    private String getTokenFromCookies(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return  null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(Constants.TOKEN_ADMIN)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
