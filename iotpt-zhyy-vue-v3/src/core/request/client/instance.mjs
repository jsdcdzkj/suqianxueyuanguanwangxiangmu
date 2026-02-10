import axios from "axios";
import { defu as merge } from "defu";
import { HEADERS, TIME_OUT, METHOD } from "./common.mjs";
import { InterceptorManager } from "./interceptors.mjs";
import { CancelTokenManager } from "./cancel-token.mjs";
import { DownloaderManger } from "./downloader.mjs";
import { UploaderManager } from "./uploader.mjs";
class RequestClient {
  options = null;
  instance = null;
  addRequestInterceptor;
  addResponseInterceptor;
  download;
  downloadFile;
  upload;
  refreshTokenQueue = [];
  cancelTokenManager;
  refreshing;
  constructor(options = {}) {
    this.options = merge(options, {
      headers: {
        ...HEADERS
      },
      timeout: TIME_OUT
    });
    this.instance = axios.create(this.options);
    const interceptorManager = new InterceptorManager(this.instance);
    this.addRequestInterceptor = interceptorManager.addRequestInterceptor.bind(interceptorManager);
    this.addResponseInterceptor = interceptorManager.addResponseInterceptor.bind(interceptorManager);
    if (this.options.enableCancelTokenManger) {
      this.cancelTokenManager = new CancelTokenManager(this);
    }
    const downloadManger = new DownloaderManger(this);
    this.download = downloadManger.download.bind(downloadManger);
    this.downloadFile = downloadManger.downloadFile.bind(downloadManger);
    const uploadManager = new UploaderManager(this);
    this.upload = uploadManager.upload.bind(downloadManger);
  }
  get(url, config = {}) {
    return this.request(merge({ url, method: METHOD.GET }, config));
  }
  post(url, data, config = {}) {
    return this.request(merge({ url, data, method: METHOD.POST }, config));
  }
  delete(url, data, config = {}) {
    return this.request(
      merge({ url, data, method: METHOD.DELETE }, config)
    );
  }
  put(url, data, config = {}) {
    return this.request(merge({ url, data, method: METHOD.PUT }, config));
  }
  async request(config) {
    try {
      const response = await this.instance?.request({
        ...config
      });
      return response;
    } catch (error) {
      return Promise.reject(error);
    }
  }
}
export { RequestClient };
