export const createDialog = (Component, props, callback) => {
    return new Promise((resolve, reject) => {
        const container = document.createElement("div");

        let instance = new Component({
            data: {
                ...props,
                callback: (...args) => {
                    console.log(args);

                    if (args.length > 0) {
                        // 异步执行
                        callback && callback(...args);
                        resolve(args[0]);
                    } else {
                        // callback &&
                        //     callback({
                        //         form: null,
                        //         done: null,
                        //         type: "cancel",
                        //         refresh: null
                        //     });
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
    });
};

export const AynsCommonDialog = {
    data() {
        return {
            // 按钮加载中
            btnLoading: false,
            // 是否是异步
            isAsync: false,
            // 显示dialog
            show: true,
            title: "标题",
            // 回调函数
            callback: null,
            // 是否显示取消按钮
            cancelButton: true,
            // 是否显示确认按钮
            confirmButton: true,
            width: "1000px"
        };
    },
    methods: {
        done() {
            this.btnLoading = !this.btnLoading;
            this.show = false;
            this.callback && this.callback();
            return this.show;
        },
        refresh() {
            this.btnLoading = !this.btnLoading;
        },

        /**
         * 确认按钮取消事件
         */
        onCancel() {
            // this.show = false;
            this._onConfirm("cancel");
        },
        /**
         * 确认按钮点击事件
         */
        onConfirm() {
            if (this.$refs.form) {
                this.$refs.form.validate((valid) => {
                    if (valid) {
                        this._onConfirm("confirm");
                    }
                });
            } else {
                this._onConfirm("confirm");
            }
        },
        _onConfirm(type) {
            if (this.isAsync) {
                this.btnLoading = true;
                this.callback &&
                    this.callback({
                        form: this.form,
                        done: this.done.bind(this),
                        refresh: this.refresh,
                        type
                    });
            } else {
                this.show = false;
                this.callback &&
                    this.callback({
                        form: this.form,
                        done: null,
                        refresh: this.refresh,
                        type
                    });
            }
        }
    }
};

export const pagenation = {
    data() {
        return {
            /**分页数据**/
            page: {
                pageIndex: 1,
                pageSize: 20
            },
            pageKey: "pageIndex",
            /**表数据**/
            table: {
                records: [],
                // 数量
                total: 0,
                // 加载中
                loading: false,
                api: null,
                extraKey: ""
            }
        };
    },
    methods: {
        /**
         * 分页改变事件
         * @param {Number} page
         * @param {Number} limit
         */
        async onPaginationChange({ page, limit }) {
            this.page[this.pageKey] = page;
            this.page.pageSize = limit;
            await this.fetchTableData();
        },
        async handleSizeChange(size) {
            this.page.pageSize = size;
            await this.fetchTableData();
        },
        async pageCurrentChange(page) {
            this.page[this.pageKey] = page;
            await this.fetchTableData();
        },
        /**
         * 分页改变事件
         * @param {Number} page
         * @param {Number} limit
         */
        async onPaginationChange({ page, limit }) {
            this.page[this.pageKey] = page;
            this.page.pageSize = limit;
            await this.fetchTableData();
        },
        /** 请求数据 **/
        async fetchTableData(callback) {
            this.table.loading = !this.table.loading;
            try {
                const page = {};
                page[this.pageKey] = this.page[this.pageKey];
                page["pageSize"] = this.page.pageSize;
                const res = await this.table.api(
                    Object.assign(
                        {},
                        callback
                            ? callback(Reflect.get(this, this.table.extraKey))
                            : Reflect.get(this, this.table.extraKey),
                        page
                    )
                );
                if (res.code === 0) {
                    this.table.records = res.data.records;
                    this.table.total = res.data.total;
                    if (this.scrollTop) {
                        this.scrollTop();
                    }
                }
            } catch (e) {
                console.error(e);
            } finally {
                this.table.loading = !this.table.loading;
            }
        },
        /**
         * 绑定请求接口
         * @param reqApi {Function} 接口
         * @param extraKey {String} 额外参数
         * @param isStartRequest {Boolean} 是否立即立即请求接口
         */
        async bindList(reqApi, extraKey, isStartRequest = true) {
            this.table.api = reqApi;
            this.table.extraKey = extraKey;
            this.table.cacheParams = { ...this[extraKey] };
            if (isStartRequest) {
                await this.fetchTableData();
            }
        },
        indexMethod(index) {
            return (this.page[this.pageKey] - 1) * this.page.pageSize + index + 1;
        },
        handlerSearch(callback) {
            this.page[this.pageKey] = 1;
            this.fetchTableData(callback);
        },
        handlerRefresh(callback) {
            this.page[this.pageKey] = 1;
            this[this.table.extraKey] = { ...this.table.cacheParams };
            this.fetchTableData(callback);
        }
    }
};
