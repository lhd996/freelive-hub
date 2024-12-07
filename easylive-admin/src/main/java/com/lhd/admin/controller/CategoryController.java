package com.lhd.admin.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.lhd.entity.po.CategoryInfo;
import com.lhd.entity.query.CategoryInfoQuery;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.CategoryInfoService;
import org.springframework.stereotype.Controller;
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
    @RequestMapping("/loadCategory")
    public ResponseVO loadDataList(CategoryInfoQuery query){
        // 设置排序方式 会拼接在order by后面 形成 order by sort asc 可以设一个常量避免硬编码
        query.setOrderBy("sort asc");
        // 需要将列表展示为树形
        query.setConvertToTree(true);

        // 查
        List<CategoryInfo> categoryInfoList = categoryInfoService.findListByParam(query);
        // 返回结果
        return getSuccessResponseVO(categoryInfoList);

    }

    /**
     * 保存一个category
     * @param categoryId 如果前端传值 则为更新 否则为修改
     * @return
     * @author liuhd
     * 2024/12/7 15:53
     */

    @RequestMapping("/saveCategory")
    public ResponseVO saveCategory(@NotNull Integer categoryId,
                                   @NotNull String categoryCode,
                                   @NotEmpty String categoryName,
                                   @NotEmpty Integer pCategoryId,
                                   String icon,
                                   String background){
        CategoryInfo categoryInfo = new CategoryInfo();
        categoryInfo.setCategoryId(categoryId);
        categoryInfo.setCategoryCode(categoryCode);
        categoryInfo.setCategoryName(categoryName);
        categoryInfo.setpCategoryId(pCategoryId);
        categoryInfo.setIcon(icon);
        categoryInfo.setBackground(background);

        categoryInfoService.saveCategory(categoryInfo);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delCategory")
    public ResponseVO delCategory(@NotNull Integer categoryId){
        categoryInfoService.delCategoryById(categoryId);
        return getSuccessResponseVO(null);
    }
    /**
     * 改变分类顺序
     * @param categoryIds 排好序的分区id
     * @return
     * @author liuhd
     * 2024/12/7 18:00
     */
    @RequestMapping("/changeSort")
    public ResponseVO changeSort(@NotEmpty String categoryIds){
        categoryInfoService.changeSort(categoryIds);
        return getSuccessResponseVO(null);
    }
}
