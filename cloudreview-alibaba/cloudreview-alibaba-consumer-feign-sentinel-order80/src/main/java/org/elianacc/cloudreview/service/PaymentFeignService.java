package org.elianacc.cloudreview.service;

import org.elianacc.cloudreview.service.impl.PaymentFeignServiceImpl;
import org.elianacc.cloudreview.vo.ApiResult;
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
@FeignClient(value = "cloudreview-alibaba-provider-sentinel-payment", fallback = PaymentFeignServiceImpl.class) // ！！！！注意使用小写 nacos区分大小写
public interface PaymentFeignService {

    @GetMapping(value = "/paymentSQL/{id}")
    public ApiResult paymentSQL(@PathVariable("id") Long id);

}
