package com.lhd.admin.controller;

import com.lhd.component.RedisComponent;
import com.lhd.entity.dto.SysSettingDto;
import com.lhd.entity.query.UserInfoQuery;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: liuhd
 * @Date: 2025/1/8 13:55
 * @Description:
 */
@RestController
@RequestMapping("/setting")
@Validated
@Slf4j
public class SettingController extends ABaseController{
    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/getSetting")
    public ResponseVO getSetting(){
       return getSuccessResponseVO(redisComponent.getSysSettingDto());
    }


    @RequestMapping("/saveSetting")
    public ResponseVO saveSetting(SysSettingDto sysSettingDto){
        redisComponent.saveSettingDto(sysSettingDto);
        return getSuccessResponseVO(null);
    }
}
