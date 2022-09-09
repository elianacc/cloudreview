package org.elianacc.cloudreview.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.elianacc.cloudreview.controller.block.CustomBlockHandler;
import org.elianacc.cloudreview.entity.Order;
import org.elianacc.cloudreview.service.BusinessService;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@RestController
@RequestMapping("/api")
public class BusinessController {

    @Autowired
    private BusinessService businessService;


    @PostMapping("/order/create")
    @SentinelResource(value = "order-create",
            blockHandlerClass = CustomBlockHandler.class,
            blockHandler = "handlerOrderCreateException")
    public ApiResult orderCreate(@RequestBody Order order) {
        return businessService.orderCreate(order);
    }


}
