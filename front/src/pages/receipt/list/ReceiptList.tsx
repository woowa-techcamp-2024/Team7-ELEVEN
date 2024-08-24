import ReceiptListElement from "./ReceiptListElement";
import { ReceiptSimpleInfo } from "./type";
import { useEffect, useState } from "react";
import { ReceiptsRequest } from "../../../api/receipt/type";
import { requestReceiptList } from "../../../api/receipt/api";
import { usePageStore } from "../../../store/PageStore";

function ReceiptList() {
    const { currentPage, setPage } = usePageStore();
    const baseUrl = process.env.REACT_APP_API_URL || '';
    const [receiptList, setReceiptList] = useState<ReceiptSimpleInfo[]>([]);
    const [request, setRequest] = useState<ReceiptsRequest>({
        offset: 0,
        size: 20,
    });

    useEffect(() => {
        requestReceiptList(
            baseUrl,
            request,
            (newReceipts) => {
                const receipts: ReceiptSimpleInfo[] = newReceipts.map(receipt => ({
                    id: receipt.id,
                    type: receipt.type,
                    productName: receipt.productName,
                    quantity: receipt.quantity,
                    price: receipt.price
                }));
                setReceiptList(receipts);
            },
            () => {
                console.log('Failed to fetch receipts.');
                setPage('home');
            }
        );
    }, [baseUrl, request, setPage]);

    return (
        <div className="container mx-auto px-4 py-8">
            <h1 className="text-3xl font-bold text-center mb-6 text-[#62CBC6]">구매 내역</h1>
            <div className="grid gap-6 sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                {receiptList.map(receipt => (
                    <ReceiptListElement key={receipt.id} {...receipt} />
                ))}
            </div>
        </div>
    );
}

export default ReceiptList;
