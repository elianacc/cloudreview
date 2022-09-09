package org.elianacc.cloudreview.service.impl;

import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.elianacc.cloudreview.entity.AccountDto;
import org.elianacc.cloudreview.entity.Order;
import org.elianacc.cloudreview.entity.StorageDto;
import org.elianacc.cloudreview.feign.AccountFeignClient;
import org.elianacc.cloudreview.service.BusinessService;
import org.elianacc.cloudreview.feign.OrderFeignClient;
import org.elianacc.cloudreview.feign.StorageFeignClient;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private StorageFeignClient storageFeignClient;
    @Autowired
    private AccountFeignClient accountFeignClient;


    @GlobalTransactional(rollbackFor = Exception.class) // TM开启全局事务
    @Override
    public ApiResult orderCreate(Order order) {

        log.info("----->开始新建订单");
        ApiResult resultOrder = orderFeignClient.insert(order);
        if (resultOrder.getCode() != 200) {
            throw new RuntimeException("调用订单服务-插入订单异常");
        }
        log.info("----->插入订单end");

        log.info("----->扣减库存");
        StorageDto storageDto = new StorageDto();
        storageDto.setProductId(order.getProductId());
        storageDto.setCount(order.getCount());
        ApiResult resultStorage = storageFeignClient.decrease(storageDto);
        log.info(resultStorage.getMsg());
        if (resultStorage.getCode() != 200) {
            throw new RuntimeException("调用库存服务-减库存异常");
        }
        log.info("----->扣减库存end");

        log.info("----->扣减账户");
        AccountDto accountDto = new AccountDto();
        accountDto.setUserId(order.getUserId());
        accountDto.setMoney(order.getMoney());
        ApiResult resultAccount = accountFeignClient.decrease(accountDto);
        log.info(resultAccount.getMsg());
        if (resultAccount.getCode() != 200) {
            throw new RuntimeException("调用账户服务-扣减账户异常");
        }
        log.info("----->扣减账户end");

        log.info("----->订单状态更新");
        ApiResult resultOrder2 = orderFeignClient.updateStatus(order);
        if (resultOrder2.getCode() != 200) {
            throw new RuntimeException("调用订单服务-修改订单状态异常");
        }
        log.info("----->订单状态更新end");

        return ApiResult.success("下订单成功！！！！！");
    }
}
