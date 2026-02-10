package com.jsdc.iotpt.service;

import cn.hutool.json.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.G;
import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.mapper.MsgMessageInfoMapper;
import com.jsdc.iotpt.model.MsgMessageInfo;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.vo.EmailVo;
import com.jsdc.iotpt.vo.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.*;

/**
 * ClassName: MsgMessageInfoService
 * Description:
 * date: 2023/5/8 16:51
 *
 * @author bn
 */
@Service
@Slf4j
@Transactional
public class MsgMessageInfoService extends BaseService<MsgMessageInfo> {

    @Autowired
    private MsgMessageInfoMapper msgMessageInfoMapper;
    @Autowired
    private SysUserService sysUserService;

    public List<MsgMessageInfo> getList(MsgMessageInfo msgMessageInfo) {
        List<MsgMessageInfo> msgMessageInfos=msgMessageInfoMapper.selectList(Wrappers.<MsgMessageInfo>lambdaQuery());

        return msgMessageInfos;
    }

    public Integer edit(MsgMessageInfo bean) {
        if (null == bean.getId()){
            long count = msgMessageInfoMapper.selectCount(Wrappers.<MsgMessageInfo>lambdaQuery()
                    .eq(MsgMessageInfo::getIsDel,G.ISDEL_NO)
                    .eq(MsgMessageInfo::getMsgName,bean.getMsgName())
            );
            if (count > 0){
                return -1;
            }
            bean.setStatus(1);
            bean.setIsDel(G.ISDEL_NO);
            bean.setCreateTime(new Date());
            bean.setCreateUser(sysUserService.getUser().getId());
            msgMessageInfoMapper.insert(bean);
            return 1;
        }else {
            long count = msgMessageInfoMapper.selectCount(Wrappers.<MsgMessageInfo>lambdaQuery()
                    .eq(MsgMessageInfo::getIsDel,G.ISDEL_NO)
                    .eq(MsgMessageInfo::getMsgName,bean.getMsgName())
                    .ne(MsgMessageInfo::getId,bean.getId())
            );
            if (count > 0){
                return -1;
            }
            bean.setUpdateTime(new Date());
            bean.setUpdateUser(sysUserService.getUser().getId());
            msgMessageInfoMapper.updateOne(bean);
            return 1;
        }
    }

    /**
     * 根据ID删除消息服务
     * @param id
     */
    public void del(Integer id) {
        MsgMessageInfo bean = msgMessageInfoMapper.selectById(id);
        bean.setIsDel(G.ISDEL_YES);
        msgMessageInfoMapper.updateById(bean);
    }


    /**
     * 消息服务列表查询
     * @param bean
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Page<MsgMessageInfo> getMsgPage(MsgMessageInfo bean, Integer pageIndex, Integer pageSize){
        Page<MsgMessageInfo> page = new Page(pageIndex, pageSize);


        Page<MsgMessageInfo> p = baseMapper.selectPage(page, Wrappers.<MsgMessageInfo>lambdaQuery()
                .eq(MsgMessageInfo::getIsDel, G.ISDEL_NO)
                .eq(StringUtils.isNotBlank(bean.getMsgType()) ,MsgMessageInfo::getMsgType,bean.getMsgType())
                .like(StringUtils.isNotBlank(bean.getMsgName()),MsgMessageInfo::getMsgName,bean.getMsgName())
                .orderByDesc(MsgMessageInfo::getStatus,MsgMessageInfo::getCreateTime)
        );
        return p;
    }


    /**
     * 修改状态
     * @param bean
     */
    public void changeMsgStatus(MsgMessageInfo bean){
        MsgMessageInfo msgMessageInfo = msgMessageInfoMapper.selectById(bean.getId());
        msgMessageInfo.setStatus(bean.getStatus());
        msgMessageInfo.setUpdateTime(new Date());
        msgMessageInfo.setUpdateUser(sysUserService.getUser().getId());
        msgMessageInfoMapper.updateById(msgMessageInfo);
    }


    @Autowired
    private JavaMailSender javaMailSender;


    @Value("${spring.mail.username}")
    private String mailSender;



    @Value("${spring.mail.mailRecipient}")
    private String mailRecipient;

//
//    // 用于带有附件的邮件测试（文件路径）
//    String filePath = "E:\\微服.7z";


