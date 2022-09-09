package org.elianacc.cloudreview.feign;

import org.elianacc.cloudreview.entity.AccountDto;
import org.elianacc.cloudreview.feign.fallback.AccountFeignClientFallback;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@FeignClient(value = "seata-account-service", fallback = AccountFeignClientFallback.class)
public interface AccountFeignClient {

    @PutMapping("/account/decrease")
    public ApiResult decrease(@RequestBody AccountDto dto);

}
