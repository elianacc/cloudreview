package org.elianacc.cloudreview.feign;

import org.elianacc.cloudreview.entity.StorageDto;
import org.elianacc.cloudreview.feign.fallback.StorageFeignClientFallback;
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
@FeignClient(value = "seata-storage-service", fallback = StorageFeignClientFallback.class)
public interface StorageFeignClient {

    @PutMapping("/storage/decrease")
    public ApiResult decrease(@RequestBody StorageDto dto);

}
