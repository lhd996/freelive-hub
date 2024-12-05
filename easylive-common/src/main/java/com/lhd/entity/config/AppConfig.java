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
    @Value("${admin.account}")
    private String adminAccount;
    @Value("${admin.password}")
    private String adminPassword;

    public String getProjectFolder() {
        return projectFolder;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public String getAdminPassword() {
        return adminPassword;
    }
}
