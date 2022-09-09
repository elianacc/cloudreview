package org.elianacc.cloudreview.service.impl;

import org.elianacc.cloudreview.service.PaymentHystrixService;
import org.springframework.stereotype.Component;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-26
 */
@Component
public class PaymentFallbackService implements PaymentHystrixService {
    @Override
    public String paymentInfo_OK(Integer id) {
        return "-----PaymentFallbackService fall back-paymentInfo_OK ,o(╥﹏╥)o";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "-----PaymentFallbackService fall back-paymentInfo_TimeOut ,o(╥﹏╥)o";
    }

    @Override
    public String paymentCircuitBreaker(Integer id) {
        return "id 不能负数，请稍后再试，/(ㄒoㄒ)/~~   id: " +id;
    }
}
