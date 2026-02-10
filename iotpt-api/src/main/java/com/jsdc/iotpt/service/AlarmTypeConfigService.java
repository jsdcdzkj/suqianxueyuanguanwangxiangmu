package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;

import com.jsdc.iotpt.mapper.AlarmTypeConfigMapper;
import com.jsdc.iotpt.model.new_alarm.AlarmTypeConfig;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.AlarmTypeConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AlarmTypeConfigService extends BaseService<AlarmTypeConfig> {

    @Autowired
    private AlarmTypeConfigMapper alarmTypeConfigMapper;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<AlarmTypeConfig> getPageList(AlarmTypeConfigVo vo) {
        QueryWrapper<AlarmTypeConfig> queryWrapper = new QueryWrapper<>();

        //testMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        return alarmTypeConfigMapper.selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()), queryWrapper);

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<AlarmTypeConfig> getList(AlarmTypeConfig entity) {
        QueryWrapper<AlarmTypeConfig> queryWrapper = new QueryWrapper<>();
        return alarmTypeConfigMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateAlarmTypeConfig(AlarmTypeConfig bean) {
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


