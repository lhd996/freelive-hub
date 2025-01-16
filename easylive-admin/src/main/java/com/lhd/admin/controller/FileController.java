package com.lhd.admin.controller;

import com.lhd.entity.config.AppConfig;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.dto.TokenUserInfoDto;
import com.lhd.entity.dto.VideoPlayInfoDto;
import com.lhd.entity.enums.DateTimePatternEnum;
import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.entity.po.VideoInfoFile;
import com.lhd.entity.po.VideoInfoFilePost;
import com.lhd.entity.vo.ResponseVO;
import com.lhd.exception.BusinessException;
import com.lhd.service.VideoInfoFilePostService;
import com.lhd.service.VideoInfoFileService;
import com.lhd.utils.DateUtil;
import com.lhd.utils.FFmpegUtils;
import com.lhd.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * @Author: liuhd
 * @Date: 2024/12/7 21:54
 * @Description:
 */
@RestController
@RequestMapping("/file")
@Validated
@Slf4j
public class FileController extends ABaseController {
    @Resource
    private AppConfig appConfig;
    @Resource
    private FFmpegUtils fFmpegUtils;
    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;
    /**
     * 文件上传
     *
     * @param file 文件 createThumbnail 是否生成缩略图
     * @return
     * @author liuhd
     * 2024/12/7 21:58
     */

    @RequestMapping("/uploadImage")
    public ResponseVO uploadImage(@NotNull MultipartFile file, @NotNull Boolean createThumbnail) throws IOException {
        // 定义文件夹 一个月为一个大目录
        String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMM.getPattern());
        String folder = appConfig.getProjectFolder()
                + Constants.FILE_FOLDER
                + Constants.FILE_COVER + month;
        // 创建文件夹
        File folderFile = new File(folder);
        if (!folderFile.exists()){
            folderFile.mkdirs();
        }
        // 设置文件路径
          // 取出后缀
        String filename = file.getOriginalFilename();
        String fileSuffix = StringTools.getFileSuffix(filename);
          // 拼接随机字符串作为文件名 防止重名
        String realFilename = StringTools.getRandomString(Constants.LENGTH_30) + fileSuffix;
        String path = folder + "/" +  realFilename;
          //将文件复制到path
        file.transferTo(new File(path));
        // 如果需要 则生成缩略图
        if (createThumbnail){
            // 生成缩略图
            fFmpegUtils.createImageThumbnail(path);
        }
        return getSuccessResponseVO(Constants.FILE_COVER + month + "/" + realFilename);
    }
    
    /**
     * 从本地获取文件
     * @param 
     * @return 
     * @author liuhd
     * 2024/12/7 23:01
     */
    
    @RequestMapping("/getResource")
    public void getResource(HttpServletResponse response,@NotNull String sourceName){
        if (!StringTools.pathIsOk(sourceName)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String suffix = StringTools.getFileSuffix(sourceName);
        response.setContentType("image/" + suffix.replace(".", ""));
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, sourceName);
    }

    protected void readFile(HttpServletResponse response, String filePath) {
        File file = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + filePath);
        if (!file.exists()) {
            return;
        }
        try (OutputStream out = response.getOutputStream(); FileInputStream in = new FileInputStream(file)) {
            byte[] byteData = new byte[1024];
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("读取文件异常", e);
        }
    }

    /**
     * @description: 播放视频
     * @param response
     * @param fileId
     * @return
     * @author liuhd
     * 2025/1/16 11:01
     */

    @RequestMapping("/videoResource/{fileId}")
    public void getVideoResource(HttpServletResponse response, @PathVariable @NotEmpty String fileId) {
        VideoInfoFilePost videoInfoFilePost = videoInfoFilePostService.getVideoInfoFilePostByFileId(fileId);
        String filePath = videoInfoFilePost.getFilePath();
        readFile(response, filePath + "/" + Constants.M3U8_NAME);
    }

    @RequestMapping("/videoResource/{fileId}/{ts}")
    public void getVideoResourceTs(HttpServletResponse response, @PathVariable @NotEmpty String fileId, @PathVariable @NotNull String ts) {
        VideoInfoFilePost videoInfoFilePost = videoInfoFilePostService.getVideoInfoFilePostByFileId(fileId);
        String filePath = videoInfoFilePost.getFilePath() + "";
        readFile(response, filePath + "/" + ts);
    }
}
