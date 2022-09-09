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
//@FeignClient(value = "cloudreview-alibaba-provider-payment")  // ！！！！注意使用小写 nacos区分大小写
@FeignClient(value = "cloudreview-alibaba-provider-payment-gateway") // 使用网关
public interface PaymentFeignService {

    @GetMapping(value = "/payment/get/{id}")
    public ApiResult getPaymentById(@PathVariable("id") Long id);

    @PostMapping(value = "/payment/create")
    public ApiResult creat(@RequestBody Payment payment);

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout();

}
