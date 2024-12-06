package com.lhd.admin.controller;

import com.lhd.component.RedisComponent;
import com.lhd.entity.config.AppConfig;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.exception.BusinessException;
import com.lhd.service.UserInfoService;
import com.lhd.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册登录 Controller
 */
@RestController
@RequestMapping("account")
@Validated
public class AccountController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;
    /**
     * 用于用户登录/注册时将验证码数据存入redis
     *
     * @param
     * @return VO
     * @author liuhd
     * 2024/12/4 22:15
     */
    @RequestMapping("/checkCode")
    public ResponseVO checkCode() {
        // 生成一个图片对象
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(100, 42);
        // 获取图片表示的文本(验证码)
        String checkCode = arithmeticCaptcha.text();
        // 将验证码存入redis
        String checkCodeKey = redisComponent.saveCheckCode(checkCode);
        // 将当前用户的验证码key与图片与返回前端
        Map<String, Object> map = new HashMap<>();
        // 图片以Base64编码
        String checkCodeBase64 = arithmeticCaptcha.toBase64();
        map.put("checkCodeKey", checkCodeKey);
        map.put("checkCode", checkCodeBase64);
        return getSuccessResponseVO(map);
    }
    /**
     * 用户登录
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 19:28
     */
    @RequestMapping("/login")
    public ResponseVO login(HttpServletRequest request, HttpServletResponse response,
                            @NotEmpty String account,
                            @NotEmpty String password,
                            @NotEmpty String checkCodeKey,
                            @NotEmpty String checkCode) {
        try {
            // 校验用户验证码
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
                throw new BusinessException("图片验证码错误");
            }
            // 去配置文件中读取管理员信息 比较是否一致
            if (!account.equals(appConfig.getAdminAccount()) || !password.equals(StringTools.encodeByMd5(appConfig.getAdminPassword()))){
                throw new BusinessException("账号或密码错误");
            }
            // 将管理员信息存入redis
            String token = redisComponent.saveTokenInfoForAdmin(account);
            // 存入cookie 以后会把token自动会携带过来
            saveTokenToCookie(response,token);
            // 将信息返回前端 实际上还应该将token返回给前端 但是这个项目里前端从cookie中取了token
            return getSuccessResponseVO(account);
        } finally {
            // 删除redis中图片验证码
            redisComponent.cleanCheckCode(checkCodeKey);
            // 删除redis中该用户的以前的token
            // 从cookie中取出token
            String token = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(Constants.TOKEN_ADMIN)) {
                        token = cookie.getName();
                        break;
                    }
                }
                // 从redis中删除这个用户的token
                if (!StringTools.isEmpty(token)) {
                    redisComponent.cleanTokenForAdmin(token);
                }
            }
        }
    }

    /**
     * 用户退出登录
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 19:34
     */
    @RequestMapping("/logout")
    public ResponseVO logout(HttpServletRequest request, HttpServletResponse response) {
        // 清除redis与cookie中的cookie
        cleanCookies(request, response);
        return getSuccessResponseVO(null);
    }
}