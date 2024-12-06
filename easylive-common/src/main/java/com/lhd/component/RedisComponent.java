package com.lhd.component;

import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.redis.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Author: liuhd
 * @Date: 2024/12/5 00:24
 * @Description: 封装redis相关的业务操作
 */
@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;
    /**
     * 保存验证码
     * @param 
     * @return 
     * @author liuhd
     * 2024/12/5 0:27
     */
    
    public String saveCheckCode(String code){
        // 随机一个验证码的key,防止不同用户使用同一个key导致验证码的覆盖
        String checkCodeKey = UUID.randomUUID().toString();
        // 将验证码存入redis
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey,code,Constants.REDIS_KEY_EXPIRES_ONE_MIN * 10);
        return checkCodeKey;
    }
    /**
     * 根据验证key获取验证码
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 12:07
     */

    public String getCheckCode(String checkCoedKey){
        return (String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCoedKey);
    }

    /**
     * 根据key清除验证码
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 15:54
     */

    public void cleanCheckCode(String checkCodeKey){
        redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    /**
     * 将token存入redis
     * @param 
     * @return 
     * @author liuhd
     * 2024/12/5 16:00
     */
    

    public void saveTokenInfo(TokenUserInfoDto tokenUserInfoDto){
        // UUID作为键
        String token = UUID.randomUUID().toString();
        // 设置过期时间
        tokenUserInfoDto.setExpireAt(System.currentTimeMillis() + Constants.REDIS_KEY_EXPIRES_SEVEN_DAY);
        // 设置token
        tokenUserInfoDto.setToken(token);
        // 将token信息存入redis
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_WEB + token,tokenUserInfoDto,Constants.REDIS_KEY_EXPIRES_SEVEN_DAY);
    }

    /**
     * 从redis中删除token
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 17:26
     */

    public void cleanToken(String token){
        redisUtils.delete(Constants.REDIS_KEY_TOKEN_WEB + token);
    }

    /**
     * 根据token取出token信息
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 19:16
     */

    public TokenUserInfoDto getTokenInfo(String token){
        return (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_TOKEN_WEB + token);
    }

    /**
     * 将admin端的token信息存入redis，这次只需要传一个账户即可
     * @param account 账户
     * @return token UUID
     * @author liuhd
     * 2024/12/5 23:34
     */
    
    public String saveTokenInfoForAdmin(String account){
        // UUID作为键
        String token = UUID.randomUUID().toString();
        // 将token信息存入redis
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_ADMIN + token,account,Constants.REDIS_KEY_EXPIRES_ONE_DAY);
        return token;
    }

    /**
     * 从redis中删除token
     * @param token
     * @return
     * @author liuhd
     * 2024/12/5 23:51
     */

    public void cleanTokenForAdmin(String token){
        redisUtils.delete(Constants.REDIS_KEY_TOKEN_ADMIN + token);
    }

    /**
     * 从redis中取出token信息 for admin server
     * @param
     * @return
     * @author liuhd
     * 2024/12/6 1:35
     */

    public String getTokenInfoForAdmin(String token){
        return (String) redisUtils.get(Constants.REDIS_KEY_TOKEN_ADMIN + token);
    }
}
