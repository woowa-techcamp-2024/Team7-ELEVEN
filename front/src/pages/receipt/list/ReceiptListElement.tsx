import { ReceiptSimpleInfo } from "./type";
import { getPriceFormatted } from "../../../util/NumberUtil";
import { usePageStore } from "../../../store/PageStore";
import { useReceiptStore } from "../../../store/ReceiptStore";

function ReceiptListElement({
                                id,
                                type,
                                productName,
                                quantity,
                                price
                            }: ReceiptSimpleInfo) {
    const { currentPage, setPage } = usePageStore();
    const { receiptId, setReceiptId } = useReceiptStore();

    const changeDetailPage = () => {
        setReceiptId(id);
        setPage('receiptDetail');
    };

    // Total price calculation
    const totalPrice = price * quantity;

    // Tag text and styles based on type
    const getTag = (type: string) => {
        switch (type) {
            case 'PURCHASED':
                return { text: '구매완료', color: 'bg-blue-500' };
            case 'REFUND':
                return { text: '환불완료', color: 'bg-red-500' };
            default:
                return { text: '', color: 'bg-gray-500' };
        }
    };

    const { text: tagText, color: tagColor } = getTag(type);

    return (
        <div
            className="bg-white border border-gray-300 rounded-lg shadow-md p-4 cursor-pointer hover:bg-gray-100 transition-colors duration-300"
            onClick={changeDetailPage}
        >
            <div className="relative">
                {tagText && (
                    <span
                        className={`absolute top-2 right-2 px-2 py-1 text-xs font-semibold text-white rounded-full ${tagColor}`}
                    >
                        {tagText}
                    </span>
                )}
                <div className="flex flex-col md:flex-row items-start md:items-center space-y-4 md:space-y-0 md:space-x-4">
                    <div className="flex-1">
                        <h3 className="text-lg font-semibold text-gray-800">{productName}</h3>
                        <p className="text-sm text-gray-600">단가: {getPriceFormatted(price)}</p>
                        <p className="text-sm text-gray-600">수량: {quantity}</p>
                        {/*<hr/>*/}
                        {/*<p className="text-sm text-gray-600">합계: {getPriceFormatted(totalPrice)}</p>*/}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ReceiptListElement;
