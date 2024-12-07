package com.lhd.mappers;

import com.lhd.entity.po.CategoryInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类信息 数据库操作接口
 */
public interface CategoryInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据CategoryId更新
	 */
	 Integer updateByCategoryId(@Param("bean") T t,@Param("categoryId") Integer categoryId);


	/**
	 * 根据CategoryId删除
	 */
	 Integer deleteByCategoryId(@Param("categoryId") Integer categoryId);


	/**
	 * 根据CategoryId获取对象
	 */
	 T selectByCategoryId(@Param("categoryId") Integer categoryId);


	/**
	 * 根据CategoryCode更新
	 */
	 Integer updateByCategoryCode(@Param("bean") T t,@Param("categoryCode") String categoryCode);


	/**
	 * 根据CategoryCode删除
	 */
	 Integer deleteByCategoryCode(@Param("categoryCode") String categoryCode);


	/**
	 * 根据CategoryCode获取对象
	 */
	 T selectByCategoryCode(@Param("categoryCode") String categoryCode);


	Integer selectMaxSort(@Param("pCategoryId") Integer pCategoryId);

	/**
	 * 根据params批量更新sort
	 * @param
	 * @return
	 * @author liuhd
	 * 2024/12/7 21:11
	 */

	void updateSortBatch(@Param("params") List<CategoryInfo> params);
}