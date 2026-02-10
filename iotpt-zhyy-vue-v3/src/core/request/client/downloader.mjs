class DownloaderManger {
  client;
  constructor(client) {
    this.client = client;
  }
  download(url, config) {
    const donwloadConfig = {
      ...config,
      responseType: "blob"
    };
    return this.client.get(url, donwloadConfig);
  }
  downloadFile(blob, fileName) {
    const aEl = document.createElement("a");
    const href = URL.createObjectURL(blob);
    aEl.href = href;
    aEl.download = fileName;
    aEl.click();
    URL.revokeObjectURL(href);
  }
}
export { DownloaderManger };
