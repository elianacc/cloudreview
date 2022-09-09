package org.elianacc.cloudreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-26
 */
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashboardMain6001 {
    public static void main(String[] args) {
        SpringApplication.run(HystrixDashboardMain6001.class, args);
    }
}
