package com.lhd.service;

import java.util.List;

import com.lhd.entity.query.VideoInfoQuery;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.vo.PaginationResultVO;


/**
 * 视频信息 业务接口
 */
public interface VideoInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<VideoInfo> findListByParam(VideoInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(VideoInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoInfo> findListByPage(VideoInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(VideoInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<VideoInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(VideoInfo bean,VideoInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(VideoInfoQuery param);

	/**
	 * 根据VideoId查询对象
	 */
	VideoInfo getVideoInfoByVideoId(String videoId);


	/**
	 * 根据VideoId修改
	 */
	Integer updateVideoInfoByVideoId(VideoInfo bean,String videoId);


	/**
	 * 根据VideoId删除
	 */
	Integer deleteVideoInfoByVideoId(String videoId);

	/**
	 * @description:
	 * @param videoId
	 * @param userId
	 * @param interaction
	 * @return
	 * @author liuhd
	 * 2025/1/13 16:26
	 */

	void changeInteraction(String videoId,String userId,String interaction);

	/**
	 * @description: 删除视频
	 * @param videoId
	 * @param userId
	 * @return
	 * @author liuhd
	 * 2025/1/13 16:34
	 */

	void deleteVideo(String videoId,String userId);

	/**
	 * @description: 给视频增加播放量
	 * @param videoId
	 * @return
	 * @author liuhd
	 * 2025/1/14 12:47
	 */
	void addReadCount(String videoId);
}