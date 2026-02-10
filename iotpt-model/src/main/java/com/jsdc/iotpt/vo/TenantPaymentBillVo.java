package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.TenantPaymentBill;
import lombok.Data;

@Data
public class TenantPaymentBillVo extends TenantPaymentBill {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    private String tenantName;

    private String createUserName;

    private String expenseTypeName;

    private String payTypeName;

    private Integer floorId;

    private String address;
}
