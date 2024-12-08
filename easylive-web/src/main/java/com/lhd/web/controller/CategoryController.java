package com.lhd.web.controller;

import com.lhd.entity.po.CategoryInfo;
import com.lhd.entity.query.CategoryInfoQuery;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.CategoryInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: liuhd
 * @Date: 2024/12/6 00:46
 * @Description:
 */

@RestController
@RequestMapping("/category")
public class CategoryController extends ABaseController {
    @Resource
    private CategoryInfoService categoryInfoService;
    /**
     * 根据查询参数查询分类
     * @param
     * @return
     * @author liuhd
     * 2024/12/7 14:44
     */
    @RequestMapping("/loadAllCategory")
    public ResponseVO loadAllCategory(){
        List<CategoryInfo> categoryInfoList = categoryInfoService.getAllCategoryList();
        // 返回结果
        return getSuccessResponseVO(categoryInfoList);
    }
}
