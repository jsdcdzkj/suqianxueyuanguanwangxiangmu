<template>
    <el-drawer :visible="show" size="840px" append-to-body :withHeader="false" :before-close="close">
        <div flex="dir:top main:left cross:top" style="height: calc(100vh - 6px)">
            <div class="header w100" flex="main:justify cross:center">
                <span>文件预览</span>
                <i class="el-icon-close" @click="close()"></i>
            </div>
            <div class="content w100" flex-box="1" flex>
                <div class="content_item" flex="dir:top" flex-box="1" style="padding: 24px">
                    <div class="preview" v-loading="loading" element-loading-text="文件读取中">
                        <el-image
                            :src="imgUrl"
                            style="width: 100%; height: auto"
                            :preview-src-list="[imgUrl]"
                            v-show="imgUrl"
                        ></el-image>
                        <div id="previewContent" v-show="showWord"></div>
                        <iframe :src="iframeUrl" class="iframe" frameborder="0" v-show="iframeUrl"></iframe>
						<div v-if="textContent" class="textContent">{{textContent}}</div>
						<div v-show="!imgUrl&&!showWord&&!iframeUrl&&!excelUrl&&!textContent" class="nothing" flex="dir:top main:center cross:center">
							<el-image :src="require('@/assets/nothing.png')" style="width: 200px;height: 200px;"></el-image>
							<span>该文件不支持在线预览，请下载后在本地预览</span>
						</div>
                    </div>
                </div>
            </div>
            <div class="footer w100" flex="main:right cross:center">
                <el-button @click="close()">关闭</el-button>
            </div>
        </div>
    </el-drawer>
</template>

<script>
import axios from "axios";
import "./components/jszip.min.js";
import { renderAsync } from "./components/docx-preview";
import { downloadContract } from "@/api/tenant/bussinessManage";
var createUrl = null;

export default {
    props: {
        list: {
            type: Array,
            default: []
        },
        show: {
            type: Boolean,
            default: false
        },
        current: {
            type: Number,
            default: 0
        }
    },
    data() {
        return {
            active: 0,
            loading: false,
            iframeUrl: "",
            imgUrl: "",
            excelUrl: "",
			textContent:'',
            showWord: false
        };
    },
    watch: {
        current(val) {
			if(this.show){
				this.preview(val);
			}
        },
		show(val){
			if(val){
				this.preview(this.current);
			}else{
				if(createUrl){
					URL.revokeObjectURL(createUrl);
					createUrl = null;
				}
			}
		}
    },
    mounted() {
        //window.open('http://www.pfile.com.cn/api/profile/onlinePreview?url='+encodeURIComponent(“文档地址”));
    },
    methods: {
        close(down) {
            this.$emit("update:show", false);
            this.excelUrl = '';
            down && down();
        },
        preview(index) {
            this.imgUrl = "";
            this.excelUrl = "";
            this.showWord = false;
            this.iframeUrl = "";
			this.textContent = '';
            this.active = index;
			if(createUrl){
				URL.revokeObjectURL(createUrl);
				createUrl = null;
			}
            var data = this.list[index];
            console.log('888888888888888', this.list, index, data)
            if (!data) return;
            const suffix = (data.name || data.file_name).split(".").pop().toLowerCase();
			this.loading = true;
            if (["webp", "jpg", "jpeg", "png", "gif",'svg'].includes(suffix)) {
                if (data.file_path) {
					this.loading = false;
                    this.imgUrl = data.file_path;
                } else {
                    const reader = new FileReader();
                    reader.readAsDataURL(data.raw);
                    reader.addEventListener(
                        "load",
                        () => {
							this.loading = false;
                            this.imgUrl = reader.result;
                        },
                        false
                    );
                }
            } else if (["docx"].includes(suffix)) {
                console.log('111111111111111data.file_path',data.file_path,data.raw)
                if (data.file_path) {
                    downloadContract({fileId: data.id}, { responseType: "blob" }).then((res) => {
                        this.showWord = true;
						this.loading = false;
                        this.$nextTick(() => {
                            renderAsync(res.data, document.getElementById("previewContent"));
                        });
                    }).catch(()=>{
						this.loading = false;
						this.$message.error('文件获取失败');
					});
                } else {
                    this.showWord = true;
					this.loading = false;
                    this.$nextTick(() => {
                        renderAsync(data.raw, document.getElementById("previewContent"));
                    });
                }
            } else if (["txt"].includes(suffix)){
				if (data.file_path) {
				    downloadContract({fileId: data.id}, { responseType: "blob" }).then((res) => {
						const reader = new FileReader();
						reader.readAsText(res.data);
						reader.addEventListener(
						    "load",
						    () => {
								this.loading = false;
								this.textContent = reader.result;
						    },
						    false
						);
				    }).catch(()=>{
						this.loading = false;
						this.$message.error('文件获取失败');
					});
				} else {
				    const reader = new FileReader();
				    reader.readAsText(data.raw);
				    reader.addEventListener(
				        "load",
				        () => {
				    		this.loading = false;
				    	   this.textContent = reader.result;
				        },
				        false
				    );
				}
			}else if (["xlsx"].includes(suffix)) {
				this.loading = false;
                console.log('222222222222222data.file_path',data.file_path,data.raw)
                this.excelUrl = data.file_path || data.raw;
            } else if (["pdf", "mp4",'ogg','mp3'].includes(suffix)) {
				this.loading = false;
				if(data.file_path){
					this.iframeUrl = data.file_path;
				}else{
					createUrl = URL.createObjectURL(data.raw);
					this.iframeUrl = createUrl;
				}
            } else {
				this.loading = false;
                //this.$alert("该文件不支持在线预览，请下载后在本地预览", "提示");
                //window.open('http://www.pfile.com.cn/api/profile/onlinePreview?url='+encodeURIComponent(data.file_path));
            }
        }
    }
};
</script>

