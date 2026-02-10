package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.WalkFocalPersonEarlyWarnMapper;
import com.jsdc.iotpt.model.WalkFocalPersonEarlyWarn;
import com.jsdc.iotpt.util.FileUtils;
import com.jsdc.iotpt.vo.WalkCaptureVo;
import com.jsdc.iotpt.vo.WalkFocalPersonEarlyWarnVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WalkFocalPersonEarlyWarnService extends BaseService<WalkFocalPersonEarlyWarn> {

    @Autowired
    private WalkFocalPersonEarlyWarnMapper focalPersonEarlyWarnMapper;


    /**
     * 抓拍记录查询
     *
     * @param pageNo
     * @param pageSize
     * @param bean
     * @return
     */
    public Page<WalkCaptureVo> snapRecord(Integer pageNo, Integer pageSize, WalkCaptureVo bean) {
        Page<WalkCaptureVo> page = new Page<>(pageNo, pageSize);
        Page<WalkCaptureVo> pageList = focalPersonEarlyWarnMapper.getList(page, bean.getDeptId(), bean.getStartTime(), bean.getEndTime());
        pageList.getRecords().forEach(a -> {
            a.setBase64(FileUtils.filePathToBase64(a.getFilePath()));
        });
        return pageList;
    }


    public Page<WalkFocalPersonEarlyWarn> findAll(WalkFocalPersonEarlyWarnVo vo) {
        QueryWrapper<WalkFocalPersonEarlyWarn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDel", 0);
        if (vo != null) {
            if (Strings.isNotEmpty(vo.getEarlyWarnType())) {
                queryWrapper.eq("earlyWarnType", vo.getEarlyWarnType());
            }
            if (Strings.isNotEmpty(vo.getLabel())) {
                queryWrapper.eq("label", vo.getLabel());
            }
            if (vo.getDeptId() != null) {
                queryWrapper.eq("deptId", vo.getDeptId());
            }
            if (vo.getEquipmentId() != null) {
                queryWrapper.eq("equipmentId", vo.getEquipmentId());
            }
            if (vo.getUserId() != null) {
                queryWrapper.eq("userId", vo.getUserId());
            }
            if (vo.getFaceId() != null) {
                queryWrapper.eq("faceId", vo.getFaceId());
            }
        }
        queryWrapper.eq("isDelete", 0);
        return focalPersonEarlyWarnMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);
    }


    /**
     * getListInfo 比getList多一个位置信息
     *
     * @param pageNo
     * @param pageSize
     * @param deptId
     * @return
     */
    public Page<WalkCaptureVo> getListInfo(Integer pageNo, Integer pageSize, Integer deptId) {
        Page<WalkCaptureVo> page = new Page<WalkCaptureVo>(pageNo, pageSize);
        Page<WalkCaptureVo> pageList = focalPersonEarlyWarnMapper.getListInfo(page, deptId);
        return pageList;
    }

}
