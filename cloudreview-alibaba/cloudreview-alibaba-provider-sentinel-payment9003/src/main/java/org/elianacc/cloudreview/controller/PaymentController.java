package org.elianacc.cloudreview.controller;

import org.elianacc.cloudreview.entity.Payment;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-08-29
 */
@RestController
@RefreshScope // 支持Nacos配置的动态刷新功能。
public class PaymentController {
    @Value("${server.port}")
    private String serverPort;

    @Value("${config.info}")
    private String configInfo;

    // 模拟数据库
    public static HashMap<Long, Payment> hashMap = new HashMap<>();

    static {
        hashMap.put(1L, new Payment(1L, "28a8c1e3bc2742d8848569891fb42181"));
        hashMap.put(2L, new Payment(2L, "bba8c1e3bc2742d8848569891ac32182"));
        hashMap.put(3L, new Payment(3L, "6ua8c1e3bc2742d8848569891xt92183"));
    }

    @GetMapping(value = "/paymentSQL/{id}")
    public ApiResult paymentSQL(@PathVariable("id") Long id) {
        if (id == 0) {
            int er = 10 / 0;  // 测试feign的fallback
        }
        return ApiResult.success("获取成功！" + serverPort + configInfo, hashMap.get(id));
    }

}
