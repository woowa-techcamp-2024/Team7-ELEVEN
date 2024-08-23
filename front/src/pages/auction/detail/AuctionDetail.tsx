import {formatVariationDuration, getKrDateFormat} from "../../../util/DateUtil";
import {useEffect, useState} from "react";
import {AuctionDetailInfo} from "./type";
import PricePolicyElement from "./PricePolicyElement";
import {requestAuctionBid, requestAuctionDetail} from "../../../api/auction/api";
import {usePageStore} from "../../../store/PageStore";
import useAlert from "../../../hooks/useAlert";
import {getAuctionProgress} from "../../../util/NumberUtil";


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
                    description: "이 1970년식 혼다 CB750 K0는 매우 희귀하고 오리지널 상태의 바이크입니다. 이 바이크는 2009년에 미국에서 영국으로 수입되었습니다.",
                    imageUrl: "https://cdn.usegalileo.ai/stability/441b95f1-3714-46e5-b38e-7570c57700cd.png",
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
                            <p className="text-sm text-primary">{getKrDateFormat(auction.finishedAt)}</p>
                        </div>
                    </div>

                    <div className="mb-4">
                        <p className="text-base font-medium">변동 주기</p>
                        <p className="text-sm text-primary">{formatVariationDuration(auction.variationDuration)}</p>
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

                    <div className="mt-4">
                        <div className="flex justify-end">
                            <button
                                className="btn btn-outline btn-square"
                                onClick={() => decreaseQuantity()}
                            >-
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
                            >+
                            </button>

                        </div>

                        <div className="mt-2 text-sm text-end">
                            최대 구매 가능 수량은 <span className="text-primary">{auction.maximumPurchaseLimitCount}개</span>입니다.
                        </div>
                        <div className="card-actions justify-end ml-4 mt-4">
                            <button
                                className="btn btn-primary"
                                onClick={onClickBidButton}
                            >입찰하기
                            </button>
                        </div>


                    </div>

                </div>
            </div>
        </>
    );
}

export default AuctionDetail;