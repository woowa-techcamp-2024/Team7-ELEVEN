import React from 'react';

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

// 상대적인 시간 값을 가져옵니다. (10초 뒤, 한시간 전, 내일 등)
const getRelativeTime = (date: Date): string => {
    const now = new Date();
    const targetTime = date;
    const diffInSeconds = Math.floor((targetTime.getTime() - now.getTime()) / 1000);

    // 상대 시간 계산을 위한 매핑
    const timeIntervals: { unit: Intl.RelativeTimeFormatUnit; value: number }[] = [
        { unit: 'second', value: 60 },
        { unit: 'minute', value: 60 },
        { unit: 'hour', value: 24 },
        { unit: 'day', value: 7 },
        { unit: 'week', value: 4.345 },
        { unit: 'month', value: 12 },
        { unit: 'year', value: Number.POSITIVE_INFINITY },
    ];

    let timeDiff = diffInSeconds;
    let unitIndex = 0;

    // 작은 단위에서 큰 단위로 변환
    while (Math.abs(timeDiff) >= timeIntervals[unitIndex].value && unitIndex < timeIntervals.length - 1) {
        timeDiff /= timeIntervals[unitIndex].value;
        unitIndex++;
    }

    timeDiff = Math.floor(timeDiff);

    const rtf = new Intl.RelativeTimeFormat('ko', { numeric: 'auto' });

    // 타입 캐스팅을 통해 타입 오류를 방지합니다.
    return rtf.format(timeDiff, timeIntervals[unitIndex].unit as Intl.RelativeTimeFormatUnit);
};

// 경매의 현재 상태를 가져옵니다. (곧 시작, 진행 예정, 진행 중, 종료)
const getAuctionStatus = (startedAt: Date, endedAt: Date): { status: string; timeInfo: string; color: string } => {
    const now = new Date();

    // 초 단위로 시간 포맷팅
    const formatTime = (seconds: number): string => {
        const days = Math.floor(seconds / (24 * 3600));
        const hours = Math.floor((seconds % (24 * 3600)) / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = seconds % 60;

        let timeString = '';
        if (days > 0) timeString += `${days}일 `;
        if (hours > 0 || days > 0) timeString += `${hours}시간 `;
        if (minutes > 0 || hours > 0 || days > 0) timeString += `${minutes}분 `;
        if (days === 0 && hours === 0 && minutes === 0) timeString += `${secs}초`;
        timeString += seconds > 0 ? '후' : '전';

        return timeString.trim();
    };

    // 종료된 경우의 시간 포맷팅
    const formatEndTime = (seconds: number): string => {
        const days = Math.floor(seconds / (24 * 3600));
        const hours = Math.floor((seconds % (24 * 3600)) / 3600);

        let timeString = '약 ';
        if (days > 0) timeString += `${days}일 `;
        if (hours > 0) timeString += `${hours}시간 `;
        if (days === 0 && hours === 0) timeString += '종료됨';  // 작은 단위가 없을 때

        return timeString.trim() + ' 전 종료됨';
    };

    if (now < startedAt) {
        // 경매 시작 전
        const timeUntilStart = Math.floor((startedAt.getTime() - now.getTime()) / 1000);
        if (timeUntilStart <= 300) {
            // 5분 이내
            return {
                status: '곧 시작',
                timeInfo: formatTime(timeUntilStart),
                color: 'bg-red-500'
            };
        } else {
            // 5분 이상
            return {
                status: '진행 예정',
                timeInfo: formatTime(timeUntilStart),
                color: 'bg-blue-500'
            };
        }
    } else if (now >= startedAt && now <= endedAt) {
        // 경매 진행 중
        const timeRemaining = Math.floor((endedAt.getTime() - now.getTime()) / 1000);
        return {
            status: '진행 중',
            timeInfo: `남은 시간: ${formatTime(timeRemaining)}`,
            color: 'bg-red-500'
        };
    } else {
        // 경매 종료
        const timeElapsed = Math.floor((now.getTime() - endedAt.getTime()) / 1000);
        return {
            status: '종료',
            timeInfo: formatEndTime(timeElapsed),
            color: 'bg-gray-500'
        };
    }
};

// iso 8601 형식의 문자열을 분과 초로 변환하는 함수
function formatVariationDuration(duration: string): string {
    // 정규식을 사용해 "PTnM" 또는 "PTnS" 형식에서 n을 추출합니다.
    const match = duration.match(/^PT(?:(\d+)M)?(?:(\d+)S)?$/);

    if (match) {
        const minutes = match[1] ? parseInt(match[1], 10) : 0;
        const seconds = match[2] ? parseInt(match[2], 10) : 0;

        if (minutes === 0) {
            return `${seconds}초`;
        }
        return `${minutes}분 ${seconds}초`;
    } else {
        throw new Error("Invalid duration format. Expected format: PTnM or PTnS or PTnMnS");
    }
}

function getMsFromIso8601Duration(duration: string): number {
    // "PTnM" 또는 "PTnS" 또는 "PTnMnS" 형식에서 분과 초를 추출합니다.
    const match = duration.match(/^PT(?:(\d+)M)?(?:(\d+)S)?$/);

    if (match) {
        const minutes = match[1] ? parseInt(match[1], 10) : 0;
        const seconds = match[2] ? parseInt(match[2], 10) : 0;

        // 분과 초를 합쳐 밀리초로 변환합니다.
        return (minutes * 60 + seconds) * 1000;
    } else {
        throw new Error("Invalid duration format. Expected format: PTnM or PTnS or PTnMnS");
    }
}

function getTimeDifferenceInMs(date1: Date, date2: Date): number {
    return date2.getTime() - date1.getTime();
}

// function getTimeDifferenceInMs(date1: Date, date2: Date): number {
//     const timeDifference = date2.getTime() - date1.getTime();
//     if (timeDifference < 0) {
//         return -timeDifference;
//     } else {
//         return timeDifference;
//     }
// }

export {
    getKrDateFormat,
    getRelativeTime,
    getAuctionStatus,
    formatVariationDuration,
    getMsFromIso8601Duration,
    getTimeDifferenceInMs,
};