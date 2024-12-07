package com.lhd.service;

import java.util.List;

import com.lhd.entity.query.CategoryInfoQuery;
import com.lhd.entity.po.CategoryInfo;
import com.lhd.entity.vo.PaginationResultVO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * 分类信息 业务接口
 */
public interface CategoryInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<CategoryInfo> findListByParam(CategoryInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(CategoryInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<CategoryInfo> findListByPage(CategoryInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(CategoryInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<CategoryInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<CategoryInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(CategoryInfo bean,CategoryInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(CategoryInfoQuery param);

	/**
	 * 根据CategoryId查询对象
	 */
	CategoryInfo getCategoryInfoByCategoryId(Integer categoryId);


	/**
	 * 根据CategoryId修改
	 */
	Integer updateCategoryInfoByCategoryId(CategoryInfo bean,Integer categoryId);


	/**
	 * 根据CategoryId删除
	 */
	Integer deleteCategoryInfoByCategoryId(Integer categoryId);


	/**
	 * 根据CategoryCode查询对象
	 */
	CategoryInfo getCategoryInfoByCategoryCode(String categoryCode);


	/**
	 * 根据CategoryCode修改
	 */
	Integer updateCategoryInfoByCategoryCode(CategoryInfo bean,String categoryCode);


	/**
	 * 根据CategoryCode删除
	 */
	Integer deleteCategoryInfoByCategoryCode(String categoryCode);
	
	/**
	 * 新增Category
	 * @param 
	 * @return 
	 * @author liuhd
	 * 2024/12/7 15:16
	 */
	
	void saveCategory(CategoryInfo bean);
	
	/**
	 * 删除category by id
	 * @param 
	 * @return 
	 * @author liuhd
	 * 2024/12/7 16:09
	 */
	
	void delCategoryById(Integer categoryId);
	
	/**
	 * 改变Sort号 实现排序
	 * @param 
	 * @return 
	 * @author liuhd
	 * 2024/12/7 21:02
	 */
	
	void changeSort(String categoryIds);
}