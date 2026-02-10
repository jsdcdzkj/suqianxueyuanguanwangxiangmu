<template>
    <BasePage v-bind="page" :aside-left-width="280">
        <template #pageLeft>
            <areaChoose></areaChoose>
        </template>
        <template #pageBody>
            <div
                class="flex items-center justify-between h-48px border-b-1 border-b-solid border-[#0000000F] mt--10px m-l--10px">
                <div class="flex">
                    <div class="w-152px h-48px flex items-center justify-center text-16px cursor-pointer"
                        :class="tabs == 1 ? 'bg-[#345BAD1A] text-[#345BADFF] border-r-0 border-b-3 border-b-solid border-b-[#345BADFF]' : 'border-r-1 border-r-solid border-[#0000000F] text-[#000000D9]'"
                        @click="tabs = 1">
                        全部(145)
                    </div>
                    <div class="w-152px h-48px flex items-center justify-center text-16px cursor-pointer"
                        :class="tabs == 2 ? 'bg-[#345BAD1A] text-[#345BADFF] border-r-0 border-b-3 border-b-solid border-b-[#345BADFF]' : 'border-r-1 border-r-solid border-[#0000000F] text-[#000000D9]'"
                        @click="tabs = 2">
                        <div class="size-12px rounded-full bg-[#57BD94FF] mr-8px"></div>
                        <span>在线(140)</span>
                    </div>
                    <div class="w-152px h-48px flex items-center justify-center text-16px cursor-pointer"
                        :class="tabs == 3 ? 'bg-[#345BAD1A] text-[#345BADFF] border-r-0 border-b-3 border-b-solid border-b-[#345BADFF]' : 'border-r-1 border-r-solid border-[#0000000F] text-[#000000D9]'"
                        @click="tabs = 3">
                        <div class="size-12px rounded-full bg-[#ED777AFF] mr-8px"></div>
                        <span>告警(40)</span>
                    </div>
                    <div class="w-152px h-48px flex items-center justify-center text-16px cursor-pointer"
                        :class="tabs == 4 ? 'bg-[#345BAD1A] text-[#345BADFF] border-r-0 border-b-3 border-b-solid border-b-[#345BADFF]' : ''"
                        @click="tabs = 4">
                        <div class="size-12px rounded-full bg-[#00000026] mr-8px"></div>
                        <span>离线(10)</span>
                    </div>
                </div>
                <div class="flex gap-8px">
                    <el-input v-model="queryParams.name" placeholder="请输入设备名称" class="w-200px"></el-input>
                    <el-select v-model="queryParams.status" placeholder="请选择设备状态" class="w-200px">
                        <el-option label="全部" value=""></el-option>
                        <el-option label="在线" value="1"></el-option>
                        <el-option label="告警" value="2"></el-option>
                        <el-option label="离线" value="3"></el-option>
                    </el-select>
                    <el-button type="primary" :icon="Search">查询</el-button>
                    <el-button type="default" :icon="Refresh" class="!mr-0">重置</el-button>
                </div>
            </div>
            <el-row :gutter="12" class="overflow-auto flex-1 content-start mt-12px">
                <!-- <el-col :span="8" class="mb12px">
                    <div class="rounded-8px overflow-hidden border-1 border-solid border-[#00000026] !border-t-none">
                        <div
                            class="flex items-center justify-between h-44px px-12px border-t-3 border-t-solid border-t-[#ED777AFF] bg-[#ED777A1F]">
                            <div>
                                <i class="ri-dashboard-2-line text-20px text-[#ED777AFF]"></i>
                                <span class="ml-8px text-16px text-[#000000D9] font-bold">教学楼A01</span>
                            </div>
                            <div class="flex items-center text-14px text-[#000000A6]">
                                <span>状态</span>
                                <div class="text-white text-14px p-4px rounded-4px bg-[#ED777AFF] ml-4px">告警</div>
                            </div>
                        </div>
                    </div>
                </el-col>
                <el-col :span="8" class="mb12px">
                    <div class="rounded-8px overflow-hidden border-1 border-solid border-[#00000026] !border-t-none">
                        <div
                            class="flex items-center justify-between h-44px px-12px border-t-3 border-t-solid border-t-[#00000073] bg-[#0000000F]">
                            <div>
                                <i class="ri-dashboard-2-line text-20px text-[#00000073]"></i>
                                <span class="ml-8px text-16px text-[#000000D9] font-bold">教学楼A01</span>
                            </div>
                            <div class="flex items-center text-14px text-[#000000A6]">
                                <span>状态</span>
                                <div class="text-white text-14px p-4px rounded-4px bg-[#00000073] ml-4px">离线</div>
                            </div>
                        </div>
                    </div>
                </el-col> -->
                <el-col :span="8" class="mb12px" v-for="i in 12" @click="openDetail">
                    <div
                        class="rounded-8px overflow-hidden border-1 border-solid border-[#00000026] !border-t-none cursor-pointer">
                        <div
                            class="flex items-center justify-between h-44px px-12px border-t-3 border-t-solid border-t-[#57BD94FF] bg-[#57BD941F]">
                            <div>
                                <i class="ri-dashboard-2-line text-20px text-[#57BD94FF]"></i>
                                <span class="ml-8px text-16px text-[#000000D9] font-bold">教学楼A01</span>
                            </div>
                            <div class="flex items-center text-14px text-[#000000A6]">
                                <span>状态</span>
                                <div class="text-white text-14px p-4px rounded-4px bg-[#57BD94FF] ml-4px">在线</div>
                            </div>
                        </div>
                        <div class="p-12px flex items-center">
                            <div class="flex-1 mr-8px">
                                <div class="flex items-center text-14px text-[#000000D9] mb-8px">
                                    <div class="w-78px font-bold">设备类型:</div>
                                    <div class="p-4px rounded-4px bg-[#F8F8F8FF] text-[#000000A6">主管网水表</div>
                                </div>
                                <div class="flex items-center text-14px text-[#000000D9] mb-8px">
                                    <div class="w-78px font-bold">所在区域:</div>
                                    <div class="text-[#000000A6">宿迁学院 / 东北校区 / 商学院</div>
                                </div>
                                <div class="p-8px rounded-4px bg-[#F8F8F8FF]">
                                    <div class="flex items-center text-14px text-[#000000D9] mb-8px">
                                        <div class="w-70px font-bold">压力:</div>
                                        <div class="text-[#000000A6"><span class="text-[#57BD94FF]">0.46</span> MPa
                                        </div>
                                    </div>
                                    <div class="flex items-center text-14px text-[#000000D9] mb-8px">
                                        <div class="w-70px font-bold">瞬时流量:</div>
                                        <div class="text-[#000000A6"><span class="text-[#57BD94FF]">0.46</span> m³/h
                                        </div>
                                    </div>
                                    <div class="flex items-center text-14px text-[#000000D9]">
                                        <div class="w-70px font-bold">累计用量:</div>
                                        <div class="text-[#000000A6"><span class="text-[#57BD94FF]">0.46</span> m³/h
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <el-image class="w-122px h-122px"></el-image>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </template>
    </BasePage>
