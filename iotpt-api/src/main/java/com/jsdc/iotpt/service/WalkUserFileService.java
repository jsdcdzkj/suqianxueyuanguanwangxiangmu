package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.model.WalkUserFile;
import com.jsdc.iotpt.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WalkUserFileService extends BaseService<WalkUserFile> {


    /**
     * 根据条件查询数据
     *
     * @param faceId
     * @return
     */
    public WalkUserFile selectByFaceId(String faceId) {
        LambdaQueryWrapper<WalkUserFile> queryWrapper = Wrappers.<WalkUserFile>lambdaQuery()
                .eq(StringUtils.isNotEmpty(faceId), WalkUserFile::getFaceId,faceId);
        return getOne(queryWrapper);
    }
}
