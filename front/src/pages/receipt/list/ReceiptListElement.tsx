import {ReceiptSimpleInfo} from "./type";
import {getPriceFormatted} from "../../../util/NumberUtil";
import {usePageStore} from "../../../store/PageStore";
import {useReceiptStore} from "../../../store/ReceiptStore";

function ReceiptListElement({
                                id,
                                type,
                                productName,
                                quantity,
                                price
                            }: ReceiptSimpleInfo) {

    const {currentPage, setPage} = usePageStore();
    const {receiptId, setReceiptId} = useReceiptStore();

    const changeDetailPage = () => {
        setReceiptId(id);
        setPage('receiptDetail');
    }

    return (
        <tr
            className="border-b border-gray-200"
            onClick={changeDetailPage}
        >
            <td className="px-4 py-2">{type}</td>
            <td className="px-4 py-2">{productName}</td>
            <td className="px-4 py-2">{quantity}</td>
            <td className="px-4 py-2">{getPriceFormatted(price)}</td>
        </tr>
    );
}

export default ReceiptListElement;
