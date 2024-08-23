// Date 시간을 한국 시간으로 변환하는 함수
function getKrDateFormat(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}년 ${month}월 ${day}일 ${hours}시 ${minutes}분`;
}

// iso 8601 형식의 문자열을 n분으로 변환하는 함수
function formatVariationDuration(duration: string): string {
    // 정규식을 사용해 "PTnM" 형식에서 n을 추출합니다.
    const match = duration.match(/^PT(\d+)M$/);

    if (match && match[1]) {
        const minutes = parseInt(match[1], 10);
        return `${minutes}분`;
    } else {
        throw new Error("Invalid duration format. Expected format: PTnM");
    }
}

function getMsFromIso8601Duration(duration: string): number {
    const match = duration.match(/^PT(\d+)M$/);

    if (match && match[1]) {
        const minutes = parseInt(match[1], 10);
        return minutes * 60 * 1000;
    } else {
        throw new Error("Invalid duration format. Expected format: PTnM");
    }
}

function getTimeDifferenceInMs(date1: Date, date2: Date): number {
    const timeDifference = date2.getTime() - date1.getTime();
    if (timeDifference < 0) {
        return -timeDifference;
    } else {
        return timeDifference;
    }
}

export {
    getKrDateFormat,
    formatVariationDuration,
    getMsFromIso8601Duration,
    getTimeDifferenceInMs,
};