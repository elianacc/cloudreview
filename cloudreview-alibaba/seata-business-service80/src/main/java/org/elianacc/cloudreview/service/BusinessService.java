package org.elianacc.cloudreview.service;

import org.elianacc.cloudreview.entity.Order;
import org.elianacc.cloudreview.vo.ApiResult;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
public interface BusinessService {

    public ApiResult orderCreate(Order order);

}