    /**
     * @description 发送HTML格式文件邮件
     * @param list  中参数  subject 邮件主题  messageText 邮件文本内容
     */
    public void sendSimpleMail(List<EmailVo> list){
//        for (int i = 0 ; i < 2 ; i++){
//            EmailVo vo = new EmailVo();
//            vo.setSubject(i+"");
//            vo.setMessageText(i+""+i);
//            list.add(vo);
//        }

        for (EmailVo vo : list){
            //1、创建邮件对象（设置参数后提交）
            SimpleMailMessage message = new SimpleMailMessage();
            //2、设置主题
            message.setSubject(vo.getSubject());
            //3、设置邮件发送者

            message.setFrom(mailSender);
            //4、设置邮件接受者，多个接受者传参为数组格式

            message.setTo(mailRecipient);
            //5、设置邮件正文（邮件的正式内容）
            message.setText(vo.getMessageText());
            //6、发送邮件

            try {
                javaMailSender.send(message);
                log.info("文本邮件已发送成功");
            } catch (MailException e) {
                e.printStackTrace();
                throw new RuntimeException("邮件发送异常");
            }
        }
    }


    /**
     * @description 发送HTML格式文件邮件
     * @param list  中参数  subject 邮件主题  htmlText HTML格式的邮件内容
     */
    public void sendHtmlMail(List<EmailVo> list){
//        for (int i = 0 ; i < 2 ; i++){
//            EmailVo vo = new EmailVo();
//            vo.setSubject(i+"");
//            vo.setHtmlText("<div>\n" +
//                    "    <p>这是一个HTML格式的邮件</p> <br>" +
//                    "    <img src=\"https://w.wallhaven.cc/full/p9/wallhaven-p9o51m.png\" width='500' height='300' /><br>\n" +
//                    "    <a href=\"https://www.yb2cc.cn\" title=\"码上就去学习\" target=\"_blank\">点我码上就去学习</a>\n" +
//                    "</div>");
//            list.add(vo);
//        }
        for (EmailVo vo : list){
            //1、获取MimeMessage对象，多用途的网际邮件扩充协议
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper;

            try {
                mimeMessageHelper =  new  MimeMessageHelper(mimeMessage,true);
                //2、设置邮件发送者
                mimeMessageHelper.setFrom(mailSender);
                //3、设置邮件接受方
                mimeMessageHelper.setTo(mailRecipient);
                //4、设置邮件主题
                mimeMessageHelper.setSubject(vo.getSubject());
                //5、设置邮件内容（HTML格式邮件内容），带html格式第二个参数true
                mimeMessageHelper.setText(vo.getHtmlText(),true);
                //6、发送邮件
                javaMailSender.send(mimeMessage);
                log.info("html格式的邮件发送成功");

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException("邮件发送异常");
            }
        }
    }


    /**
     * 发送带有附件的邮件
     * @param
     * @param list 中参数   subject 邮件主题  messageText 邮件文本内容  filePathList 附件路径
     */
    public void sendAppendixMail( List<EmailVo> list){
//
//        for (int i = 0 ; i < 2 ; i++){
//            EmailVo vo = new EmailVo();
//            vo.setSubject(i+"");
//            vo.setMessageText(i+""+i);
//
//            List<String> filePathList = new ArrayList<>();
//            filePathList.add(filePath);
//            vo.setFilePathList(filePathList);
//            list.add(vo);
//        }

        for (EmailVo vo : list){
            //1、获取MimeMessage对象，多用途的网际邮件扩充协议
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper ;

            try {
                mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
                //2、设置邮件发送方
                mimeMessageHelper.setFrom(mailSender);
                //3、设置邮件接收方
                mimeMessageHelper.setTo(mailRecipient);
                //4、设置邮件主题
                mimeMessageHelper.setSubject(vo.getSubject());
                //5、设置邮件内容（第二个参数需要为true）
                mimeMessageHelper.setText(vo.getMessageText(),true);
                //附件内容
                for (String filePath : vo.getFilePathList()) {
                    //FileSystemResource是Spring框架中的一个类,用来以URL或者File方式访问文件系统中的文件
                    //通过FileSystemResource对象以File方式访问指定文件
                    // public FileSystemResource(File file): filePath表示要访问的文件路径
                    FileSystemResource file = new FileSystemResource(new File(filePath));

                    //从给定的文件路径中提取出文件的文件名
//                FileSystemResource file = new FileSystemResource(new File("文件地址"));
//                String filename = file.getFilename(); // 获取附件名称
                    // 例如：如果输入的文件路径“C:\folder\file.txt”，那么它将提取出文件名为“file.txt”。
                    String fileName =  file.getFilename();
                    log.info("附件名称:"+fileName);
                    //addAttachment()方法:添加附件到电子邮件中。它接收两个参数，
                    // 第一个参数:文件的文件名，
                    // 第二个参数:文件对象
                    // 结合使用这两个参数，可以将一个或多个文件添加到电子邮件中
                    mimeMessageHelper.addAttachment(fileName,file);
                }
                //6、发送文件
                javaMailSender.send(mimeMessage);
                log.info("带有附件的邮件发送成功");

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException("邮件发送异常");
            }

        }

    }


}
