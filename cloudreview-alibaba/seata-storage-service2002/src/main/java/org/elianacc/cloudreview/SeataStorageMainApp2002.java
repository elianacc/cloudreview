package org.elianacc.cloudreview;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-05
 */
@SpringBootApplication
@MapperScan(basePackages = {"org.elianacc.cloudreview.dao"})
@EnableDiscoveryClient
public class SeataStorageMainApp2002 {

    public static void main(String[] args) {
        SpringApplication.run(SeataStorageMainApp2002.class, args);
    }

}
