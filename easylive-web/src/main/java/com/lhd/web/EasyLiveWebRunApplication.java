package com.lhd.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: liuhd
 * @Date: 2024/12/4 15:12
 * @Description:
 */
@SpringBootApplication(scanBasePackages = {"com.lhd"})
@MapperScan(basePackages = {"com.lhd.mappers"})
@EnableTransactionManagement
@EnableScheduling
public class EasyLiveWebRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyLiveWebRunApplication.class,args);
    }
}
