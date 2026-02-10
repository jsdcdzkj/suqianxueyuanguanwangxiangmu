package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.mapper.CustomerAccessRecordMapper;
import com.jsdc.iotpt.mapper.CustomerFollowHistoryMapper;
import com.jsdc.iotpt.mapper.CustomerMapper;
import com.jsdc.iotpt.mapper.InvestmentPersonMapper;
import com.jsdc.iotpt.model.*;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
@Slf4j
public class CustomerService extends BaseService<Customer> {
    @Autowired
    private CustomerMapper mapper;
    @Autowired
    private CustomerAccessRecordMapper customerAccessRecordMapper;
    @Autowired
    private CustomerFollowHistoryMapper customerFollowHistoryMapper;
    @Autowired
    private InvestmentPersonMapper investmentPersonMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysFileService fileService;

    public Page<Customer> getPageList(Customer bean) {
        if (StringUtils.isNotEmpty(bean.getSearchType())) {
            //查询类型 1我的客户 2全部客户 3客户公海 4网站客户
            if ("1".equals(bean.getSearchType())) {
                InvestmentPerson investmentPerson = investmentPersonMapper.selectOne(new LambdaQueryWrapper<InvestmentPerson>().eq(InvestmentPerson::getIsDel, 0).eq(InvestmentPerson::getUser_id, sysUserService.getUser().getId()));
                if (null != investmentPerson) {
                    bean.setInvestmentPersonId(investmentPerson.getId());
                } else {
                    bean.setInvestmentPersonId(0);
                }
                //公海 0否 1是
                bean.setIsHighSeas("0");
            } else if ("2".equals(bean.getSearchType())) {
                bean.setIsHighSeas("0");
            }  else if ("3".equals(bean.getSearchType())) {
                bean.setIsHighSeas("1");
            } else if ("4".equals(bean.getSearchType())) {
                bean.setSource("2");
            }
        }

        Page<Customer> page = new Page<>(bean.getPageNo(), bean.getPageSize());
        Page<Customer> pageList = this.mapper.getPageList(page, bean);

        //客户类型【字典】
        HashMap<String, SysDict> customerTypes = RedisDictDataUtil.getDictByType("customerTypes");
        //行业【字典】
        HashMap<String, SysDict> industryTypes = RedisDictDataUtil.getDictByType("industryTypes");
        //意向项目【字典】
        HashMap<String, SysDict> projectTypes = RedisDictDataUtil.getDictByType("projectTypes");
        //客户状态【字典】
        HashMap<String, SysDict> customerStatus = RedisDictDataUtil.getDictByType("customerStatus");

        for (Customer customer : pageList.getRecords()) {
            if (customerTypes != null) {
                customer.setTypeName(customerTypes.get(customer.getType()) == null ? "" : customerTypes.get(customer.getType()).getDictLabel());
            }
            if (industryTypes != null) {
                customer.setIndustryName(industryTypes.get(customer.getIndustryValue()) == null ? "" : industryTypes.get(customer.getIndustryValue()).getDictLabel());
            }
            if (projectTypes != null) {
                customer.setProjectName(projectTypes.get(customer.getProjectValue()) == null ? "" : projectTypes.get(customer.getProjectValue()).getDictLabel());
            }
            if (customerStatus != null) {
                customer.setStatusName(customerStatus.get(customer.getStatus()) == null ? "新增" : customerStatus.get(customer.getStatus()).getDictLabel());
            }
        }
        return pageList;
    }

    /**
     * 新增
     */
    public ResultInfo add(Customer bean) {
        bean.setCreateUser(sysUserService.getUser().getId());
        bean.setUpdateUser(sysUserService.getUser().getId());
        bean.setCreateTime(new Date());
        bean.setUpdateTime(new Date());
        bean.setIsDel(G.ISDEL_NO);
        bean.setSource("1");
        bean.setIsHighSeas("0");

        //跟进人员类型 1指定人员 2加入公海 3分配给创建人
        if ("1".equals(bean.getInvestmentType())) {
            if (StringUtils.isNull(bean.getInvestmentPersonId())) {
                return ResultInfo.error("请选择指定人员");
            }
        } else if ("2".equals(bean.getInvestmentType())) {
            //公海 0否 1是
            bean.setIsHighSeas("1");
        } else if ("3".equals(bean.getInvestmentType())) {
            InvestmentPerson investmentPerson = investmentPersonMapper.selectOne(new LambdaQueryWrapper<InvestmentPerson>().eq(InvestmentPerson::getIsDel, 0).eq(InvestmentPerson::getUser_id, sysUserService.getUser().getId()));
            if (StringUtils.isNull(investmentPerson)) {
                return ResultInfo.error("当前登录账号非招商人员");
            }
            bean.setInvestmentPersonId(investmentPerson.getId());
        }
        save(bean);

        //跟进人员类型 1指定人员 2加入公海 3分配给创建人
        if ("2".equals(bean.getInvestmentType())) {
            CustomerAccessRecord record = new CustomerAccessRecord();
            record.setCustomerId(bean.getId());
            record.setStatus("1");
            record.setCreateTime(new Date());
            record.setIsDel(G.ISDEL_NO);

            record.setInvestmentPersonId(sysUserService.getUser().getId());

            customerAccessRecordMapper.insert(record);
        }

        return ResultInfo.success();
    }

