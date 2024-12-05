package com.lhd.utils;

import com.lhd.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Stack;


public class StringTools {

    public static void checkParam(Object param) {
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            boolean notEmpty = false;
            for (Field field : fields) {
                String methodName = "get" + StringTools.upperCaseFirstLetter(field.getName());
                Method method = param.getClass().getMethod(methodName);
                Object object = method.invoke(param);
                if (object != null && object instanceof java.lang.String && !StringTools.isEmpty(object.toString())
                        || object != null && !(object instanceof java.lang.String)) {
                    notEmpty = true;
                    break;
                }
            }
            if (!notEmpty) {
                throw new BusinessException("多参数更新，删除，必须有非空条件");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("校验参数是否为空失败");
        }
    }

    public static String upperCaseFirstLetter(String field) {
        if (isEmpty(field)) {
            return field;
        }
        //如果第二个字母是大写，第一个字母不大写
        if (field.length() > 1 && Character.isUpperCase(field.charAt(1))) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 生成一个随机字符串
     * @param len:字符串长度
     * @return 随机字符串
     * @author liuhd
     * 2024/12/5 12:30
     */

    public static String getRandomString(Integer len) {
        return RandomStringUtils.random(len,true,true);
    }

    /**
     * 生成一个随机数字字符串
     * @param len:数字字符串长度
     * @return 数字字符串
     * @author liuhd
     * 2024/12/5 12:31
     */

    public static String getRandomNumberString(Integer len) {
        return RandomStringUtils.random(len,false,true);
    }
    /**
     * 字符串md5加密
     * @param originString 原始字符串
     * @return 加密后的字符串
     * @author liuhd
     * 2024/12/5 12:37
     */

    public static String encodeByMd5(String originString){
        return StringTools.isEmpty(originString) ? null : DigestUtils.md5Hex(originString);
    }
}
