package org.elianacc.cloudreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-11-04
 */
@SpringBootApplication
@EnableFeignClients
public class OrderNacosMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderNacosMain80.class, args);
    }
}
