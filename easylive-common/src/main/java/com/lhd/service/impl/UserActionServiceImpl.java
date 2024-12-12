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
import com.lhd.entity.query.UserActionQuery;
import com.lhd.entity.po.UserAction;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.query.SimplePage;
import com.lhd.mappers.UserActionMapper;
import com.lhd.service.UserActionService;
import com.lhd.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 用户行为 点赞、评论 业务接口实现
 */
@Service("userActionService")
public class UserActionServiceImpl implements UserActionService {

    @Resource
    private UserActionMapper<UserAction, UserActionQuery> userActionMapper;
    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<UserAction> findListByParam(UserActionQuery param) {
        return this.userActionMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(UserActionQuery param) {
        return this.userActionMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<UserAction> findListByPage(UserActionQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<UserAction> list = this.findListByParam(param);
        PaginationResultVO<UserAction> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(UserAction bean) {
        return this.userActionMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<UserAction> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userActionMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<UserAction> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userActionMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(UserAction bean, UserActionQuery param) {
        StringTools.checkParam(param);
        return this.userActionMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(UserActionQuery param) {
        StringTools.checkParam(param);
        return this.userActionMapper.deleteByParam(param);
    }

    /**
     * 根据ActionId获取对象
     */
    @Override
    public UserAction getUserActionByActionId(Integer actionId) {
        return this.userActionMapper.selectByActionId(actionId);
    }

    /**
     * 根据ActionId修改
     */
    @Override
    public Integer updateUserActionByActionId(UserAction bean, Integer actionId) {
        return this.userActionMapper.updateByActionId(bean, actionId);
    }

    /**
     * 根据ActionId删除
     */
    @Override
    public Integer deleteUserActionByActionId(Integer actionId) {
        return this.userActionMapper.deleteByActionId(actionId);
    }

    /**
     * 根据VideoIdAndCommentIdAndActionTypeAndUserId获取对象
     */
    @Override
    public UserAction getUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId, Integer commentId, Integer actionType, String userId) {
        return this.userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(videoId, commentId, actionType, userId);
    }

    /**
     * 根据VideoIdAndCommentIdAndActionTypeAndUserId修改
     */
    @Override
    public Integer updateUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(UserAction bean, String videoId, Integer commentId, Integer actionType, String userId) {
        return this.userActionMapper.updateByVideoIdAndCommentIdAndActionTypeAndUserId(bean, videoId, commentId, actionType, userId);
    }

    /**
     * 根据VideoIdAndCommentIdAndActionTypeAndUserId删除
     */
    @Override
    public Integer deleteUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId, Integer commentId, Integer actionType, String userId) {
        return this.userActionMapper.deleteByVideoIdAndCommentIdAndActionTypeAndUserId(videoId, commentId, actionType, userId);
    }

    /**
     * @param userAction
     * @return
     * @description: 保存用户动作
     * @author liuhd
     * 2024/12/12 22:03
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAction(UserAction userAction) {
        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(userAction.getVideoId());
        if (videoInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 获得当前动作的类型
        UserActionTypeEnum userActionTypeEnum = UserActionTypeEnum.getByType(userAction.getActionType());
        if (userActionTypeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        userAction.setVideoUserId(videoInfo.getUserId());
        // 获取用户行为
        UserAction dbUserAction = userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(userAction.getVideoId(),
                userAction.getCommentId(), userAction.getActionType(), userAction.getUserId());
        userAction.setActionTime(new Date());

        // 对于每一种行为
        switch (userActionTypeEnum) {
            case VIDEO_LIKE:
            case VIDEO_COLLECT:
                // 没有这个行为
                if (dbUserAction == null) {
                    // 说明是做
                    userActionMapper.insert(userAction);
                } else {
                    // 说明是取消
                    userActionMapper.deleteByActionId(dbUserAction.getActionId());
                }
                // 做是1 取消是-1
                Integer changeCount = dbUserAction == null ? Constants.ONE : -Constants.ONE;
                // 然后更新视频表
                videoInfoMapper.updateCountInfo(userAction.getVideoId(), userActionTypeEnum.getField(), changeCount);
                if (userActionTypeEnum == UserActionTypeEnum.VIDEO_COLLECT){
                    // TODO 更新es的收藏
                }
                break;
            case VIDEO_COIN:
                // 不能给自己投币
                if (videoInfo.getUserId().equals(userAction.getUserId())){
                    throw new BusinessException("UP主不能给自己投币");
                }
                // 如果投过币了
                if (dbUserAction != null){
                    throw new BusinessException("一个视频只能投一次币");
                }
                // 没投过 将行为插入用户行为表  增加视频投币数 增加UP硬币数 减少用户硬币数
                  // 减少自己的硬币
                Integer userRes = userInfoMapper.updateCoinCountInfo(userAction.getUserId(), -userAction.getActionCount());
                if (userRes == 0){
                    throw new BusinessException("您的硬币不够");
                }
                  // UP主增加硬币
                Integer UPRes = userInfoMapper.updateCoinCountInfo(videoInfo.getUserId(), userAction.getActionCount());
                if (UPRes == 0){
                    throw new BusinessException("投币失败");
                }
                  // 将行为插入用户行为表
                userActionMapper.insert(userAction);
                  // 增加视频投币数
                videoInfoMapper.updateCountInfo(videoInfo.getVideoId(), userActionTypeEnum.getField(), userAction.getActionCount());
                break;
        }
    }
}