// 生成随机日期数组（假设每月30天）
const generateDays = (year: number, month: number): string[] => {
	const days: string[] = [];
	for (let i = 1; i <= 30; i++) {
		const day = i < 10 ? `0${i}` : `${i}`;
		days.push(`${year}-${month}-${day}`);
	}
	return days;
};

// 生成随机数组
const generateRandomArray = (length: number, min: number, max: number): number[] => {
	return Array.from({ length }, () => Number((Math.random() * (max - min) + min).toFixed(2)));
};

// 获取当前日期信息
const now = new Date();
const currentYear = now.getFullYear();
const currentMonth = now.getMonth() + 1;
const lastMonth = currentMonth === 1 ? 12 : currentMonth - 1;
const lastMonthYear = currentMonth === 1 ? currentYear - 1 : currentYear;

// 生成日期标签
const currentMonthDays = generateDays(currentYear, currentMonth);
const lastMonthDays = generateDays(lastMonthYear, lastMonth);

// 生成每日用水量数据（立方米）
const currentMonthWaterUsage = generateRandomArray(30, 10, 50);
const lastMonthWaterUsage = generateRandomArray(30, 8, 45);

// 生成每日费用数据（元）
const currentMonthCost = generateRandomArray(30, 2000, 4000);
const lastMonthCost = generateRandomArray(30, 1800, 3800);

// 计算总量
const currentMonthTotalWater = currentMonthWaterUsage.reduce((a, b) => a + b, 0).toFixed(2);
const lastMonthTotalWater = lastMonthWaterUsage.reduce((a, b) => a + b, 0).toFixed(2);

const currentMonthTotalCost = currentMonthCost.reduce((a, b) => a + b, 0).toFixed(2);
const lastMonthTotalCost = lastMonthCost.reduce((a, b) => a + b, 0).toFixed(2);

// 计算百分比变化
const waterChangePercent = (
	((parseFloat(currentMonthTotalWater) - parseFloat(lastMonthTotalWater)) / parseFloat(lastMonthTotalWater)) *
	100
).toFixed(1);
const costChangePercent = (
	((parseFloat(currentMonthTotalCost) - parseFloat(lastMonthTotalCost)) / parseFloat(lastMonthTotalCost)) *
	100
).toFixed(1);

export default [
	{
		url: "/api/monitoring-water/realtime",
		method: "get",
		response: () => {
			return {
				code: 200,
				data: {
					// 本月用水数据
					currentMonth: {
						totalWater: currentMonthTotalWater, // 本月总用水量（立方米）
						totalCost: currentMonthTotalCost, // 本月总费用（元）
						dailyWaterUsage: currentMonthWaterUsage, // 本月每日用水量数组
						dailyCost: currentMonthCost, // 本月每日费用数组
						monthLabel: `${currentYear}年${currentMonth}月`
					},
					// 上月用水数据
					lastMonth: {
						totalWater: lastMonthTotalWater, // 上月总用水量（立方米）
						totalCost: lastMonthTotalCost, // 上月总费用（元）
						dailyWaterUsage: lastMonthWaterUsage, // 上月每日用水量数组
						dailyCost: lastMonthCost, // 上月每日费用数组
						monthLabel: `${lastMonthYear}年${lastMonth}月`
					},
					// 对比数据
					comparison: {
						waterType: Number(waterChangePercent) < 0 ? "up" : "down",
						costType: Number(costChangePercent) < 0 ? "up" : "down",
						waterChangePercent: waterChangePercent, // 用水量变化百分比
						costChangePercent: costChangePercent // 费用变化百分比
					},
					// 日期标签（用于图表X轴）
					dateLabels: currentMonthDays
				}
			};
		}
	}
];
