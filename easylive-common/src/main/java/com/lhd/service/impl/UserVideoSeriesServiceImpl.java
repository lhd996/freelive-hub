package com.lhd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.entity.po.UserVideoSeriesVideo;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.query.UserVideoSeriesVideoQuery;
import com.lhd.entity.query.VideoInfoQuery;
import com.lhd.exception.BusinessException;
import com.lhd.mappers.UserVideoSeriesVideoMapper;
import com.lhd.mappers.VideoInfoMapper;
import org.springframework.stereotype.Service;

import com.lhd.entity.enums.PageSize;
import com.lhd.entity.query.UserVideoSeriesQuery;
import com.lhd.entity.po.UserVideoSeries;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.query.SimplePage;
import com.lhd.mappers.UserVideoSeriesMapper;
import com.lhd.service.UserVideoSeriesService;
import com.lhd.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 用户视频序列归档 业务接口实现
 */
@Service("userVideoSeriesService")
public class UserVideoSeriesServiceImpl implements UserVideoSeriesService {

	@Resource
	private UserVideoSeriesMapper<UserVideoSeries, UserVideoSeriesQuery> userVideoSeriesMapper;
	@Resource
	private VideoInfoMapper<VideoInfo,VideoInfoQuery> videoInfoMapper;
	@Resource
	private UserVideoSeriesVideoMapper<UserVideoSeriesVideo, UserVideoSeriesVideoQuery> userVideoSeriesVideoMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserVideoSeries> findListByParam(UserVideoSeriesQuery param) {
		return this.userVideoSeriesMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserVideoSeriesQuery param) {
		return this.userVideoSeriesMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserVideoSeries> findListByPage(UserVideoSeriesQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserVideoSeries> list = this.findListByParam(param);
		PaginationResultVO<UserVideoSeries> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserVideoSeries bean) {
		return this.userVideoSeriesMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserVideoSeries> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userVideoSeriesMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserVideoSeries> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userVideoSeriesMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserVideoSeries bean, UserVideoSeriesQuery param) {
		StringTools.checkParam(param);
		return this.userVideoSeriesMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserVideoSeriesQuery param) {
		StringTools.checkParam(param);
		return this.userVideoSeriesMapper.deleteByParam(param);
	}

	/**
	 * 根据SeriesId获取对象
	 */
	@Override
	public UserVideoSeries getUserVideoSeriesBySeriesId(Integer seriesId) {
		return this.userVideoSeriesMapper.selectBySeriesId(seriesId);
	}

	/**
	 * 根据SeriesId修改
	 */
	@Override
	public Integer updateUserVideoSeriesBySeriesId(UserVideoSeries bean, Integer seriesId) {
		return this.userVideoSeriesMapper.updateBySeriesId(bean, seriesId);
	}

	/**
	 * 根据SeriesId删除
	 */
	@Override
	public Integer deleteUserVideoSeriesBySeriesId(Integer seriesId) {
		return this.userVideoSeriesMapper.deleteBySeriesId(seriesId);
	}

	@Override
	public List<UserVideoSeries> getUserAllSeries(String userId) {
		return userVideoSeriesMapper.selectUserAllSeries(userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveUserVideoSeries(UserVideoSeries bean, String videoIds) {
		// 新增但是没有视频
		if (bean.getSeriesId() == null && StringTools.isEmpty(videoIds)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		// 新增
		if (bean.getSeriesId() == null){
			// 校验
			checkVideoIds(bean.getUserId(), videoIds);
			bean.setUpdateTime(new Date());
			// 新增的放在最前面 它有最大的sort
			bean.setSort(this.userVideoSeriesMapper.selectMaxSort(bean.getUserId()) + 1);
			this.userVideoSeriesMapper.insert(bean);
			// 新增分类中的视频
			this.saveSeriesVideo(bean.getUserId(), bean.getSeriesId(),videoIds);
		}else{
			// 修改
			UserVideoSeriesQuery videoSeriesQuery = new UserVideoSeriesQuery();
			videoSeriesQuery.setUserId(bean.getUserId());
			videoSeriesQuery.setSeriesId(bean.getSeriesId());

			this.userVideoSeriesMapper.updateByParam(bean,videoSeriesQuery);
		}

	}
	// 校验列表中的视频是否有问题
	private void checkVideoIds(String userId,String videoIds){
		String[] videoIdArray = videoIds.split(",");
		VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
		videoInfoQuery.setVideoIdArray(videoIdArray);
		videoInfoQuery.setUserId(userId);

		Integer count = videoInfoMapper.selectCount(videoInfoQuery);
		if (count != videoIdArray.length){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
	}
	/**
	 * @description: 新增或修改分类中的视频
	 * @param userId
	 * @param seriesId
	 * @param videoIds
	 * @return
	 * @author liuhd
	 * 2025/1/13 13:28
	 */

	@Override
	public void saveSeriesVideo(String userId, Integer seriesId, String videoIds) {
		UserVideoSeries userVideoSeries = getUserVideoSeriesBySeriesId(seriesId);
		if (userVideoSeries == null ||  !userVideoSeries.getUserId().equals(userId)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		checkVideoIds(userId,videoIds);

		String[] videoIdArray = videoIds.split(",");
		Integer sort = this.userVideoSeriesVideoMapper.selectMaxSort(seriesId);

		// 将视频加入合集表中
		List<UserVideoSeriesVideo> seriesVideoList  = new ArrayList<>();
		for (String videoId : videoIdArray) {
			UserVideoSeriesVideo videoSeriesVideo = new UserVideoSeriesVideo();
			videoSeriesVideo.setVideoId(videoId);
			videoSeriesVideo.setSort(++ sort);
			videoSeriesVideo.setSeriesId(seriesId);
			videoSeriesVideo.setUserId(userId);
			seriesVideoList.add(videoSeriesVideo);
		}
		this.userVideoSeriesVideoMapper.insertOrUpdateBatch(seriesVideoList);
	}

	@Override
	public void delSeriesVideo(String userId,Integer seriesId,String videoId) {
		UserVideoSeriesVideoQuery videoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
		videoSeriesVideoQuery.setUserId(userId);
		videoSeriesVideoQuery.setSeriesId(seriesId);
		videoSeriesVideoQuery.setVideoId(videoId);
		userVideoSeriesVideoMapper.deleteByParam(videoSeriesVideoQuery);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delVideoSeries(String userId, Integer seriesId) {
		UserVideoSeriesQuery videoSeriesQuery = new UserVideoSeriesQuery();
		videoSeriesQuery.setUserId(userId);
		videoSeriesQuery.setSeriesId(seriesId);
		Integer count = userVideoSeriesMapper.deleteByParam(videoSeriesQuery);
		if (count == 0){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserVideoSeriesVideoQuery seriesVideoQuery = new UserVideoSeriesVideoQuery();
		seriesVideoQuery.setSeriesId(seriesId);
		seriesVideoQuery.setUserId(userId);
		userVideoSeriesVideoMapper.deleteByParam(seriesVideoQuery);
	}

	@Override
	public void changeVideoSeriesSort(String userId, String seriesIds) {
		String[] seriesIdArray = seriesIds.split(",");
		// seriesIds是排好序的分类顺序
		// 以下是构造更新条件以及更新的sort
		List<UserVideoSeries> videoSeriesList = new ArrayList<>();
		Integer sort = 0;
		for (String seriesId : seriesIdArray) {
			UserVideoSeries videoSeries = new UserVideoSeries();
			videoSeries.setUserId(userId);
			videoSeries.setSort(++ sort);
			videoSeries.setSeriesId(Integer.parseInt(seriesId));
			videoSeriesList.add(videoSeries);
		}
		userVideoSeriesMapper.changeSort(videoSeriesList);
	}

	@Override
	public List<UserVideoSeries> findListWithVideoList(UserVideoSeriesQuery videoSeriesQuery) {
		return userVideoSeriesMapper.seriesListWithVideo(videoSeriesQuery);
	}
}