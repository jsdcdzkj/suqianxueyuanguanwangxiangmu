package com.jsdc.iotpt.util;


import java.util.ArrayList;
import java.util.List;

/**
 * @program: wys-service
 * @description: 集合分组工具类
 * @author: wuyuanshn
 **/
public class ListGroupUtil {
    /**
     * 将集合按指定数量分组
     *
     * @param list     数据集合
     * @param quantity 分组数量
     * @return 分组结果
     */
    public static <T> List<List<T>> groupListByQuantity(List<T> list, int quantity) {
        if (list == null || list.size() == 0) {
            return null;
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Wrong quantity.");
        }

        List<List<T>> wrapList = new ArrayList<List<T>>();
        int count = 0;
        while (count < list.size()) {
            wrapList.add(new ArrayList<T>(list.subList(count, Math.min((count + quantity), list.size()))));
            count += quantity;
        }

        return wrapList;
    }




}

