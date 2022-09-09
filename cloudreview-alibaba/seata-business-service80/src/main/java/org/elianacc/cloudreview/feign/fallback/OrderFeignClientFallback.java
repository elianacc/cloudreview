package org.elianacc.cloudreview.feign.fallback;

import org.elianacc.cloudreview.entity.Order;
import org.elianacc.cloudreview.feign.OrderFeignClient;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.stereotype.Component;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@Component
public class OrderFeignClientFallback implements OrderFeignClient {

    @Override
    public ApiResult insert(Order order) {
        return ApiResult.fail("服务降级返回,---order insert");
    }

    @Override
    public ApiResult updateStatus(Order order) {
        return ApiResult.fail("服务降级返回,---order updateStatus");
    }

}
