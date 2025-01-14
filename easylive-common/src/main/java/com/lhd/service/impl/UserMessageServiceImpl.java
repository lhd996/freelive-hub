package com.lhd.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.lhd.entity.dto.UserMessageCountDto;
import com.lhd.entity.dto.UserMessageExtendDto;
import com.lhd.entity.enums.MessageReadTypeEnum;
import com.lhd.entity.enums.MessageTypeEnum;
import com.lhd.entity.po.VideoComment;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.po.VideoInfoPost;
import com.lhd.entity.query.*;
import com.lhd.mappers.VideoCommentMapper;
import com.lhd.mappers.VideoInfoMapper;
import com.lhd.mappers.VideoInfoPostMapper;
import com.lhd.utils.JsonUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lhd.entity.enums.PageSize;
import com.lhd.entity.po.UserMessage;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.mappers.UserMessageMapper;
import com.lhd.service.UserMessageService;
import com.lhd.utils.StringTools;


/**
 * 用户消息表 业务接口实现
 */
@Service("userMessageService")
public class UserMessageServiceImpl implements UserMessageService {

	@Resource
	private UserMessageMapper<UserMessage, UserMessageQuery> userMessageMapper;
	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;
	@Resource
	private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;
	@Resource
	private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostQuery> videoInfoPostMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserMessage> findListByParam(UserMessageQuery param) {
		return this.userMessageMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserMessageQuery param) {
		return this.userMessageMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserMessage> findListByPage(UserMessageQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserMessage> list = this.findListByParam(param);
		PaginationResultVO<UserMessage> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserMessage bean) {
		return this.userMessageMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserMessage> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userMessageMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserMessage> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userMessageMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserMessage bean, UserMessageQuery param) {
		StringTools.checkParam(param);
		return this.userMessageMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserMessageQuery param) {
		StringTools.checkParam(param);
		return this.userMessageMapper.deleteByParam(param);
	}

	/**
	 * 根据MessageId获取对象
	 */
	@Override
	public UserMessage getUserMessageByMessageId(Integer messageId) {
		return this.userMessageMapper.selectByMessageId(messageId);
	}

	/**
	 * 根据MessageId修改
	 */
	@Override
	public Integer updateUserMessageByMessageId(UserMessage bean, Integer messageId) {
		return this.userMessageMapper.updateByMessageId(bean, messageId);
	}

	/**
	 * 根据MessageId删除
	 */
	@Override
	public Integer deleteUserMessageByMessageId(Integer messageId) {
		return this.userMessageMapper.deleteByMessageId(messageId);
	}

	@Override
	@Async
	public void saveUserMessage(String videoId, String sendUserId, MessageTypeEnum messageTypeEnum, String content, Integer replyCommentId) {
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoId);
		if (videoInfo == null){
			return;
		}
		// 设置扩展信息
		UserMessageExtendDto extendDto = new UserMessageExtendDto();
		extendDto.setMessageContent(content);

		// 视频的主人
		String userId = videoInfo.getUserId();

		// 收藏与点赞，已经记录的，不再记录
		if (ArrayUtils.contains(new Integer[]{MessageTypeEnum.LIKE.getType(),MessageTypeEnum.COLLECTION.getType()},messageTypeEnum.getType())){
			UserMessageQuery userMessageQuery = new UserMessageQuery();
			userMessageQuery.setMessageType(messageTypeEnum.getType());
			userMessageQuery.setUserId(userId);
			userMessageQuery.setSendUserId(sendUserId);
			Integer count = userMessageMapper.selectCount(userMessageQuery);
			if (count > 0){
				return;
			}
		}
		UserMessage userMessage = new UserMessage();
		userMessage.setVideoId(videoId);
		userMessage.setReadType(MessageReadTypeEnum.NO_READ.getType());
		userMessage.setCreateTime(new Date());
		userMessage.setMessageType(messageTypeEnum.getType());
		userMessage.setSendUserId(sendUserId);
		// 评论特殊处理 应该是发布评论的人接受消息
		if (replyCommentId != null){
			VideoComment comment = videoCommentMapper.selectByCommentId(replyCommentId);
			if (comment != null){
				userId = comment.getUserId();
				// 你发布的评论的内容
				extendDto.setMessageContentReply(comment.getContent());
			}
		}
		userMessage.setUserId(userId);
		if (userId.equals(sendUserId)){
			return;
		}
		// 系统消息
		if (MessageTypeEnum.SYS == messageTypeEnum){
			VideoInfoPost videoInfoPost = videoInfoPostMapper.selectByVideoId(videoId);
			// 设置状态 因为要通知用户视频状态
			extendDto.setAuditStatus(videoInfoPost.getStatus());
		}

		userMessage.setExtendJson(JsonUtils.convertObj2Json(extendDto));
		userMessageMapper.insert(userMessage);
	}

	@Override
	public List<UserMessageCountDto> getMessageTypeNoReadCount(String userId) {
		return userMessageMapper.getMessageTypeNoReadCount(userId);
	}
}