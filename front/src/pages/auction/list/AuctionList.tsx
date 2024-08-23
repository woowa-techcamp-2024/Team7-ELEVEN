import AuctionListElement from "./AuctionListElement";
import {AuctionItem, AuctionsRequest} from "../../../api/auction/type";
import {requestAuctionList} from "../../../api/auction/api";
import {useEffect, useState} from "react";
import useAlert from "../../../hooks/useAlert";

function AuctionList() {

    const [request, setRequest] = useState<AuctionsRequest>({
        offset: 0,
        size: 20,
    });

    const [auctions, setAuctions] = useState<AuctionItem[]>([]);
    const {showAlert} = useAlert();
    const baseUrl = process.env.REACT_APP_API_URL || ''

    useEffect(() => {
        requestAuctionList(
            baseUrl,
            request,
            (newAuctions) => {
                setAuctions([...newAuctions]);
            },
            () => {
            }
        );
    }, []);

    const nextPage = () => {
        requestAuctionList(
            baseUrl,
            {
                offset: request.offset + request.size,
                size: request.size,
            },
            (newAuctions) => {
                if (newAuctions.length !== 0) {
                    setAuctions([...newAuctions]);
                    setRequest({...request, offset: request.offset + request.size});
                }
                showAlert("마지막 페이지입니다.");
            },
            () => {
            }
        );
    }

    const previousPage = () => {
        requestAuctionList(
            baseUrl,
            {
                offset: request.offset - request.size,
                size: request.size,
            },
            (newAuctions) => {
                setAuctions([...newAuctions]);
                setRequest({...request, offset: request.offset - request.size});
            },
            () => {
                showAlert("첫 페이지입니다.");
            }
        );
    }


    return (
        <>
            <div className="flex flex-col navbar bg-base-100 p-4 relative">

                <div className="navbar-center w-full flex justify-center">
                    <span className="text-lg font-bold">Lucky Vicky</span>
                </div>

                {/*<div className="flex justify-center gap-2 p-4">*/}
                {/*    <button className="btn btn-sm btn-outline text-[#62CBC6] border-[#62CBC6]">*/}
                {/*        전체*/}
                {/*    </button>*/}
                {/*    <button className="btn btn-sm btn-outline text-[#62CBC6] border-[#62CBC6]">*/}
                {/*        종료 임박*/}
                {/*    </button>*/}
                {/*    <button className="btn btn-sm btn-outline text-[#62CBC6] border-[#62CBC6]">*/}
                {/*        최근 추가*/}
                {/*    </button>*/}
                {/*    <button className="btn btn-sm btn-outline text-[#62CBC6] border-[#62CBC6]">*/}
                {/*        즉시 구매*/}
                {/*    </button>*/}
                {/*</div>*/}

            </div>
            <div className="p-4">
                <div className="grid grid-cols-1 gap-[10px]">
                    {
                        auctions.map((auction) => (
                            <AuctionListElement
                                id={auction.id}
                                title={auction.title}
                                price={auction.price}
                                startedAt={new Date(auction.startedAt)}
                                endedAt={new Date(auction.finishedAt)}
                            />
                        ))
                    }
                </div>
            </div>
            <div className="flex justify-between p-4">
                <button
                    className="btn btn-outline text-[#62CBC6] border-[#62CBC6]"
                    onClick={previousPage}
                >
                    이전 페이지
                </button>
                <button
                    className="btn btn-outline text-[#62CBC6] border-[#62CBC6]"
                    onClick={nextPage}
                >
                    다음 페이지
                </button>
            </div>
        </>
    );
}

export default AuctionList;