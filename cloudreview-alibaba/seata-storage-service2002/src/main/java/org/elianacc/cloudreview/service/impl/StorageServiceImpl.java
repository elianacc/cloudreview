package org.elianacc.cloudreview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elianacc.cloudreview.dao.StorageMapper;
import org.elianacc.cloudreview.entity.Storage;
import org.elianacc.cloudreview.entity.StorageDto;
import org.elianacc.cloudreview.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述
 *
 * @author ELiaNaCc
 * @since 2022-09-06
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService {

    @Autowired
    private StorageMapper storageMapper;


    @Override
    public int decrease(StorageDto dto) {
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        Storage storage = storageMapper.selectOne(queryWrapper
                .eq("product_id", dto.getProductId()));
        if (storage == null) {
            throw new RuntimeException("product not exist!");
        }
        // 没有库存剩余了
        if (storage.getResidue() - dto.getCount() < 0) {
            throw new RuntimeException("product storage is no remaining!");
        }
        UpdateWrapper<Storage> updateWrapper = new UpdateWrapper<>();
        return storageMapper.update(dto, updateWrapper
                .setSql("used = used+" + dto.getCount())
                .setSql("residue = residue-" + dto.getCount())
                .eq("product_id ", dto.getProductId()));
    }
}
