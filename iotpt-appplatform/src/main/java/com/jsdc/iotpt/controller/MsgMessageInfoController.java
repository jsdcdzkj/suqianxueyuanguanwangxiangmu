package com.jsdc.iotpt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.common.logaop.LogInfo;
import com.jsdc.iotpt.enums.LogEnums;
import com.jsdc.iotpt.model.MsgMessageInfo;
import com.jsdc.iotpt.service.MsgMessageInfoService;
import com.jsdc.iotpt.vo.LogVo;
import com.jsdc.iotpt.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: MsgMessageInfoController
 * Description: 消息服务
 * date: 2023/5/8 16:48
 *
 * @author bn
 */
@RestController
@RequestMapping("msgMessageInfo")
@Api(tags = "消息服务管理")
public class MsgMessageInfoController {

    @Autowired
    private MsgMessageInfoService msgMessageInfoService;


    @PostMapping("getMsgPage.do")
    @ApiOperation("消息服务分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageIndex",value = "页码",dataType = "int"),
            @ApiImplicitParam(name = "pageSize",value = "每页条数",dataType = "int")
    })
    public ResultInfo getMsgPage(MsgMessageInfo bean, @RequestParam(name = "pageNo",defaultValue = "1") Integer pageIndex, @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize){
        Page<MsgMessageInfo> page = msgMessageInfoService.getMsgPage(bean, pageIndex, pageSize);
        return ResultInfo.success(page);
    }


    @PostMapping("changeMsg.do")
    @ApiOperation("修改服务状态")
    @LogInfo(LogEnums.LOG_MESSAGE)
    public ResultInfo changeMsgStatus( MsgMessageInfo bean){
        try {
            msgMessageInfoService.changeMsgStatus(bean);
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
        return ResultInfo.success(new LogVo("修改消息服务状态成功"));
    }


    @PostMapping("del.do")
    @ApiOperation("删除消息服务")
    @LogInfo(LogEnums.LOG_MESSAGE)
    public ResultInfo del( MsgMessageInfo bean){
        try {
            msgMessageInfoService.del(bean.getId());
        } catch (Exception e) {
            return ResultInfo.error(e.getMessage());
        }
        return ResultInfo.success(new LogVo("消息服务删除"));
    }


    @PostMapping("edit.do")
    @ApiOperation("新增编辑消息服务")
    @LogInfo(LogEnums.LOG_MESSAGE)
    public ResultInfo edit(MsgMessageInfo bean){
        boolean flag = false;
        if (null == bean.getId()){
            flag = true;
        }
        Integer num = msgMessageInfoService.edit(bean);
        if (num == -1){
            return ResultInfo.error("服务名称已存在");
        }

        if (flag){
            return ResultInfo.success(new LogVo("消息服务新增"));
        }else {
            return ResultInfo.success(new LogVo("消息服务修改"));
        }

    }


    @PostMapping("getList.do")
    @ApiOperation("获取消息服务集合")
    public ResultInfo getList(MsgMessageInfo bean){

        return ResultInfo.success(msgMessageInfoService.getList(bean));
    }


    @GetMapping("getById.do")
    @ApiOperation("根据id获取消息服务")
    @ApiImplicitParam(name = "id",value = "消息服务主键",dataType = "Integer")
    public ResultInfo getById(Integer id){

        return ResultInfo.success(msgMessageInfoService.getById(id));
    }


    @GetMapping("del.do")
    @ApiOperation("删除消息服务")
    @ApiImplicitParam(name = "id",value = "消息服务主键",dataType = "Integer")
    public ResultInfo del(Integer id){
        msgMessageInfoService.del(id);
        return ResultInfo.success();
    }





}
