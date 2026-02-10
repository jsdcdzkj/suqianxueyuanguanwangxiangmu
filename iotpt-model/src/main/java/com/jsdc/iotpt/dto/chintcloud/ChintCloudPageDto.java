package com.jsdc.iotpt.dto.chintcloud;

import lombok.Data;

import java.util.List;

/**
 * 泰杰赛智慧照明系统 分页响应体
 */

@Data
public class ChintCloudPageDto<T> {

    /**
     * 当前页码
     */
    private Integer currentPage;

    /**
     * 总页码
     */
    private Integer totalPages;

    /**
     * 总数据条数
     */
    private Integer totalItems;

    /**
     * 分页数据条数
     */
    private Integer pageSize;

    /**
     * 数据
     */
    private List<T> items;

}
