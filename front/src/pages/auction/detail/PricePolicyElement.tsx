import {AuctionDetailInfo, ConstantPricePolicy, PercentagePricePolicy} from "./type";
import {getPriceFormatted} from "../../../util/NumberUtil";
import { getAuctionStatus } from "../../../util/DateUtil";
import {
    formatVariationDuration,
    getMsFromIso8601Duration,
    getTimeDifferenceInMs
} from "../../../util/DateUtil";
import {useEffect, useState} from "react";

interface PricePolicyElementProps {
    priceLimit: number;
    auction: AuctionDetailInfo,
    setAuction: React.Dispatch<React.SetStateAction<AuctionDetailInfo | null>>
}

function PricePolicyElement(
    {
        priceLimit,
        auction,
        setAuction
    }: PricePolicyElementProps) {

    const [nowPrice, setNowPrice] = useState<number>(0);
    const [isLastPrice, setIsLastPrice] = useState<boolean>(false);
    const [timeUntilNextChange, setTimeUntilNextChange] = useState({ minutes: 0, seconds: 0 });

    useEffect(() => {
        // 시작 가격을 설정한다.
        setNowPrice(auction.originPrice);

        // 현재 가격 갱신 타이머
        const intervalId = setInterval(() => {

            // 가격 하락 정책이 종료되었는지 체크
            const now = new Date();
            let isLastTime = false;
            if (now >= auction.finishedAt) {
                isLastTime = true;
                setIsLastPrice(true);
            }

            // 현재 가격 계산 로직
            const durationMs = getMsFromIso8601Duration(auction.variationDuration);
            const diffMsBetweenStartedAndNow = getTimeDifferenceInMs(
                auction.startedAt, isLastTime ? auction.finishedAt : now
            );
            const times = Math.floor(diffMsBetweenStartedAndNow / durationMs);

            let currentPrice = auction.originPrice;
            for (let i = 0; i < times; i++) {   // times번 만큼 할인된 가격을 구하는 로직
                const calculatePrice = calculateNextPrice(currentPrice);
                if (priceLimit <= calculatePrice) {
                    currentPrice = calculatePrice;
                } else {
                    currentPrice = priceLimit;
                }
            }

            // 가격 설정
            setNowPrice(currentPrice);

            // 다음 가격 변동까지 남은 시간 계산
            const msUntilNextChange = durationMs - (diffMsBetweenStartedAndNow % durationMs);
            const minutesUntilNextChange = Math.floor(msUntilNextChange / 60000);
            const secondsUntilNextChange = Math.floor((msUntilNextChange % 60000) / 1000);

            setTimeUntilNextChange({
                minutes: minutesUntilNextChange,
                seconds: secondsUntilNextChange
            });

        }, 1000);

        return () => clearInterval(intervalId);
    }, []);

    function calculateNextPrice(currentPrice: number): number {
        if (auction.pricePolicy.type === "CONSTANT") {
            return currentPrice - (auction.pricePolicy as ConstantPricePolicy).variationWidth;
        } else if (auction.pricePolicy.type === "PERCENTAGE") {
            return currentPrice - (currentPrice * (auction.pricePolicy as PercentagePricePolicy).discountRate / 100);
        } else {
            return -1;
        }
    }

    const component = () => {
        switch (auction.pricePolicy.type) {
            case "CONSTANT":
                return (
                    <div id="discountStrategy" className="text-gray-700 text-lg">
                        {formatVariationDuration(auction.variationDuration)}마다 <span
                        className="text-[#62CBC6] font-semibold">{auction.pricePolicy.variationWidth}원</span> 할인이 적용됩니다.
                    </div>
                )
            case "PERCENTAGE":
                return (
                    <div id="discountStrategy" className="text-gray-700 text-lg">
                        {formatVariationDuration(auction.variationDuration)}마다 <span
                        className="text-[#62CBC6] font-semibold">${auction.pricePolicy.discountRate}%</span> 할인이 적용됩니다.
                    </div>
                )
            default:
                return (
                    <div>
                        이 상품에 대한 할인 정보가 없습니다.
                    </div>
                )
        }
    }

    return (
        <div>
            <div className="mt-6">
                <h3 className="text-xl font-bold text-gray-800 mb-2">할인 정책</h3>

                <div id="discountStrategy" className="text-gray-700 text-lg">
                    {component()}
                </div>
                <div className="grid grid-cols-2">
                    <div>
                        <h2 className="text-2xl font-bold pt-5">현재 가격</h2>
                        <h1 className="text-2xl font-bold">{getPriceFormatted(nowPrice)}</h1>
                    </div>
                        {
                            isLastPrice
                                ? <div>
                                    <h2 className="text-2xl font-bold pt-5">최종 가격</h2>
                                    <h1 className="text-2xl font-bold">{getPriceFormatted(nowPrice)}</h1>
                                </div>
                                : <div>
                                    <h2 className="text-2xl font-bold pt-5">{timeUntilNextChange.minutes}분 {timeUntilNextChange.seconds}초 뒤</h2>
                                    <h1 className="text-2xl font-bold">{getPriceFormatted(calculateNextPrice(nowPrice))}</h1>
                                </div>
                        }
                </div>
            </div>
        </div>
    );
}

export default PricePolicyElement;
