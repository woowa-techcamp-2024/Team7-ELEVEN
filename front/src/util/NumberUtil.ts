function getPriceFormatted(price: number): string {
    return new Intl.NumberFormat('ko-KR', {
        style: 'currency',
        currency: 'KRW',
        minimumFractionDigits: 0
    }).format(price);
}

function getAuctionProgress(currentStock: number, originStock: number): number {
    const progress = (1 - (currentStock / originStock)) * 100;
    return Math.round(progress * 100) / 100;
}

export {getPriceFormatted, getAuctionProgress};
