package com.jsdc.iotpt.vo;


import com.jsdc.iotpt.model.SysMenu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RouterVo extends SysMenu implements TreeEntity<RouterVo> {

    private static final long serialVersionUID = 1L;

//    private String id;

//    private String parentId;

    private String path;

    private String component;

    private String componentUrl;

    private String name;

    private String redirect;

    private MetaVo meta;

    private List<RouterVo> children;


//    @Override
//    public String getId() {
//        return this.id;
//    }
//
//    @Override
//    public String getParentId() {
//        return this.parentId;
//    }

    @Override
    public void setChildList(List<RouterVo> childList) {
        this.children = childList;
    }
}
