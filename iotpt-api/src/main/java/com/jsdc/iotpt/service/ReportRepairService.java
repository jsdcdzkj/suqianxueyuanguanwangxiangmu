package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.ReportRepairMapper;
import com.jsdc.iotpt.model.ReportRepair;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.model.operate.JobPlan;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.vo.ReportRepairVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * ClassName: ReportRepairService
 * Description:
 * date: 2023/11/22 13:50
 *
 * @author bn
 */
@Service
@Transactional
public class ReportRepairService extends BaseService<ReportRepair> {

    @Autowired
    private ReportRepairMapper reportRepairMapper;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private SysBuildService sysBuildService;
    @Autowired
    private SysBuildFloorService sysBuildFloorService;
    @Autowired
    private SysBuildAreaService sysBuildAreaService;
    @Autowired
    private SysDictService sysDictService;

    @Value("${jsdc.nginxPath}")
    private String nginxPath;


    private QueryWrapper getWrapper(ReportRepair bean) {

        QueryWrapper queryWrapper=new QueryWrapper();

        queryWrapper.eq("isDel",0);

        queryWrapper.orderByDesc("reportTime");


        return queryWrapper;
    }


    public Page<ReportRepair> getPageList(Integer pageIndex, Integer pageSize, ReportRepair bean) {
        Page page = new Page(pageIndex, pageSize);

        Page<ReportRepair> p = reportRepairMapper.selectPage(page, getWrapper(bean));

        p.getRecords().forEach(x->{

            if (null!=x.getUrgency()){
                SysDict sysDict=sysDictService.selectByValueAndType("appEmergent",x.getUrgency());
                x.setUrgencyName(sysDict!=null?sysDict.getDictLabel():"");
            }

            if (null!=x.getReportType()){
                SysDict sysDict=sysDictService.selectByValueAndType("appReport",x.getReportType());
                x.setReportTypeName(sysDict!=null?sysDict.getDictLabel():"");
            }

            if (null!=x.getAreaId()&&null!=x.getAreaType()){
                if (x.getAreaType().equals("0")){
                    SysBuild sysBuild=sysBuildService.getById(x.getAreaId());
                    x.setAreaName(sysBuild!=null?sysBuild.getBuildName():"");
                }else if (x.getAreaType().equals("1")){
                    SysBuildFloor sysBuildFloor=sysBuildFloorService.getById(x.getAreaId());
                    x.setAreaName(sysBuildFloor!=null?sysBuildFloor.getFloorName():"");
                }else if (x.getAreaType().equals("2")){
                    SysBuildArea sysBuildArea=sysBuildAreaService.getById(x.getAreaId());
                    x.setAreaName(sysBuildArea!=null?sysBuildArea.getAreaName():"");
                }
            }


            List<SysFile> sysFiles=sysFileService.list(Wrappers.<SysFile>lambdaQuery().
                    eq(SysFile::getBizType,"8").
                    eq(SysFile::getBizId,x.getId()+""));

            sysFiles.forEach(y->{
                y.setFileUrl(nginxPath+y.getFileUrl());
            });

            x.setSysFiles(sysFiles);

        });

        return p;
    }


    public ResultInfo saveReportRepair(ReportRepairVo bean) {

        bean.setHandleStatus(1);
        bean.setIsDel(0);
        bean.setCreateTime(new Date());
        bean.setReportTime(new Date());

        reportRepairMapper.insert(bean);

        bean.getSysFiles().forEach(x->{
            x.setBizType("8");
            x.setBizId(String.valueOf(bean.getId()));
            x.setCreateTime(new Date());
            sysFileService.saveOrUpdate(x);
        });


        return ResultInfo.success();
    }

    public JSONArray getTreeBuild() {
        JSONArray allArray=new JSONArray();

        List<SysBuild> sysBuildList=sysBuildService.list(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel,0));

        sysBuildList.forEach(x->{
            JSONObject buildJson=new JSONObject();
            buildJson.put("id","BBBB"+x.getId());
            buildJson.put("label",x.getBuildName());
            buildJson.put("realId",x.getId());
            buildJson.put("type",0);
            List<SysBuildFloor> sysBuildFloors=sysBuildFloorService.list(Wrappers.<SysBuildFloor>lambdaQuery().
                    eq(SysBuildFloor::getIsDel,0).
                    eq(SysBuildFloor::getDictBuilding,x.getId()).orderByAsc(SysBuildFloor::getSort));
            JSONArray childFloor=new JSONArray();
            sysBuildFloors.forEach(y->{
                JSONObject floorJson=new JSONObject();
                floorJson.put("id","FFFF"+y.getId());
                floorJson.put("label",y.getFloorName());
                floorJson.put("realId",y.getId());
                floorJson.put("type",1);
                List<SysBuildArea> sysBuildAreas=sysBuildAreaService.list(Wrappers.<SysBuildArea>lambdaQuery().
                        eq(SysBuildArea::getIsDel,0).
                        eq(SysBuildArea::getFloorId,y.getId()));
                JSONArray areaArray=new JSONArray();
                sysBuildAreas.forEach(a->{
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("id","AAAA"+a.getId());
                    jsonObject.put("label",a.getAreaName());
                    jsonObject.put("realId",a.getId());
                    jsonObject.put("type",2);
                    areaArray.add(jsonObject);
                });

                floorJson.put("children",areaArray);
                childFloor.add(floorJson);
            });
            buildJson.put("children",childFloor);
            allArray.add(buildJson);
        });


        return allArray;
    }
}
