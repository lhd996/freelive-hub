package com.lhd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.lhd.component.RedisComponent;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.enums.*;
import com.lhd.entity.po.VideoInfoFilePost;
import com.lhd.entity.query.VideoInfoFilePostQuery;
import com.lhd.entity.query.VideoInfoFileQuery;
import com.lhd.exception.BusinessException;
import com.lhd.mappers.VideoInfoFilePostMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import com.lhd.entity.query.VideoInfoPostQuery;
import com.lhd.entity.po.VideoInfoPost;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.query.SimplePage;
import com.lhd.mappers.VideoInfoPostMapper;
import com.lhd.service.VideoInfoPostService;
import com.lhd.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 视频信息 业务接口实现
 */
@Service("videoInfoPostService")
public class VideoInfoPostServiceImpl implements VideoInfoPostService {

    @Resource
    private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostQuery> videoInfoPostMapper;
    @Resource
    private VideoInfoFilePostMapper<VideoInfoFilePost, VideoInfoFilePostQuery> videoInfoFilePostMapper;
    @Resource
    private RedisComponent redisComponent;


    /**
     * 根据条件查询列表
     */
    @Override
    public List<VideoInfoPost> findListByParam(VideoInfoPostQuery param) {
        return this.videoInfoPostMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(VideoInfoPostQuery param) {
        return this.videoInfoPostMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<VideoInfoPost> findListByPage(VideoInfoPostQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<VideoInfoPost> list = this.findListByParam(param);
        PaginationResultVO<VideoInfoPost> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(VideoInfoPost bean) {
        return this.videoInfoPostMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<VideoInfoPost> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoInfoPostMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<VideoInfoPost> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.videoInfoPostMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(VideoInfoPost bean, VideoInfoPostQuery param) {
        StringTools.checkParam(param);
        return this.videoInfoPostMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(VideoInfoPostQuery param) {
        StringTools.checkParam(param);
        return this.videoInfoPostMapper.deleteByParam(param);
    }

    /**
     * 根据VideoId获取对象
     */
    @Override
    public VideoInfoPost getVideoInfoPostByVideoId(String videoId) {
        return this.videoInfoPostMapper.selectByVideoId(videoId);
    }

    /**
     * 根据VideoId修改
     */
    @Override
    public Integer updateVideoInfoPostByVideoId(VideoInfoPost bean, String videoId) {
        return this.videoInfoPostMapper.updateByVideoId(bean, videoId);
    }

    /**
     * 根据VideoId删除
     */
    @Override
    public Integer deleteVideoInfoPostByVideoId(String videoId) {
        return this.videoInfoPostMapper.deleteByVideoId(videoId);
    }

    /**
     * 保存视频信息
     *
     * @param videoInfoPost 发布视频信息 uploadFileList 发布视频的分p
     * @return
     * @author liuhd
     * 2024/12/8 17:30
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoInfo(VideoInfoPost videoInfoPost, List<VideoInfoFilePost> uploadFileList) {
        // 如果当前视频的分p数量大于系统值
        if (uploadFileList.size() > redisComponent.getSysSettingDto().getVideoPCount()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 如果当前视频id不为空 说明我们做更新操作
        if (!StringTools.isEmpty(videoInfoPost.getVideoId())) {
            // 如果这个id不在视频数据库中 说明id有问题
            VideoInfoPost videoInfoPostDB = videoInfoPostMapper.selectByVideoId(videoInfoPost.getVideoId());
            if (videoInfoPostDB == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            // 如果在视频数据库中 但是是正在转码或者待审核状态
            if (ArrayUtils.contains(new Integer[]{VideoStatusEnum.STATUS0.getStatus(), VideoStatusEnum.STATUS2.getStatus()}, videoInfoPostDB.getStatus())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        Date cur = new Date();
        String videoId = videoInfoPost.getVideoId();
        List<VideoInfoFilePost> delFileList = new ArrayList<>();
        List<VideoInfoFilePost> addFileList = uploadFileList;

        // 如果视频id为空 插入视频数据库
        if (StringTools.isEmpty(videoId)) {
            videoId = StringTools.getRandomString(Constants.LENGTH_10);
            videoInfoPost.setUserId(videoId);
            videoInfoPost.setCreateTime(cur);
            videoInfoPost.setLastUpdateTime(cur);
            videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
            videoInfoPostMapper.insert(videoInfoPost);
        } else {
            // 否则修改视频数据库
            // 先查出文件库中所有文件/分p
            VideoInfoFilePostQuery filePostQuery = new VideoInfoFilePostQuery();
            filePostQuery.setVideoId(videoId);
            // 想一想 如果不设置uid 另一个直接发请求到后端请求别人的视频会怎样
            filePostQuery.setUserId(videoInfoPost.getUserId());
            // 取出分p数据
            List<VideoInfoFilePost> dbfilePostList = videoInfoFilePostMapper.selectList(filePostQuery);
            // 将用户界面现有的分p 转成Map(uploadId : 分p)
            Map<String, VideoInfoFilePost> uploadFileMap = uploadFileList.stream().collect(Collectors.toMap(item -> item.getUploadId(), Function.identity(), (data1, data2) -> data2));

            // 遍历数据库中的分p
            // 分p有没有修改名字
            Boolean updateFileName = false;
            for (VideoInfoFilePost filePost : dbfilePostList) {
                // 根据数据库中的uploadId去前端找分p
                VideoInfoFilePost updateFile = uploadFileMap.get(filePost.getUploadId());
                // 数据库中有 前端没有
                if (updateFile == null) {
                    // 说明在前端被用户删了 我们应该在数据库中也删除
                    delFileList.add(filePost);
                    // 否则数据库中与前端都有 看看有没有修改分p名
                } else if (!updateFile.getFileName().equals(filePost.getFileName())) {
                    // 修改了
                    updateFileName = true;
                }
            }
            // 将用户界面中没有入库分p的收集起来  没有入库 <==> getFileId() == null
            addFileList = uploadFileList.stream().filter(item -> item.getFileId() == null).collect(Collectors.toList());


            // 视频是否修改
            Boolean changeVideoInfo = changeVideoInfo(videoInfoPost);

            // 如果新增文件了
            if (!addFileList.isEmpty()) {
                // 状态为转码中
                videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
                // 没新增文件 但是改了视频内容
            } else if (changeVideoInfo || updateFileName) {
                // 状态为审核
                videoInfoPost.setStatus(VideoStatusEnum.STATUS2.getStatus());
            }
            // 更新视频数据库
            videoInfoPost.setLastUpdateTime(cur);
            videoInfoPostMapper.updateByVideoId(videoInfoPost, videoId);
        }

        if (!delFileList.isEmpty()){
            // 删除文件数据库中的文件
            List<String> delFileIdList = delFileList.stream().map(item -> item.getFileId()).collect(Collectors.toList());
            videoInfoFilePostMapper.deleteBatchByFileIds(delFileIdList,videoInfoPost.getUserId());


            List<String> delFilePathList = delFileList.stream().map(item -> item.getFilePath()).collect(Collectors.toList());
            // 将路径文件添加到队列中 异步删除
            // 我有个问题 点击×的时候不是直接从文件系统中删除了吗？？？
            redisComponent.addFile2DelQueue(videoId,delFilePathList);
        }

        // 更新文件数据库
        Integer index = 0;
        for (VideoInfoFilePost uploadFile : uploadFileList) {
            uploadFile.setFileIndex(++ index);
            uploadFile.setVideoId(videoId);
            uploadFile.setUserId(videoInfoPost.getUserId());
            // 新增
            if (uploadFile.getFileId() == null){
                uploadFile.setFileId(StringTools.getRandomString(Constants.LENGTH_10 * 2));
                uploadFile.setUpdateType(VideoFileUpdateTypeEnum.UPDATE.getStatus());
                uploadFile.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());
            }
        }
        videoInfoFilePostMapper.insertOrUpdateBatch(uploadFileList);
        if (addFileList != null && !addFileList.isEmpty()){
            for (VideoInfoFilePost file : addFileList) {
                file.setUserId(videoInfoPost.getUserId());
                file.setVideoId(videoId);
            }
            // 将待转码文件加入队列 异步转码
            redisComponent.addFile2TransferQueue(addFileList);
        }
    }

    /**
     * 判断视频是否更新过
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/8 18:37
     */

    private Boolean changeVideoInfo(VideoInfoPost videoInfoPost) {
        VideoInfoPost dbVideoInfoPost = videoInfoPostMapper.selectByVideoId(videoInfoPost.getVideoId());
        // 标题 封面 标签 简介 是否变化
        return !videoInfoPost.getVideoName().equals(dbVideoInfoPost.getVideoName())
                || !videoInfoPost.getVideoCover().equals(dbVideoInfoPost.getVideoCover())
                || !videoInfoPost.getTags().equals(dbVideoInfoPost.getTags())
                || !videoInfoPost.getIntroduction().equals(dbVideoInfoPost.getIntroduction());
    }
}