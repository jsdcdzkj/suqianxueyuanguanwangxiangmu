package com.jsdc.iotpt.service;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;

import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.mapper.AlarmRecordsMapper;
import com.jsdc.iotpt.mapper.FireAlarmInfoMapper;
import com.jsdc.iotpt.mapper.SysUserMapper;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.new_alarm.AlarmRecords;
import com.jsdc.iotpt.model.new_alarm.FireAlarmInfo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.FireAlarmInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class FireAlarmInfoService extends BaseService<FireAlarmInfo> {

    @Autowired
    private FireAlarmInfoMapper fireAlarmInfoMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private AlarmRecordsMapper alarmRecordsMapper;
    @Autowired
    private AlarmRecordsService alarmRecordsService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    public Page<FireAlarmInfoVo> getPageList(FireAlarmInfoVo vo) {
        Page<FireAlarmInfoVo> page =fireAlarmInfoMapper.getEntityList(new Page<>(vo.getPageNo(), vo.getPageSize()),vo);
        page.getRecords().forEach(a -> {
            List<String> hanlder = Arrays.asList(a.getHanlderUsers().split(","));
            List<SysUser> hanlderUsers = sysUserMapper.selectBatchIds(hanlder);
            a.setHanlderUser(hanlderUsers);
            List<String> duty = Arrays.asList(a.getDutyUsers().split(","));
            List<SysUser> dutyUsers = sysUserMapper.selectBatchIds(duty);
            a.setDutyUser(dutyUsers);
        });
        return page;

    }

    /**
     * 查询 todo
     *
     * @return
     */
    public List<FireAlarmInfo> getList(FireAlarmInfo entity) {
        QueryWrapper<FireAlarmInfo> queryWrapper = new QueryWrapper<>();
        return fireAlarmInfoMapper.selectList(queryWrapper);
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    public ResultInfo saveOrUpdateFireAlarmInfo(FireAlarmInfo bean) {
        if (bean.getId() == null) {
            bean.setIsDel("0");
            AlarmRecords info=new AlarmRecords();
            info.setHandleStatus("2");
            info.setId(Integer.parseInt(bean.getAlarmId()));
            alarmRecordsMapper.updateById(info);
            save(bean);
        }else {
            updateById(bean);
        }
        return ResultInfo.success();
    }

    /**
     * 根据id获取类 todo
     *
     * @param id
     * @return
     */
    public ResultInfo getEntityById(Integer id) {
        FireAlarmInfo fireAlarmInfo = getById(id);
        List<String> hanlder = Arrays.asList(fireAlarmInfo.getHanlderUsers().split(","));
        List<SysUser> hanlderUsers = sysUserMapper.selectBatchIds(hanlder);
        fireAlarmInfo.setHanlderUser(hanlderUsers);
        List<String> duty = Arrays.asList(fireAlarmInfo.getDutyUsers().split(","));
        List<SysUser> dutyUsers = sysUserMapper.selectBatchIds(duty);
        fireAlarmInfo.setDutyUser(dutyUsers);
        FireAlarmInfoVo vo = new FireAlarmInfoVo();
        BeanUtils.copyProperties(fireAlarmInfo, vo);
        AlarmRecords record = alarmRecordsService.getEntityById(Integer.parseInt(vo.getAlarmId()));
        vo.setRecords(record);
        return ResultInfo.success(vo);
    }

    /**
     * 根据id获取类 todo
     *
     * @param bean
     * @return
     */
    public void del(FireAlarmInfoVo bean) {
        List<String> list = Arrays.asList(bean.getIds().split(","));
        LambdaUpdateWrapper<FireAlarmInfo> wrapper=new LambdaUpdateWrapper<>();
        wrapper.in(FireAlarmInfo::getId, list).set(FireAlarmInfo::getIsDel,1);
        update(wrapper);
    }

    /**
     * 导出
     */
    public void export(FireAlarmInfoVo vo, HttpServletResponse response) {
        // 查询数据
        List<FireAlarmInfoVo> FireAlarmInfoVos = fireAlarmInfoMapper.export(vo);
        FireAlarmInfoVos.forEach(a -> {
            List<String> hanlder = Arrays.asList(a.getHanlderUsers().split(","));
            List<SysUser> hanlderUsers = sysUserMapper.selectBatchIds(hanlder);
            a.setHanlderUsers(hanlderUsers.stream().map(SysUser::getRealName).collect(Collectors.joining(",")));
            List<String> duty = Arrays.asList(a.getDutyUsers().split(","));
            List<SysUser> dutyUsers = sysUserMapper.selectBatchIds(duty);
            a.setDutyUsers(dutyUsers.stream().map(SysUser::getRealName).collect(Collectors.joining(",")));
        });
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.addHeaderAlias("deviceName", "设备名称");
        writer.addHeaderAlias("areaName", "报警区域");
        writer.addHeaderAlias("alarmContentStr", "报警内容");
        writer.addHeaderAlias("alarmTime", "报警时间");
        writer.addHeaderAlias("hanlderUsers", "处理人");
        writer.addHeaderAlias("dutyUsers", "值班人");
        writer.addHeaderAlias("hanlderTime", "处理时间");
        writer.addHeaderAlias("content", "处理结果");
        writer.setOnlyAlias(true);
        writer.write(FireAlarmInfoVos, true);
        OutputStream outputStream = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel");
        try {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("fireAlarm.xls", "UTF-8"));
            outputStream = response.getOutputStream();
            writer.flush(outputStream, true);
//            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Map<String,Object>line(Integer type){
        HashMap<String, Object> map = new HashMap<>();
       if (type == 0) {//月
           FireAlarmInfoVo vo=new FireAlarmInfoVo();
           vo.setStartTime(DateUtils.localDateToStr(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())));
           vo.setEndTime(DateUtils.localDateToStr(LocalDate.now()));
           map.put("day", DateUtils.getDatesFromMonthStartToToday(LocalDate.now()).stream().map(LocalDate::toString).collect(Collectors.toList()));
           map.put("line", fireAlarmInfoMapper.lineM(vo));
       }else {//年
           List<YearMonth> datesFromYearStartToMonth = DateUtils.getDatesFromYearStartToMonth(LocalDate.now());
           FireAlarmInfoVo vo=new FireAlarmInfoVo();
           vo.setStartTime(datesFromYearStartToMonth.get(0).toString());
           vo.setEndTime(datesFromYearStartToMonth.get(datesFromYearStartToMonth.size() - 1).toString());
           map.put("day",datesFromYearStartToMonth.stream().map(YearMonth::toString).collect(Collectors.toList()));
           map.put("line", fireAlarmInfoMapper.lineY(vo));
       }
       return map;
    }



    public Map<String,Object>column(Integer type){
        HashMap<String, Object> map = new HashMap<>();
        FireAlarmInfoVo vo=new FireAlarmInfoVo();
        if (type == 0) {//月
            vo.setStartTime(YearMonth.of(LocalDate.now().getYear(),LocalDate.now().getMonth()).toString());
            vo.setStartHanlder(YearMonth.of(LocalDate.now().getYear(),LocalDate.now().getMonth()).toString());
        }else {//年
            vo.setStartHanlder(LocalDate.now().getYear()+"");
            vo.setStartTime(LocalDate.now().getYear()+"");
        }
        map.put("not", fireAlarmInfoMapper.column(vo,type,1));
        map.put("all", fireAlarmInfoMapper.column(vo,type,0));
        return map;
    }
}


