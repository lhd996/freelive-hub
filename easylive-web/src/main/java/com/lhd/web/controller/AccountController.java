package com.lhd.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lhd.component.RedisComponent;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.query.UserInfoQuery;
import com.lhd.entity.po.UserInfo;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.exception.BusinessException;
import com.lhd.redis.RedisUtils;
import com.lhd.service.UserInfoService;
import com.lhd.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.apache.http.HttpResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 注册登录 Controller
 */
@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisComponent redisComponent;

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
     * 用户注册
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/4 23:32
     */
    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty @Email String email,
                               @NotEmpty @Size(max = 20) String nickName,
                               @NotEmpty String registerPassword,
                               @NotEmpty String checkCodeKey,
                               @NotEmpty String checkCode) {
        try {
            // 校验用户验证码
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
                throw new BusinessException("图片验证码错误");
            }
            //  注册
            userInfoService.register(email, nickName, registerPassword);
            return getSuccessResponseVO(null);
        } finally {
            // 删除图片验证码
            redisComponent.cleanCheckCode(checkCodeKey);
        }
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
                            @NotEmpty @Email String email,
                            @NotEmpty String password,
                            @NotEmpty String checkCodeKey,
                            @NotEmpty String checkCode) {
        try {
            // 校验用户验证码
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
                throw new BusinessException("图片验证码错误");
            }
            // 获取ip
            String ip = getIpAddr();
            // 登录逻辑 并将用户信息存入redis(token值 : tokenUserInfoDto)
            TokenUserInfoDto tokenUserInfoDto = userInfoService.login(email, password, ip);
            // 将token存入cookie,有些请求前端做不到将token放在请求头中,我们只能放在cookie中,然后从cookie中拿
            saveTokenToCookie(response, tokenUserInfoDto.getToken());
            // TODO 设置粉丝数 关注数
            // 将token送给前端,前端要把token放在请求头中
            return getSuccessResponseVO(tokenUserInfoDto);
        } finally {
            // 删除redis中图片验证码
            redisComponent.cleanCheckCode(checkCodeKey);
            // 删除redis中该用户的以前的token
            // 从cookie中取出token
            String token = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(Constants.TOKEN_WEB)) {
                        token = cookie.getName();
                        break;
                    }
                }
                // 从redis中删除这个用户的token
                if (!StringTools.isEmpty(token)) {
                    redisComponent.cleanToken(token);
                }
            }
        }
    }

    /**
     * 用于用户状态的自动续期
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 19:30
     */
    @RequestMapping("/autoLogin")
    public ResponseVO autoLogin(HttpServletRequest request, HttpServletResponse response) {
        // 从redis中拿token信息
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        // 长时间没登录 过期了 去登陆吧
        if (tokenUserInfoDto == null) {
            return getSuccessResponseVO(null);
        }
        // 如果只剩下一天了 重新申请token
        if (tokenUserInfoDto.getExpireAt() - System.currentTimeMillis() < Constants.REDIS_KEY_EXPIRES_ONE_DAY) {
            // 重新申请token 此时tokenUserInfoDto中的token属性以及过期时间被更新
            redisComponent.saveTokenInfo(tokenUserInfoDto);
            // 再将token放到cookie中 下次自动携带在请求头中
            saveTokenToCookie(response, tokenUserInfoDto.getToken());
        }
        // TODO 设置粉丝，关注，硬币数
        // 把最新的tokenUserInfoDto传回去
        return getSuccessResponseVO(tokenUserInfoDto);
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