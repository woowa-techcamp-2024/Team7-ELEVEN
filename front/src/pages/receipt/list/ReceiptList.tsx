import ReceiptListElement from "./ReceiptListElement";
import {ReceiptSimpleInfo} from "./type";
import {useEffect, useState} from "react";
import {ReceiptsRequest} from "../../../api/receipt/type";
import {requestReceiptList} from "../../../api/receipt/api";
import {usePageStore} from "../../../store/PageStore";

function ReceiptListPage() {

    const {currentPage, setPage} = usePageStore();
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
                const receipts: Array<ReceiptSimpleInfo> = newReceipts.map(receipt => ({
                    id: receipt.id,
                    type: receipt.type,
                    productName: receipt.productName,
                    quantity: receipt.quantity,
                    price: receipt.price
                }));
                setReceiptList([...receipts]);
            },
            () => {
                console.log('Failed to fetch receipts.');
                setPage('home');
            }
        );
    }, []);

    return (
        <div className="grow mb-[64px]">
            <div className="container mx-auto px-4 py-8">
                <h1 className="text-3xl font-bold text-center mb-6 text-[#62CBC6]">구매 내역</h1>

                <div className="overflow-x-auto">
                    <table className="table-auto w-full bg-white border border-gray-300 rounded-lg shadow-md">
                        <thead className="bg-[#62CBC6] text-white">
                        <tr>
                            <th className="px-4 py-2 text-left">유형</th>
                            <th className="px-4 py-2 text-left">이름</th>
                            <th className="px-4 py-2 text-left">수량</th>
                            <th className="px-4 py-2 text-left">가격</th>
                        </tr>
                        </thead>
                        <tbody>
                        {receiptList.map(receipt => (
                            <ReceiptListElement key={receipt.id} {...receipt} />
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )
}

export default ReceiptListPage;
