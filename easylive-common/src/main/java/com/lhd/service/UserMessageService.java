package com.lhd.service;

import java.util.List;

import com.lhd.entity.dto.UserMessageCountDto;
import com.lhd.entity.enums.MessageTypeEnum;
import com.lhd.entity.query.UserMessageQuery;
import com.lhd.entity.po.UserMessage;
import com.lhd.entity.vo.PaginationResultVO;


/**
 * 用户消息表 业务接口
 */
public interface UserMessageService {

	/**
	 * 根据条件查询列表
	 */
	List<UserMessage> findListByParam(UserMessageQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserMessageQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserMessage> findListByPage(UserMessageQuery param);

	/**
	 * 新增
	 */
	Integer add(UserMessage bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserMessage> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserMessage> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserMessage bean,UserMessageQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserMessageQuery param);

	/**
	 * 根据MessageId查询对象
	 */
	UserMessage getUserMessageByMessageId(Integer messageId);


	/**
	 * 根据MessageId修改
	 */
	Integer updateUserMessageByMessageId(UserMessage bean,Integer messageId);


	/**
	 * 根据MessageId删除
	 */
	Integer deleteUserMessageByMessageId(Integer messageId);

	/**
	 * @description: 点赞，收藏，评论，系统消息
	 * @param videoId 哪个视频
	 * @param sendUserId 发送消息的人
	 * @param messageTypeEnum 消息的类型
	 * @param content 消息内容
	 * @param replyCommentId 被回复的评论id,即自己的评论id
	 * @return
	 * @author liuhd
	 * 2025/1/14 18:02
	 */

	void saveUserMessage(String videoId, String sendUserId, MessageTypeEnum messageTypeEnum,String content,Integer replyCommentId);

	/**
	 * @description: 获取各种未读消息的数量
	 * @param userId
	 * @return java.util.List<com.lhd.entity.dto.UserMessageCountDto>
	 * @author liuhd
	 * 2025/1/14 20:32
	 */
	List<UserMessageCountDto> getMessageTypeNoReadCount(String userId);
}