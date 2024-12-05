package com.lhd.entity.constants;

/**
 * @Author: liuhd
 * @Date: 2024/12/5 00:30
 * @Description: 常量类,封装可能会用到的常量
 */
public class Constants {
    public static final Integer TEN = 10;
    public static final Integer ZERO = 0;
    public static final Integer ONE = 1;
    public static final Integer LENGTH_10 = 10;
    // 区分不同项目
    public static final String REDIS_KEY_PREFIX = "easylive:";
    // redis验证码前缀
    public static final String REDIS_KEY_CHECK_CODE = REDIS_KEY_PREFIX +  "checkcode:";
    // redis key过期时间,一分钟
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 1000 * 60;
    // redis key过期时间,一天
    public static final Integer REDIS_KEY_EXPIRES_ONE_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;
    // redis key过期时间,七天
    public static final Integer REDIS_KEY_EXPIRES_SEVEN_DAY = REDIS_KEY_EXPIRES_ONE_DAY * 7;

    // 一天
    public static final Integer TIME_SECONDS_DAY = REDIS_KEY_EXPIRES_ONE_DAY / 1000;
    // redis token的前缀
    public static final String REDIS_KEY_TOKEN_WEB = REDIS_KEY_PREFIX + "token:web:";
    // token
    public static final String TOKEN_WEB = "token";
    // 密码正则
    // 至少有8个字符长。
    //至少包含一个小写字母。
    //至少包含一个大写字母。
    //至少包含一个数字。
    //至少包含一个特殊字符（如 !, @, #, $, %, ^, &, *, (, )）。
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$";
}
