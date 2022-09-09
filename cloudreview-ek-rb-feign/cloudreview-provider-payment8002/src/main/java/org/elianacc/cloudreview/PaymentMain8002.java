package org.elianacc.cloudreview;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 8002启动类
 *
 * @author CNY
 * @since 2021-10-18
 */
@SpringBootApplication
@MapperScan(basePackages = {"org.elianacc.cloudreview.dao"})
@EnableEurekaClient
public class PaymentMain8002 {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMain8002.class, args);
    }

}
