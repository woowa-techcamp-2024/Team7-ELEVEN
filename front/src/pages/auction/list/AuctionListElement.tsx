import {usePageStore} from "../../../store/PageStore";
import {getKrDateFormat} from "../../../util/DateUtil";
import {getPriceFormatted} from "../../../util/NumberUtil";
import {useAuctionStore} from "../../../store/AuctionStore";

interface AuctionSimpleInfo {
    id: number;
    title: string;
    price: number;
    startedAt: Date;
    endedAt: Date;
}

function AuctionListElement(
    {
        id,
        title,
        price,
        startedAt,
        endedAt,
    }: AuctionSimpleInfo
) {

    const {currentPage, setPage} = usePageStore();
    const {auctionId, setAuctionId} = useAuctionStore();

    const changePage = (page: string) => {
        setAuctionId(id);
        setPage(page);
    }

    return (
        <div className="card bg-base-100 shadow-xl border border-gray-300"
             onClick={() => changePage('auctionDetail')}>
            <div className="card-body">
                <h2 className="card-title text-base">{title}</h2>
            </div>
            <div className="card-body">
                <p className="text-sm text-[#62CBC6]">시작 시간: {getKrDateFormat(startedAt)}</p>
                <p className="text-sm text-[#62CBC6]">종료 시간: {getKrDateFormat(endedAt)}</p>
                <p className="text-sm text-[#62CBC6]">현재가: {getPriceFormatted(price)}</p>
            </div>
        </div>
    );
}

export default AuctionListElement;
