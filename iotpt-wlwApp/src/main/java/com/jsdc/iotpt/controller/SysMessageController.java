package com.jsdc.iotpt.controller;

import com.jsdc.iotpt.model.sys.SysMessage;
import com.jsdc.iotpt.service.SysMessageService;
import com.jsdc.iotpt.util.exception.CustomException;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.SysMessageVo;
import com.jsdc.iotpt.vo.operate.MissionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: SysMessageController
 * Description:
 * date: 2024/11/21 9:15
 *
 * @author bn
 */
@RestController
@RequestMapping("/app/message")
@Api(tags = "系统消息")
public class SysMessageController {


    @Autowired
    private SysMessageService messageService;

    @PostMapping("selectMessage.do")
    @ApiOperation(value = "消息查询", position = 1)
    public ResultInfo selectMessage(@RequestBody SysMessageVo bean){

        try {
            return messageService.selectMsg(bean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("消息查询失败");
        }
    }

    @GetMapping("toIndex.do")
    public ResultInfo toIndex() {
        try {
            return ResultInfo.success(messageService.toIndex());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取数据失败");
        }
    }


    @PostMapping("pageList.do")
    @ApiOperation(value = "分页查询", position = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex", value = "页码", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", dataType = "int")
    })
    public ResultInfo pageList(@RequestBody SysMessageVo bean) {

        try {
            return ResultInfo.success(messageService.appPageList(bean));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("获取列表失败");
        }
    }


    @PostMapping("allEditMsg.do")
    public ResultInfo allEditMsg(@RequestBody SysMessageVo bean){
        try {
            return messageService.allEditMsg(bean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("操作失败");
        }
    }

    @PostMapping("editMsg.do")
    public ResultInfo editMsg(@RequestBody SysMessageVo bean){
        try {
            return messageService.editMsg(bean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("操作失败");
        }
    }


}
