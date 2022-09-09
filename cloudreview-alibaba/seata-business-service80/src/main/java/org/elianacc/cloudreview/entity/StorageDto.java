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
public class StorageDto extends Storage implements Serializable {

    private static final long serialVersionUID = 1L;

    // 库存变动数量
    private Integer count;

}
