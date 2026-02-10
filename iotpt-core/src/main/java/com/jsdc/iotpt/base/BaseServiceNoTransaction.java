package com.jsdc.iotpt.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @projectName: ybpt
 * @className: BaseServiceNoTransaction
 * @author: wp
 * @description:
 * @date: 2023/3/2 10:04
 */
@Slf4j
public class BaseServiceNoTransaction <T> extends ServiceImpl<BaseMapper<T>,T> {

}
