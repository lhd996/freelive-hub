package com.lhd.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lhd.annotation.GlobalInterceptor;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.query.VideoDanmuQuery;
import com.lhd.entity.po.VideoDanmu;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.exception.BusinessException;
import com.lhd.service.VideoDanmuService;
import com.lhd.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 视频弹幕 Controller
 */
@RestController("videoDanmuController")
@RequestMapping("/danmu")
public class VideoDanmuController extends ABaseController{

	@Resource
	private VideoDanmuService videoDanmuService;
	@Resource
	private VideoInfoService videoInfoService;
	/**
	 * @description: 发送弹幕
	 * @param request
	 * @param videoId
	 * @param fileId
	 * @param text
	 * @param mode 弹幕出现位置
	 * @param color
	 * @param time 弹幕发送时间
	 * @return com.lhd.entity.vo.ResponseVO
	 * @author liuhd
	 * 2024/12/12 18:51
	 */

	@RequestMapping("/postDanmu")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO postDanmu(HttpServletRequest request,
								@NotEmpty String videoId, @NotEmpty String fileId,
								@NotEmpty @Size(max = 200) String text, @NotNull Integer mode,
								@NotEmpty String color, @NotNull Integer time){

		VideoDanmu videoDanmu = new VideoDanmu();
		videoDanmu.setVideoId(videoId);
		videoDanmu.setFileId(fileId);
		videoDanmu.setText(text);
		videoDanmu.setMode(mode);
		videoDanmu.setColor(color);
		videoDanmu.setTime(time);
		videoDanmu.setUserId(getTokenUserInfoDto(request).getUserId());
		videoDanmu.setPostTime(new Date());

		videoDanmuService.saveVideoDamu(videoDanmu);
		return getSuccessResponseVO(null);
	}
	/**
	 * @description: 加载分p弹幕
	 * @param videoId 视频 id
	 * @param fileId 分p id
	 * @return com.lhd.entity.vo.ResponseVO
	 * @author liuhd
	 * 2024/12/12 21:18
	 */

	@RequestMapping("/loadDanmu")
	public ResponseVO loadDanmu(@NotEmpty String videoId, @NotEmpty String fileId){
		// 判断是否关闭弹幕
		VideoInfo videoInfo = this.videoInfoService.getVideoInfoByVideoId(videoId);
		if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains(Constants.ONE.toString())){
			return getSuccessResponseVO(new ArrayList<>());
		}
		VideoDanmuQuery videoDanmuQuery = new VideoDanmuQuery();
		videoDanmuQuery.setFileId(fileId);
		videoDanmuQuery.setOrderBy("danmu_id asc");
		return getSuccessResponseVO(videoDanmuService.findListByParam(videoDanmuQuery));
	}
}