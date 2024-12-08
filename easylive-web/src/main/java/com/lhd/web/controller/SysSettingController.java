package com.lhd.web.controller;

import com.lhd.component.RedisComponent;
import com.lhd.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: liuhd
 * @Date: 2024/12/8 15:14
 * @Description:
 */
@RestController
@RequestMapping("/sysSetting")
public class SysSettingController extends ABaseController {
    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/getSetting")
    public ResponseVO getSetting(){
        return getSuccessResponseVO(redisComponent.getSysSettingDto());
    }
}
