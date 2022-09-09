package org.elianacc.cloudreview.controller;

import lombok.extern.slf4j.Slf4j;
import org.elianacc.cloudreview.entity.Payment;
import org.elianacc.cloudreview.service.PayMentService;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-18
 */
@RestController
@Slf4j
public class PaymentController {

    @Autowired
    private PayMentService payMentService;

    @Value("${server.port}")
    private String serverPort;

    @PostMapping(value = "/payment/create")
    public ApiResult creat(@RequestBody Payment payment) {
        int res = payMentService.insert(payment);
        log.info("*****插入结果：" + res);

        if (res > 0) {
            return ApiResult.success("插入数据库成功,serverPort: " + serverPort, res);
        } else {
            return ApiResult.fail("插入数据库失败");
        }
    }

    @GetMapping(value = "/payment/get/{id}")
    public ApiResult getPaymentById(@PathVariable("id") Long id) {
        Payment payment = payMentService.getById(id);

        if (payment != null) {
            return ApiResult.success("查询成功,serverPort:  " + serverPort, payment);
        } else {
            return ApiResult.fail("没有对应记录,查询ID: " + id);
        }
    }

    @GetMapping("/getTest1")
    public ApiResult getTest1(Integer id, String name) {
        return ApiResult.success("serverPort:" + serverPort + ", getTest1参数: " + id + "," + name);
    }

    @GetMapping("/getTest2")
    public ApiResult getTest2(Payment payment) {
        return ApiResult.success("serverPort:" + serverPort + ", getTest2参数: " + payment);
    }

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout() {
        // 业务逻辑处理正确，但是需要耗费3秒钟
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }
}
