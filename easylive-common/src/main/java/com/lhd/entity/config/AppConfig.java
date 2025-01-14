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
    @Value("${es.host.port:127.0.0.1:9200}")
    private String esHostPort;

    @Value("${es.index.video.name:easylive_video}")
    private String esIndexVideoName;


    public void setProjectFolder(String projectFolder) {
        this.projectFolder = projectFolder;
    }

    public void setAdminAccount(String adminAccount) {
        this.adminAccount = adminAccount;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void setShowFFmpegLog(Boolean showFFmpegLog) {
        this.showFFmpegLog = showFFmpegLog;
    }

    public String getEsHostPort() {
        return esHostPort;
    }

    public void setEsHostPort(String esHostPort) {
        this.esHostPort = esHostPort;
    }

    public String getEsIndexVideoName() {
        return esIndexVideoName;
    }

    public void setEsIndexVideoName(String esIndexVideoName) {
        this.esIndexVideoName = esIndexVideoName;
    }

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
