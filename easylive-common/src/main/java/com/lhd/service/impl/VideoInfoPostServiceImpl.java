package com.lhd.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.lhd.component.RedisComponent;
import com.lhd.entity.config.AppConfig;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.UploadingFileDto;
import com.lhd.entity.enums.*;
import com.lhd.entity.po.VideoInfo;
import com.lhd.entity.po.VideoInfoFile;
import com.lhd.entity.po.VideoInfoFilePost;
import com.lhd.entity.query.*;
import com.lhd.exception.BusinessException;
import com.lhd.mappers.VideoInfoFileMapper;
import com.lhd.mappers.VideoInfoFilePostMapper;
import com.lhd.mappers.VideoInfoMapper;
import com.lhd.utils.CopyTools;
import com.lhd.utils.FFmpegUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.internal.properties.Field;
import org.springframework.stereotype.Service;

import com.lhd.entity.po.VideoInfoPost;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.mappers.VideoInfoPostMapper;
import com.lhd.service.VideoInfoPostService;
import com.lhd.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 视频信息 业务接口实现
 */
@Service("videoInfoPostService")
@Slf4j
public class VideoInfoPostServiceImpl implements VideoInfoPostService {

    @Resource
    private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostQuery> videoInfoPostMapper;
    @Resource
    private VideoInfoFilePostMapper<VideoInfoFilePost, VideoInfoFilePostQuery> videoInfoFilePostMapper;
    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;
    @Resource
    private VideoInfoFileMapper<VideoInfoFile, VideoInfoFileQuery> videoInfoFileMapper;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private AppConfig appConfig;
    @Resource
    private FFmpegUtils fFmpegUtils;

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
            videoInfoPost.setVideoId(videoId);
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

        if (!delFileList.isEmpty()) {
            // 删除文件数据库中的文件
            List<String> delFileIdList = delFileList.stream().map(item -> item.getFileId()).collect(Collectors.toList());
            videoInfoFilePostMapper.deleteBatchByFileIds(delFileIdList, videoInfoPost.getUserId());

            List<String> delFilePathList = delFileList.stream().map(item -> item.getFilePath()).collect(Collectors.toList());
            // 将路径文件添加到队列中 异步删除
              //  如果不是第一次保存 说明是编辑视频 不能直接把文件删了 如果删除了 但是审核没过 你文件就没了
              //  为啥可以删除数据库呢？ 因为正式表中还有文件路径啊 可以正常访问文件
              //  那就只能加入删除队列保存 等审核通过后才删除 -> 532行
            redisComponent.addFile2DelQueue(videoId, delFilePathList);
        }