<style lang="scss" scoped>
::v-deep {
    .el-drawer__body {
        padding: 0!important;
    }
}

.w100 {
    width: 100%;
    box-sizing: border-box;
}
.header {
    height: 64px;
    padding: 0 24px;
    color: rgba(0, 0, 0, 0.85);
    font-size: 20px;
    font-weight: bold;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    flex-shrink: 0;
    .el-icon-close {
        font-size: 24px;
        color: rgba(0, 0, 0, 0.5);
        cursor: pointer;
    }
}
.content {
    overflow: hidden;
    .content_item {
        height: 100%;
        padding: 24px 0;
        box-sizing: border-box;
        overflow: hidden;
        border-right: 1px solid rgba(0, 0, 0, 0.06);
        &:nth-last-child(1) {
            border: none;
        }
        .list_box {
            flex: 1;
            overflow: auto;
            padding: 0 24px;
            .item {
                margin-bottom: 12px;
                height: 48px;
                background: #ffffff;
                box-shadow: 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
                border-radius: 4px;
                border: 1px solid rgba(0, 0, 0, 0.1);
                cursor: pointer;
                padding: 0 20px;
                color: rgba(0, 0, 0, 0.85);
                img {
                    width: 20px;
                    height: 20px;
                    margin-right: 12px;
                }
                .name {
                    flex: 1;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    font-size: 16px;
                }
            }
            .active {
                border-color: rgba(30, 149, 244, 1);
                background: rgba(232, 244, 254, 1);
            }
        }

        .preview {
            padding: 24px;
			box-sizing: border-box;
            flex: 1;
			width: 100%;
            overflow: auto;
			position: relative;
            background: rgba(243, 243, 243, 1);
			#previewContent,.iframe,.textContent{
				width: 100%;
				height: 100%;
				overflow: auto;
			}
			.textContent{
				white-space: pre-wrap;
			}
			.nothing{
				position: absolute;
				top: 50%;
				left: 50%;
				transform: translate(-50%,-50%);
				user-select: none;
				span{
					color: rgba(0,0,0,0.5);
					margin-top: 20px;
					white-space: nowrap;
				}
			}
        }
    }
}
.title {
    position: relative;
    height: 40px;
    line-height: 40px;
    padding-left: 20px;
    color: rgba(0, 0, 0, 0.85);
    font-size: 18px;
    font-weight: bold;
    width: calc(100% - 48px);
    margin: 0 auto;
    &:after {
        content: "";
        width: 8px;
        height: 24px;
        background: #1e95f4;
        border-radius: 4px;
        position: absolute;
        top: 8px;
        left: 0;
    }
}
.footer {
    height: 64px;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
    padding: 0 24px;
    flex-shrink: 0;
}
</style>
