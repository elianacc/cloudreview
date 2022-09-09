package org.elianacc.cloudreview.feign.fallback;

import org.elianacc.cloudreview.entity.AccountDto;
import org.elianacc.cloudreview.feign.AccountFeignClient;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.stereotype.Component;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@Component
public class AccountFeignClientFallback implements AccountFeignClient {
    @Override
    public ApiResult decrease(AccountDto dto) {
        return ApiResult.fail("服务降级返回,---account decrease");
    }
}
