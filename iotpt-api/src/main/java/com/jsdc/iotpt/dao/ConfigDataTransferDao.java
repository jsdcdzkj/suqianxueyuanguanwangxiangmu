package com.jsdc.iotpt.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.ConfigDataTransferVo;
import org.springframework.stereotype.Repository;

/**
 * @authon thr
 * @describe 数据转换模板管理
 */
@Repository
public class ConfigDataTransferDao {


    /**
     * 此方法为默认方法 请添加自己的逻辑代码
     *
     * @param page
     * @param bean
     * @return
     */
    public String getEntityList(Page page, ConfigDataTransferVo bean) {
        String sql = " SELECT t.*,d.dictLabel modelTypeName " +
                " FROM config_data_transfer t " +
                " left join SYS_DICT d on d.dictType='config_data_transfer' and t.modelType = d.dictValue" +
                " WHERE " +
                " t.isDel=0 ";
        if (StringUtils.isNotNull(bean)) {
            if (StringUtils.isNotEmpty(bean.getModelName())) {
                sql += " and t.modelName like '%" + bean.getModelName() + "%'";
            }
            if (StringUtils.isNotEmpty(bean.getModelCode())) {
                sql += " and t.modelCode = '" + bean.getModelCode() + "'";
            }
            if (StringUtils.isNotEmpty(bean.getModelType())) {
                sql += " and t.modelType = '" + bean.getModelType() + "'";
            }
        }
        sql += " order by t.createTime desc";
        return sql;
    }
}
