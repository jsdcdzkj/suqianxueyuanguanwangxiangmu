package com.jsdc.iotpt.service;

import com.jsdc.iotpt.base.BaseService;
import com.jsdc.iotpt.common.DateUtils;
import com.jsdc.iotpt.mapper.WalkActionLocusMapper;
import com.jsdc.iotpt.model.WalkActionLocus;
import com.jsdc.iotpt.model.WalkRetentionRules;
import com.jsdc.iotpt.model.WalkTimes;
import com.jsdc.iotpt.model.sys.SysOrgDept;
import com.jsdc.iotpt.util.FileUtils;
import com.jsdc.iotpt.vo.WalkActionLocusVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WalkActionLocusService extends BaseService<WalkActionLocus> {

    @Autowired
    private WalkActionLocusMapper actionLocusMapper;

    @Autowired
    private WalkRetentionRulesService retentionRulesService;

    @Autowired
    private WalkTimesService timesService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysOrgDeptService departmentService;


    public List<WalkActionLocus> getActionsById(String faceId, String time, Integer floor) {
        return actionLocusMapper.getActionsById(faceId, time, floor);
    }

    public List<WalkActionLocus> getActionsByTime(String faceId, String startTime, String endTime) {
        return actionLocusMapper.getActionsByTime(faceId, startTime, endTime);
    }

    /**
     * 滞留人员查询
     */
    public List<WalkActionLocusVo> retentionPeople(String realName, Integer isFlag, String createTime, Integer deptId) {
        // 取所有滞留规则
        WalkRetentionRules retentionRules = new WalkRetentionRules();
        retentionRules.setDeptId(deptId);
        List<WalkRetentionRules> retentionRulesList = retentionRulesService.getList(retentionRules);
        // 滞留消息
        List<WalkActionLocusVo> registerList = new ArrayList<>();
        List<WalkActionLocusVo> result = new ArrayList<>();
        retentionRulesList.forEach(x -> {
            // 取时间
            WalkTimes times = timesService.getById(x.getTimesId());
            List<String> weeks = Arrays.asList(times.getDays().split(","));
            // 不在时间范围内 退出
            if (!weeks.contains(String.valueOf(DateUtils.dateToWeek(null, createTime)))) {
                return;
            }
            List<String> timeAry = Arrays.asList(times.getTimeAry().split(","));
            List<Float> endTime = new ArrayList<>();
            for (int i = 0; i < timeAry.size(); i++) {
                if (i % 2 == 1) {
                    endTime.add(DateUtils.stringTimeToFloat(timeAry.get(i)));
                }
            }

            Collections.sort(endTime);


            // 根据时间查经访客的人员
            System.out.println("1: " + System.currentTimeMillis());
            List<WalkActionLocusVo> actionLocusVoList = actionLocusMapper.signInAndOut(realName, createTime, isFlag, deptId, "(0,1,2)");
            System.out.println("2: " + System.currentTimeMillis());
            // 根据faceId统计访客信息
            Map<String, List<WalkActionLocusVo>> groupFaceId = new HashMap<>();

            actionLocusVoList.forEach(item -> {
                if (groupFaceId.containsKey(item.getFaceId())) {
                    groupFaceId.get(item.getFaceId()).add(item);
                } else {
                    List<WalkActionLocusVo> voList = new ArrayList<>();
                    voList.add(item);
                    groupFaceId.put(item.getFaceId(), voList);
                }
            });

            groupFaceId.forEach((key, value) -> {
                // 判断这个数据是否符合规则，如果A星期天进来星期一出去,此条数据不符合本次查询,所以过滤掉
                // 所在时间组结束时间
                float timeDivision = 100;
                for (int i = 0; i < value.size(); i++) {

                    if (value.get(i).getTurnstileType() == 0 || value.get(i).getTurnstileType() == 2) { // 判断进的数据是否违规
                        for (int j = 0; j < endTime.size(); j++) {
                            if (value.get(i).getTimeDivision() <= endTime.get(j)) { // 判断出去的数据处于哪个时间滞留时间段
                                timeDivision = endTime.get(j);
                                break;
                            }
                        }
                        // timeDivision没有变化,说明不在时间段之内 直接置为滞留
                        if (timeDivision == 100) {
                            registerList.add(value.get(i));
                            continue;
                        }

                        if (i == (value.size() - 1)) {// 当天最后一条数据
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            // 时间组最后结束时间
                            String time = String.format("%s %s", createTime, DateUtils.floatToTime(timeDivision));
                            try {
                                boolean b = DateUtils.compareToDate(new Date(), sdf.parse(time));
                                if (b) {
                                    registerList.add(value.get(i));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }


                    } else { // 判断出去的数据
                        if (value.get(i).getTimeDivision() > timeDivision) {
                            registerList.add(value.get(i));
                        }

                        // timeDivision没有变化,说明不在时间段之内 直接置为滞留
                        if (timeDivision == 100) {
                            registerList.add(value.get(i));

                        }

                        timeDivision = 100;
                    }
                }
                if (CollectionUtils.isNotEmpty(registerList)) {
                    ListSort(registerList);
                    result.add(registerList.get(0));
                }

            });


        });


        //ListSort(registerList);

        return result;


    }

    /**
     * 滞留人员查询
     */
    public List<WalkActionLocusVo> retentionPeople2(Integer isFlag, String createTime, Integer deptId, Integer floor) {
        // 取所有滞留规则
        WalkRetentionRules retentionRules = new WalkRetentionRules();
        retentionRules.setDeptId(deptId);
        List<WalkRetentionRules> retentionRulesList = retentionRulesService.getList(retentionRules);

        List<WalkActionLocusVo> result = new ArrayList<>();
        retentionRulesList.forEach(x -> {
            // 取时间
            WalkTimes times = timesService.getById(x.getTimesId());
            List<String> weeks = Arrays.asList(times.getDays().split(","));
            // 不在时间范围内 退出
            if (!weeks.contains(String.valueOf(DateUtils.dateToWeek(null, createTime)))) {
                return;
            }
            List<String> timeAry = Arrays.asList(times.getTimeAry().split(","));
            List<Float> endTime = new ArrayList<>();
            for (int i = 0; i < timeAry.size(); i++) {
                if (i % 2 == 1) {
                    endTime.add(DateUtils.stringTimeToFloat(timeAry.get(i)));
                }
            }

            Collections.sort(endTime);


            // 根据时间查经访客的人员
            List<WalkActionLocusVo> actionLocusVoList = actionLocusMapper.signInAndOut2(createTime, isFlag, deptId, "(0,1,2)", floor);

            // 根据faceId统计访客信息
            Map<String, List<WalkActionLocusVo>> groupFaceId = new HashMap<>();

            actionLocusVoList.forEach(item -> {
                if (groupFaceId.containsKey(item.getFaceId())) {
                    groupFaceId.get(item.getFaceId()).add(item);
                } else {
                    List<WalkActionLocusVo> voList = new ArrayList<>();
                    voList.add(item);
                    groupFaceId.put(item.getFaceId(), voList);
                }
            });

            groupFaceId.forEach((key, value) -> {
                // 滞留消息
                List<WalkActionLocusVo> registerList = new ArrayList<>();
                // 判断这个数据是否符合规则，如果A星期天进来星期一出去,此条数据不符合本次查询,所以过滤掉
                // 所在时间组结束时间
                float timeDivision = 100;
                for (int i = 0; i < value.size(); i++) {

                    if (value.get(i).getTurnstileType() == 0 || value.get(i).getTurnstileType() == 2) { // 判断进的数据是否违规
                        for (int j = 0; j < endTime.size(); j++) {
                            if (value.get(i).getTimeDivision() <= endTime.get(j)) { // 判断出去的数据处于哪个时间滞留时间段
                                timeDivision = endTime.get(j);
                                break;
                            }
                        }
                        // timeDivision没有变化,说明不在时间段之内 直接置为滞留
                        if (timeDivision == 100) {
                            registerList.add(value.get(i));
                            continue;
                        }

                        if (i == (value.size() - 1)) {// 当天最后一条数据
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            // 时间组最后结束时间
                            String time = String.format("%s %s", createTime, DateUtils.floatToTime(timeDivision));
                            try {
                                boolean b = DateUtils.compareToDate(new Date(), sdf.parse(time));
                                if (b) {
                                    registerList.add(value.get(i));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }


                    } else { // 判断出去的数据
                        if (value.get(i).getTimeDivision() > timeDivision) {
                            registerList.add(value.get(i));
                        }

                        // timeDivision没有变化,说明不在时间段之内 直接置为滞留
                        if (timeDivision == 100) {
                            registerList.add(value.get(i));

                        }

                        timeDivision = 100;
                    }
                }
                if (CollectionUtils.isNotEmpty(registerList)) {
                    ListSort(registerList);
                    result.add(registerList.get(0));
                }


            });


        });


        return result;


    }

    // 排序
    private static void ListSort(List<WalkActionLocusVo> list) {
        //用Collections这个工具类传list进来排序
        Collections.sort(list, new Comparator<WalkActionLocusVo>() {
            @Override
            public int compare(WalkActionLocusVo o1, WalkActionLocusVo o2) {
                try {
                    if (o1.getCreateTime().compareTo(o2.getCreateTime()) < 0) {
                        return 1;//小的放前面
                    } else {
                        return -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


    /**
     * 根据id 查询当天这个人的说有数据
     *
     * @param id
     * @return
     */
    public List<WalkActionLocus> selectToDayById(Integer id) {

        return actionLocusMapper.selectToDayById(id);
    }


    public Map<String, List<WalkActionLocusVo>> retentionPeopleInfo(String createTime, Integer deptId) {
        List<WalkActionLocusVo> actionLocusVoList = retentionPeople(null, null, createTime, deptId);

        // 根据faceId统计访客信息
        Map<String, List<WalkActionLocusVo>> groupFaceId = new HashMap<>();

        actionLocusVoList.forEach(item -> {
            if (groupFaceId.containsKey(item.getFaceId())) {
                groupFaceId.get(item.getFaceId()).add(item);
            } else {
                List<WalkActionLocusVo> voList = new ArrayList<>();
                voList.add(item);
                groupFaceId.put(item.getFaceId(), voList);
            }
        });

        return groupFaceId;

    }

    public List<WalkActionLocusVo> retentionEnd(String createTime, Integer deptId) {
        Map<String, List<WalkActionLocusVo>> map = retentionPeopleInfo(createTime, deptId);

        List<WalkActionLocusVo> locusVos = new ArrayList<>();
        map.forEach((k, v) -> {
            locusVos.add(v.get(0));
        });

//        locusVos.forEach(x->{
//
//        });

        for (WalkActionLocusVo item : locusVos) {

            String base64 = null;
            try {
                base64 = FileUtils.filePathToBase64(item.getFilePath());
            } catch (Exception e) {

            }
            item.setBase64(base64);

        }

        ListSort(locusVos);

        return locusVos;

    }
}
