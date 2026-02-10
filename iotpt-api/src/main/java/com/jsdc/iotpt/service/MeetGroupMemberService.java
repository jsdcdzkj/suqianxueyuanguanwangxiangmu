package com.jsdc.iotpt.service;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.model.MeetGroupMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeetGroupMemberService extends BaseService<MeetGroupMember> {
}
