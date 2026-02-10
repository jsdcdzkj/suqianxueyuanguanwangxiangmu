package com.jsdc.iotpt.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.CustomerMapper;
import com.jsdc.iotpt.mapper.InvestmentPersonMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class InvestmentPersonService extends BaseService<InvestmentPerson> {

    @Autowired
    private InvestmentPersonMapper investmentPersonMapper;
    @Autowired
    private SysUserService sysUserService ;

    @Autowired
    private CustomerMapper customerMapper;


    /**
     *
     *新增招商人员
     * @author wzn
     * @date 2025/09/25 09:09
     */
    public ResultInfo addInvestmentPerson(InvestmentPerson investmentPerson) {

        QueryWrapper<InvestmentPerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", investmentPerson.getUser_id()) ;
        queryWrapper.eq("isDel", "0") ;
        Long count = investmentPersonMapper.selectCount(queryWrapper);
        if(count >0){
            return ResultInfo.error("该人员已存在,禁止重复添加!") ;
        }

        SysUser sysUser = sysUserService.getUser();
        investmentPerson.setCreateUser(sysUser.getId());
        investmentPerson.setCreateTime(new Date());
        investmentPerson.setIsDel(0);

        investmentPerson.insert();
        return ResultInfo.success();
    }




    /**
     *
     *移出招商人员
     * @author wzn
     * @date 2025/09/25 09:09
     */
    public ResultInfo delInvestmentPerson(InvestmentPerson investmentPerson) {
        investmentPerson.setIsDel(1);
        updateById(investmentPerson);
        return ResultInfo.success();
    }



    /**
     *
     *招商人员列表
     * @author wzn
     * @date 2025/09/26 09:09
     */
    public Page<InvestmentPerson> investmentPersonList(InvestmentPerson investmentPerson) {
        Page<InvestmentPerson> page = new Page<>(investmentPerson.getPageNo(), investmentPerson.getPageSize());
        Page<InvestmentPerson> investmentPersonPage = investmentPersonMapper.investmentPersonList(page, investmentPerson);
        if(CollectionUtils.isNotEmpty(investmentPersonPage.getRecords())){
            for(InvestmentPerson  investmentPerson1:investmentPersonPage.getRecords()){
                QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("isDel", "0") ;
                queryWrapper.eq("investmentPersonId", investmentPerson1.getId()) ;
                Long count = customerMapper.selectCount(queryWrapper) ;
                investmentPerson1.setCustomersCount(Integer.valueOf(count+""));
            }
        }
        return investmentPersonPage;
    }

    /**
     *
     *招商人员列表-下拉框
     * @author wzn
     * @date 2025/09/26 09:09
     */
    public List<InvestmentPerson> investmentPersonAll() {
        List<InvestmentPerson> investmentPersonPage = investmentPersonMapper.investmentPersonAll();
        return investmentPersonPage;
    }

}
