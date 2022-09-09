package org.elianacc.cloudreview.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-25
 */
@Configuration
public class FeignConfig {

//    // 配置ribbon负载均衡策略
//    @Bean
//    public IRule loadBalancedRule() {
//        return new RandomRule();
//    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
