class UploaderManager {
  client;
  constructor(client) {
    this.client = client;
  }
  upload(url, data, config) {
    const formData = new FormData();
    Object.entries(data).forEach(([key, value]) => {
      if (key == "files") {
        value.forEach(
          (item) => formData.append(config?.uploadFileName || "file", item)
        );
      } else {
        formData.append(key, value);
      }
    });
    const finalConfig = {
      ...config,
      headers: {
        "Content-Type": "multipart/form-data",
        ...config?.headers
      }
    };
    return this.client.post(url, formData, finalConfig);
  }
}
export { UploaderManager };
