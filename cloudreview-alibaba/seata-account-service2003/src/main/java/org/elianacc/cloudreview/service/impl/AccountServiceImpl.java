package org.elianacc.cloudreview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elianacc.cloudreview.dao.AccountMapper;
import org.elianacc.cloudreview.entity.Account;
import org.elianacc.cloudreview.entity.AccountDto;
import org.elianacc.cloudreview.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public int decrease(AccountDto dto) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        Account account = accountMapper.selectOne(queryWrapper
                .eq("user_id", dto.getUserId()));
        if (account == null) {
            throw new RuntimeException("account not exist!");
        }
        if (account.getResidue().subtract(dto.getMoney()).compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("the residue is not enough!");
        }
        UpdateWrapper<Account> updateWrapper = new UpdateWrapper<>();
        return accountMapper.update(dto, updateWrapper
                .setSql("used = used+" + dto.getMoney())
                .setSql("residue = residue-" + dto.getMoney())
                .eq("user_id", dto.getUserId()));
    }
}
