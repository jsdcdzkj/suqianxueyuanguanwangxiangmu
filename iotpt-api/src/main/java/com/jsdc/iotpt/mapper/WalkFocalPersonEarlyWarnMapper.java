package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.WalkFocalPersonEarlyWarnDao;
import com.jsdc.iotpt.model.WalkFocalPersonEarlyWarn;
import com.jsdc.iotpt.vo.WalkCaptureVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface WalkFocalPersonEarlyWarnMapper extends BaseMapper<WalkFocalPersonEarlyWarn> {

    @SelectProvider(type = WalkFocalPersonEarlyWarnDao.class, method = "getList")
    Page<WalkCaptureVo> getList(Page page, Integer deptId, String startTime, String endTime);

    @SelectProvider(type = WalkFocalPersonEarlyWarnDao.class, method = "getListInfo")
    Page<WalkCaptureVo> getListInfo(Page page, Integer deptId);
}
