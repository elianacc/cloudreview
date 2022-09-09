package org.elianacc.cloudreview.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@Data
public class Storage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 总库存
     */
    private Integer total;

    /**
     * 已用库存
     */
    private Integer used;

    /**
     * 剩余库存
     */
    private Integer residue;

}
