package org.elianacc.cloudreview.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elianacc.cloudreview.entity.Payment;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-19
 */
@RestController
@Slf4j
public class OrderController {

//    public static final String PAYMENT_URL = "http://localhost:8001";  // 单机支付服务
    public static final String PAYMENT_URL = "http://CLOUDREVIEW-PROVIDER-PAYMENT";  // 多个支付服务

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/create")
    public ApiResult create(Payment payment) {
        return restTemplate.postForObject(PAYMENT_URL + "/payment/create", payment, ApiResult.class);
    }

    @GetMapping("/consumer/payment/get/{id}")
    public ApiResult getPayment(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id, ApiResult.class);
    }

    @GetMapping("/consumer/payment/getForEntity/{id}")
    public ApiResult getPayment2(@PathVariable("id") Long id) {
        ResponseEntity<ApiResult> entity = restTemplate.getForEntity(PAYMENT_URL + "/payment/get/" + id, ApiResult.class);

        if (entity.getStatusCode().is2xxSuccessful()) {
            return entity.getBody();//getForObject()
        } else {
            return ApiResult.fail("操作失败");
        }
    }

    @GetMapping("/getTest1")
    public ApiResult getTest1(Integer id, String name) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        paramMap.put("name", name);
        return restTemplate.getForObject(PAYMENT_URL + "/getTest1?id={id}&name={name}", ApiResult.class, paramMap);
    }

    @GetMapping("/getTest2")
    public ApiResult getTest2(Payment payment) {
        ResponseEntity<ApiResult> response = restTemplate.exchange(PAYMENT_URL + "/getTest2?id={id}&serial={serial}", HttpMethod.GET, null, ApiResult.class, JSON.parseObject(JSON.toJSONString(payment)));
        return response.getBody();
    }

}