    /**
     * 编辑
     */
    public ResultInfo edit(Customer bean) {
        bean.setUpdateUser(sysUserService.getUser().getId());
        bean.setUpdateTime(new Date());

        //跟进人员类型 1指定人员 2加入公海 3分配给创建人
        if ("1".equals(bean.getInvestmentType())) {
            if (StringUtils.isNull(bean.getInvestmentPersonId())) {
                return ResultInfo.error("请选择指定人员");
            }
        } else if ("2".equals(bean.getInvestmentType())) {
            //公海 0否 1是
            bean.setIsHighSeas("1");
        } else if ("3".equals(bean.getInvestmentType())) {
            InvestmentPerson investmentPerson = investmentPersonMapper.selectOne(new LambdaQueryWrapper<InvestmentPerson>().eq(InvestmentPerson::getIsDel, 0).eq(InvestmentPerson::getUser_id, sysUserService.getUser().getId()));
            bean.setInvestmentPersonId(investmentPerson.getId());
        }
        updateById(bean);

        //跟进人员类型 1指定人员 2加入公海 3分配给创建人
        if ("2".equals(bean.getInvestmentType())) {
            CustomerAccessRecord record = new CustomerAccessRecord();
            record.setCustomerId(bean.getId());
            record.setStatus("1");
            record.setCreateTime(new Date());
            record.setIsDel(G.ISDEL_NO);

            record.setInvestmentPersonId(sysUserService.getUser().getId());

            customerAccessRecordMapper.insert(record);
        }

        return ResultInfo.success();
    }

    /**
     * 删除
     */
    public ResultInfo del(Customer bean) {
        bean.setIsDel(G.ISDEL_YES);
        bean.setUpdateUser(sysUserService.getUser().getId());
        bean.setUpdateTime(new Date());
        return ResultInfo.success();
    }

