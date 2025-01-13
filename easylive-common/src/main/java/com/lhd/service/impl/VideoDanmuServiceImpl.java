package com.lhd.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.lhd.entity.constants.Constants;
import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.entity.enums.UserActionTypeEnum;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.query.VideoInfoQuery;
import com.lhd.exception.BusinessException;
import com.lhd.mappers.VideoInfoMapper;
import org.springframework.stereotype.Service;

import com.lhd.entity.enums.PageSize;
import com.lhd.entity.query.VideoDanmuQuery;
import com.lhd.entity.po.VideoDanmu;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.query.SimplePage;
import com.lhd.mappers.VideoDanmuMapper;
import com.lhd.service.VideoDanmuService;
import com.lhd.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 视频弹幕 业务接口实现
 */
@Service("videoDanmuService")
public class VideoDanmuServiceImpl implements VideoDanmuService {

    @Resource
    private VideoDanmuMapper<VideoDanmu, VideoDanmuQuery> videoDanmuMapper;
    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<VideoDanmu> findListByParam(VideoDanmuQuery param) {
        return this.videoDanmuMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(VideoDanmuQuery param) {
        return this.videoDanmuMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<VideoDanmu> findListByPage(VideoDanmuQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<VideoDanmu> list = this.findListByParam(param);
        PaginationResultVO<VideoDanmu> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(VideoDanmu bean) {
        return this.videoDanmuMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<VideoDanmu> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoDanmuMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<VideoDanmu> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoDanmuMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(VideoDanmu bean, VideoDanmuQuery param) {
        StringTools.checkParam(param);
        return this.videoDanmuMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(VideoDanmuQuery param) {
        StringTools.checkParam(param);
        return this.videoDanmuMapper.deleteByParam(param);
    }

    /**
     * 根据DanmuId获取对象
     */
    @Override
    public VideoDanmu getVideoDanmuByDanmuId(Integer danmuId) {
        return this.videoDanmuMapper.selectByDanmuId(danmuId);
    }

    /**
     * 根据DanmuId修改
     */
    @Override
    public Integer updateVideoDanmuByDanmuId(VideoDanmu bean, Integer danmuId) {
        return this.videoDanmuMapper.updateByDanmuId(bean, danmuId);
    }

    /**
     * 根据DanmuId删除
     */
    @Override
    public Integer deleteVideoDanmuByDanmuId(Integer danmuId) {
        return this.videoDanmuMapper.deleteByDanmuId(danmuId);
    }

    /**
     * @param videoDanmu
     * @return
     * @description: 发送弹幕
     * @author liuhd
     * 2024/12/12 18:53
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoDamu(VideoDanmu videoDanmu) {
        // 查视频互动配置 是否关闭弹幕
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoDanmu.getVideoId());
		// 虚假信息 抛异常
		if (videoInfo == null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		// 如果up关闭弹幕
        // null:无限制 0:关闭评论 1:关闭弹幕 0,1:都关闭
		if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains(Constants.ONE.toString())){
			throw new BusinessException("up主关闭了弹幕功能");
		}
        // 插入数据库
        this.videoDanmuMapper.insert(videoDanmu);
        // 弹幕加1
        this.videoInfoMapper.updateCountInfo(videoDanmu.getVideoId(), UserActionTypeEnum.VIDEO_DANMU.getField(),Constants.ONE);
        //TODO 更新es

	}

    @Override
    public void deleteDanmu(Integer danmuId, String userId) {
        VideoDanmu videoDanmu = videoDanmuMapper.selectByDanmuId(danmuId);
        if (videoDanmu == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoDanmu.getVideoId());
        if (videoInfo == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (userId != null && !videoInfo.getUserId().equals(userId)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        videoDanmuMapper.deleteByDanmuId(danmuId);
    }
}