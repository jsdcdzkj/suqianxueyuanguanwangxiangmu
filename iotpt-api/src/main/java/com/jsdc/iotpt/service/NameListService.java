package com.jsdc.iotpt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dahuatech.icc.exception.ClientException;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.mapper.NameListMapper;
import com.jsdc.iotpt.model.NameList;
import com.jsdc.iotpt.model.Stranger;
import com.jsdc.iotpt.model.SysFile;
import com.jsdc.iotpt.vo.FaceVo;
import com.jsdc.iotpt.vo.ResultInfo;
import com.jsdc.iotpt.vo.UploadParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * (NameList)表服务接口
 *
 * @author wangYan
 * @since 2023-10-13
 */
@Service
public class NameListService extends BaseService<NameList> {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private StrangerService strangerService;
    @Autowired
    private NameListMapper nameListMapper;
    /**
     * 新增编辑
     *
     * @param nameList
     * @param file
     * @return
     */
    public String insertOrUpdate(NameList nameList) throws ClientException {
        String msg = "";
        FaceVo  faceVo=new FaceVo();
        if (nameList.getId() == null) {
            nameList.setCreateUser(sysUserService.getUser().getId());
            nameList.setCreateTime(new Date());
            nameList.setIsDel(0);
            faceVo.setName(nameList.getName());
            faceVo.setSex(nameList.getSex());
            faceVo.setCardtype("1");
            faceVo.setCardid(nameList.getCardId());
            faceVo.setGroupid(Long.valueOf("100003"));
            faceVo.setBirthday(nameList.getBirthday());
            msg = "添加成功";
        } else {
            nameList.setUpdateUser(sysUserService.getUser().getId());
            nameList.setUpdateTime(new Date());
            faceVo.setName(nameList.getName());
            faceVo.setSex(nameList.getSex());
            faceVo.setCardtype("1");
            faceVo.setCardid(nameList.getCardId());
            faceVo.setGroupid(Long.valueOf("100003"));
            faceVo.setBirthday(nameList.getBirthday());
            faceVo.setPersonId(nameList.getIccUserId());
            msg = "编辑成功";
        }

        this.saveOrUpdate(nameList);
//        if (file != null) {
            sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId", nameList.getId()+"").eq("bizType", "5"));
//            UploadParams params = new UploadParams("/nameList", file, "5", nameList.getId() + "");
//            sysFileService.uploadFile(params);
            SysFile sysFile = new SysFile();
            sysFile.setFileName(nameList.getFileOld());
//            sysFile.setFileType(multipartFile.getContentType());
//            sysFile.setFileSize(multipartFile.getSize() + "");
            sysFile.setFileUrl(nameList.getFileNew());
            sysFile.setBizType("5");
            sysFile.setBizId(nameList.getId()+"");
            sysFile.setCreateTime(new Date());
            sysFile.insert();
//        }
        if (null != nameList.getStranger()) {
            Stranger stranger = strangerService.getById(nameList.getStranger());

            strangerService.updateById(stranger);
        }
        updateById(nameList);
        return msg;
    }

    public ResultInfo uploadFile(MultipartFile file, String id) {
        UploadParams params = null;
        if (file != null) {
            sysFileService.remove(new QueryWrapper<SysFile>().eq("bizId", id+"").eq("bizType", "5"));
            params = new UploadParams("/nameList", file, "5", id);
        }
        return sysFileService.uploadFile(params);
    }


    public String saveOrUpdStranger(NameList nameList, MultipartFile file) {
        String msg = "";
        FaceVo  faceVo=new FaceVo();
        if (nameList.getId() == null) {
            nameList.setCreateUser(sysUserService.getUser().getId());
            nameList.setCreateTime(new Date());
            nameList.setIsDel(0);
            faceVo.setName(nameList.getName());
            faceVo.setSex(nameList.getSex());
            faceVo.setCardid(nameList.getCardId());
            faceVo.setCardtype("1");
            faceVo.setGroupid(Long.valueOf(nameList.getGroupid()));
            faceVo.setBirthday(nameList.getBirthday());
            msg = "添加成功";
        } else {
            nameList.setUpdateUser(sysUserService.getUser().getId());
            nameList.setUpdateTime(new Date());
            faceVo.setName(nameList.getName());
            faceVo.setSex(nameList.getSex());
            faceVo.setCardid(nameList.getCardId());
            faceVo.setCardtype("1");
            faceVo.setGroupid(Long.valueOf(nameList.getGroupid()));
            faceVo.setBirthday(nameList.getBirthday());
            faceVo.setPersonId(nameList.getIccUserId());
            msg = "编辑成功";
        }
        this.saveOrUpdate(nameList);
        if (nameList.getFileIds().size() > 0 && !nameList.getFileIds().isEmpty()) {
            nameList.getFileIds().forEach(a -> {
                SysFile fle = sysFileService.getById(a);
                SysFile f = new SysFile();
                f.setBizId(nameList.getId() + "");
                f.setBizType("5");
                f.setFileUrl(fle.getFileUrl());
                f.setCreateTime(new Date());
                sysFileService.save(f);
                faceVo.setFacepath(f.getFileUrl());
            });
        }
        if (null != nameList.getStranger()) {
            Stranger stranger = strangerService.getById(nameList.getStranger());
            stranger.setIs_nameList(1);
            strangerService.updateById(stranger);
        }

        return msg;
    }

    /**
     * 获取图片信息
     *
     * @param id
     * @return
     */
    public SysFile getFile(Integer id) {
        SysFile file = new SysFile();
        List<SysFile> sysFile = sysFileService.list(new QueryWrapper<SysFile>().eq("bizId", id+"").eq("bizType", "5"));
        if (sysFile != null && !sysFile.isEmpty()) {
            file = sysFile.get(sysFile.size() - 1);
            file.setFileUrl(sysFile.get(sysFile.size() - 1).getFileUrl());
        }
        return file;
    }


    public ResultInfo delect(NameList nameList){
            nameList.setIsDel(1);
            nameListMapper.updateById(nameList);
        return ResultInfo.success();
    }


}

