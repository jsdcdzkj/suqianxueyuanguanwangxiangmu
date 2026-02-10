export const clientResponseInterceptor = (response) => {
  return response;
};
export const clientRequestInterceptor = (config) => {
  return config;
};
export class InterceptorManager {
  instance;
  constructor(instance) {
    this.instance = instance;
  }
  addRequestInterceptor(onFulfilled, onRejected) {
    this.instance.interceptors.request.use(onFulfilled, onRejected);
  }
  addResponseInterceptor(onFulfilled, onRejected) {
    this.instance.interceptors.response.use(onFulfilled, onRejected);
  }
}