        // 更新文件数据库
        Integer index = 0;
        for (VideoInfoFilePost uploadFile : uploadFileList) {
            uploadFile.setFileIndex(++index);
            uploadFile.setVideoId(videoId);
            uploadFile.setUserId(videoInfoPost.getUserId());
            // 新增
            if (uploadFile.getFileId() == null) {
                uploadFile.setFileId(StringTools.getRandomString(Constants.LENGTH_10 * 2));
                uploadFile.setUpdateType(VideoFileUpdateTypeEnum.UPDATE.getStatus());
                uploadFile.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());
            }
        }
        videoInfoFilePostMapper.insertOrUpdateBatch(uploadFileList);
        if (addFileList != null && !addFileList.isEmpty()) {
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
                || !videoInfoPost.getIntroduction().equals(dbVideoInfoPost.getIntroduction() == null ? "" : dbVideoInfoPost.getIntroduction());
    }

    /**
     * 文件转码
     * 将一个视频文件的分片从temp目录移动到video目录并转成ts切片
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/9 9:48
     */

    @Override
    public void transferVideoFile(VideoInfoFilePost videoInfoFilePost) {
        VideoInfoFilePost updateFilePost = new VideoInfoFilePost();
        try {
            // 取出redis中文件信息
            UploadingFileDto fileDto = redisComponent.getUploadVideoFileInfo(videoInfoFilePost.getUserId(), videoInfoFilePost.getUploadId());
            // 拿到视频文件目录
            String tempFilePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + fileDto.getFilePath();
            File tempFile = new File(tempFilePath);
            // 拷贝到真实目录
            String targetFilePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_VIDEO + fileDto.getFilePath();
            File targetFile = new File(targetFilePath);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            FileUtils.copyDirectory(tempFile, targetFile);
            // 删除临时目录
            FileUtils.forceDelete(tempFile);
            // 删除redis中的文件信息
            redisComponent.delVideoInfo(videoInfoFilePost.getUserId(), videoInfoFilePost.getUploadId());
            // 合并视频切片
             //在目标目录中创建的临时文件路径
            String tempVideoPath = targetFilePath + Constants.TEMP_VIDEO_NAME;
             // 将切片合并成一个文件(注意当前文件只是名字叫xx.mp4 但他不一定是MP4文件 因为用户上传的不一定是MP4文件 那么分片上的每一片也不是MP4格式的)
            this.union(targetFilePath,tempVideoPath,true);

            // 获取播放时长
            Integer duration = fFmpegUtils.getVideoInfoDuration(tempVideoPath);
            updateFilePost.setDuration(duration);
            updateFilePost.setFileSize(new File(tempFilePath).length());
            updateFilePost.setFilePath(Constants.FILE_VIDEO + fileDto.getFilePath());
            updateFilePost.setTransferResult(VideoFileTransferResultEnum.SUCCESS.getStatus());

            //将临时.mp4文件转成TS分片 TS分片才是真正的MP4文件
            this.convertVideo2Ts(tempVideoPath);

        } catch (Exception e) {
            log.error("文件转码失败", e);
            updateFilePost.setTransferResult(VideoFileTransferResultEnum.FAIL.getStatus());
        } finally {
            // 更新视频文件表
            videoInfoFilePostMapper.updateByUploadIdAndUserId(updateFilePost,videoInfoFilePost.getUploadId(),videoInfoFilePost.getUserId());

            // 查一下有没有转码失败的
            VideoInfoFilePostQuery filePostQuery = new VideoInfoFilePostQuery();
            filePostQuery.setVideoId(videoInfoFilePost.getVideoId());
            filePostQuery.setTransferResult(VideoFileTransferResultEnum.FAIL.getStatus());
            Integer failCount = videoInfoFilePostMapper.selectCount(filePostQuery);
            // 有的话
            if (failCount > 0){
                // 更新状态为转码失败（更新视频表）
                VideoInfoPost videoInfoPostUpdate = new VideoInfoPost();
                videoInfoPostUpdate.setStatus(VideoStatusEnum.STATUS1.getStatus());
                videoInfoPostMapper.updateByVideoId(videoInfoPostUpdate,videoInfoFilePost.getVideoId());
            }
            // 查一下有没有转码中的
            filePostQuery.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());
            Integer transferCount = videoInfoFilePostMapper.selectCount(filePostQuery);
            // 如果没有
            if (transferCount == 0){
                // 计算视频总时长 设置视频状态待审核（更新视频表）
                Integer duration = videoInfoFilePostMapper.sumDuration(videoInfoFilePost.getVideoId());
                VideoInfoPost videoInfoPostUpdate = new VideoInfoPost();
                videoInfoPostUpdate.setStatus(VideoStatusEnum.STATUS2.getStatus());
                videoInfoPostUpdate.setDuration(duration);
                videoInfoPostMapper.updateByVideoId(videoInfoPostUpdate,videoInfoFilePost.getVideoId());
            }
        }
    }

    private void convertVideo2Ts(String videoFilePath) {
        File videoFile = new File(videoFilePath);
        //创建同名切片目录
        File tsFolder = videoFile.getParentFile();
        String codec = fFmpegUtils.getVideoCodec(videoFilePath);
        //如果不是MP4则转码
        if (!Constants.VIDEO_CODE_H264.equals(codec)) {
            String tempFileName = videoFilePath + Constants.VIDEO_CODE_TEMP_FILE_SUFFIX;
            new File(videoFilePath).renameTo(new File(tempFileName));
            fFmpegUtils.convertHevc2Mp4(tempFileName, videoFilePath);
            new File(tempFileName).delete();
        }

        //视频转为ts切片
        fFmpegUtils.convertVideo2Ts(tsFolder, videoFilePath);

        //删除视频文件
        videoFile.delete();
    }
    /** 文件合并
     * @param dirPath 起始目录路径 toFilePath 目的文件路径 delSource是否删除源文件
     * @return
     * @author liuhd
     * 2024/12/9 10:29
     */

    private void union(String dirPath, String toFilePath, Boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException("目录不存在");
        }
        File fileList[] = dir.listFiles();
        File targetFile = new File(toFilePath);
        try (RandomAccessFile writeFile = new RandomAccessFile(targetFile, "rw")) {
            byte[] b = new byte[1024 * 10];
            for (int i = 0; i < fileList.length; i++) {
                int len = -1;
                //创建读块文件的对象
                File chunkFile = new File(dirPath + File.separator + i);
                RandomAccessFile readFile = null;
                try {
                    readFile = new RandomAccessFile(chunkFile, "r");
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    log.error("合并分片失败", e);
                    throw new BusinessException("合并文件失败");
                } finally {
                    readFile.close();
                }
            }
        } catch (Exception e) {
            throw new BusinessException("合并文件" + dirPath + "出错了");
        } finally {
            if (delSource) {
                for (int i = 0; i < fileList.length; i++) {
                    fileList[i].delete();
                }
            }
        }
    }

    /**
     * 审核视频
     * @param status 是否通过 reason 不通过原因
     * @return
     * @author liuhd
     * 2024/12/9 19:56
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditVideo(String videoId, Integer status, String reason) {
        VideoStatusEnum videoStatusEnum = VideoStatusEnum.getByStatus(status);
        if (videoStatusEnum == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 乐观锁避免多人审核时数据库状态不一致
        // 设置要更新的字段
        VideoInfoPost videoInfoPost = new VideoInfoPost();
        videoInfoPost.setStatus(status);

        // 设置查询条件
        VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
        videoInfoPostQuery.setStatus(VideoStatusEnum.STATUS2.getStatus());
        videoInfoPostQuery.setVideoId(videoId);
        // 更新视频状态
        /*
              只有status为待审核才能改！！！！
              假设现在有两个人同时审核 现在一个人将视频变成审核通过，提交事务  然后另一个人又接着视频变成审核失败，就会导致一致性问题
              为了避免这种情况 我们改的时候必须判断状态是否是待审核
         */
        // update video_info_post set status = #{status} where video_id = #{videoId} and status = 2
        Integer auditCount = this.videoInfoPostMapper.updateByParam(videoInfoPost, videoInfoPostQuery);
        if (auditCount == 0){
            throw new BusinessException("审核失败,请稍后再试");
        }

        // 更新所有分p的状态为未更新
        VideoInfoFilePost videoInfoFilePost = new VideoInfoFilePost();
        videoInfoFilePost.setUpdateType(VideoFileUpdateTypeEnum.NO_UPDATE.getStatus());

        VideoInfoFilePostQuery filePostQuery = new VideoInfoFilePostQuery();
        filePostQuery.setVideoId(videoId);
        this.videoInfoFilePostMapper.updateByParam(videoInfoFilePost,filePostQuery);

        // 如果是审核不通过的话
        if (videoStatusEnum == VideoStatusEnum.STATUS4){
            return;
        }

        // 判断是否是第一次审核通过
        VideoInfo dbVideoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (dbVideoInfo == null){
            // TODO 给用户添加硬币

        }
        // 将发布视频表内容copy到正式视频表
        VideoInfoPost dbvideoInfoPost = this.videoInfoPostMapper.selectByVideoId(videoId);
        VideoInfo videoInfo = CopyTools.copy(dbvideoInfoPost, VideoInfo.class);
        // 第一次审核是insert 多次审核是update
        this.videoInfoMapper.insertOrUpdate(videoInfo);

        // 将发布文件表copy到正式文件表 先删除 再添加
          // 删除正式文件表中相关文件信息
        VideoInfoFileQuery videoInfoFileQuery = new VideoInfoFileQuery();
        videoInfoFileQuery.setVideoId(videoId);
        this.videoInfoFileMapper.deleteByParam(videoInfoFileQuery);
          // 取出发布文件表中相关文件信息
        VideoInfoFilePostQuery filePostQuery1 = new VideoInfoFilePostQuery();
        filePostQuery1.setVideoId(videoId);
        List<VideoInfoFilePost> filePostList = this.videoInfoFilePostMapper.selectList(filePostQuery1);
         // 塞入正式文件表
        List<VideoInfoFile> videoInfoFileList = CopyTools.copyList(filePostList, VideoInfoFile.class);
        this.videoInfoFileMapper.insertBatch(videoInfoFileList);

        // 此时数据安全转移到正式表中 终于可以删除删除队列中涉及的文件了  268行<-
          // 删除文件
        List<String> filesPathList = redisComponent.getFilesFromDelQueue(videoId);
        if (filesPathList != null && !filePostList.isEmpty()){
            for (String path : filesPathList) {
                File file = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + path);
                if (file.exists()){
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        log.error("删除文件失败",e);
                    }
                }
            }
        }
         // 删除redis删除队列
        redisComponent.cleanDelQueue(videoId);

        // TODO 保存信息到es

    }
}


