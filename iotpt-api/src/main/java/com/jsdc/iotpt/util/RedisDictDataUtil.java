package com.jsdc.iotpt.util;

import com.jsdc.iotpt.common.utils.RedisUtils;
import com.jsdc.iotpt.model.sys.SysDict;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 根据类型获取redis缓存中的字典集合
 */
public class RedisDictDataUtil {

    /**
     * 根据字典类型获取对应Hash字典，用于根据key翻译字典值
     *
     * @param dictType 字典类型 value 字典value
     * @return
     */
    public static HashMap<String, SysDict> getDictByType(String dictType) {
        Map<String, HashMap> dicData = (Map<String, HashMap>) RedisUtils.getBeanValue("dictData");
        HashMap<String, SysDict> dictMap = dicData.get(dictType);
        return dictMap;
    }

    /**
     * 组装成树形结构数据,children为子节点, i 为顶级节点
     * @param list
     * @param i
     * @return
     */
    public static List<SysDict> getTreeList(List<SysDict> list, int i) {
        List<SysDict> collect = list.stream().filter(sysDict -> sysDict.getParentId() == i).collect(Collectors.toList());
        for (SysDict sysDict : collect) {
            List<SysDict> children = getTreeList(list, sysDict.getId());
            sysDict.setChildren(children);
        }
        return collect;
    }
}
