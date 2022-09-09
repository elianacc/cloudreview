package org.elianacc.cloudreview.service.impl;

import org.elianacc.cloudreview.service.PaymentFeignService;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-08-29
 */
@Component
public class PaymentFeignServiceImpl implements PaymentFeignService {

    @Override
    public ApiResult paymentSQL(Long id) {
        return ApiResult.fail("服务降级返回,---PaymentFeignServiceImpl" + id);
    }

}
