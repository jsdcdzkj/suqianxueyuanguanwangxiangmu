import dayjs from "dayjs";

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
