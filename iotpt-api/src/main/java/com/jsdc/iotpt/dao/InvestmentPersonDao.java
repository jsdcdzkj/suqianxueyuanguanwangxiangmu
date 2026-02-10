package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.model.InvestmentPerson;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.CarVo;
import org.springframework.stereotype.Repository;


@Repository
public class InvestmentPersonDao {

    public String investmentPersonList(Page page, InvestmentPerson investmentPerson){
        String sql = "SELECT\n" +
                "\tsu.REALNAME,ip.is_manager,ip.id,su.PHONE\n" +
                "FROM\n" +
                "\tinvestment_person ip\n" +
                "LEFT JOIN SYS_USER su ON\n" +
                "\tip.USER_ID = su.id\n" +
                "WHERE\n" +
                "\tip.isDel = '0'" ;
        if(StringUtils.isNotEmpty(investmentPerson.getRealname())){
            sql += "and su.REALNAME like '%"+investmentPerson.getRealname()+"%'" ;
        }
        if(StringUtils.isNotEmpty(investmentPerson.getPhone())){
            sql += "and su.PHONE like '%"+investmentPerson.getPhone()+"%'" ;
        }
         sql +=" order by ip.createTime desc";
        return sql ;
    }

    public String investmentPersonAll(){
        String sql = "SELECT\n" +
                "\tsu.REALNAME,ip.is_manager,ip.id,su.PHONE\n" +
                "FROM\n" +
                "\tinvestment_person ip\n" +
                "LEFT JOIN SYS_USER su ON\n" +
                "\tip.USER_ID = su.id\n" +
                "WHERE\n" +
                "\tip.isDel = '0'" ;

        sql +=" order by ip.createTime desc";
        return sql ;
    }

}
