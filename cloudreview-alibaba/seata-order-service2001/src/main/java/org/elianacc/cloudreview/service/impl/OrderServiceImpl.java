package org.elianacc.cloudreview.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elianacc.cloudreview.dao.OrderMapper;
import org.elianacc.cloudreview.entity.Order;
import org.elianacc.cloudreview.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-05
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public int insert(Order order) {
        order.setStatus(0);
        return orderMapper.insert(order);
    }

    @Override
    public int updateStatus(Order order) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        return orderMapper.update(order, updateWrapper.set("status", 1)
                .eq("id", order.getId())
                .eq("status", 0));
    }

}
