package com.lhd.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Author: liuhd
 * @Date: 2024/12/4 15:13
 * @Description:
 */
@SpringBootApplication(scanBasePackages = {"com.lhd"})
@MapperScan(basePackages = {"com.lhd.mappers"})
public class EasyLiveAdminRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyLiveAdminRunApplication.class,args);
    }
}
