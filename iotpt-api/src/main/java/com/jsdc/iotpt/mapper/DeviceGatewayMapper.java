package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.DeviceGatewayDao;
import com.jsdc.iotpt.model.DeviceGateway;
import com.jsdc.iotpt.vo.DeviceGatewayVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface DeviceGatewayMapper extends BaseMapper<DeviceGateway> {

    @SelectProvider(type = DeviceGatewayDao.class, method = "getEntityList")
    Page<DeviceGateway> getEntityList(Page page, DeviceGateway bean);

    @SelectProvider(type = DeviceGatewayDao.class, method = "getPageList")
    Page<DeviceGatewayVo> getPageList(DeviceGatewayVo vo, Page page);

    @SelectProvider(type = DeviceGatewayDao.class, method = "getEntityById")
    DeviceGatewayVo getEntityById(Integer id);
}
