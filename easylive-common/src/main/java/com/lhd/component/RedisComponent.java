package com.lhd.component;

import com.lhd.entity.config.AppConfig;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.SysSettingDto;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.dto.UploadingFileDto;
import com.lhd.entity.dto.VideoPlayInfoDto;
import com.lhd.entity.enums.DateTimePatternEnum;
import com.lhd.entity.po.CategoryInfo;
import com.lhd.entity.po.VideoInfoFilePost;
import com.lhd.redis.RedisUtils;
import com.lhd.utils.DateUtil;
import com.lhd.utils.StringTools;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: liuhd
 * @Date: 2024/12/5 00:24
 * @Description: 封装redis相关的业务操作
 */
@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private AppConfig appConfig;

    /**
     * 保存验证码
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 0:27
     */

    public String saveCheckCode(String code) {
        // 随机一个验证码的key,防止不同用户使用同一个key导致验证码的覆盖
        String checkCodeKey = UUID.randomUUID().toString();
        // 将验证码存入redis
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey, code, Constants.REDIS_KEY_EXPIRES_ONE_MIN * 10);
        return checkCodeKey;
    }

    /**
     * 根据验证key获取验证码
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 12:07
     */

    public String getCheckCode(String checkCoedKey) {
        return (String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCoedKey);
    }

    /**
     * 根据key清除验证码
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 15:54
     */

    public void cleanCheckCode(String checkCodeKey) {
        redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    /**
     * 将token存入redis
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 16:00
     */


    public void saveTokenInfo(TokenUserInfoDto tokenUserInfoDto) {
        // UUID作为键
        String token = UUID.randomUUID().toString();
        // 设置过期时间
        tokenUserInfoDto.setExpireAt(System.currentTimeMillis() + Constants.REDIS_KEY_EXPIRES_SEVEN_DAY);
        // 设置token
        tokenUserInfoDto.setToken(token);
        // 将token信息存入redis
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_WEB + token, tokenUserInfoDto, Constants.REDIS_KEY_EXPIRES_SEVEN_DAY);
    }
    /**
     * @description: 更新token信息
     * @param tokenUserInfoDto
     * @return
     * @author liuhd
     * 2025/1/11 14:19
     */
    public void updateTokenInfo(TokenUserInfoDto tokenUserInfoDto){

        // 将token信息存入redis
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_WEB + tokenUserInfoDto.getToken(), tokenUserInfoDto, Constants.REDIS_KEY_EXPIRES_SEVEN_DAY);
    }

    /**
     * 从redis中删除token
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 17:26
     */

    public void cleanToken(String token) {
        redisUtils.delete(Constants.REDIS_KEY_TOKEN_WEB + token);
    }

    /**
     * 根据token取出token信息
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/5 19:16
     */

    public TokenUserInfoDto getTokenInfo(String token) {
        return (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_TOKEN_WEB + token);
    }

    /**
     * 将admin端的token信息存入redis，这次只需要传一个账户即可
     *
     * @param account 账户
     * @return token UUID
     * @author liuhd
     * 2024/12/5 23:34
     */

    public String saveTokenInfoForAdmin(String account) {
        // UUID作为键
        String token = UUID.randomUUID().toString();
        // 将token信息存入redis
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_ADMIN + token, account, Constants.REDIS_KEY_EXPIRES_ONE_DAY);
        return token;
    }

    /**
     * 从redis中删除token
     *
     * @param token
     * @return
     * @author liuhd
     * 2024/12/5 23:51
     */

    public void cleanTokenForAdmin(String token) {
        redisUtils.delete(Constants.REDIS_KEY_TOKEN_ADMIN + token);
    }

    /**
     * 从redis中取出token信息 for admin server
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/6 1:35
     */

    public String getTokenInfoForAdmin(String token) {
        return (String) redisUtils.get(Constants.REDIS_KEY_TOKEN_ADMIN + token);
    }

    /**
     * 将分区列表缓存到redis中
     *
     * @param categoryInfoList 分区列表
     * @return
     * @author liuhd
     * 2024/12/7 21:29
     */

    public void saveCategoryList(List<CategoryInfo> categoryInfoList) {
        redisUtils.set(Constants.REDIS_KEY_CATEGORY_LIST, categoryInfoList);
    }

    /**
     * 从redis中拿到缓存的categoryList
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/8 10:24
     */

    public List<CategoryInfo> getCategoryList() {
        return (List<CategoryInfo>) redisUtils.get(Constants.REDIS_KEY_CATEGORY_LIST);
    }

    /** 将预上传的视频文件信息保存到redis中
     * @param chunks 这个文件有多少片
     * @return
     * @author liuhd
     * 2024/12/8 11:46
     */

    public String savePreVideoFileInfo(String uid, String fileName, Integer chunks) {
        // 每一个文件上传的目录是不重复的的
        String uploadId = StringTools.getRandomString(Constants.LENGTH_15);
        // 设置预上传的文件信息
        UploadingFileDto fileDto = new UploadingFileDto();
        fileDto.setChunks(chunks);
        fileDto.setFileName(fileName);
        fileDto.setUploadId(uploadId);
        //文件还没上传 当前分片肯定是0
        fileDto.setChunkIndex(Constants.ZERO);
        // 创建文件保存目录
          // 以天为单位创建视频存放目录
        String day = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMMDD.getPattern());
        String filePath = day + "/" + uid + uploadId;
        fileDto.setFilePath(filePath);
          // 文件应该上传到临时目录(本项目目录/file/tmp/day/uid + uploadId)
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + filePath;
        File file = new File(folder);
          // 目录不存在则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 保存预上传文件信息
        redisUtils.setex(Constants.REDIS_KEY_UPLOADING_FILE + uid + uploadId,fileDto,Constants.REDIS_KEY_EXPIRES_ONE_DAY);
        return uploadId;
    }
    /**
     * 获取视频文件信息
     * @param 
     * @return 
     * @author liuhd
     * 2024/12/8 13:13
     */
    
    public UploadingFileDto getUploadVideoFileInfo(String uid, String uploadId) {
       return (UploadingFileDto) redisUtils.get(Constants.REDIS_KEY_UPLOADING_FILE + uid + uploadId);
    }

    /**
     * 更新视频文件信息
     * @param fileDto 最新的文件信息
     * @return
     * @author liuhd
     * 2024/12/8 13:46
     */

    public void updateVideoFileInfo(String userId, UploadingFileDto fileDto) {
        redisUtils.setex(Constants.REDIS_KEY_UPLOADING_FILE + userId + fileDto.getUploadId(), fileDto,Constants.REDIS_KEY_EXPIRES_ONE_DAY);
    }

    /**
     * 获取系统配置
     * @param
     * @return
     * @author liuhd
     * 2024/12/8 13:25
     */

    public SysSettingDto getSysSettingDto(){
        SysSettingDto sysSettingDto = (SysSettingDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        // 如果没有 就用默认值
        if (sysSettingDto == null){
            sysSettingDto = new SysSettingDto();
        }
        return sysSettingDto;
    }

    public void saveSettingDto(SysSettingDto sysSettingDto){
        redisUtils.set(Constants.REDIS_KEY_SYS_SETTING,sysSettingDto);
    }
    /**
     * 删除视频文件信息
     * @param
     * @return
     * @author liuhd
     * 2024/12/8 15:05
     */

    public void delVideoInfo(String userId, String uploadId) {
        redisUtils.delete(Constants.REDIS_KEY_UPLOADING_FILE + userId + uploadId);
    }
    /**
     * 将视频的要删除的分p加入删除队列
     * @param videoId 视频id delFilePathList 要删除的分p路径List
     * @return
     * @author liuhd
     * 2024/12/8 20:26
     */

    public void addFile2DelQueue(String videoId, List<String> delFilePathList) {
        redisUtils.lpushAll(Constants.REDIS_KEY_FILE_DEL + videoId,delFilePathList,Constants.REDIS_KEY_EXPIRES_ONE_DAY);
    }
    /**
     * 将视频分p加入转码的删除队列（针对所有视频）
     * @param
     * @return
     * @author liuhd
     * 2024/12/8 20:55
     */

    public List<String> getFilesFromDelQueue(String videoId){
        return redisUtils.getQueueList(Constants.REDIS_KEY_FILE_DEL + videoId);
    }

    /**
     * 清除删除队列
     * @param
     * @return
     * @author liuhd
     * 2024/12/9 21:33
     */

    public void cleanDelQueue(String videoId) {
        redisUtils.delete(Constants.REDIS_KEY_FILE_DEL + videoId);
    }

    public void addFile2TransferQueue(List<VideoInfoFilePost> addFileList) {
        redisUtils.lpushAll(Constants.REDIS_KEY_QUEUE_TRANSFER,addFileList,0);
    }

    /**
     * 从转码队列中取出视频分p
     * @param
     * @return
     * @author liuhd
     * 2024/12/9 21:22
     */

    public VideoInfoFilePost getFileFromTransferQueue(){
        return (VideoInfoFilePost) redisUtils.rpop(Constants.REDIS_KEY_QUEUE_TRANSFER);
    }

    /**
     * @description: 记录在线人数
     * @param fileId (分片)文件id
     * @param deviceId 设备id
     * @return java.lang.Integer
     * @author liuhd
     * 2025/1/11 10:47
     */

    public Integer reportVideoOnline(String fileId,String deviceId){
        // 当前用户在看的证明
        String userPlayOnlineKey = String.format(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER,fileId,deviceId);
        // 当前在看的人数
        String playOnlineCountKey = String.format(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE,fileId);

        // 如果用户刚点击这个视频
        if (!redisUtils.keyExists(userPlayOnlineKey)){
            // 设置8s的有效期 （我们每5s会轮询发送一次请求来检验用户是否在线,这种技术叫心跳）
            redisUtils.setex(userPlayOnlineKey,fileId,Constants.REDIS_KEY_EXPIRES_ONE_SECONDS * 8);

            return redisUtils.incrementex(playOnlineCountKey,Constants.REDIS_KEY_EXPIRES_ONE_SECONDS * 10).intValue();
        }
        // 如果用户在看 给在线人数续期
        redisUtils.expire(playOnlineCountKey,Constants.REDIS_KEY_EXPIRES_ONE_SECONDS * 10);
        redisUtils.expire(userPlayOnlineKey,Constants.REDIS_KEY_EXPIRES_ONE_SECONDS * 8);
        Integer count = (Integer) redisUtils.get(playOnlineCountKey);
        return count == null ? 1 : count;
    }
    public void decrementPlayOnlineCount(String key){
        redisUtils.decrement(key);
    }

    /**
     * @description: 记录热词，搜索一次加1
     * @param keyword
     * @return
     * @author liuhd
     * 2025/1/14 11:13
     */

    public void addKeywordCount(String keyword){
        redisUtils.zaddCount(Constants.REDIS_KEY_VIDEO_SEARCH_COUNT,keyword);
    }

    /**
     * @description: 获取前多少个热词
     * @param top
     * @return java.util.List<java.lang.String>
     * @author liuhd
     * 2025/1/14 11:15
     */

    public List<String> getKeywordTop(Integer top){
        return redisUtils.getZSetList(Constants.REDIS_KEY_VIDEO_SEARCH_COUNT,top-1);
    }

    /**
     * @description:
     * @param videoPlayInfoDto
     * @return
     * @author liuhd
     * 2025/1/14 12:11
     */

    public void addVideoPlay(VideoPlayInfoDto videoPlayInfoDto){
        redisUtils.lpush(Constants.REDIS_KEY_QUEUE_VIDEO_PLAY,videoPlayInfoDto,null);
    }

    /**
     * @description: 从队列中取出数据
     * @param
     * @return com.lhd.entity.dto.VideoPlayInfoDto
     * @author liuhd
     * 2025/1/14 12:57
     */

    public VideoPlayInfoDto getVideoPlayFromVideoPlayQueue(){
        return (VideoPlayInfoDto) redisUtils.rpop(Constants.REDIS_KEY_QUEUE_VIDEO_PLAY);
    }

    /**
     * @description: 按天记录视频的播放量
     * @param videoId
     * @return
     * @author liuhd
     * 2025/1/14 12:57
     */
    public void recordVideoPlayCount(String videoId){
        String date = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        redisUtils.incrementex(Constants.REDIS_KEY_VIDEO_PLAY_COUNT + date + ":" + videoId,Constants.REDIS_KEY_EXPIRES_ONE_DAY * 2L);
    }
    /**
     * @description: 获取某一天的视频播放量
     * @param date
     * @return java.util.Map<java.lang.String, java.lang.Integer>
     * @author liuhd
     * 2025/1/15 10:25
     */
    public Map<String,Integer> getVideoPlayCount(String date){
        Map<String,Integer> videoPlayMap = redisUtils.getBatch(Constants.REDIS_KEY_VIDEO_PLAY_COUNT + date);
        return videoPlayMap;
    }



}
