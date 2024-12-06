package com.lhd.admin.controller;

import java.util.List;

import com.lhd.entity.query.CategoryInfoQuery;
import com.lhd.entity.po.CategoryInfo;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.CategoryInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 分类信息 Controller
 */
@RestController("categoryInfoController")
@RequestMapping("/categoryInfo")
public class CategoryInfoController extends ABaseController{

	@Resource
	private CategoryInfoService categoryInfoService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(CategoryInfoQuery query){
		return getSuccessResponseVO(categoryInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(CategoryInfo bean) {
		categoryInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<CategoryInfo> listBean) {
		categoryInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<CategoryInfo> listBean) {
		categoryInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CategoryId查询对象
	 */
	@RequestMapping("/getCategoryInfoByCategoryId")
	public ResponseVO getCategoryInfoByCategoryId(Integer categoryId) {
		return getSuccessResponseVO(categoryInfoService.getCategoryInfoByCategoryId(categoryId));
	}

	/**
	 * 根据CategoryId修改对象
	 */
	@RequestMapping("/updateCategoryInfoByCategoryId")
	public ResponseVO updateCategoryInfoByCategoryId(CategoryInfo bean,Integer categoryId) {
		categoryInfoService.updateCategoryInfoByCategoryId(bean,categoryId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CategoryId删除
	 */
	@RequestMapping("/deleteCategoryInfoByCategoryId")
	public ResponseVO deleteCategoryInfoByCategoryId(Integer categoryId) {
		categoryInfoService.deleteCategoryInfoByCategoryId(categoryId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CategoryCode查询对象
	 */
	@RequestMapping("/getCategoryInfoByCategoryCode")
	public ResponseVO getCategoryInfoByCategoryCode(String categoryCode) {
		return getSuccessResponseVO(categoryInfoService.getCategoryInfoByCategoryCode(categoryCode));
	}

	/**
	 * 根据CategoryCode修改对象
	 */
	@RequestMapping("/updateCategoryInfoByCategoryCode")
	public ResponseVO updateCategoryInfoByCategoryCode(CategoryInfo bean,String categoryCode) {
		categoryInfoService.updateCategoryInfoByCategoryCode(bean,categoryCode);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CategoryCode删除
	 */
	@RequestMapping("/deleteCategoryInfoByCategoryCode")
	public ResponseVO deleteCategoryInfoByCategoryCode(String categoryCode) {
		categoryInfoService.deleteCategoryInfoByCategoryCode(categoryCode);
		return getSuccessResponseVO(null);
	}
}