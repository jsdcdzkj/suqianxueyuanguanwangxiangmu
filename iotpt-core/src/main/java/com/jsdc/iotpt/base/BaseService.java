package com.jsdc.iotpt.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName BaseService
 * @Description TODO
 * @Author xujian
 * @Date 2022/3/28 12:59
 * @Version 1.0
 */
@Slf4j
public class BaseService<T> extends ServiceImpl<BaseMapper<T>,T> {
}
