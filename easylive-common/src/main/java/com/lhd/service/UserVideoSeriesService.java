package com.lhd.service;

import java.util.List;

import com.lhd.entity.query.UserVideoSeriesQuery;
import com.lhd.entity.po.UserVideoSeries;
import com.lhd.entity.vo.PaginationResultVO;


/**
 * 用户视频序列归档 业务接口
 */
public interface UserVideoSeriesService {

	/**
	 * 根据条件查询列表
	 */
	List<UserVideoSeries> findListByParam(UserVideoSeriesQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserVideoSeriesQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserVideoSeries> findListByPage(UserVideoSeriesQuery param);

	/**
	 * 新增
	 */
	Integer add(UserVideoSeries bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserVideoSeries> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserVideoSeries> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserVideoSeries bean,UserVideoSeriesQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserVideoSeriesQuery param);

	/**
	 * 根据SeriesId查询对象
	 */
	UserVideoSeries getUserVideoSeriesBySeriesId(Integer seriesId);


	/**
	 * 根据SeriesId修改
	 */
	Integer updateUserVideoSeriesBySeriesId(UserVideoSeries bean,Integer seriesId);


	/**
	 * 根据SeriesId删除
	 */
	Integer deleteUserVideoSeriesBySeriesId(Integer seriesId);
	/**
	 * @description: 获取所有分类
	 * @param userId
	 * @return java.util.List<com.lhd.entity.po.UserVideoSeries>
	 * @author liuhd
	 * 2025/1/12 20:59
	 */

	List<UserVideoSeries> getUserAllSeries(String userId);

	/**
	 * @description: 保存或新增合集
	 * @param videoSeries 一个分类
	 * @param videoIds 分类中的视频
	 * @return
	 * @author liuhd
	 * 2025/1/12 20:59
	 */

	void saveUserVideoSeries(UserVideoSeries videoSeries,String videoIds);

	// 将合集中的视频加入合集视频表中
	void saveSeriesVideo(String userId,Integer seriesId,String videoIds);
	/**
	 * @description: 删除合集中的一个视频
	 * @param userId
	 * @param videoId
	 * @param seriesId
	 * @return
	 * @author liuhd
	 * 2025/1/13 13:01
	 */

	void delSeriesVideo(String userId,Integer seriesId,String videoId);
	/**
	 * @description: 删除一个视频分类
	 * @param userId
	 * @param seriesId
	 * @return
	 * @author liuhd
	 * 2025/1/13 13:11
	 */

	void delVideoSeries(String userId,Integer seriesId);
	/**
	 * @description: 改变分类排序
	 * @param userId 用户id
	 * @param seriesIds 分类ids
	 * @return
	 * @author liuhd
	 * 2025/1/13 13:33
	 */

	void changeVideoSeriesSort(String userId,String seriesIds);
	/**
	 * @description: 获取视频分类以及每个分类的前5个视频
	 * @param videoSeriesQuery
	 * @return java.util.List<com.lhd.entity.po.UserVideoSeries>
	 * @author liuhd
	 * 2025/1/13 14:03
	 */

	List<UserVideoSeries> findListWithVideoList(UserVideoSeriesQuery videoSeriesQuery);
}