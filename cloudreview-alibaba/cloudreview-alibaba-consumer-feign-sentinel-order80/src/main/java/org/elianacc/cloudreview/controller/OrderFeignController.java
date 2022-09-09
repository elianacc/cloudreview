package org.elianacc.cloudreview.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.elianacc.cloudreview.controller.block.CustomBlockHandler;
import org.elianacc.cloudreview.service.PaymentFeignService;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping(value = "/consumer/paymentSQL/{id}")
    @SentinelResource(value = "consumer-paymentSQL",
            blockHandlerClass = CustomBlockHandler.class,
            blockHandler = "handlerException")
    public ApiResult paymentSQL(@PathVariable("id") Long id) {
        return paymentFeignService.paymentSQL(id);
    }

}
