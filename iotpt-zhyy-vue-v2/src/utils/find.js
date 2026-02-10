// 总的时间范围
const tRange = [{ start: "00:00:00", end: "23:59:59" }];

// 不可用的时间段
const unavailableRanges = [
  { start: "09:00:00", end: "10:00:00" },
  { start: "10:30:00", end: "10:40:00" },
  { start: "12:30:00", end: "12:50:00" },
  { start: "14:30:00", end: "15:30:00" },
];

// 找出可用的时间段
export function findAvailableRanges(unavailableRanges, totalRange = tRange) {
  let availableRanges = [];

  for (let i = 0; i < totalRange.length; i++) {
    const totalStart = new Date(`2024-01-15T${totalRange[i].start}`);
    const totalEnd = new Date(`2024-01-15T${totalRange[i].end}`);

    let availableStart = totalStart;

    for (let j = 0; j < unavailableRanges.length; j++) {
      const unavailableStart = new Date(
        `2024-01-15T${unavailableRanges[j].start}`
      );
      const unavailableEnd = new Date(`2024-01-15T${unavailableRanges[j].end}`);

      if (availableStart < unavailableStart) {
        availableRanges.push({
          start: availableStart.toTimeString().slice(0, 8),
          end: unavailableStart.toTimeString().slice(0, 8),
        });
      }

      availableStart = unavailableEnd < totalEnd ? unavailableEnd : totalEnd;
    }

    if (availableStart < totalEnd) {
      availableRanges.push({
        start: availableStart.toTimeString().slice(0, 8),
        end: totalEnd.toTimeString().slice(0, 8),
      });
    }
  }

  return availableRanges;
}

// 判断两个时分秒区间列表是否有交叉
function areAnyRangesOverlapping(list1, list2) {
  for (const range1 of list1) {
    for (const range2 of list2) {
      const start1 = new Date(`2024-01-15T${range1.start}`);
      const end1 = new Date(`2024-01-15T${range1.end}`);
      const start2 = new Date(`2024-01-15T${range2.start}`);
      const end2 = new Date(`2024-01-15T${range2.end}`);

      if (start1 < end2 && end1 > start2) {
        return true; // 有交叉
      }
    }
  }
  return false; // 无交叉
}

// 判断任意数量的时分秒区间列表是否有交叉
export function areTimeRangesListsOverlapping(...lists) {
  for (let i = 0; i < lists.length - 1; i++) {
    const list1 = lists[i];
    for (let j = i + 1; j < lists.length; j++) {
      const list2 = lists[j];
      if (areAnyRangesOverlapping(list1, list2)) {
        return true; // 有交叉
      }
    }
  }
  return false; // 无交叉
}
