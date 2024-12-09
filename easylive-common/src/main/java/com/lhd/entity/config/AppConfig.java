package com.lhd.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: liuhd
 * @Date: 2024/12/5 23:23
 * @Description: 读取配置类信息
 */
@Configuration
public class AppConfig {
    @Value("${project.folder}")
    private String projectFolder;
    @Value("${admin.account:admin}")
    private String adminAccount;
    @Value("${admin.password:admin123}")
    private String adminPassword;
    @Value("${showFFmpeg:true}")
    private Boolean showFFmpegLog;

    public String getProjectFolder() {
        return projectFolder;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public Boolean getShowFFmpegLog() {
        return showFFmpegLog;
    }
}
