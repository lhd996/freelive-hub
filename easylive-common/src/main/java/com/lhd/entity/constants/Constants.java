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
    public static final Integer LENGTH_15 = 15;
    public static final Integer LENGTH_30 = 30;
    public static final Long MB_SIZE = 1024 * 1024L;
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
      // for web服务
    public static final String REDIS_KEY_TOKEN_WEB = REDIS_KEY_PREFIX + "token:web:";
      // for admin服务
    public static final String REDIS_KEY_TOKEN_ADMIN = REDIS_KEY_PREFIX + "token:admin:";
    // token for web server
    public static final String TOKEN_WEB = "token";
    // token for admin server
    public static final String TOKEN_ADMIN = "adminToken";

    public static final String REDIS_KEY_CATEGORY_LIST = REDIS_KEY_PREFIX + "category:list:";

    public static final String FILE_FOLDER = "file/";
    public static final String FILE_COVER = "cover/";
    public static final String FILE_VIDEO = "video/";
    public static final String FILE_FOLDER_TEMP = "tmp/";

    public static final String IMAGE_THUMBNAIL_SUFFIX = "_thumbnail.jpg";

    public static final String REDIS_KEY_UPLOADING_FILE = REDIS_KEY_PREFIX + "uploading:";

    //系统设置
    public static final String REDIS_KEY_SYS_SETTING = REDIS_KEY_PREFIX + "sysSetting:";
    // 密码正则
    // 至少有8个字符长。
    //至少包含一个小写字母。
    //至少包含一个大写字母。
    //至少包含一个数字。
    //至少包含一个特殊字符（如 !, @, #, $, %, ^, &, *, (, )）。
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$";
}
