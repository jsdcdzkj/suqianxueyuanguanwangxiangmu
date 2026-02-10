import axios from "axios";
export const doAuthResponseInterceptor = ({
  client,
  doAuth,
  doRefreshToken,
  enableRefreshToken
}) => {
  return {
    onRejected: async (error) => {
      const { config, response } = error;
      if (response?.status !== 401) {
        throw error;
      }
      if (!enableRefreshToken || config.__noAuthToLogin) {
        await doAuth();
        throw error;
      }
      if (client.refreshing) {
        return new Promise((resolve) => {
          client.refreshTokenQueue.push((newToken) => {
            config.headers.Authorization = newToken;
            resolve(client.request(config));
          });
        });
      }
      client.refreshing = true;
      config.__noAuthToLogin = true;
      try {
        const newToken = await doRefreshToken();
        client.refreshTokenQueue.forEach(
          (callback) => callback(newToken)
        );
        client.refreshTokenQueue = [];
        return client.request(config);
      } catch (error2) {
        client.refreshTokenQueue.forEach((callback) => callback(""));
        client.refreshTokenQueue = [];
        await doAuth();
        throw error2;
      }
    }
  };
};
export const doResponseInterceptor = (messageFunc) => {
  return {
    onRejected(error) {
      if (axios.isCancel(error)) {
        return Promise.reject(error);
      }
      const err = error?.toString() ?? "";
      let errMsg = "";
      if (err?.includes("Network Error")) {
        errMsg = "\u7F51\u7EDC\u9519\u8BEF";
      } else if (error?.message?.includes?.("timeout")) {
        errMsg = "\u8BF7\u6C42\u8D85\u65F6";
      }
      if (errMsg) {
        messageFunc?.(errMsg, error);
        return Promise.reject(error);
      }
      let errorMessage = error?.response?.data?.error?.message ?? "";
      const status = error?.response?.status;
      switch (status) {
        case 400: {
          errorMessage = "\u574F\u7684\u8BF7\u6C42";
          break;
        }
        case 401: {
          errorMessage = "\u767B\u5F55\u72B6\u4F53\u5931\u6548";
          break;
        }
        case 403: {
          errorMessage = "\u670D\u52A1\u5668\u62D2\u7EDD\u8BF7\u6C42";
          break;
        }
        case 404: {
          errorMessage = "\u8BF7\u6C42\u5730\u5740\u672A\u627E\u5230";
          break;
        }
        case 408: {
          errorMessage = "\u8BF7\u6C42\u8D85\u65F6";
          break;
        }
        default: {
          errorMessage = "\u670D\u52A1\u5668\u5F02\u5E38\u9519\u8BEF";
        }
      }
      messageFunc?.(errorMessage, error);
      return Promise.reject(error);
    }
  };
};
