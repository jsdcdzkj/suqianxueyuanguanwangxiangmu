import dayjs from "dayjs";
// 导出一个名为 downFile 的函数，用于下载文件
export const downFile = (fileUrl: string, fileName?: string) => {
  // 创建一个 <a> 元素
  const a = document.createElement("a");
  // 将 fileUrl 按照斜杠分割成一个数组
  const list = fileUrl.split("/");
  // 设置 <a> 元素的 href 属性为文件的 URL
  a.href = fileUrl;
  // 设置 <a> 元素的 download 属性，如果提供了 fileName 则使用 fileName，否则使用 URL 中的最后一个部分作为文件名
  a.download = fileName ? fileName : list[list.length - 1];
  // 模拟点击 <a> 元素，触发文件下载
  a.click();
};

// 导出一个名为 downFile 的函数，用于下载文件
export const downBlobFile = (fileUrl: Blob, fileName: string) => {
  // 创建一个 <a> 元素
  const a = document.createElement("a");
  // 设置 <a> 元素的 href 属性为文件的 URL
  a.href = URL.createObjectURL(fileUrl);
  // 设置 <a> 元素的 download 属性，如果提供了 fileName 则使用 fileName，否则使用 URL 中的最后一个部分作为文件名
  a.download = fileName;
  // 模拟点击 <a> 元素，触发文件下载
  a.click();
};

// 文件导入 动态创建 input type file

export const createFileInput = (accept?: string): Promise<File> => {
  return new Promise((resolve, reject) => {
    const input = document.createElement("input");
    input.type = "file";
    input.style.display = "none";
    input.accept = accept || "*";

    input.onchange = () => {
      const file = input.files?.[0];
      if (file) {
        resolve(file);
      } else {
        reject(new Error("未选择文件"));
      }
    };
    input.click();
  });
};

/**
 * @param {*} month  YYYY-MM
 * @returns
 */
export const getMonthLimit = (month) => {
  const days = dayjs(month).daysInMonth();
  return {
      startTime: `${month}-01`,
      endTime: `${month}-${days}`,
  };
};

/**
* @param {*} year  YYYY
* @returns
*/
export const getYearLimit = (year) => {
  return {
      startTime: `${year}-01-01`,
      endTime: `${year}-12-31`,
  };
};

export const getCurrentDate = () => {
  const date = new Date();
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();

  return `${year}年${month > 9 ? month : "0" + month}月${
      day > 9 ? day : "0" + day
  }`;
};

