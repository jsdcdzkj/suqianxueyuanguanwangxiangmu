package com.jsdc.iotpt.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dahuatech.icc.exception.ClientException;
import com.jsdc.iotpt.model.NameList;
import com.jsdc.iotpt.model.Stranger;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.service.NameListService;
import com.jsdc.iotpt.service.StrangerService;
import com.jsdc.iotpt.service.SysFileService;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UploadParams;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * 黑白名单
 *
 * @author wangYan
 * @since 2023-10-13
 */
@RestController
@RequestMapping("/app/nameList")
@Api(tags = "黑白名单")
public class NameListController {
    /**
     * 服务对象
     */
    @Resource
    private NameListService nameListService;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private StrangerService strangerService;

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
            List<SysFile> sysFile = sysFileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizType, "5").eq(SysFile::getBizId, nl.getId()+""));
            if (sysFile != null && !sysFile.isEmpty()) {
                nl.setFileUrl(sysFile.get(sysFile.size() - 1).getFileUrl());
                nl.setFileNew(sysFile.get(sysFile.size() - 1).getFileUrl());
                nl.setFileOld(sysFile.get(sysFile.size() - 1).getFileUrl());
            }
            StringJoiner joiner = new StringJoiner(", ");
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
     * 新增修改数据
     *
     * @param nameList 实体对象
     * @return 新增结果
     */
    @RequestMapping("/insertOrUpdate")
    public ResultInfo insertOrUpdate(@RequestBody NameList nameList) throws Exception {
        return ResultInfo.success(this.nameListService.insertOrUpdate(nameList));
    }


    /**
     * 加入黑白名单
     *
     * @param nameList
     * @param file
     * @return
     */
    @RequestMapping("/saveOrUpdStranger")
    public ResultInfo saveOrUpdStranger(@RequestPart NameList nameList, @RequestPart(value = "file", required = false) MultipartFile file) throws ClientException {
        return ResultInfo.success(this.nameListService.insertOrUpdate(nameList));
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 新增结果
     */
    @RequestMapping("/uploadFile")
    public ResultInfo updateFile(@RequestPart(value = "file", required = false) MultipartFile file) {
        SysFile sysFile = sysFileService.uploadFiles(new UploadParams("/nameList", file));
        sysFile.insert();
        return ResultInfo.success(sysFile);
    }


    /**
     * 新增数据
     *
     * @param nameList 实体对象
     * @return 新增结果
     */
    @RequestMapping("/insert")
    public ResultInfo insert(@RequestBody NameList nameList) {
        nameList.setIsDel(0);
        nameList.setCreateTime(new Date());
        this.nameListService.save(nameList);
        if (StringUtils.isNotEmpty(nameList.getFileUrl())) {
            SysFile sysFile = sysFile = sysFileService.getOne(new LambdaQueryWrapper<SysFile>().eq(SysFile::getFileUrl, nameList.getFileUrl()));
            if (sysFile != null) {
                sysFile.setBizId(String.valueOf(nameList.getId()));
                sysFile.setBizType("5");
                sysFile.updateById();
            }
        }
        if (null != nameList.getStranger()) {
            Stranger stranger = strangerService.getById(nameList.getStranger());
            stranger.setIs_nameList(1);
            strangerService.updateById(stranger);
        }
        return ResultInfo.success(nameList);
    }

    /**
     * 修改数据
     *
     * @param nameList 实体对象
     * @return 修改结果
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody NameList nameList) {
        nameList.setUpdateTime(new Date());
        if (StringUtils.isNotEmpty(nameList.getFileUrl())) {
            SysFile sysFile = sysFile = sysFileService.getOne(new LambdaQueryWrapper<SysFile>().eq(SysFile::getFileUrl, nameList.getFileUrl()));
            if (sysFile != null) {
                sysFile.setBizId(String.valueOf(nameList.getId()));
                sysFile.setBizType("5");
                sysFile.updateById();
            }
        }
        if (null != nameList.getStranger()) {
            Stranger stranger = strangerService.getById(nameList.getStranger());
            stranger.setIs_nameList(1);
            strangerService.updateById(stranger);
        }
        return ResultInfo.success(this.nameListService.updateById(nameList));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @RequestMapping("/del")
    public ResultInfo del(Integer id) throws ClientException {
        NameList nameList = new NameList();
        nameList.setId(id);
        nameList.setIsDel(1);
        NameList byId = nameListService.getById(id);
        return ResultInfo.success(this.nameListService.updateById(nameList));
    }

    /**
     * 启用禁用数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @RequestMapping("/enableDisable")
    public ResultInfo enableDisable(Integer id, Integer isEnable) {
        NameList nameList = new NameList();
        nameList.setId(id);
        nameList.setIsEnable(isEnable);
        return ResultInfo.success(this.nameListService.updateById(nameList));
    }

    /**
     * 获取详情
     *
     * @param id 主键
     * @return 删除结果
     */
    @RequestMapping("/getById")
    public ResultInfo getById(Integer id) {
        List<SysFile> sysFile = sysFile = sysFileService.list(new LambdaQueryWrapper<SysFile>().eq(SysFile::getBizType, "5").eq(SysFile::getBizId, id+""));
        NameList namelist = this.nameListService.getById(id);
        if (!sysFile.isEmpty()) {
            namelist.setFileUrl(sysFile.get(sysFile.size() - 1).getFileUrl());
            namelist.setFileNew(sysFile.get(sysFile.size() - 1).getFileUrl());
            namelist.setFileOld(sysFile.get(sysFile.size() - 1).getFileUrl());
        }
        return ResultInfo.success(namelist);
    }
}

