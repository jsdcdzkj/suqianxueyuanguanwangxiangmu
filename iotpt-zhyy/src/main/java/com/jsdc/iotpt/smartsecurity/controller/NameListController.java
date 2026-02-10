package com.jsdc.iotpt.smartsecurity.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dahuatech.icc.exception.ClientException;
import com.jsdc.iotpt.model.NameList;
import com.jsdc.iotpt.service.NameListService;
import com.jsdc.iotpt.service.SysBuildAreaService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.StringJoiner;

/**
 * (NameList)表控制层
 *
 * @author wangYan
 * @since 2023-10-13
 */
@RestController
@RequestMapping("/nameList")
public class NameListController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 服务对象
     */
    @Resource
    private NameListService nameListService;
    @Autowired
    private SysBuildAreaService areaService;


    /**
     * 分页查询所有数据
     *
     * @param nameList 查询实体
     * @return 所有数据
     */
    @RequestMapping("/getPage")
    public ResultInfo getPage(NameList nameList) {
        LambdaQueryWrapper wrapperPage = new LambdaQueryWrapper<NameList>()
                .like(StringUtils.isNotEmpty(nameList.getName()), NameList::getName, nameList.getName())
                .eq(null != nameList.getSex(), NameList::getSex, nameList.getSex())
                .eq(null != nameList.getIsEnable(), NameList::getIsEnable, nameList.getIsEnable())
                .eq(null != nameList.getType(), NameList::getType, nameList.getType())
                .eq(NameList::getIsDel, 0)
                .orderByDesc(NameList::getCreateTime);
        Page<NameList> records = this.nameListService.page(new Page<>(nameList.getPageIndex(), nameList.getPageSize()), wrapperPage);

        for (int i = 0; i < records.getRecords().size(); i++) {
            NameList nl = records.getRecords().get(i);
            StringJoiner joiner = new StringJoiner(", ");
            if (StringUtils.isNotEmpty(nl.getDoorIds())) {
                String[] doorIds = nl.getDoorIds().split(",");

            }
            nl.setDoorName(joiner.toString());
        }
        return ResultInfo.success(records);
    }
    /**
     * 获取文件信息
     *
     * @param id 主键
     * @return 单条数据
     */
    @RequestMapping("/getFile")
    public ResultInfo getFile(Integer id) {
        return ResultInfo.success(this.nameListService.getFile(id));
    }

    /**
     * 新增数据
     *
     * @param nameList 实体对象
     * @return 新增结果
     */
    @RequestMapping("/insertOrUpdate")
    public ResultInfo insert(@RequestPart NameList nameList, @RequestPart(value = "file", required = false) MultipartFile file) throws ClientException {
        return ResultInfo.success(this.nameListService.insertOrUpdate(nameList));
    }

    /**
     * 加入黑白名单
     * @param nameList
     * @param file
     * @return
     */
    @RequestMapping("/saveOrUpdStranger")
    public ResultInfo saveOrUpdStranger(@RequestPart NameList nameList, @RequestPart(value = "file", required = false) MultipartFile file) throws ClientException {
        return ResultInfo.success(this.nameListService.insertOrUpdate(nameList));
    }


    /**
     * 修改数据
     *
     * @param nameList 实体对象
     * @return 修改结果
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody NameList nameList) {
        return ResultInfo.success(this.nameListService.updateById(nameList));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @RequestMapping("/del")
    public ResultInfo del(Integer id) {
        NameList nameList = new NameList();
        nameList.setId(id);
        nameList.setIsDel(1);
        return ResultInfo.success(this.nameListService.updateById(nameList));
    }

    /**
     * 启用禁用数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @RequestMapping("/enableDisable")
    public ResultInfo enableDisable(Integer id,Integer isEnable) {
        NameList nameList = new NameList();
        nameList.setId(id);
        nameList.setIsEnable(isEnable);
        return ResultInfo.success(this.nameListService.updateById(nameList));
    }








    //删除黑名单
    @RequestMapping("/delect")
    public ResultInfo delect(NameList nameList){
        return  nameListService.delect(nameList);
    }
}

