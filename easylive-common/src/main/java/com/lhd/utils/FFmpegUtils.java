package com.lhd.utils;

import com.lhd.entity.config.AppConfig;
import com.lhd.entity.constants.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: liuhd
 * @Date: 2024/12/7 22:28
 * @Description:
 */
@Component
public class FFmpegUtils {
    @Resource
    private AppConfig appConfig;

    /**
     * 创建缩略图(等比缩小)
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/7 22:34
     */

    public void createImageThumbnail(String filePath) {
        // 定义CMD命令
        String CMD = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\"";
        CMD = String.format(CMD, filePath, filePath + Constants.IMAGE_THUMBNAIL_SUFFIX);
        // 执行命令
        ProcessUtils.executeCommand(CMD,false);
    }
}
