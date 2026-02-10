package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsdc.iotpt.dao.WalkPersonDao;
import com.jsdc.iotpt.model.WalkPerson;
import com.jsdc.iotpt.vo.WalkPersonVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;


import java.util.List;


@Mapper
public interface WalkPersonMapper extends BaseMapper<WalkPerson> {


    @SelectProvider(type = WalkPersonDao.class, method = "getPersonsByIds")
    List<WalkPerson> getPersonsByIds(String ids);


    @SelectProvider(type = WalkPersonDao.class, method = "getPersionListByDeptId")
    List<WalkPerson> getPersionListByDeptId(String deptId);


    @SelectProvider(type = WalkPersonDao.class, method = "getInnerUser")
    List<WalkPersonVo> getInnerUser(Integer deptId);

    @SelectProvider(type = WalkPersonDao.class, method = "getIUser")
    List<WalkPersonVo> getIUser(Integer deptId, String realName);
}
