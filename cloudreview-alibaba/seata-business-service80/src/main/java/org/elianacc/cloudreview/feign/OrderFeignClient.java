package org.elianacc.cloudreview.feign;

import org.elianacc.cloudreview.entity.Order;
import org.elianacc.cloudreview.feign.fallback.OrderFeignClientFallback;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@FeignClient(value = "seata-order-service", fallback = OrderFeignClientFallback.class)
public interface OrderFeignClient {

    @PostMapping("/order/insert")
    public ApiResult insert(@RequestBody Order order);

    @PutMapping("/order/updateStatus")
    public ApiResult updateStatus(@RequestBody Order order);

}
