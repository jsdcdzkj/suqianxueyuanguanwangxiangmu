package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.model.sys.SysLogo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LogoService extends BaseService<SysLogo> {

    public List<SysLogo> logoList() {
        return this.list(Wrappers.lambdaQuery(SysLogo.class).eq(SysLogo::getIsDel,0));
    }

    public ResultInfo updateLogo(List<SysLogo> sysLogs) {
        for (SysLogo sysLog : sysLogs) {
            sysLog.setIsDel(0);
            this.updateById(sysLog);
        }
        return ResultInfo.success();
    }
}
