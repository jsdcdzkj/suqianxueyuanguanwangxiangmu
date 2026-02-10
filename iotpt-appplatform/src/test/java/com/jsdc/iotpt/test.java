package com.jsdc.iotpt;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jsdc.iotpt.model.SysUser;
import com.jsdc.iotpt.service.SysUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class test {
    @Autowired
    private SysUserService sysUserService;


    @Test
    public void pic(){
        File folder = new File("F:\\nowork\\人员头像图片_2024-10-31 14-12-15");
        if (!folder.isDirectory()) {
            System.out.println("给定路径不是一个文件夹");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
        if (files != null && files.length > 0) {
            for (File file : files) {
                String oldName = file.getName();
                String code = oldName.substring(0, oldName.lastIndexOf('.')); // 假设 code 与文件名相同
                List<SysUser> sysUsers = sysUserService.getBaseMapper().selectList(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDel, 0).eq(SysUser::getCardId, code));
                if (CollUtil.isEmpty(sysUsers)) {
                    continue;
                }
                SysUser sysUser = sysUsers.get(0);
                // 从数据库中获取新的名称
                String newName = sysUser.getRealName();
                if (newName != null) {
                    // 生成新的文件名，保持原有的扩展名
                    String newFileName = newName + oldName.substring(oldName.lastIndexOf('.'));
                    File newFile = new File(folder, newFileName);
                    boolean success = file.renameTo(newFile);
                    if (success) {
                        System.out.println("成功将 " + oldName + " 重命名为 " + newFileName);
                    } else {
                        System.out.println("重命名失败: " + oldName);
                    }
                } else {
                    System.out.println("未找到 code: " + code + " 的记录");
                }
            }
        } else {
            System.out.println("文件夹中没有找到任何图片文件");
        }
    }
//    private String getNameByCode(String code) {
//        // 假设你有一个 MyBatis 的 Mapper 接口和相应的 SQL 映射
//        return
//    }
}
