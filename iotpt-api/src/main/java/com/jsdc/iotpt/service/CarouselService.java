package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.enums.CarouseStateEnums;
import com.jsdc.iotpt.model.Carousel;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.util.RedisDictDataUtil;
import com.jsdc.iotpt.util.StringUtils;
import com.jsdc.iotpt.util.uuid.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CarouselService extends BaseService<Carousel> {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysDictService sysDictService;

    public void saveCarousel(Carousel carousel) {
        saveCarousel(carousel, sysUserService.getUser());
    }

    /**
     * 新增,编辑轮播图
     */
    public void saveCarousel(Carousel carousel, SysUser current) {

        carousel.setIsDel(G.ISDEL_NO);

        if (null != carousel.getId() && null != baseMapper.selectById(carousel.getId())) {
            baseMapper.updateById(carousel);
        } else {
            carousel.setId(IdUtils.fastSimpleUUID());
            carousel.setCreateUser(current.getId());
            carousel.setCreateTime(new Date());
            baseMapper.insert(carousel);
        }
    }

    /**
     * 展示中的数据  app专用
     * @param param
     * @return
     */
    public List<Carousel> getAppShowList(Carousel param) {
        LambdaQueryWrapper<Carousel> wrappers = Wrappers.<Carousel>lambdaQuery();
        try {
            if (StringUtils.isNotBlank(param.getStartTimeStr()) && StringUtils.isNotBlank(param.getEndTimeStr())) {
                // startTime,比传过来的大, entTime, 比传过来的小,用and拼接
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startTime = sdf.parse(param.getStartTimeStr());
                Date endTime = sdf.parse(param.getEndTimeStr());
                wrappers.and(
                        i -> i.ge(Carousel::getStartTime, startTime).le(Carousel::getEndTime, endTime)
                );
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        List<Carousel> pageList = baseMapper.selectList(wrappers
                .eq(Carousel::getIsDel, G.ISDEL_NO)
                // 关键字
                .like(StringUtils.isNotBlank(param.getTitle()), Carousel::getTitle, param.getTitle())
                // 位置
                .eq(null != param.getPosition(), Carousel::getPosition, param.getPosition())
                // 状态
                .eq(Carousel::getState, CarouseStateEnums.STATE_ING.getValue())
                .orderByAsc(Carousel::getRotationOrder)
        );

        HashMap<String, SysDict> positionTypes = RedisDictDataUtil.getDictByType("carouselPositionType");
        for (Carousel bean : pageList) {
            // 从字典取出位置名称
            if (null != bean.getPosition() && null != positionTypes.get(bean.getPosition() + "")) {
                bean.setPositionName(positionTypes.get(bean.getPosition() + "").getDictLabel());
            }
            // 从字典取出状态名称
            if (null != bean.getState() && null != CarouseStateEnums.getDescByValue(bean.getState())) {
                bean.setStateName(CarouseStateEnums.getDescByValue(bean.getState()));
            }
            // 设置预览地址,和下载地址
            if (StringUtils.isNotBlank(bean.getImage())) {
                bean.setImageUrl("/minio/previewFile?fileName=" + bean.getImage());
                bean.setImageUrlDown("/minio/downMinio?fileName=" + bean.getImage());
            }
        }
        return pageList;
    }

    /**
     * 查询列表信息
     */
    public Page<Carousel> getPageList(Carousel param, Integer pageIndex, Integer pageSize) {
        Page<Carousel> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<Carousel> wrappers = Wrappers.<Carousel>lambdaQuery();
        try {
            if (StringUtils.isNotBlank(param.getStartTimeStr()) && StringUtils.isNotBlank(param.getEndTimeStr())) {
                // startTime,比传过来的大, entTime, 比传过来的小,用and拼接
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startTime = sdf.parse(param.getStartTimeStr());
                Date endTime = sdf.parse(param.getEndTimeStr());
                wrappers.and(
                        i -> i.ge(Carousel::getStartTime, startTime).le(Carousel::getEndTime, endTime)
                );
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        Page<Carousel> pageList = baseMapper.selectPage(page, wrappers
                .eq(Carousel::getIsDel, G.ISDEL_NO)
                // 关键字
                .like(StringUtils.isNotBlank(param.getTitle()), Carousel::getTitle, param.getTitle())
                // 位置
                .eq(null != param.getPosition(), Carousel::getPosition, param.getPosition())
                // 状态
                .eq(null != param.getState(), Carousel::getState, param.getState())
                .orderByAsc(Carousel::getRotationOrder)
        );
        Map<String, String> postionTypeMap = sysDictService.selectByType("carouselPositionType").stream().collect(Collectors.toMap(
                SysDict::getDictValue,
                SysDict::getDictLabel
        ));
        for (Carousel bean : pageList.getRecords()) {
            // 从字典取出位置名称
            bean.setPositionName(MapUtils.getString(postionTypeMap, bean.getPosition() + ""));
            // 从字典取出状态名称
            if (null != bean.getState() && null != CarouseStateEnums.getDescByValue(bean.getState())) {
                bean.setStateName(CarouseStateEnums.getDescByValue(bean.getState()));
            }
            // 设置预览地址,和下载地址
            if (StringUtils.isNotBlank(bean.getImage())) {
                bean.setImageUrl("/minio/previewFile?fileName=" + bean.getImage());
                bean.setImageUrlDown("/minio/downMinio?fileName=" + bean.getImage());
            }
        }
        return pageList;
    }

    /**
     * 删除轮播图
     */
    public void deleteCarousel(String id) {
        Carousel carousel = new Carousel();
        carousel.setId(id);
        carousel.setIsDel(G.ISDEL_YES);
        baseMapper.updateById(carousel);
    }

    public Carousel getEntity(String id) {
        if (StringUtils.isBlank(id)) {
            throw new RuntimeException("id不能为空");
        }
        Carousel bean = baseMapper.selectById(id);
        HashMap<String, SysDict> positionTypes = RedisDictDataUtil.getDictByType("carouselPositionType");
        // 从字典取出位置名称
        if (null != bean.getPosition() && null != positionTypes.get(bean.getPosition() + "")) {
            bean.setPositionName(positionTypes.get(bean.getPosition() + "").getDictLabel());
        }
        // 从字典取出状态名称
        if (null != bean.getState() && null != CarouseStateEnums.getDescByValue(bean.getState())) {
            bean.setStateName(CarouseStateEnums.getDescByValue(bean.getState()));
        }
        // 设置预览地址,和下载地址
        if (StringUtils.isNotBlank(bean.getImage())) {
            bean.setImageUrl("/minio/previewFile?fileName=" + bean.getImage());
            bean.setImageUrlDown("/minio/downMinio?fileName=" + bean.getImage());
        }
        return bean;
    }

    /**
     * 根据当前时间,判断轮播图状态
     */
    public void updateCarouselState() {
        // 查询所有未删除的轮播图
        LambdaQueryWrapper<Carousel> wrappers = Wrappers.<Carousel>lambdaQuery();
        wrappers.eq(Carousel::getIsDel, G.ISDEL_NO);
        wrappers.orderByAsc(Carousel::getRotationOrder);
        for (Carousel bean : baseMapper.selectList(wrappers)) {
            // 如果当前时间大于开始时间,小于结束时间,则设置为展示中
            if (new Date().after(bean.getStartTime()) && new Date().before(bean.getEndTime())) {
                bean.setState(CarouseStateEnums.STATE_ING.getValue());
            } else if (new Date().before(bean.getStartTime())) {
                // 如果当前时间小于开始时间,则设置为未开始
                bean.setState(CarouseStateEnums.STATE_START.getValue());
            } else {
                // 如果当前时间大于结束时间,则设置为已结束
                bean.setState(CarouseStateEnums.STATE_END.getValue());
            }
            baseMapper.updateById(bean);
        }
    }



    /**
     * 定时任务,更新轮播图状态
     */
    public void updateCarouselStateTask() {
        // 排除已结束的轮播图, 判断开始时间大于当前时间,设置为未开始
        baseMapper.update(null, Wrappers.<Carousel>lambdaUpdate()
                .eq(Carousel::getState, CarouseStateEnums.STATE_ING.getValue())
                .ge(Carousel::getStartTime, new Date())
                .eq(Carousel::getIsDel, G.ISDEL_NO)
                .ne(Carousel::getState, CarouseStateEnums.STATE_END.getValue())
        );
        // 排除已结束的轮播图, 判断结束时间小于当前时间,设置为已结束
        baseMapper.update(null, Wrappers.<Carousel>lambdaUpdate()
                .eq(Carousel::getState, CarouseStateEnums.STATE_ING.getValue())
                .le(Carousel::getEndTime, new Date())
                .eq(Carousel::getIsDel, G.ISDEL_NO)
                .ne(Carousel::getState, CarouseStateEnums.STATE_END.getValue())
                .ne(Carousel::getState, CarouseStateEnums.STATE_START.getValue())
        );
        // 排除已结束的轮播图, 判断开始时间小于当前时间,结束时间大于当前时间,设置为展示中
        baseMapper.update(null, Wrappers.<Carousel>lambdaUpdate()
                .eq(Carousel::getState, CarouseStateEnums.STATE_START.getValue())
                .le(Carousel::getStartTime, new Date())
                .ge(Carousel::getEndTime, new Date())
                .eq(Carousel::getIsDel, G.ISDEL_NO)
                .ne(Carousel::getState, CarouseStateEnums.STATE_END.getValue())
        );

    }


}
