package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.enums.AbnormalPatrolEnum;
import com.jsdc.iotpt.mapper.AbnormalPatrolMapper;
import com.jsdc.iotpt.mapper.MissionAssignMapper;
import com.jsdc.iotpt.model.operate.AbnormalPatrol;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.model.operate.MissionAssign;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@Slf4j
public class AbnormalPatrolService extends BaseService<AbnormalPatrol> {
    @Autowired
    private AbnormalPatrolMapper mapper;

    @Autowired
    private MissionAssignMapper missionAssignMapper;

    @Autowired
    private MissionService missionService;

    @Autowired
    private SysUserService sysUserService;

    public Page<AbnormalPatrol> getPageList(AbnormalPatrol vo, Integer pageIndex, Integer pageSize) {
        Page<AbnormalPatrol> page = new Page<>(pageIndex, pageSize);
        Page<AbnormalPatrol> pageList = this.mapper.getPageList(page, vo);
        HashMap<String, SysDict> taskTypes = RedisDictDataUtil.getDictByType("taskTypes");
        for (AbnormalPatrol record : pageList.getRecords()) {
            Integer taskType = record.getTaskType();
            SysDict sysDict = taskTypes.get(String.valueOf(taskType));
            record.setTaskTypeName(sysDict.getDictLabel());
        }
        return pageList;
    }

    /**
     * @Author: yc
     * @Params: 必填项: abnormalDescription, deviceName, deviceId, abnormalLevel, missionId
     * @Return: bool
     * @Description：生成巡检异常接口  返回true,则成功生成一条异常
     * @Date ：2023/8/24 下午 1:38
     * @Modified By：
     */
    public boolean generateAbnormalPatrol(AbnormalPatrol bean) throws CustomException{

        if (!StringUtils.hasText(bean.getAbnormalDescription())) {
            throw new CustomException("缺少异常描述");
        }
        if (!StringUtils.hasText(bean.getDeviceName())) {
            throw new CustomException("缺少异常对象(设备名称)deviceName");
        }
        if (null == bean.getDeviceId()) {
            throw new CustomException("缺少异常对象(设备)deviceId");
        }
        if (null == bean.getAbnormalLevel()) {
            throw new CustomException("缺少异常等级");
        }
        LambdaQueryWrapper<MissionAssign> missionAssignLambdaQueryWrapper = new LambdaQueryWrapper<>();
        missionAssignLambdaQueryWrapper.eq(MissionAssign::getId, bean.getMissionId());
        List<MissionAssign> missionAssignDB = this.missionAssignMapper.selectList(missionAssignLambdaQueryWrapper);
        if (missionAssignDB.isEmpty()) {
            throw new CustomException("在missionAssign中无法查询到对应的missionId");
        }

        AbnormalPatrol abnormalPatrol = new AbnormalPatrol();
        abnormalPatrol.setAbnormalDescription(bean.getAbnormalDescription());
        abnormalPatrol.setDeviceName(bean.getDeviceName());
        abnormalPatrol.setDeviceId(bean.getDeviceId());
        abnormalPatrol.setAbnormalLevel(bean.getAbnormalLevel());
        abnormalPatrol.setMissionId(bean.getMissionId());
        abnormalPatrol.setHandleResult(AbnormalPatrolEnum.Handle.UNHANDLE.ordinal());
        return this.save(abnormalPatrol);
    }

        public void saveOperation(AbnormalPatrol bean) {
            AbnormalPatrol abnormalPatrolDB = this.getById(bean.getId());
            abnormalPatrolDB.setComments(bean.getComments());
            // 如果operation是2.则为上报操作
            if (2 == bean.getOperation()) {
                abnormalPatrolDB.setGeneratedMissionLevel(bean.getGeneratedMissionLevel());
                abnormalPatrolDB.setHandleResult(AbnormalPatrolEnum.Handle.HANDLING.ordinal());
                // 如果operation是1.则为忽略操作
            } else if(1 == bean.getOperation()) {
                abnormalPatrolDB.setHandleResult(AbnormalPatrolEnum.Handle.MISS.ordinal());
            }
            abnormalPatrolDB.setOperation(bean.getOperation());
            this.updateById(abnormalPatrolDB);

            // 如果operation是2.则为上报操作
            if (2 == bean.getOperation()) {
                Mission mission = new Mission();
                mission.setSource(3);
                mission.setSourceId(bean.getId());
                mission.setSubstance(abnormalPatrolDB.getAbnormalDescription());
                mission.setNotes(bean.getComments());
                mission.setReportingTime(new Date());
                mission.setStates(1);
                mission.setUserId(this.sysUserService.getUser().getId());
                mission.setLevels(bean.getGeneratedMissionLevel());
                mission.setCreateTime(new Date());
                this.missionService.saveMission(mission);
            }

        }
}
