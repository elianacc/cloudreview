package org.elianacc.cloudreview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.elianacc.cloudreview.entity.Account;
import org.elianacc.cloudreview.entity.AccountDto;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
public interface AccountService extends IService<Account> {

    public int decrease(AccountDto dto);

}
