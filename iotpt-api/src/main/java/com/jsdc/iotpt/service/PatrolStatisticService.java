package com.jsdc.iotpt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.PatrolDataMapper;
import com.jsdc.iotpt.mapper.PatrolStatisticMapper;
import com.jsdc.iotpt.mapper.PlaceModelMapper;
import com.jsdc.iotpt.model.PatrolData;
import com.jsdc.iotpt.model.PatrolStatistic;
import com.jsdc.iotpt.model.PatrolTask;
import com.jsdc.iotpt.model.PlaceModel;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.vo.PatrolStatisticVo;
import com.jsdc.iotpt.vo.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: PatrolStatisticService
 * Description:
 * date: 2024/11/28 10:34
 *
 * @author bn
 */
@Service
@Transactional
public class PatrolStatisticService  extends BaseService<PatrolStatistic> {

    @Autowired
    private PatrolStatisticMapper statisticMapper;
    @Autowired
    private PatrolDataMapper dataMapper;
    @Autowired
    private PlaceModelMapper placeModelMapper;

    public Page<PatrolStatistic> getPageList(PatrolStatisticVo bean) {
        Page page = new Page(bean.getPageIndex(), bean.getPageSize());
        QueryWrapper<PatrolStatistic> queryWrapper=new QueryWrapper<>();

        if (StringUtils.isNotEmpty(bean.getStartTime())){
            queryWrapper.apply("patrolDate >= TO_DATE({0}, 'yyyy-MM-dd HH24:mi:ss')",bean.getStartTime()+" 00:00:00");
        }

        if (StringUtils.isNotEmpty(bean.getEndTime())){
            queryWrapper.apply("patrolDate <= TO_DATE({0}, 'yyyy-MM-dd HH24:mi:ss')",bean.getEndTime()+" 23:59:59");
        }

        if (null!=bean.getPatrolResult()){
            queryWrapper.eq("patrolResult",bean.getPatrolResult());
        }

        queryWrapper.orderByDesc("patrolDate");


        return statisticMapper.selectPage(page,queryWrapper);

    }


    public JSONObject getStatisticView(PatrolStatisticVo bean){
        JSONObject result=new JSONObject();

        List<PlaceModel> placeModels=placeModelMapper.selectList(Wrappers.<PlaceModel>lambdaQuery().
                eq(PlaceModel::getIsDel,0));

        PatrolStatistic patrolStatistic=statisticMapper.selectById(bean.getId());

        result.put("static",patrolStatistic);
        List<PatrolData> dataList=dataMapper.
                selectList(Wrappers.<PatrolData>lambdaQuery().
                        eq(PatrolData::getStatisticId,bean.getId()).orderByDesc(PatrolData::getPatrolTime));

        long noPatrols=dataList.stream().filter(x->1==x.getIsPatrol()).count();

        result.put("isPatrols",placeModels.size());

        result.put("noPatrols",noPatrols);

        result.put("placeModels",placeModels);
        result.put("dataList",dataList);

        return result;
    }



}
