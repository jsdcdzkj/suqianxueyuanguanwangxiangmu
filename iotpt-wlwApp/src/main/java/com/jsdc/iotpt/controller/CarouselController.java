package com.jsdc.iotpt.controller;


import com.jsdc.iotpt.model.Carousel;
import com.jsdc.iotpt.service.CarouselService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 轮播图管理
 * @author bn
 */
@RestController
@RequestMapping("/app/carousel")
@Api(tags = "轮播图管理")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("列表查询轮播图")
    public ResultInfo getPageList(Carousel bean) {
        return ResultInfo.success(carouselService.getPageList(bean, null == bean.getPageIndex()?1:bean.getPageIndex(), null == bean.getPageSize()?10:bean.getPageSize()));
    }

    /**
     * 分页查询 展示中的轮播图 todo
     *
     * @return
     */
    @RequestMapping("/getShowList")
    @ApiOperation("列表查询轮播图")
    public ResultInfo getShowList(Carousel bean) {
        return ResultInfo.success(carouselService.getAppShowList(bean));
    }

    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("增加修改")
    public ResultInfo saveOrUpdateClockingRecord(Carousel bean) {
        try {
            carouselService.saveCarousel(bean);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
        return ResultInfo.success();
    }

    /**
     * 获取实体类
     *
     * @param id
     * @return
     */
    @RequestMapping("/getEntity")
    @ApiOperation("查询")
    public ResultInfo getEntity(String id) {
        return ResultInfo.success(carouselService.getEntity(id));
    }

}
