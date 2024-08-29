import {PricePolicy} from "../../pages/auction/detail/type";

interface AuctionsRequest {
    offset: number;
    size: number;
}

interface AuctionBidResponse {
    uuid: number;
    message: string;
    errorCode: string;
}

interface AuctionItem {
    id: number;
    title: string;
    price: number;
    startedAt: string;
    finishedAt: string;
}

interface AuctionDetailItem {
    auctionId: number;
    sellerId: number;
    productName: string;
    originPrice: number;
    currentPrice: number;
    originStock: number;
    currentStock: number;
    stock: number;
    maximumPurchaseLimitCount: number;
    pricePolicy: PricePolicy;
    variationDuration: string;
    startedAt: string;
    finishedAt: string;
}

interface AuctionPurchaseRequest {
    quantity: number;
    price: number;
}

export type {
    AuctionsRequest,
    AuctionBidResponse,
    AuctionItem,
    AuctionDetailItem,
    AuctionPurchaseRequest,
};
