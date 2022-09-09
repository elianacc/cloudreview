package org.elianacc.cloudreview.controller;

import org.elianacc.cloudreview.entity.AccountDto;
import org.elianacc.cloudreview.service.AccountService;
import org.elianacc.cloudreview.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PutMapping("/decrease")
    public ApiResult decrease(@RequestBody AccountDto dto) {
        accountService.decrease(dto);
        return ApiResult.success("扣减账户余额成功！");
    }

}
