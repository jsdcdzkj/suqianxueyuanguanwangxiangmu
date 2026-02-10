package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.JobPlanAreaMapper;
import com.jsdc.iotpt.model.operate.JobPlanArea;
import com.jsdc.iotpt.model.sys.SysBuild;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: JobPlanAreaService
 * Description:
 * date: 2023/8/24 11:00
 *
 * @author bn
 */
@Service
@Transactional
public class JobPlanAreaService extends BaseService<JobPlanArea> {

    @Autowired
    private JobPlanAreaMapper mapper;

    @Autowired
    private SysBuildService sysBuildService;

    @Autowired
    private SysBuildFloorService sysBuildFloorService;

    @Autowired
    private SysBuildAreaService sysBuildAreaService;

    public JSONArray getTreeBuild() {
        JSONArray allArray=new JSONArray();

        List<SysBuild> sysBuildList=sysBuildService.list(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel,0));

        sysBuildList.forEach(x->{
            JSONObject buildJson=new JSONObject();
            buildJson.put("id","BBBB"+x.getId());
            buildJson.put("label",x.getBuildName());
            buildJson.put("realId",x.getId());
            buildJson.put("type","buildId");
            List<SysBuildFloor> sysBuildFloors=sysBuildFloorService.list(Wrappers.<SysBuildFloor>lambdaQuery().
                    eq(SysBuildFloor::getIsDel,0).
                    eq(SysBuildFloor::getDictBuilding,x.getId()));
            JSONArray childFloor=new JSONArray();
            sysBuildFloors.forEach(y->{
                JSONObject floorJson=new JSONObject();
                floorJson.put("id","FFFF"+y.getId());
                floorJson.put("label",y.getFloorName());
                floorJson.put("realId",y.getId());
                floorJson.put("type","floorId");
                List<SysBuildArea> sysBuildAreas=sysBuildAreaService.list(Wrappers.<SysBuildArea>lambdaQuery().
                        eq(SysBuildArea::getIsDel,0).
                        eq(SysBuildArea::getFloorId,y.getId()));
                JSONArray areaArray=new JSONArray();
                sysBuildAreas.forEach(a->{
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("id","AAAA"+a.getId());
                    jsonObject.put("label",a.getAreaName());
                    jsonObject.put("realId",a.getId());
                    jsonObject.put("type","areaId");
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

    public JSONArray getTreeBuildDisable() {
        JSONArray allArray=new JSONArray();

        List<SysBuild> sysBuildList=sysBuildService.list(Wrappers.<SysBuild>lambdaQuery().
                eq(SysBuild::getIsDel,0));

        sysBuildList.forEach(x->{
            JSONObject buildJson=new JSONObject();
            buildJson.put("id","BBBB"+x.getId());
            buildJson.put("label",x.getBuildName());
            buildJson.put("realId",x.getId());
            buildJson.put("type","buildId");
            buildJson.put("disabled",true);
            List<SysBuildFloor> sysBuildFloors=sysBuildFloorService.list(Wrappers.<SysBuildFloor>lambdaQuery().
                    eq(SysBuildFloor::getIsDel,0).
                    eq(SysBuildFloor::getDictBuilding,x.getId()));
            JSONArray childFloor=new JSONArray();
            sysBuildFloors.forEach(y->{
                JSONObject floorJson=new JSONObject();
                floorJson.put("id","FFFF"+y.getId());
                floorJson.put("label",y.getFloorName());
                floorJson.put("realId",y.getId());
                floorJson.put("type","floorId");
                floorJson.put("disabled",true);
                List<SysBuildArea> sysBuildAreas=sysBuildAreaService.list(Wrappers.<SysBuildArea>lambdaQuery().
                        eq(SysBuildArea::getIsDel,0).
                        eq(SysBuildArea::getFloorId,y.getId()));
                JSONArray areaArray=new JSONArray();
                sysBuildAreas.forEach(a->{
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("id","AAAA"+a.getId());
                    jsonObject.put("label",a.getAreaName());
                    jsonObject.put("realId",a.getId());
                    jsonObject.put("type","areaId");
                    jsonObject.put("disabled",true);
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
