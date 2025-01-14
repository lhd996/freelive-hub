package com.lhd.web.controller;

import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.po.VideoDanmu;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.query.VideoDanmuQuery;
import com.lhd.entity.query.VideoPlayHistoryQuery;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.service.VideoDanmuService;
import com.lhd.service.VideoInfoService;
import com.lhd.service.VideoPlayHistoryService;
import com.lhd.web.annotation.GlobalInterceptor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;


@RestController
@RequestMapping("/history")
public class VideoPlayHistoryController extends ABaseController{

	@Resource
	private VideoPlayHistoryService videoPlayHistoryService;

	/**
	 * @description: 获取历史记录
	 * @param request
	 * @param pageNo
	 * @return com.lhd.entity.vo.ResponseVO
	 * @author liuhd
	 * 2025/1/14 21:47
	 */
	@RequestMapping("/loadHistory")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO loadHistory(HttpServletRequest request,Integer pageNo){
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
		VideoPlayHistoryQuery historyQuery = new VideoPlayHistoryQuery();
		historyQuery.setUserId(tokenUserInfoDto.getUserId());
		historyQuery.setPageNo(pageNo);
		historyQuery.setOrderBy("last_update_time desc");
		historyQuery.setQueryVideoDetail(true);

		return getSuccessResponseVO(videoPlayHistoryService.findListByPage(historyQuery));
	}

	/**
	 * @description: 删除所有历史记录
	 * @param request
	 * @return com.lhd.entity.vo.ResponseVO
	 * @author liuhd
	 * 2025/1/14 22:03
	 */
	@RequestMapping("/cleanHistory")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO cleanHistory(HttpServletRequest request){
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
		VideoPlayHistoryQuery historyQuery = new VideoPlayHistoryQuery();
		historyQuery.setUserId(tokenUserInfoDto.getUserId());
		videoPlayHistoryService.deleteByParam(historyQuery);
		return getSuccessResponseVO(null);
	}
	/**
	 * @description: 删除单个记录
	 * @param request
	 * @param videoId
	 * @return com.lhd.entity.vo.ResponseVO
	 * @author liuhd
	 * 2025/1/14 22:05
	 */
	@RequestMapping("/delHistory")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO cleanHistory(HttpServletRequest request,@NotEmpty String videoId){
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
		videoPlayHistoryService.deleteVideoPlayHistoryByUserIdAndVideoId(tokenUserInfoDto.getUserId(),videoId);
		return getSuccessResponseVO(null);
	}

}