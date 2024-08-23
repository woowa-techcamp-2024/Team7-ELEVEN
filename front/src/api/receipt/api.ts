import {ReceiptDetailItem, ReceiptSimpleItem, ReceiptsRequest} from "./type";

async function requestReceiptList(
    baseUrl: string,
    data: ReceiptsRequest,
    onSuccess: (receipts: ReceiptSimpleItem[]) => void,
    onFailure: () => void
) {
    try {
        console.log('Fetching receipts...', data);
        const response = await fetch(`${baseUrl}/receipts/buyer?offset=${data.offset}&size=${data.size}`, {
            method: 'GET',
            mode: 'cors',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
            },
        });

        if (response.ok) {
            const receipts: ReceiptSimpleItem[] = await response.json();
            onSuccess(receipts);
        } else {
            console.log('Failed to fetch receipts.', response.status);
            onFailure();
        }
    } catch (error) {
        console.log('Failed to fetch receipts.', error);
        onFailure();
    }
}

async function requestReceiptDetail(
    baseUrl: string,
    receiptId: number,
    onSuccess: (receipt: ReceiptDetailItem) => void,
    onFailure: () => void
) {
    try {
        const response = await fetch(`${baseUrl}/receipts/${receiptId}`, {
            method: 'GET',
            mode: 'cors',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
            },
        });

        if (response.ok) {
            const receipt: ReceiptDetailItem = await response.json();
            onSuccess(receipt);
        } else {
            onFailure();
        }
    } catch (error) {
        onFailure();
    }
}

async function requestRefund(
    baseUrl: string,
    receiptId: number,
    onSuccess: () => void,
    onFailure: () => void
) {
    try {
        const response = await fetch(`${baseUrl}/receipts/${receiptId}/refund`, {
            method: 'PUT',
            mode: 'cors',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
            },
        });

        if (response.ok) {
            onSuccess();
        } else {
            onFailure();
        }
    } catch (error) {
        onFailure();
    }
}

export {requestReceiptList, requestReceiptDetail, requestRefund};