    /**
     * 跟进
     */
    public ResultInfo toFollowUp(CustomerFollowHistory bean) {
        Customer customer = mapper.selectById(bean.getCustomerId());
        customer.setStatus(bean.getStatus());
        customer.setUpdateUser(sysUserService.getUser().getId());
        customer.setUpdateTime(new Date());
        updateById(customer);

        bean.setIsDel(G.ISDEL_NO);
        bean.setCreateTime(new Date());
        InvestmentPerson investmentPerson = investmentPersonMapper.selectOne(new LambdaQueryWrapper<InvestmentPerson>().eq(InvestmentPerson::getIsDel, 0).eq(InvestmentPerson::getUser_id, sysUserService.getUser().getId()));
        bean.setInvestmentPersonId(investmentPerson.getId());
        customerFollowHistoryMapper.insert(bean);

        //向图片表中插入数据
        if (!CollectionUtils.isEmpty(bean.getFileList())) {
            for (SysFile file : bean.getFileList()) {
                try {
                    file.setBizId(bean.getId().toString());
                    file.setBizType("18");
                    file.setIsDel(0);
                    file.insert();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return ResultInfo.success();
    }

    /**
     * 移出公海
     */
    public ResultInfo toExitHighSeas(Customer bean) {
        //公海 0否 1是
        bean.setIsHighSeas("1");
        bean.setUpdateUser(sysUserService.getUser().getId());
        bean.setUpdateTime(new Date());
        updateById(bean);

        //移出公海
        CustomerAccessRecord record = new CustomerAccessRecord();
        record.setCustomerId(bean.getId());
        record.setStatus("2");
        record.setCreateTime(new Date());
        record.setIsDel(G.ISDEL_NO);

        record.setInvestmentPersonId(sysUserService.getUser().getId());

        customerAccessRecordMapper.insert(record);
        return ResultInfo.success();
    }

    /**
     * 详情
     */
    public ResultInfo view(Customer customer) {
        Customer bean = mapper.selectById(customer.getId());

        //客户类型【字典】
        HashMap<String, SysDict> customerTypes = RedisDictDataUtil.getDictByType("customerTypes");
        //行业【字典】
        HashMap<String, SysDict> industryTypes = RedisDictDataUtil.getDictByType("industryTypes");
        //意向项目【字典】
        HashMap<String, SysDict> projectTypes = RedisDictDataUtil.getDictByType("projectTypes");
        //客户状态【字典】
        HashMap<String, SysDict> customerStatus = RedisDictDataUtil.getDictByType("customerStatus");

        if (customerTypes != null) {
            bean.setTypeName(customerTypes.get(bean.getType()) == null ? "" : customerTypes.get(bean.getType()).getDictLabel());
        }
        if (industryTypes != null) {
            bean.setIndustryName(industryTypes.get(bean.getIndustryValue()) == null ? "" : industryTypes.get(bean.getIndustryValue()).getDictLabel());
        }
        if (projectTypes != null) {
            bean.setProjectName(projectTypes.get(bean.getProjectValue()) == null ? "" : projectTypes.get(bean.getProjectValue()).getDictLabel());
        }
        if (customerStatus != null) {
            bean.setStatusName(customerStatus.get(bean.getStatus()) == null ? "新增" : customerStatus.get(bean.getStatus()).getDictLabel());
        }

        //客户跟进历史记录表
        List<CustomerFollowHistory> customerFollowHistoryList = customerFollowHistoryMapper.selectList(new LambdaQueryWrapper<CustomerFollowHistory>()
                .eq(CustomerFollowHistory::getIsDel, 0)
                .eq(CustomerFollowHistory::getCustomerId, customer.getId())
                .orderByDesc(CustomerFollowHistory::getCreateTime));
        for (CustomerFollowHistory customerFollowHistory : customerFollowHistoryList) {
            if (customerStatus != null) {
                customerFollowHistory.setStatusName(customerStatus.get(bean.getStatus()) == null ? "新增" : customerStatus.get(bean.getStatus()).getDictLabel());

                InvestmentPerson investmentPerson = investmentPersonMapper.selectById(customerFollowHistory.getInvestmentPersonId());
                SysUser sysUser = sysUserService.getById(investmentPerson.getUser_id());
                customerFollowHistory.setInvestmentPersonName(sysUser.getRealName() + " " + sysUser.getPhone());

                List<SysFile> files = fileService.list(new LambdaQueryWrapper<SysFile>()
                        .eq(SysFile::getBizId, String.valueOf(customerFollowHistory.getId()))
                        .eq(SysFile::getIsDel, 0)
                        .eq(SysFile::getBizType, "18"));

                customerFollowHistory.setFileList(files);
            }
        }

        //客户出入公海记录表
        List<CustomerAccessRecord> customerAccessRecordList = customerAccessRecordMapper.selectList(new LambdaQueryWrapper<CustomerAccessRecord>()
                .eq(CustomerAccessRecord::getIsDel, 0)
                .eq(CustomerAccessRecord::getCustomerId, customer.getId())
                .orderByDesc(CustomerAccessRecord::getCreateTime));
        for (CustomerAccessRecord customerAccessRecord : customerAccessRecordList) {
            customerAccessRecord.setStatusName();
            SysUser sysUser = sysUserService.getById(customerAccessRecord.getInvestmentPersonId());
            customerAccessRecord.setInvestmentPersonName(sysUser.getRealName() + " " + sysUser.getPhone());
        }

        bean.setCustomerFollowHistoryList(customerFollowHistoryList);
        bean.setCustomerAccessRecordList(customerAccessRecordList);
        return ResultInfo.success(bean);
    }

    /**
     * 指定分配
     */
    public ResultInfo toSpecifyAllocation(Customer bean) {
        bean.setUpdateUser(sysUserService.getUser().getId());
        bean.setUpdateTime(new Date());
        bean.setIsHighSeas("0");
        updateById(bean);

        //移出公海
        CustomerAccessRecord record = new CustomerAccessRecord();
        record.setCustomerId(bean.getId());
        record.setStatus("2");
        record.setCreateTime(new Date());
        record.setIsDel(G.ISDEL_NO);

        record.setInvestmentPersonId(sysUserService.getUser().getId());

        customerAccessRecordMapper.insert(record);

        return ResultInfo.success();
    }

    /**
     * 网站客户指定分配
     */
    public ResultInfo toWebSpecifyAllocation(Customer bean) {
        bean.setUpdateUser(sysUserService.getUser().getId());
        bean.setUpdateTime(new Date());
        bean.setAllocateTime(new Date());
        bean.setAllocateUser(sysUserService.getUser().getId());
        bean.setIsHighSeas("0");
        updateById(bean);

        return ResultInfo.success();
    }

    /**
     * 默认7天没有跟进联系客户进入公海
     */
    public void toHighSeas() {
        Customer customer = new Customer();
        customer.setStartTime(new DateTime().plusDays(-7).toString("yyyy-MM-dd"));
        List<Customer> list = mapper.getHistoryList(customer);
        for (Customer bean : list) {
            bean.setId(bean.getId());
            bean.setIsHighSeas("1");
            bean.setUpdateTime(new Date());
            updateById(bean);

            //移入
            CustomerAccessRecord record = new CustomerAccessRecord();
            record.setCustomerId(bean.getId());
            record.setStatus("1");
            record.setCreateTime(new Date());
            record.setIsDel(G.ISDEL_NO);

            customerAccessRecordMapper.insert(record);
        }
    }

    /**
     * 网站新增
     */
    public ResultInfo webAdd(Customer bean) {
        bean.setCreateTime(new Date());
        bean.setIsDel(G.ISDEL_NO);
        bean.setSource("2");

        //公海 0否 1是
        bean.setIsHighSeas("0");
        save(bean);

        return ResultInfo.success();
    }

    /**
     * 统计客户数量
     */
    public ResultInfo onCustomerCount(Customer bean) {
        if (StringUtils.isNotEmpty(bean.getSearchType())) {
            //查询类型 1我的客户 2全部客户 3客户公海 4网站客户
            if ("1".equals(bean.getSearchType())) {
                InvestmentPerson investmentPerson = investmentPersonMapper.selectOne(new LambdaQueryWrapper<InvestmentPerson>().eq(InvestmentPerson::getIsDel, 0).eq(InvestmentPerson::getUser_id, sysUserService.getUser().getId()));
                Integer personId = 0;
                if (null != investmentPerson) {
                    personId = investmentPerson.getId();
                }
                Long totalCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getInvestmentPersonId, personId)
                        .eq(Customer::getIsHighSeas, "0"));

                Long dayCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getInvestmentPersonId, personId)
                        .eq(Customer::getIsHighSeas, "0")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentDay(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentDay(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                Long weekCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getInvestmentPersonId, personId)
                        .eq(Customer::getIsHighSeas, "0")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                Long monthCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getIsHighSeas, "0")
                        .eq(Customer::getInvestmentPersonId, personId)
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                bean.setTotalCount(totalCount);
                bean.setDayCount(dayCount);
                bean.setWeekCount(weekCount);
                bean.setMonthCount(monthCount);

            } else if ("2".equals(bean.getSearchType())) {
                Long totalCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0));

                Long dayCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getIsHighSeas, "0")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentDay(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentDay(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                Long weekCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getIsHighSeas, "0")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                Long monthCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getIsHighSeas, "0")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                bean.setTotalCount(totalCount);
                bean.setDayCount(dayCount);
                bean.setWeekCount(weekCount);
                bean.setMonthCount(monthCount);

            } else if ("3".equals(bean.getSearchType())) {
                Long totalCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getIsHighSeas, "1"));

                Long dayCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getIsHighSeas, "1")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentDay(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentDay(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                Long weekCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getIsHighSeas, "1")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                Long monthCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getIsHighSeas, "1")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                bean.setTotalCount(totalCount);
                bean.setDayCount(dayCount);
                bean.setWeekCount(weekCount);
                bean.setMonthCount(monthCount);

            } else if ("4".equals(bean.getSearchType())) {
                Long totalCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getSource, "2"));

                Long dayCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getSource, "2")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentDay(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentDay(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                Long weekCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getSource, "2")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentWeek(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                Long monthCount = mapper.selectCount(new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getIsDel, 0)
                        .eq(Customer::getSource, "2")
                        .apply(" createTime >= TO_DATE('" + DateUtils.getStartTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') ")
                        .apply(" createTime <= TO_DATE('" + DateUtils.getEndTimeOfCurrentMonth(new Date()) + "','YYYY-MM-DD HH24:mi:ss') "));

                bean.setTotalCount(totalCount);
                bean.setDayCount(dayCount);
                bean.setWeekCount(weekCount);
                bean.setMonthCount(monthCount);
            }
        }
        return ResultInfo.success(bean);
    }

}
