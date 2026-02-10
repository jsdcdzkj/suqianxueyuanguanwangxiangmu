package com.jsdc.iotpt.smartsecurity.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.Vehicle;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.SysDictService;
import com.jsdc.iotpt.service.VehicleService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 车辆布控
 */
@RestController
@RequestMapping("/vehicle")
public class VehicleController {
    /**
     * 服务对象
     */
    @Resource
    private VehicleService vehicleService;
    @Autowired
    private SysDictService sysDictService;

    /**
     * 分页查询所有数据
     *
     * @param vehicle 查询实体
     * @return 所有数据
     */
    @RequestMapping("/getPage")
    public ResultInfo getPage(Vehicle vehicle) {
        Date timeStart = null;
        Date timeEnd = null;
        if(StringUtils.isNotEmpty(vehicle.getTimeStart())){
            timeStart = DateUtil.parseDate(vehicle.getTimeStart());
        }
        if(StringUtils.isNotEmpty(vehicle.getTimeEnd())){
            timeEnd = DateUtil.parseDate(vehicle.getTimeEnd());
        }
        LambdaQueryWrapper wrapperPage = new LambdaQueryWrapper<Vehicle>()
                .like(StringUtils.isNotEmpty(vehicle.getCode()), Vehicle::getCode, vehicle.getCode())
                .eq(null != vehicle.getIsEnable(), Vehicle::getIsEnable, vehicle.getIsEnable())
                .eq(null != vehicle.getType(), Vehicle::getType, vehicle.getType())
                .eq(null != vehicle.getNameListType(), Vehicle::getNameListType, vehicle.getNameListType())
                .between(timeStart != null && timeEnd != null, Vehicle::getUpdateTime,timeStart,timeEnd)
                .eq(Vehicle::getIsDel, 0)
                .orderByDesc(Vehicle::getCreateTime);
        Page<Vehicle> records = this.vehicleService.page(new Page<>(vehicle.getPageIndex(), vehicle.getPageSize()), wrapperPage);


        SysDict sysDict = new SysDict();
        sysDict.setDictType("vehicleType");
        List<SysDict> sysDictList = sysDictService.getList(sysDict);
        Map<String, String> dictMap = sysDictList.stream().collect(Collectors.toMap(SysDict::getDictValue, SysDict::getDictLabel));

        for (int i = 0; i < records.getRecords().size(); i++) {
            Vehicle v = records.getRecords().get(i);

            StringJoiner joiner = new StringJoiner(",");
            if (StringUtils.isNotEmpty(v.getDoorIds())) {
                String[] doorIds = v.getDoorIds().split(",");

            }
            v.setDoorName(joiner.toString());

            if (null != v.getType()) {
                v.setTypeName(dictMap.get(String.valueOf(v.getType())));
            }
        }
        return ResultInfo.success(records);
    }

    /**
     * 新增修改数据
     *
     * @param vehicle 实体对象
     * @return 新增结果
     */
    @RequestMapping("/insertOrUpdate")
    public ResultInfo insert(Vehicle vehicle, @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResultInfo.success(this.vehicleService.insertOrUpdate(vehicle, file));
    }


    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @RequestMapping("/del")
    public ResultInfo del(Integer id) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setIsDel(1);
        return ResultInfo.success(this.vehicleService.updateById(vehicle));
    }

    /**
     * 启用禁用数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @RequestMapping("/enableDisable")
    public ResultInfo enableDisable(Integer id, Integer isEnable) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setIsEnable(isEnable);
        return ResultInfo.success(this.vehicleService.updateById(vehicle));
    }

    /**
     * 获取文件信息
     *
     * @param id 主键
     * @return 单条数据
     */
    @RequestMapping("/getFile")
    public ResultInfo getFile(Integer id) {
        return ResultInfo.success(this.vehicleService.getFile(id));
    }
}

