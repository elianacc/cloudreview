package org.elianacc.cloudreview.service;

import org.elianacc.cloudreview.entity.Payment;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-25
 */
@Component
//@FeignClient(value = "CLOUDREVIEW-PROVIDER-PAYMENT")
@FeignClient(value = "CLOUDREVIEW-PROVIDER-PAYMENT-GATEWAY")   // 使用网关后
public interface PaymentFeignService {

    @GetMapping(value = "/payment/get/{id}")
    public ApiResult getPaymentById(@PathVariable("id") Long id);

    @PostMapping(value = "/payment/create")
    public ApiResult creat(@RequestBody Payment payment);

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout();

}
