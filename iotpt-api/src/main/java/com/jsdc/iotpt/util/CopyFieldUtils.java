package com.jsdc.iotpt.util;


import java.lang.reflect.Field;

/**
 * @author wr
 */
public class CopyFieldUtils {
    /***
     * 将source 中 与 destination 相同的字段 值拷贝到 destination
     * @Param source
     * @Param destination
     * @Date 2023/8/30 11:08
     * @Author wr
     */
    public static void copyFields(Object source, Object destination) {
        Class<?> sourceClass = source.getClass();
        Class<?> destinationClass = destination.getClass();

        for (Field sourceField : sourceClass.getDeclaredFields()) {
            for (Field destinationField : destinationClass.getDeclaredFields()) {
                if (!sourceField.getName().equals("id")){
                    if (sourceField.getName().equals(destinationField.getName())) {
                        try {
                            sourceField.setAccessible(true);
                            destinationField.setAccessible(true);
                            Object value = sourceField.get(source);
                            destinationField.set(destination, value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

//    public static void main(String[] args) {
//        SetUpProject setUpProject = new SetUpProject();
//        setUpProject.setStatus(2);
//        setUpProject.setProjectName("测试项目");
//        Procure procure = new Procure();
//        System.out.println(procure);
//        copyFields(setUpProject,procure);
////        BeanUtils.copyProperties(setUpProject,procure);
//        System.out.println(procure);
//    }
}
