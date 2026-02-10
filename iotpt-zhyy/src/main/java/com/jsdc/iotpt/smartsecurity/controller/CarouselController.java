package com.jsdc.iotpt.smartsecurity.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.model.Carousel;
import com.jsdc.iotpt.model.sys.SysDict;
import com.jsdc.iotpt.service.CarouselService;
import com.jsdc.iotpt.service.SysDictService;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 轮播图管理
 * @author bn
 */
@RestController
@RequestMapping("/carousel")
@Api(tags = "轮播图管理")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private SysDictService sysDictService;

    /**
     * 分页查询 todo
     *
     * @return
     */
    @RequestMapping("/getPageList")
    @ApiOperation("列表查询轮播图")
    public ResultInfo getPageList(@RequestBody Carousel bean) {
        return ResultInfo.success(carouselService.getPageList(bean, null == bean.getPageIndex()?1:bean.getPageIndex(), null == bean.getPageSize()?10:bean.getPageSize()));
    }



    /**
     * 添加/编辑 todo
     *
     * @param bean
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    @ApiOperation("增加修改")
    public ResultInfo saveOrUpdateClockingRecord(@RequestBody Carousel bean) {
        try {
            carouselService.saveCarousel(bean);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
        return ResultInfo.success();
    }

    /**
     * 删除 todo
     *
     * @return
     */
    @RequestMapping("/deleteClockingRecord")
    @ApiOperation("删除")
    public ResultInfo deleteClockingRecord(@RequestBody Carousel carousel) {
        try {
            carouselService.getBaseMapper().update(null, Wrappers.<Carousel>lambdaUpdate().eq(Carousel::getId, carousel.getId()).set(Carousel::getIsDel, G.ISDEL_YES));
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


    /**
     * 得到位置字典
     */
    @RequestMapping("/getPositionDict")
    @ApiOperation("查询")
    public ResultInfo getPositionDict() {
        return ResultInfo.success(sysDictService.getList(SysDict.builder().dictType("carouselPositionType").build()));
    }

    /**
     * 修改轮播时间
     */
    @PostMapping("/updateTime")
    @ApiOperation("修改轮播时间")
    public ResultInfo updateTime(Integer time) {
        if (null == time){
            return ResultInfo.error("时间不能为空");
        }
        try {
            carouselService.getBaseMapper().update(null, Wrappers.<Carousel>lambdaUpdate().set(Carousel::getTime, time).eq(Carousel::getIsDel, G.ISDEL_NO));
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
        return ResultInfo.success();
    }

}
