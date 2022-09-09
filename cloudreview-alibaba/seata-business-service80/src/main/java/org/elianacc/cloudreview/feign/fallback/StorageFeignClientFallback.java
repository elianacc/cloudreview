package org.elianacc.cloudreview.feign.fallback;

import org.elianacc.cloudreview.entity.StorageDto;
import org.elianacc.cloudreview.feign.StorageFeignClient;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.stereotype.Component;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@Component
public class StorageFeignClientFallback implements StorageFeignClient {
    @Override
    public ApiResult decrease(StorageDto dto) {
        return ApiResult.fail("服务降级返回,---storage decrease");
    }
}
