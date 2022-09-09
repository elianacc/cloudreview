package org.elianacc.cloudreview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.elianacc.cloudreview.entity.Order;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-05
 */
public interface OrderService extends IService<Order> {

    public int insert(Order order);

    public int updateStatus(Order order);

}
