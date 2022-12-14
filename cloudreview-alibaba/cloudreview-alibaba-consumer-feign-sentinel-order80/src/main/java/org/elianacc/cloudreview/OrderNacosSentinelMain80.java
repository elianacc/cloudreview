package org.elianacc.cloudreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-11-04
 */
@SpringBootApplication
@EnableFeignClients
public class OrderNacosSentinelMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderNacosSentinelMain80.class, args);
    }
}
