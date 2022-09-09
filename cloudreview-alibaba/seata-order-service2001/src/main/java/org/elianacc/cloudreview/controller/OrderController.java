package org.elianacc.cloudreview.controller;

import org.elianacc.cloudreview.entity.Order;
import org.elianacc.cloudreview.service.OrderService;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-05
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/insert")
    public ApiResult insert(@RequestBody Order order) {
        orderService.insert(order);
        return ApiResult.success("订单插入成功");
    }

    @PutMapping("/updateStatus")
    public ApiResult updateStatus(@RequestBody Order order) {
        orderService.updateStatus(order);
        return ApiResult.success("订单修改状态成功");
    }

}
