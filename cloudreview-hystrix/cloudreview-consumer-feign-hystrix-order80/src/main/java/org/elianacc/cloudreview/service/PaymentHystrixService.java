package org.elianacc.cloudreview.service;

import org.elianacc.cloudreview.service.impl.PaymentFallbackService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-25
 */
@Component
//@FeignClient(value = "CLOUDREVIEW-PROVIDER-HYSTRIX-PAYMENT")
// 使用接口实现类的方式对应每个service的fallback
@FeignClient(value = "CLOUDREVIEW-PROVIDER-HYSTRIX-PAYMENT", fallback = PaymentFallbackService.class)
public interface PaymentHystrixService {
    @GetMapping("/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id);

    @GetMapping("/payment/hystrix/timeout/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id);

    @GetMapping("/payment/circuit/{id}")
    public String paymentCircuitBreaker(@PathVariable("id") Integer id);
}
