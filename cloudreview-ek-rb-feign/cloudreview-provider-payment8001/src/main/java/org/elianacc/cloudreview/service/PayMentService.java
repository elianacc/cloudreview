package org.elianacc.cloudreview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.elianacc.cloudreview.entity.Payment;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-18
 */
public interface PayMentService extends IService<Payment> {

    public int insert(Payment payment);

    public Payment getById(Long id);

}
