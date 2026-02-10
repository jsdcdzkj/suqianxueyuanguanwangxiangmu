package com.jsdc.iotpt.vo;

import com.jsdc.iotpt.model.sys.SysMessage;
import lombok.Data;

import java.util.List;

/**
 * ClassName: SysMessageVo
 * Description:
 * date: 2024/11/27 11:50
 *
 * @author bn
 */
@Data
public class SysMessageVo extends SysMessage {


    private List<Integer> msgIds;

    private Integer pageIndex;

    private Integer pageSize;

    private String cid;

}
