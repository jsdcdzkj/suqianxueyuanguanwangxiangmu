import Vue from "vue";
import store from "@/store";
class DialogFactory {
    cacheMap = new Map();
    _componentInstances = [];
    showCurrent = -1;

    createDialogAsync(Component, props, dialogConfig = {}) {
        const componentInstance = this.createDialog(Component, props, dialogConfig);
        return new Promise((resolve) => {
            componentInstance.$on("submit", (data) => {
                resolve(data);
            });
        });
    }

    createDialog(Component, props, dialogConfig = {}) {
        const componentInstance = this._createComponent(Component, props);
        const container = this._createContainer();

        // 组件销毁
        componentInstance.$on("destroy", () => {
            componentInstance.$destroy();
            this._componentInstances.splice(
                this._componentInstances.findIndex((item) => item === componentInstance),
                1
            );
        });

        // 挂载
        document.body.appendChild(container);
        componentInstance.$mount(container);
        // 绑定
        this._componentInstances.push(componentInstance);
        this.showCurrent = this._componentInstances.length - 1;
        // 更新配置

        componentInstance.$children[0].$set(componentInstance.$children[0], "config", {
            ...componentInstance.$children[0].config,
            ...dialogConfig
        });
        return componentInstance;
    }

    getCurrentInstance() {
        if (this._componentInstance.length > 0) {
            return this._componentInstances[this._componentInstance.length - 1];
        }
        return null;
    }

    destoryAll() {
        for (let i = 0; i < this._componentInstances.length; i++) {
            const element = this._componentInstances[i];
            element.$destroy();
            this._componentInstances.pop();
        }
        this.showCurrent = -1;
    }

    _createContainer() {
        return document.createElement("div");
    }

    _createComponent(Component, props) {
        Component = Vue.extend(Component);
        return new Component({
            data: {
                ...props
            },
            store
        });
    }
}

export const dialogFactoryInstance = new DialogFactory();
