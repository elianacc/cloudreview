package org.elianacc.cloudreview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elianacc.cloudreview.dao.PayMentMapper;
import org.elianacc.cloudreview.entity.Payment;
import org.elianacc.cloudreview.service.PayMentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-18
 */
@Service
public class PayMentServiceImpl extends ServiceImpl<PayMentMapper, Payment> implements PayMentService {

    @Autowired
    private PayMentMapper payMentMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insert(Payment payment) {
        return payMentMapper.insert(payment);
    }

    @Override
    public Payment getById(Long id) {
        return payMentMapper.selectById(id);
    }
}
