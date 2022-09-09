package org.elianacc.cloudreview.controller;

import lombok.extern.slf4j.Slf4j;
import org.elianacc.cloudreview.entity.Payment;
import org.elianacc.cloudreview.service.PaymentFeignService;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-25
 */
@RestController
@Slf4j
public class OrderFeignController {

    @Autowired
    private PaymentFeignService paymentFeignService;

    @GetMapping(value = "/consumer/payment/get/{id}")
    public ApiResult getPaymentById(@PathVariable("id") Long id) {
        return paymentFeignService.getPaymentById(id);
    }

    @PostMapping(value = "/consumer/payment/create")
    public ApiResult creat(@RequestBody Payment payment) {
        return paymentFeignService.creat(payment);
    }

    @GetMapping(value = "/consumer/payment/feign/timeout")
    public String paymentFeignTimeout() {
        // OpenFeign客户端一般默认时等待1秒钟（可以配置更改，有些复杂业务需要更多时间）
        return paymentFeignService.paymentFeignTimeout();
    }
}
