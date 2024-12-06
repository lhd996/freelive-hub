package com.lhd.admin.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.lhd.entity.vo.ResponseVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liuhd
 * @Date: 2024/12/6 00:46
 * @Description:
 */
@RestController
@RequestMapping("/category")
public class CategoryController extends ABaseController {
    @RequestMapping("/loadCategory")
    public ResponseVO loadDataList(){
        return getSuccessResponseVO(null);
    }
}
