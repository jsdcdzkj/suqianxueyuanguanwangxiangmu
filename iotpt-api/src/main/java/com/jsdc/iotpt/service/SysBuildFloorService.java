package com.jsdc.iotpt.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.SysBuildAreaMapper;
import com.jsdc.iotpt.mapper.SysBuildFloorMapper;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysBuildArea;
import com.jsdc.iotpt.model.sys.SysBuildFloor;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.DeviceQueryVo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UploadParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class SysBuildFloorService extends BaseService<SysBuildFloor> {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysBuildFloorMapper sysBuildFloorMapper;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private SysBuildAreaMapper sysBuildAreaMapper;

    @Autowired
    MemoryCacheService memoryCacheService;

    /**
     * 楼层新增
     * Author wzn
     * Date 2023/5/9 9:01
     */
    public void addSysBuildFloor(SysBuildFloor sysBuildFloor) {
        SysUser sysUser = sysUserService.getUser();
        sysBuildFloor.setCreateUser(sysUser.getId());
        sysBuildFloor.setCreateTime(new Date());
        sysBuildFloor.setIsDel(0);
        sysBuildFloor.insert();
        if (sysBuildFloor.getFile() != null) {
            sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId", sysBuildFloor.getId() + "").eq("bizType", "1"));
            UploadParams params = new UploadParams("/build", sysBuildFloor.getFile(), "1", sysBuildFloor.getId() + "");
            sysFileService.uploadFile(params);
        }

        memoryCacheService.putAllFloor();
    }

    /**
     * 楼层修改
     * Author wzn
     * Date 2023/5/9 9:06
     */
    public void updateSysBuildFloor(SysBuildFloor sysBuildFloor) {
        SysBuildFloor sysBuildFloor1 = sysBuildFloorMapper.selectById(sysBuildFloor.getId());
        SysUser sysUser = sysUserService.getUser();
        if (sysBuildFloor.getFile() != null) {
            sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId", sysBuildFloor.getId() + "").eq("bizType", "1"));
            UploadParams params = new UploadParams("/build", sysBuildFloor.getFile(), "1", sysBuildFloor.getId() + "");
            sysFileService.uploadFile(params);
        } else {
            if (sysBuildFloor.getDelPic()) {
                sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId", sysBuildFloor.getId() + "").eq("bizType", "1"));
            }
        }
        sysBuildFloor1.setFloorName(sysBuildFloor.getFloorName());
        sysBuildFloor1.setSort(sysBuildFloor.getSort());
        sysBuildFloor1.setMemo(sysBuildFloor.getMemo());
        sysBuildFloor1.setIsLargeScreenDisplay(sysBuildFloor.getIsLargeScreenDisplay());
        sysBuildFloor1.setUpdateUser(sysUser.getId());
        sysBuildFloor1.setUpdateTime(new Date());
        sysBuildFloor1.setFilename(sysBuildFloor.getFilename());
        sysBuildFloor1.setOriginalFilename(sysBuildFloor.getOriginalFilename());

        sysBuildFloor1.updateById();

        memoryCacheService.putAllFloor();
    }


    /**
     * 楼层列表
     * Author wzn
     * Date 2023/5/9 9:09
     */
    public Page<SysBuildFloor> selectBuildFloorList(SysBuildFloor sysBuildFloor) {
        Page<SysBuildFloor> page = new Page<>(sysBuildFloor.getPageNo(), sysBuildFloor.getPageSize());
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (!"".equals(sysBuildFloor.getFloorName()) && null != sysBuildFloor.getFloorName()) {
            queryWrapper.like("floorName", sysBuildFloor.getFloorName());
        }
        if (!"".equals(sysBuildFloor.getDictBuilding()) && null != sysBuildFloor.getDictBuilding()) {
            queryWrapper.like("dictBuilding", sysBuildFloor.getDictBuilding());
        }
        queryWrapper.orderByDesc("createTime");
        Page<SysBuildFloor> sysBuildFloorPage = sysBuildFloorMapper.selectPage(page, queryWrapper);
        return sysBuildFloorPage;
    }


    /**
     * 楼层详情
     * Author wzn
     * Date 2023/5/9 9:10
     */
    public ResultInfo info(String id) {
        JSONObject jsonObject = new JSONObject();
        SysBuildFloor sysBuildFloor = sysBuildFloorMapper.selectById(id);
        List<SysFile> fileList = sysFileService.list(new QueryWrapper<SysFile>().eq("bizType", "1").eq("bizId", sysBuildFloor.getId()));
        jsonObject.put("sysBuildFloor", sysBuildFloor);
        jsonObject.put("fileList", fileList);
        return ResultInfo.success(jsonObject);
    }

    /**
     * 楼层删除查是否有区域
     * Author wzn
     * Date 2023/7/11 9:09
     */
    public boolean exist(String id) {
        boolean exist = false;
        //查区域
        QueryWrapper<SysBuildArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("floorId", id);
        queryWrapper.eq("isDel", 0);
        Long count = sysBuildAreaMapper.selectCount(queryWrapper);
        if (count != 0) {
            exist = true;
        }
        return exist;
    }

    public List<SysBuildFloor> getList(SysBuildFloor vo) {
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        return sysBuildFloorMapper.selectList(queryWrapper);
    }


    public List<SysBuildFloor> getList4GS(SysBuildFloor vo) {
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.eq("isLargeScreenDisplay", 1);
        return sysBuildFloorMapper.selectList(queryWrapper);
    }

    /**
     * 根据楼宇id查询楼层列表
     */
    public List<SysBuildFloor> getListByBuild(SysBuildFloor vo) {
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (StringUtils.isNotNull(vo.getDictBuilding())) {
            queryWrapper.eq("dictBuilding", vo.getDictBuilding());
        }
        if (null!=vo.getId()) {
            queryWrapper.eq("id", vo.getId());
        }
        queryWrapper.orderByAsc("sort");
        return sysBuildFloorMapper.selectList(queryWrapper);
    }

    /**
     * 所有楼层列表
     */
    public JSONObject getFloorList(SysBuildFloor bean) {
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<SysBuildFloor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        queryWrapper.orderByAsc("sort");
        List<SysBuildFloor> list = sysBuildFloorMapper.selectList(queryWrapper);
        Integer personnelNumber = 0;
        BigDecimal floorArea = new BigDecimal(0);
        String occupantDensity = "0";

        for (SysBuildFloor floor : list) {
            if (StringUtils.isNotNull(floor.getPersonnelNumber())) {
                personnelNumber = personnelNumber + floor.getPersonnelNumber();
            }
            if (StringUtils.isNotEmpty(floor.getFloorArea())) {
                floorArea = floorArea.add(new BigDecimal(floor.getFloorArea()));
            }
        }
        jsonObject.put("list", list);

        //人员数量
        jsonObject.put("personnelNumber", personnelNumber);
        //楼层面积
        jsonObject.put("floorArea", floorArea);
        //人员密度
        if (personnelNumber > 0 && floorArea.compareTo(BigDecimal.ZERO) > 0) {
            occupantDensity = new BigDecimal(personnelNumber.toString()).divide(floorArea, 2, BigDecimal.ROUND_HALF_UP).toString();
        }
        jsonObject.put("occupantDensity", occupantDensity);
        return jsonObject;
    }

    /**
     * 所有楼层列表
     */
    public void updFloorList(DeviceQueryVo vo) {
        if (StringUtils.isNotNull(vo.getSysBuildFloorList()) && vo.getSysBuildFloorList().size() > 0) {
            for (SysBuildFloor floor : vo.getSysBuildFloorList()) {
                SysBuildFloor sysBuildFloor = new SysBuildFloor();
                sysBuildFloor.setId(floor.getId());
                sysBuildFloor.setPersonnelNumber(floor.getPersonnelNumber());
                sysBuildFloor.setFloorArea(floor.getFloorArea());
                sysBuildFloor.setOccupantDensity(floor.getOccupantDensity());
                updateById(sysBuildFloor);
            }
        }
    }

}
