// export const formatTime = (date) => {
//     const year = date.getFullYear();
//     const month = date.getMonth() + 1;
//     const day = date.getDate();
//     const hour = date.getHours();
//     const minute = date.getMinutes();
//     const second = date.getSeconds();
//     return `${[year, month, day].map(formatNumber).join('-')}`;
// };
// export const formatDataTime = (date) => {
//     const year = date.getFullYear();
//     const month = date.getMonth() + 1;
//     const day = date.getDate();
//     const hour = date.getHours();
//     const minute = date.getMinutes();
//     return `${[year, month, day].map(formatNumber).join('-')} ${[hour, minute].map(formatNumber).join(':')}`;
// };
// const formatNumber = (n) => {
//     n = n.toString();
//     return n[1] ? n : `0${n}`;
// };

function formatTime(date){
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const minute = date.getMinutes();
    const second = date.getSeconds();
    return `${[year, month, day].map(formatNumber).join('-')}`;
};
function formatDataTime(date){
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const minute = date.getMinutes();
    return `${[year, month, day].map(formatNumber).join('-')} ${[hour, minute].map(formatNumber).join(':')}`;
};
function formatNumber(n) {
    n = n.toString();
    return n[1] ? n : `0${n}`;
};

function formatStrTime(number, format) {
  var formateArr = ['Y', 'M', 'D', 'h', 'm', 's'];
  var returnArr = [];
 
  var date = new Date(number);
  returnArr.push(date.getFullYear());
  returnArr.push(formatNumber(date.getMonth() + 1));
  returnArr.push(formatNumber(date.getDate()));
 
  returnArr.push(formatNumber(date.getHours()));
  returnArr.push(formatNumber(date.getMinutes()));
  returnArr.push(formatNumber(date.getSeconds()));
 
  returnArr.forEach(function(item,index) {
    format = format.replace(formateArr[index], item);
  })
  return format;
}

export default {
    formatTime,
    formatDataTime,
    formatNumber,
	formatStrTime,
}

