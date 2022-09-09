package org.elianacc.cloudreview;

import org.rbrule.MySelfRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

/**
 * 80启动类
 *
 * @author CNY
 * @since 2021-10-19
 */
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "CLOUDREVIEW-PROVIDER-PAYMENT", configuration = MySelfRule.class)
public class OrderMain80 {

    public static void main(String[] args) {
        SpringApplication.run(OrderMain80.class, args);
    }

}
