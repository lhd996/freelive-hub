package com.lhd.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import com.lhd.entity.config.AppConfig;
import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.entity.po.*;
import com.lhd.entity.query.*;
import com.lhd.exception.BusinessException;
import com.lhd.mappers.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.lhd.entity.enums.PageSize;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.service.VideoInfoService;
import com.lhd.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 视频信息 业务接口实现
 */
@Service("videoInfoService")
@Slf4j
public class VideoInfoServiceImpl implements VideoInfoService {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;
    @Resource
    private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostQuery> videoInfoPostMapper;
    @Resource
    private VideoInfoFileMapper<VideoInfoFile, VideoInfoFileQuery> videoInfoFileMapper;
    @Resource
    private VideoInfoFilePostMapper<VideoInfoFilePost, VideoInfoFilePostQuery> videoInfoFilePostMapper;
    @Resource
    private VideoDanmuMapper<VideoDanmu, VideoDanmuQuery> videoDanmuMapper;
    @Resource
    private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;
    @Resource
    private AppConfig appConfig;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<VideoInfo> findListByParam(VideoInfoQuery param) {
        return this.videoInfoMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(VideoInfoQuery param) {
        return this.videoInfoMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<VideoInfo> findListByPage(VideoInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<VideoInfo> list = this.findListByParam(param);
        PaginationResultVO<VideoInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(VideoInfo bean) {
        return this.videoInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<VideoInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<VideoInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(VideoInfo bean, VideoInfoQuery param) {
        StringTools.checkParam(param);
        return this.videoInfoMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(VideoInfoQuery param) {
        StringTools.checkParam(param);
        return this.videoInfoMapper.deleteByParam(param);
    }

    /**
     * 根据VideoId获取对象
     */
    @Override
    public VideoInfo getVideoInfoByVideoId(String videoId) {
        return this.videoInfoMapper.selectByVideoId(videoId);
    }

    /**
     * 根据VideoId修改
     */
    @Override
    public Integer updateVideoInfoByVideoId(VideoInfo bean, String videoId) {
        return this.videoInfoMapper.updateByVideoId(bean, videoId);
    }

    /**
     * 根据VideoId删除
     */
    @Override
    public Integer deleteVideoInfoByVideoId(String videoId) {
        return this.videoInfoMapper.deleteByVideoId(videoId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeInteraction(String videoId, String userId, String interaction) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setInteraction(interaction);
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setUserId(userId);
        videoInfoQuery.setVideoId(videoId);
        videoInfoMapper.updateByParam(videoInfo, videoInfoQuery);

        VideoInfoPost videoInfoPost = new VideoInfoPost();
        videoInfoPost.setInteraction(interaction);
        VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
        videoInfoQuery.setUserId(userId);
        videoInfoQuery.setVideoId(videoId);
        videoInfoPostMapper.updateByParam(videoInfoPost, videoInfoPostQuery);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(String videoId, String userId) {
        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null || userId != null && !userId.equals(videoInfo.getUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        // 删除视频
        videoInfoMapper.deleteByVideoId(videoId);
        videoInfoPostMapper.deleteByVideoId(videoId);
        // TODO 减少用户硬币
        // TODO 删除es信息

        // 异步删除 弹幕 评论 分p 文件
        executorService.execute(() -> {
            // 删除分p
            VideoInfoFileQuery videoInfoFileQuery = new VideoInfoFileQuery();
            videoInfoFileQuery.setVideoId(videoId);
            videoInfoFileMapper.deleteByParam(videoInfoFileQuery);

            VideoInfoFilePostQuery filePostQuery = new VideoInfoFilePostQuery();
            filePostQuery.setVideoId(videoId);
            videoInfoFilePostMapper.deleteByParam(filePostQuery);

            // 删除弹幕
            VideoDanmuQuery videoDanmuQuery = new VideoDanmuQuery();
            videoDanmuQuery.setVideoId(videoId);
            videoDanmuMapper.deleteByParam(videoDanmuQuery);

            // 删除评论
            VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
            videoCommentQuery.setVideoId(videoId);
            videoCommentMapper.deleteByParam(videoCommentQuery);


            // 删除文件
            List<VideoInfoFile> videoInfoFileList = videoInfoFileMapper.selectList(videoInfoFileQuery);
            for (VideoInfoFile file : videoInfoFileList) {
                try {
                    FileUtils.deleteDirectory(new File(appConfig.getProjectFolder() + file.getFilePath()));
                } catch (IOException e) {
                    log.error("删除文件失败,文件路径：{}",file.getFilePath());
                }
            }
        });
    }
}