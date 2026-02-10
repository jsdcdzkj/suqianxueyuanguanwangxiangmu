<template>
    <div class="tinymce-box">
        <div v-if="detail" class="overly"></div>
        <div id="tinyselect"></div>
    </div>
</template>
<script>
import "./tinymce.min.js";
import "./themes/silver/theme.min.js";
import "./icons/default/icons.min.js";
import "./models/dom/model.min.js";
import "./langs/zh_CN.js";
import "./plugins/image/plugin.min.js";
import "./plugins/link/plugin.min.js";
import "./plugins/importcss/plugin.min.js";
import "./plugins/preview/plugin.min.js";
import { importData } from "@/api/common";

export default {
    name: "tinymce-editor",
    props: {
        content: {
            type: String,
            default: ''
        },
        detail: {
            type: Boolean,
            default: false
        }
        
    },
    watch: {
        content: {
            handler(val) {
                tinymce?.activeEditor?.setContent(val)
            },
            immediate: true, //立即执行
        },
            
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            tinymce.init({
                selector: "#tinyselect",
                language: "zh_CN",
                plugins: "link image preview",
                toolbar: "undo redo | styles fontsize fontfamily | bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | link image preview",
                menubar: false,
                paste_data_images: true,
                toolbar_mode: 'sliding',
                font_size_formats: '8pt 10pt 12pt 14pt 16pt 18pt 24pt 36pt 48pt',
                font_family_formats: "微软雅黑='微软雅黑';宋体='宋体';黑体='黑体';仿宋='仿宋';楷体='楷体';隶书='隶书';幼圆='幼圆';Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings",
                style_formats: [
                {
                    title: "Line Height",
                    items: [
                    { title: "1", block: "p", styles: { "line-height": "1.0" } },
                    { title: "1.5", block: "p", styles: { "line-height": "1.5" } },
                    { title: "1.75", block: "p", styles: { "line-height": "1.75" } },
                    { title: "2", block: "p", styles: { "line-height": "2" } },
                    { title: "3", block: "p", styles: { "line-height": "3" } },
                    { title: "4", block: "p", styles: { "line-height": "4" } },
                    { title: "5", block: "p", styles: { "line-height": "5" } }
                    ]
                }],
                images_upload_handler: (blobInfo, progress) => new Promise(async(resolve, reject) => {
                    const formData = new FormData();
                    formData.append('file', blobInfo.blob());
                    const res = await importData(formData)
                    if (res.data.code == 0) {
                        let file = process.env.VUE_APP_BASE_API + "/minio/previewFile?fileName=" + res.data.data.filename;
                        resolve(file);
                        return;
                    }
                    reject('上传失败');
                }),
                height: '500px',
                setup:(editor)=>{
                    editor.on('init',(e)=>{editor.setContent(this.content)});
                },
                //·监听:input·称·change·李件，史时更新 
                init_instance_callback:(editor)=>{
                    editor.on('input',(e)=>{
                        this.$emit('inputFuc',e.target.innerHTML)
                    })
                    editor.on('Change',(e)=>{
                        this.$emit('inputFuc', e.level.content)
                    })
                }
            });
        }
    },
    beforeDestroy() {

        tinymce.remove("#tinyselect")
    },
};
</script>
<style lang="scss">
@import url("./skins/ui/oxide/skin.min.css");

.tox-tinymce-aux {
    z-index: 2300;
}

.tox .tox-statusbar {
    display: none;
}

.tinymce-box {
    position: relative;

    .overly {
        width: calc(100% - 16px);
        height: calc(100% - 16px);
        background: rgba(245,247,250,0.32);
        position: absolute;
        top:0;
        left:0;
        z-index:3000;
    }
}
</style>