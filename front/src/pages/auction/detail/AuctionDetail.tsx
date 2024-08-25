import {formatVariationDuration, getKrDateFormat} from "../../../util/DateUtil";
import {useEffect, useState} from "react";
import {AuctionDetailInfo} from "./type";
import PricePolicyElement from "./PricePolicyElement";
import {requestAuctionBid, requestAuctionDetail} from "../../../api/auction/api";
import {usePageStore} from "../../../store/PageStore";
import useAlert from "../../../hooks/useAlert";
import {getAuctionProgress} from "../../../util/NumberUtil"
import arrowLeftIcon from '../../../img/arrow-left.svg';


function AuctionDetail({auctionId}: { auctionId?: number }) {

    const {currentPage, setPage} = usePageStore();
    const baseUrl = process.env.REACT_APP_API_URL || ''
    const {showAlert} = useAlert();
    const [auction, setAuction] = useState<AuctionDetailInfo | null>(null);
    const [quantity, setQuantity] = useState<number>(1);

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

    const onClickBidButton = () => {
        requestAuctionBid(
            baseUrl,
            auction?.auctionId!,
            {quantity: quantity, price: auction!.currentPrice},
            () => {
                setPage('home');
            },
            () => {
                showAlert("입찰에 실패했습니다.");
            }
        );
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
            <div className="fixed top-0 left-0 right-0 bg-white shadow-lg p-2 flex items-center justify-center z-50">
                <button
                    className="absolute left-2 bg-white border-none p-2"
                    onClick={() => window.location.href = '/'}
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
                            <p className="text-base font-medium">경매 종료 시간</p>
                            <p className="text-sm text-[#61CBC6]">{getKrDateFormat(auction.finishedAt)}</p>
                        </div>
                    </div>

                    <div className="mb-4">
                        <p className="text-base font-medium">변동 주기</p>
                        <p className="text-sm text-[#61CBC6]">{formatVariationDuration(auction.variationDuration)}</p>
                    </div>

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
                                className="btn bg-[#61CBC6] text-white"
                                onClick={onClickBidButton}
                            >
                                입찰하기
                            </button>
                        </div>
                    </div>


                </div>
            </div>
        </>
    );
}

export default AuctionDetail;