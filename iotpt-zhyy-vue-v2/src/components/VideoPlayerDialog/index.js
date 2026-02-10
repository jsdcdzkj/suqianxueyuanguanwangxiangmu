import Vue from "vue";

import VideoPlayerDialog, { PlayerType } from "@/components/VideoPlayerDialog/index.vue";
import reserveHistoryDialog from "@/views/iTOperationManage/smartDining/Calendar/reserveHistoryDialog.vue";
import sendMessage from "@/views/iTOperationManage/smartDining/Calendar/sendMessage.vue";
/**
 *  创建组件
 * @param Component 组件
 * @param props 参数
 * @param callback 回调
 * @returns {*}
 */
export const createDialog = (Component, props, callback) => {
    const container = document.createElement("div");

    let instance = new Component({
        data: {
            ...props,
            callback: (...args) => {
                if (args.length === 2) {
                    // 异步执行
                    if (args[1] instanceof Function) {
                        callback && callback(...args);
                    } else {
                        // 延迟执行，需要在窗口动画结束之后进行组件销毁
                        setTimeout(() => {
                            if (instance) {
                                instance.$destroy();
                                instance = undefined;
                            }
                        }, 500);
                        callback && callback(...args);
                    }
                } else {
                    setTimeout(() => {
                        if (instance) {
                            instance.$destroy();
                            instance = undefined;
                        }
                    }, 500);
                }
            }
        }
    });

    document.body.appendChild(container);
    instance.$mount(container);
    return instance;
};

/**
 * 创建默认确认弹框
 * @param {videoUrl:String,title?:String,type?: PlayerType}  props
 * showVideoPlayerDialog({videoUrl:""})
 */
export const showVideoPlayerDialog = (props) => {
    return createDialog(Vue.extend(VideoPlayerDialog), props, null);
};
export const showReserveHistoryDialog = (props) => {
    return createDialog(Vue.extend(reserveHistoryDialog), props, null);
};

export const showSendMessageDialog = (props, callback) => {
    return createDialog(Vue.extend(sendMessage), props, callback);
};
