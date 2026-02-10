package com.jsdc.iotpt.service;


import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.model.PatrolReport;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.model.operate.Mission;
import com.jsdc.iotpt.vo.UploadParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PatrolReportService extends BaseService<PatrolReport> {

    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private MissionService missionService;


    public boolean report(PatrolReport report, MultipartFile file) {
        report.setUserId(sysUserService.getUser().getId());
        report.insert();

        Mission mission = new Mission();
        if (file != null) {
            UploadParams params = new UploadParams("/PatrolReport", file, "6",  String.valueOf(report.getId()));
            SysFile sysFile = sysFileService.minioReturnFile(params);
            sysFile.insert();

            //图片上传功能
            List<SysFile> fileList = new ArrayList<>();
            fileList.add(sysFile);
            mission.setFileList(fileList);
        }

        //1、待指派；2、待处理；3、已处理  0、暂存 4、开启 5、撤销,6：忽略
        mission.setStates(1);
        //来源1、(系统工单)告警上报；2、人员上报；3、（巡检工单）巡检异常上报；4、巡检计划生成  5、（服务工单）报事报修  6、（投诉工单）投诉申请
        mission.setSource(1);
        //关联外键id
        mission.setSourceId(report.getId());
        //取字典（warnLevel）
        // 1 紧急 2 警告 3 次要 4 重要
        mission.setLevels(2);
        //任务标题
        mission.setTitle("视频巡更");
        //描述内容
        mission.setNotes(report.getRemarks());
        //设备信息
        missionService.saveMission(mission);
        return true;
    }
}
