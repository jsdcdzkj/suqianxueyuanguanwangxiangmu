package com.jsdc.iotpt.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class SysMenuVo implements TreeEntity<SysMenuVo>{

    private static final long serialVersionUID = 1L;

    private String id;

    private String parentId;

    private String title;

    private String routerName;

    private String routerUrl;

    private String vueUrl;

    private String redirectType;

    private String icon;

    private Integer isShow;

    private Integer sort;

    private Integer menuType;

    private Integer systemId;

    private Date createTime;

    private Integer createUser;

    private Date updateTime;

    private Integer updateUser;

    private Integer isDel;

    private List<SysMenuVo> children;

    @Override
    public void setChildList(List<SysMenuVo> childList) {
        this.children = childList;
    }
}
