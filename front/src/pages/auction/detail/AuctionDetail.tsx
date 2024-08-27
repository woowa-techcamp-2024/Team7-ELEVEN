import {
    formatVariationDuration,
    getAuctionStatus,
    getKrDateFormat,
    getMsFromIso8601Duration, getTimeDifferenceInMs
} from "../../../util/DateUtil";
import {useEffect, useState} from "react";
import {AuctionDetailInfo, ConstantPricePolicy, PercentagePricePolicy} from "./type";
import PricePolicyElement from "./PricePolicyElement";
import {requestAuctionBid, requestAuctionDetail} from "../../../api/auction/api";
import {usePageStore} from "../../../store/PageStore";
import useAlert from "../../../hooks/useAlert";
import {getAuctionProgress} from "../../../util/NumberUtil"
import arrowLeftIcon from '../../../img/arrow-left.svg';
import Confetti from 'react-confetti';


function AuctionDetail({auctionId}: { auctionId?: number }) {

    const {currentPage, setPage} = usePageStore();
    const baseUrl = process.env.REACT_APP_API_URL || ''
    const {showAlert} = useAlert();
    const [auction, setAuction] = useState<AuctionDetailInfo | null>(null);
    const [quantity, setQuantity] = useState<number>(1);
    const [leftInfo, setLeftInfo] = useState<String>("불러오는 중...");

    const [isNotRunning, setIsNotRunning] = useState<boolean>(true);
    const [isButtonDisabled, setIsButtonDisabled] = useState(false);
    const [countdown, setCountdown] = useState(0);
    const [showConfetti, setShowConfetti] = useState(false);

    const increaseQuantity = (maximum: number) => {
        if (quantity >= maximum) {
            showAlert("최대 구매 수량을 초과하였습니다.");
        } else {
            setQuantity(quantity + 1);
        }
    }

    const decreaseQuantity = () => {
        if (quantity > 1) {
            setQuantity(quantity - 1);
        } else {
            showAlert("최소 구매 수량은 1개입니다.");
        }
    }

    // 경매 단건 조회 요청
    useEffect(() => {
        if (auctionId === undefined) {
            return;
        }
        requestAuctionDetail(
            baseUrl,
            auctionId,
            (auctionDetailItem) => {
                setAuction({
                    auctionId: auctionDetailItem.auctionId,
                    sellerId: auctionDetailItem.sellerId,
                    productName: auctionDetailItem.productName,
                    description: "",
                    imageUrl: "https://sitem.ssgcdn.com/75/03/86/item/1000551860375_i1_750.jpg",
                    originPrice: auctionDetailItem.originPrice,
                    currentPrice: auctionDetailItem.currentPrice,
                    currentStock: auctionDetailItem.currentStock,
                    originStock: auctionDetailItem.originStock,
                    maximumPurchaseLimitCount: auctionDetailItem.maximumPurchaseLimitCount,
                    pricePolicy: auctionDetailItem.pricePolicy,
                    variationDuration: auctionDetailItem.variationDuration,
                    startedAt: new Date(auctionDetailItem.startedAt),
                    finishedAt: new Date(auctionDetailItem.finishedAt),
                });
            },
            () => {
                showAlert("상품 정보를 가져오는데 실패했습니다.");
            }
        );

    }, []);

    useEffect(() => {

        if (auctionId === undefined) {
            return;
        }

        // 재고 갱신
        const intervalId = setInterval(() => {
            requestAuctionDetail(baseUrl, auctionId,
                (auctionDetailItem) => {
                    setAuction(prevAuction =>
                        prevAuction ? {...prevAuction, currentStock: auctionDetailItem.currentStock} : null
                    );
                    console.log("현재 재고: " + auctionDetailItem.currentStock);
                },
                () => {console.log("현재 재고량을 가져오는데 실패하였습니다.")}
            );
        }, 1000);

        return () => clearInterval(intervalId);
    }, []);

    useEffect(() => {
        if (!auction) {
            return;
        }

        // 현재 가격 갱신 타이머
        setIsNotRunning(true);
        const intervalId = setInterval(() => {
            const { status, timeInfo } = getAuctionStatus(auction.startedAt, auction.finishedAt);
            if (status === "종료") {
                let krDateFormat = "경매 종료 (" + getKrDateFormat(auction.finishedAt) + ")";
                setLeftInfo(krDateFormat);
            } if (status === "진행 예정") {
                setLeftInfo(status + " (" + timeInfo + ")");
            } if (status === "곧 시작") {
                setLeftInfo(status + " (" + timeInfo + ")");
            } else {
                setLeftInfo(status + " (" + timeInfo + ")");
                setIsNotRunning(false);  // 입찰 버튼 활성화
            }

        }, 1000);

        return () => clearInterval(intervalId);
    }, []);

    useEffect(() => {
        if (isButtonDisabled) {
            const timer = setInterval(() => {
                setCountdown((prevCount) => {
                    if (prevCount <= 1) {
                        clearInterval(timer);
                        setIsButtonDisabled(false);
                        return 0;
                    }
                    return prevCount - 1;
                });
            }, 1000);

            return () => clearInterval(timer);
        }
    }, [isButtonDisabled]);

    useEffect(() => {
        if (showConfetti) {
            const timer = setTimeout(() => setShowConfetti(false), 5000); // 5초 후에 confetti 효과 종료
            return () => clearTimeout(timer);
        }
    }, [showConfetti]);

    function getCurrentPrice(): number {
        if (!auction) {
            return 1;
        }

        // 가격 하락 정책이 종료되었는지 체크
        const now = new Date();
        let isLastTime = false;
        if (now >= auction.finishedAt) {
            isLastTime = true;
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
            currentPrice = calculatePrice;
        }

        return currentPrice;
    }

    function calculateNextPrice(currentPrice: number): number {
        if (!auction) {
            return 1;
        }

        if (auction.pricePolicy.type === "CONSTANT") {
            return currentPrice - (auction.pricePolicy as ConstantPricePolicy).variationWidth;
        } else if (auction.pricePolicy.type === "PERCENTAGE") {
            return currentPrice - (currentPrice * (auction.pricePolicy as PercentagePricePolicy).discountRate / 100);
        } else {
            return -1;
        }
    }

    const onClickBidButton = () => {
        const currentPrice = getCurrentPrice();

        requestAuctionBid(
            baseUrl,
            auction?.auctionId!,
            {quantity: quantity, price: currentPrice},
            () => {
                setShowConfetti(true);  // 성공 시 confetti 효과 시작
                setIsButtonDisabled(true);  // 버튼 비활성화
                setCountdown(5);  // 5초 카운트다운 시작
            },
            (message) => {
                showAlert(message);
            }
        );
    }

    const getButtonText = () => {
        if (isButtonDisabled) return `${countdown}초 남음`;
        return '입찰하기';
    }

    const onClickBackButton = () => {
        setPage('home');
    }

    if (auction === null) {
        return <div className="container mx-auto p-6">
            <div className="max-w-lg mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
                <div className="p-6">
                    <p>상세페이지를 불러오는 중입니다...</p>
                </div>
            </div>
        </div>
    }

    return (
        <>
            {showConfetti && (
                <Confetti
                    width={window.innerWidth}
                    height={window.innerHeight}
                    recycle={false}
                    numberOfPieces={400}
                />
            )}
            <div className="fixed top-0 left-0 right-0 bg-white shadow-lg p-2 flex items-center justify-center z-50">
                <button
                    className="absolute left-2 bg-white border-none p-2"
                    onClick={onClickBackButton}
                >
                    <img src={arrowLeftIcon} alt="뒤로가기" className="w-5 h-5"/>
                </button>
                <h1 className="text-sm font-bold">
                    {auction.productName}
                </h1>
            </div>

            <div className="card bg-base-100 shadow-xl">
                <figure>
                    <img
                        src={auction.imageUrl}
                        alt="Honda CB750 Four K0"
                    />
                </figure>
                <div className="card-body">
                    <h1 className="card-title text-2xl font-bold">{auction.productName}</h1>
                    <p className="text-base">
                        {auction.description}
                    </p>

                    <div className="flex items-center gap-4 py-2">
                        <div>
                            <p className="text-base font-medium">경매 진행 상황</p>
                            <p className="text-sm text-[#61CBC6]">{leftInfo}</p>
                        </div>
                    </div>

                    {/*<div className="mb-4">*/}
                    {/*    <p className="text-base font-medium">변동 주기</p>*/}
                    {/*    <p className="text-sm text-[#61CBC6]">{formatVariationDuration(auction.variationDuration)}</p>*/}
                    {/*</div>*/}

                    <div>
                        <div className="flex justify-between">
                            <p className="text-base font-medium">경매 진행률</p>
                            <p className="text-sm">{getAuctionProgress(auction.currentStock, auction.originStock)}%</p>
                        </div>
                        <progress className="progress progress-primary w-full"
                                  value={100 - (auction.currentStock / auction.originStock * 100)} max="100"></progress>
                        <div className="flex justify-between mt-2">
                            <p className="text-sm">현재 재고: {auction.currentStock}개</p>
                            <p className="text-sm">총 재고: {auction.originStock}개</p>
                        </div>
                    </div>

                    <PricePolicyElement
                        priceLimit={0}
                        auction={auction}
                        setAuction={setAuction}
                    />


                    <div className="text-sm text-left mt-2">
                        최대 구매 가능 수량은 <span
                        className="text-[#61CBC6]">{auction.maximumPurchaseLimitCount}개</span>입니다.
                    </div>

                    <div
                        className="fixed bottom-0 left-0 right-0 bg-white shadow-lg p-4 flex justify-between items-center z-50">
                        <div className="flex flex-col">
                            <div className="flex items-center">
                                <button
                                    className="btn btn-outline btn-square"
                                    onClick={() => decreaseQuantity()}
                                >
                                    -
                                </button>
                                <div className="h-full">
                                    <input
                                        type="number"
                                        min="1"
                                        max={auction.maximumPurchaseLimitCount}
                                        value={quantity}
                                        className="input input-bordered text-center mx-2"
                                        disabled
                                        style={{
                                            backgroundColor: 'white',
                                            color: 'black',
                                            opacity: 1,
                                            cursor: 'default'
                                        }}
                                    />
                                </div>
                                <button
                                    className="btn btn-outline btn-square"
                                    onClick={() => increaseQuantity(auction.maximumPurchaseLimitCount)}
                                >
                                    +
                                </button>
                            </div>
                        </div>

                        <div>
                            <button
                                className={`btn text-white ${isNotRunning || isButtonDisabled ? 'bg-gray-400 cursor-not-allowed' : 'bg-[#61CBC6]'} `}
                                onClick={onClickBidButton}
                                disabled={isNotRunning || isButtonDisabled}
                            >
                                {getButtonText()}  {/*입찰하기 버튼*/}
                            </button>
                        </div>

                    </div>
                </div>
            </div>
        </>
    );
}

export default AuctionDetail;