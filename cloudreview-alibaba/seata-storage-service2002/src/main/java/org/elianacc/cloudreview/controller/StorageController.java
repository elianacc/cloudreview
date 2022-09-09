package org.elianacc.cloudreview.controller;

import org.elianacc.cloudreview.entity.StorageDto;
import org.elianacc.cloudreview.service.StorageService;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PutMapping("/decrease")
    public ApiResult decrease(@RequestBody StorageDto dto) {
        storageService.decrease(dto);
        return ApiResult.success("扣减库存成功！");
    }

}
