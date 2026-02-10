package com.jsdc.iotpt.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsdc.iotpt.model.WalkLabel;
import com.jsdc.iotpt.model.WalkPerson;
import com.jsdc.iotpt.service.*;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.WalkPersonVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;


@Controller
@RequestMapping("walkFaceset")
@Api(tags = "人脸信息管理")
public class WalkFacesetController {

    @Autowired
    private WalkPersonService personService;
    @Autowired
    private WalkLabelService labelService;


    @RequestMapping("selectGroupUsersPageList.do")
    @ApiOperation("人脸信息管理分页")
    public ResultInfo selectGroupUsersPageList(WalkPersonVo bean) {
        return ResultInfo.success(personService.selectGroupUsersPageList(bean));
    }


    @RequestMapping(value = "detailsById.do")
    @ApiOperation("根据id回显数据")
    public ResultInfo detailsById(Integer personId) {
        JSONObject object = new JSONObject();
        List<WalkLabel> labelList = labelService.list(new QueryWrapper<WalkLabel>().eq("isDelete", 0));
        if (null != personId) {
            WalkPerson person = personService.getById(personId);
            object.put("person", person);
        }
        object.put("labels", labelList);
        return ResultInfo.success(object);
    }


    @RequestMapping("savePerson.do")
    @ApiOperation("保存功能")
    public ResultInfo savePerson(WalkPerson person, MultipartFile image) {
        return ResultInfo.success(personService.savePerson(person, image));
    }


    @RequestMapping(value = "updateById.do")
    @ApiOperation("修改功能")
    public ResultInfo updateById(@RequestParam Integer personId) {
        return ResultInfo.success(personService.getById(personId));
    }


    @RequestMapping(value = "deleteById.do")
    @ApiOperation("删除功能")
    public ResultInfo deleteById(WalkPerson person) {
        return ResultInfo.success(personService.updateById(person));
    }


    @RequestMapping(value = "batchImport.do")
    @ApiOperation("批量模版下载")
    public ResultInfo batchImport(HttpServletResponse response) {
        int len = 0;
        //5.创建数据缓冲区
        byte[] buffer = new byte[1024];
        //通过response对象获取OutputStream流
        try (OutputStream out = response.getOutputStream()) {
            InputStream in = this.getClass().getResourceAsStream("/model/批量模板.xls");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("批量模板.xls", "UTF-8"));
            //将FileInputStream流写入到buffer缓冲区
            while ((len = in.read(buffer)) > 0) {
                //8.使用OutputStream将缓冲区的数据输出到客户端浏览器
                out.write(buffer, 0, len);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultInfo.success();
    }


    @RequestMapping("warning.do")
    @ApiOperation("解除预警")
    public ResultInfo warning(Integer personId) {
        WalkPerson person = personService.getById(personId);
        person.setIsWarning(person.getIsWarning() ^ 1);
        return ResultInfo.success(personService.updateById(person));
    }

    @RequestMapping(value = "faceSearch.do")
    @ApiOperation("人脸搜索")
    public ResultInfo faceSearch(MultipartFile image, String maxNum) {
        return ResultInfo.success(personService.faceSearch(image, maxNum));
    }

    @RequestMapping(value = "selectPersonCount.do")
    @ApiOperation("获取登记人脸人数")
    public ResultInfo selectPersonCount() {
        return ResultInfo.success(personService.selectPersonCount());
    }

}


