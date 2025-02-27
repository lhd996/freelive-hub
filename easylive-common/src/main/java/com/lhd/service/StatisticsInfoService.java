package com.lhd.service;

import java.util.List;
import java.util.Map;

import com.lhd.entity.query.StatisticsInfoQuery;
import com.lhd.entity.po.StatisticsInfo;
import com.lhd.entity.vo.PaginationResultVO;


/**
 *  业务接口
 */
public interface StatisticsInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<StatisticsInfo> findListByParam(StatisticsInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(StatisticsInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<StatisticsInfo> findListByPage(StatisticsInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(StatisticsInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<StatisticsInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<StatisticsInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(StatisticsInfo bean,StatisticsInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(StatisticsInfoQuery param);

	/**
	 * 根据StatisticsDateAndUserIdAndDataType查询对象
	 */
	StatisticsInfo getStatisticsInfoByStatisticsDateAndUserIdAndDataType(String statisticsDate,String userId,Integer dataType);


	/**
	 * 根据StatisticsDateAndUserIdAndDataType修改
	 */
	Integer updateStatisticsInfoByStatisticsDateAndUserIdAndDataType(StatisticsInfo bean,String statisticsDate,String userId,Integer dataType);


	/**
	 * 根据StatisticsDateAndUserIdAndDataType删除
	 */
	Integer deleteStatisticsInfoByStatisticsDateAndUserIdAndDataType(String statisticsDate,String userId,Integer dataType);

	/**
	 * @description: 统计所有用户的数据
	 * @param
	 * @return
	 * @author liuhd
	 * 2025/1/15 10:13
	 */
	void statisticsData();

	/**
	 * @description: 获取用户所有的统计信息(实时)
	 * @param userId
	 * @return java.util.Map<java.lang.Integer, java.lang.Integer>
	 * @author liuhd
	 * 2025/1/15 21:55
	 */
	Map<String,Integer> getStatisticsInfoActualTime(String userId);

	/**
	 * @description: 获取列表中各个类型的统计数据数量
	 * @param query
	 * @return java.util.List<com.lhd.entity.po.StatisticsInfo>
	 * @author liuhd
	 * 2025/1/15 23:50
	 */
	List<StatisticsInfo> findListTotalInfoByParam(StatisticsInfoQuery query);

	/**
	 * @description: 获取系统用户总数
	 * @param query
	 * @return java.util.List<com.lhd.entity.po.StatisticsInfo>
	 * @author liuhd
	 * 2025/1/15 23:49
	 */
	List<StatisticsInfo> findUserCountTotalInfoByParam(StatisticsInfoQuery query);
}