</template>

<script setup lang="tsx">
import BasePage from "@/core/struct/page/base-page";
import { ref } from 'vue'
import { Search, Refresh } from "@element-plus/icons-vue";
import { usePage } from "@/core/struct/page";
import areaChoose from "@/components/area/index.vue";
import Detail from './components/detail.vue'
import { createDrawerAsync } from '@/core/dialog/index'
defineOptions({
    name: 'water-meter-monitoring'
})
const tabs = ref(1);
const dataList = ref([]);
const queryParams = {
    pageNo: 1,
    pageIndex: 20,
    name: '',
    status: ''
}
const { page, pageApi } = usePage({
    title: "",
    pagination: {
        currentPage: 1,
        pageSize: 20,
        total: 15,
        background: true,
        layout: "prev, pager, next, jumper, ->, total",
        onChange(pageNo: number, pageSize: number) {
            if (page.pagination) {
                page.pagination.currentPage = pageNo;
                page.pagination.pageSize = pageSize;
                pageApi.pageList();
            }
        }
    }
});
const openDetail = () => {
    createDrawerAsync(
        { title: "水表检测详情", width: '960px', showNext: false, showCancel: false, showConfirm: false },
        {},
        <Detail />
    ).then(() => {
        // pageApi.pageList();
    });
}
</script>

<style scoped lang="scss"></style>