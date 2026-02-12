package com.jsdc.iotpt.vo;

import java.util.List;

public interface TreeEntity<E> {

    public Integer getId();

    public Integer getParentId();

    public void setChildList(List<E> childList);
}
