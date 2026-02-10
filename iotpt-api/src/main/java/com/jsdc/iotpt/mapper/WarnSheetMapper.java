package com.jsdc.iotpt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.dao.WarnSheetDao;
import com.jsdc.iotpt.model.WarnSheet;
import com.jsdc.iotpt.vo.WarnSheetVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface WarnSheetMapper extends BaseMapper<WarnSheet> {

    /**
     * 告警等級統計
     * Author wzn
     * Date 2023/7/21 9:00
     */

    @SelectProvider(type = WarnSheetDao.class, method = "warnList")
    List<WarnSheetVo> warnList();

    /**
     * 告警来源统计
     * Author wzn
     * Date 2023/7/21 15:47
     */
    @SelectProvider(type = WarnSheetDao.class, method = "warnSourceList")
    List<WarnSheetVo> warnSourceList();



    /**
     * /**
     * 安防看板  实时告警列表
     *
     * @return
     */
    @SelectProvider(type = WarnSheetDao.class, method = "getRealTimeWarning")
    Page<WarnSheetVo> getRealTimeWarning(Page page,WarnSheetVo vo);


    /**
     * 安防看板  实时告警列表
     *
     * @return
     */
    @SelectProvider(type = WarnSheetDao.class, method = "getKeyAreaWarning")
    List<WarnSheetVo> getKeyAreaWarning();

    /**
     * 安防看板  告警重点设备排行
     *
     * @return
     */
    @SelectProvider(type = WarnSheetDao.class, method = "getCollectWarningTop")
    List<WarnSheetVo> getCollectWarningTop();

    @SelectProvider(type = WarnSheetDao.class, method = "getHistoryWarning")
    Page<WarnSheetVo> getHistoryWarning(WarnSheetVo vo);

}
