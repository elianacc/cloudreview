package org.elianacc.cloudreview.controller.block;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.elianacc.cloudreview.entity.Order;
import org.elianacc.cloudreview.vo.ApiResult;

/**
 * 自定义限流处理逻辑
 *
 * @author ELiaNaCc
 * @since 2022-08-30
 */
public class CustomBlockHandler {

    public static ApiResult handlerOrderCreateException(Order order, BlockException exception) {
        return ApiResult.warn("已经限流,global handlerOrderCreateException----oreder create");
    }

}
