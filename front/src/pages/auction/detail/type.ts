interface PercentagePricePolicy {
    type: 'PERCENTAGE';
    discountRate: number;
}

interface ConstantPricePolicy {
    type: 'CONSTANT';
    variationWidth: number;
}

type PricePolicy = PercentagePricePolicy | ConstantPricePolicy;

interface AuctionDetailInfo {
    auctionId: number;
    sellerId: number;
    productName: string;
    description: string;
    imageUrl: string;
    originPrice: number;
    currentPrice: number;
    currentStock: number;
    originStock: number;
    maximumPurchaseLimitCount: number;
    pricePolicy: PricePolicy;
    variationDuration: string;
    startedAt: Date;
    finishedAt: Date;
}

export type {PricePolicy, PercentagePricePolicy, ConstantPricePolicy, AuctionDetailInfo};
