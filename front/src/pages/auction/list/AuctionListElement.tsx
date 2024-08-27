import React, { useEffect, useState } from 'react';
import { usePageStore } from "../../../store/PageStore";
import { getAuctionStatus, getKrDateFormat } from "../../../util/DateUtil";
import { getPriceFormatted } from "../../../util/NumberUtil";
import { useAuctionStore } from "../../../store/AuctionStore";

interface AuctionSimpleInfo {
    id: number;
    title: string;
    price: number;
    startedAt: Date;
    endedAt: Date;
}

const AuctionListElement: React.FC<AuctionSimpleInfo> = ({
                                                             id,
                                                             title,
                                                             price,
                                                             startedAt,
                                                             endedAt,
                                                         }) => {
    const { currentPage, setPage } = usePageStore();
    const { auctionId, setAuctionId } = useAuctionStore();

    const [status, setStatus] = useState<string>('');
    const [timeInfo, setTimeInfo] = useState<string>('');

    useEffect(() => {
        const updateStatus = () => {
            const { status, timeInfo } = getAuctionStatus(startedAt, endedAt);
            setStatus(status);
            setTimeInfo(timeInfo);
        };

        updateStatus();
        const interval = setInterval(updateStatus, 1000);

        return () => clearInterval(interval);
    }, [startedAt, endedAt]);

    const changePage = (page: string) => {
        setAuctionId(id);
        setPage(page);
    }

    // 상태에 따라 현재가의 색상 설정
    const priceColor = status !== '진행 중' ? 'text-gray-400' : 'text-[#62CBC6]';
    const greyScale = status !== '진행 중' ? 'grayscale' : '';

    return (
        <div
            className="card bg-white shadow-lg rounded-lg overflow-hidden border border-gray-200 cursor-pointer hover:shadow-xl transition-shadow duration-300"
            onClick={() => changePage('auctionDetail')}
        >
            <div className="relative">
                {/* 이미지 영역 */}
                <img
                    src="https://woowahan-cdn.woowahan.com/new_resources/image/card/c263eb7ff44f4fe081bfe5365f3dea5a.png" // 이미지 URL을 여기에 입력하세요
                    alt="Auction Item"
                    className={`w-full h-40 object-cover ${greyScale}`} // Apply grayscale based on status
                />
                {/* 상태 태그 */}
                <span className="absolute top-2 right-2 bg-blue-500 text-white text-xs px-2 py-1 rounded-full">
                    {status}
                </span>
            </div>
            <div className="p-4">
                <h2 className="text-xl font-semibold mb-2">{title}</h2>
                <p className="text-gray-600 text-sm mb-1">시작 시간: {getKrDateFormat(startedAt)}</p>
                <p className="text-gray-600 text-sm mb-1">종료 시간: {getKrDateFormat(endedAt)}</p>
                <p className={`text-lg font-bold mb-2 ${priceColor}`}>시작 가격: {getPriceFormatted(price)}</p>
                <p className="text-gray-600 text-sm">{status}! {timeInfo}</p>
            </div>
        </div>
    );
}

export default AuctionListElement;
