interface ReceiptsRequest {
    size: number;
    offset: number;
}

interface ReceiptSimpleItem {
    id: number;
    auctionId: number;
    type: 'PURCHASED' | 'REFUND' | 'FAILED'
    productName: string;
    quantity: number;
    price: number;
}

interface ReceiptDetailItem {
    receiptId: number;
    productName: string;
    price: number;
    quantity: number;
    receiptStatus: 'PURCHASED' | 'REFUND' | 'FAILED';
    auctionId: number;
    sellerId: number;
    buyerId: number;
    createdAt: string;
    updatedAt: string;
}

export type {ReceiptsRequest, ReceiptSimpleItem, ReceiptDetailItem};
