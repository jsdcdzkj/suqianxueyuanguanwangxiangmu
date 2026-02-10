package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;

import com.jsdc.iotpt.mapper.FalseAlarmRecordsMapper;
import com.jsdc.iotpt.model.new_alarm.FalseAlarmRecords;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.FalseAlarmRecordsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FalseAlarmRecordsService extends BaseService<FalseAlarmRecords> {

    @Autowired
    private FalseAlarmRecordsMapper falseAlarmRecordsMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<FalseAlarmRecords> getPageList(FalseAlarmRecordsVo vo) {
        QueryWrapper<FalseAlarmRecords> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return falseAlarmRecordsMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<FalseAlarmRecords> getList(FalseAlarmRecords entity) {
        QueryWrapper<FalseAlarmRecords> queryWrapper = new QueryWrapper<>();
        return falseAlarmRecordsMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateFalseAlarmRecords(FalseAlarmRecords bean) {
        saveOrUpdate(bean);
        return ResultInfo.success();
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ResultInfo getEntityById(Integer id) {
        return ResultInfo.success(getById(id));
    }

}


