package org.elianacc.cloudreview.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@Data
public class AccountDto extends Account implements Serializable {

    private static final long serialVersionUID = 1L;

    // 变动金钱
    private BigDecimal money;

}
