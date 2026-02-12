package com.jsdc.iotpt.util;

import com.jsdc.iotpt.vo.TreeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析树形数据工具类
 *
 * @Description:
 */
public class TreeParserUtils {

    /**
     * 解析树形数据
     *
     * @param topId
     * @param entityList
     * @return
     * @author yusheng
     * @date 2020/3/18
     */
    public static <E extends TreeEntity<E>> List<E> getTreeList(String topId, List<E> entityList) {
        List<E> resultList = new ArrayList<>();

        //获取顶层元素集合
        Integer parentId;
        for (E entity : entityList) {
            parentId = entity.getParentId();
            if (parentId == null || topId.equals(parentId.toString())) {
                resultList.add(entity);
            }
        }

        //获取每个顶层元素的子数据集合
        for (E entity : resultList) {
            entity.setChildList(getSubList(entity.getId().toString(), entityList));
        }

        return resultList;
    }

    /**
     * 获取子数据集合
     *
     * @param id
     * @param entityList
     * @return
     * @author yusheng
     * @date 2020/3/18 11:23
     */
    private static <E extends TreeEntity<E>> List<E> getSubList(String id, List<E> entityList) {
        List<E> childList = new ArrayList<>();
        String parentId;

        //子集的直接子对象
        for (E entity : entityList) {
            parentId = entity.getParentId().toString();
            if (id.equals(parentId)) {
                childList.add(entity);
            }
        }

        //子集的间接子对象
        for (E entity : childList) {
            entity.setChildList(getSubList(entity.getId().toString(), entityList));
        }

        //递归退出条件
        if (childList.size() == 0) {
            return childList;
        }

        return childList;
    }
}
