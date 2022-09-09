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
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 总额度
     */
    private BigDecimal total;

    /**
     * 已用额度
     */
    private BigDecimal used;

    /**
     * 剩余额度
     */
    private BigDecimal residue;
}
