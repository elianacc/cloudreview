package org.elianacc.cloudreview.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.elianacc.cloudreview.entity.Storage;
import org.elianacc.cloudreview.entity.StorageDto;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
public interface StorageService extends IService<Storage> {

    public int decrease(StorageDto dto);

}
