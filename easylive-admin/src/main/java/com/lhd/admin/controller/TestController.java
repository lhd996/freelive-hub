package com.lhd.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liuhd
 * @Date: 2024/12/4 15:18
 * @Description:
 */
@RestController
public class TestController {
    @RequestMapping("/test")
    public String test(){
        return "admin模块启动！！";
    }
}
