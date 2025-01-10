package com.lhd.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.lhd.entity.constants.Constants;
import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.entity.enums.UserActionTypeEnum;
import com.lhd.entity.po.UserInfo;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.query.UserInfoQuery;
import com.lhd.entity.query.VideoInfoQuery;
import com.lhd.exception.BusinessException;
import com.lhd.mappers.UserInfoMapper;
import com.lhd.mappers.VideoInfoMapper;
import org.springframework.stereotype.Service;

import com.lhd.entity.enums.PageSize;
import com.lhd.entity.query.VideoCommentQuery;
import com.lhd.entity.po.VideoComment;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.query.SimplePage;
import com.lhd.mappers.VideoCommentMapper;
import com.lhd.service.VideoCommentService;
import com.lhd.utils.StringTools;


/**
 * 评论 业务接口实现
 */
@Service("videoCommentService")
public class VideoCommentServiceImpl implements VideoCommentService {

	@Resource
	private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;
	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<VideoComment> findListByParam(VideoCommentQuery param) {
		if (param.getLoadChildren() != null && param.getLoadChildren()) {
			return this.videoCommentMapper.selectListWithChildren(param);
		}
		return this.videoCommentMapper.selectList(param);
	}


	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(VideoCommentQuery param) {
		return this.videoCommentMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<VideoComment> findListByPage(VideoCommentQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<VideoComment> list = this.findListByParam(param);
		PaginationResultVO<VideoComment> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(VideoComment bean) {
		return this.videoCommentMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<VideoComment> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoCommentMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<VideoComment> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoCommentMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(VideoComment bean, VideoCommentQuery param) {
		StringTools.checkParam(param);
		return this.videoCommentMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(VideoCommentQuery param) {
		StringTools.checkParam(param);
		return this.videoCommentMapper.deleteByParam(param);
	}

	/**
	 * 根据CommentId获取对象
	 */
	@Override
	public VideoComment getVideoCommentByCommentId(Integer commentId) {
		return this.videoCommentMapper.selectByCommentId(commentId);
	}

	/**
	 * 根据CommentId修改
	 */
	@Override
	public Integer updateVideoCommentByCommentId(VideoComment bean, Integer commentId) {
		return this.videoCommentMapper.updateByCommentId(bean, commentId);
	}

	/**
	 * 根据CommentId删除
	 */
	@Override
	public Integer deleteVideoCommentByCommentId(Integer commentId) {
		return this.videoCommentMapper.deleteByCommentId(commentId);
	}


	@Override
	public void postComment(VideoComment comment, Integer replyCommentId) {
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(comment.getVideoId());
		// 参数错误
		if (videoInfo == null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		// 关闭互动
		if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains(Constants.ZERO.toString())){
			throw new BusinessException("UP主已关闭评论区");
		}
		if (replyCommentId != null){
			// 拿到回复的评论对象
			VideoComment replyComment = getVideoCommentByCommentId(replyCommentId);
			if (replyComment == null || !replyComment.getVideoId().equals(comment.getVideoId())){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			// 如果我们回复的评论是一级评论
			if (replyComment.getpCommentId() == 0){
				// 那么当前评论的父评论就是它
				comment.setpCommentId(replyComment.getCommentId());
			}else {
				// 如果回复评论不是一级评论 那必然回复的是二级评论
				 //那么父评论应该是这个二级评论上的一级评论
				/*
					区分父评论与回复评论
					二级评论的父评论是一级评论 回复评论可以是一级评论 也可以是一级评论下的二级评论
					一级评论的父评论id是0  没有回复评论
				 */
				comment.setpCommentId(replyComment.getpCommentId());
				// 作用是拿到对方的信息
				comment.setReplyUserId(replyComment.getUserId());
			}
			UserInfo userInfo = userInfoMapper.selectByUserId(replyComment.getUserId());
			// 刘浩东回复了xxx
			comment.setReplyNickName(userInfo.getNickName());
		}else {
			// 说明是一级评论
			comment.setpCommentId(0);
		}
		comment.setPostTime(new Date());
		comment.setVideoUserId(videoInfo.getUserId());
		this.videoCommentMapper.insert(comment);
		// 增加评论数量（只算一级）
		if (comment.getpCommentId() == 0){
			this.videoInfoMapper.updateCountInfo(comment.getVideoId(), UserActionTypeEnum.VIDEO_COMMENT.getField(), 1);
		}
	}
}