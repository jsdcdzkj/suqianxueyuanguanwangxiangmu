package com.jsdc.iotpt.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WaterPriceMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.WaterPrice;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class WaterPriceService extends BaseService<WaterPrice> {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WaterPriceMapper waterPriceMapper;


    public ResultInfo addWaterPrice(WaterPrice waterPrice) {
        SysUser sysUser = sysUserService.getUser();
        List<WaterPrice> waterPriceList = getWaterPrice() ;
        //第一次新增  后续修改
        if(waterPriceList.size()>0)
        {
            waterPrice.setId(waterPriceList.get(0).getId());
            waterPrice.setUpdateUser(sysUser.getId());
            waterPrice.setUpdateTime(new Date());
            waterPrice.updateById();
        } else {
            waterPrice.setCreateUser(sysUser.getId());
            waterPrice.setCreateUser(sysUser.getId());
            waterPrice.setCreateTime(new Date());
            waterPrice.setIsDel(0);
            waterPrice.insert();
        }


        return ResultInfo.success();
    }



    public  List<WaterPrice> getWaterPrice() {
        QueryWrapper<WaterPrice> queryWrapper = new QueryWrapper<>();
        List<WaterPrice> waterPriceList = waterPriceMapper.selectList(queryWrapper) ;
        return waterPriceList;
    }



}
