import {ChargePointsRequest} from "../api/payments/type";
import {chargePointsApi} from "../api/payments/api";
import {useState} from "react";
import useAlert from "../hooks/useAlert";


function ChargePointPage() {

    const sessionId = 'your-session-id'; // 세션 ID는 실제 값을 사용해야 합니다.
    const {showAlert} = useAlert();
    const baseUrl = process.env.REACT_APP_API_URL || '';
    const [request, setRequest] = useState<ChargePointsRequest>({
        amount: 0,
    });

    const requestChargePoints = () => {
        chargePointsApi(
            baseUrl,
            request,
            sessionId,
            () => {
                showAlert("포인트 충전 성공!")
                setRequest({amount: 0});
            },
            () => {
                showAlert("포인트 충전 실패!")
            }
        );
    }

    return (
        <div className="flex items-center justify-center min-h-screen bg-slate-50">
            <div className="w-full max-w-xs">
                <div className="card bg-base-100 shadow-xl border border-gray-300">
                    <div className="card-body">
                        <h2 className="text-2xl font-bold text-center mb-4 text-[#62CBC6]">포인트 충전</h2>
                        <form>
                            <div className="mb-6">
                                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="points">
                                    충전할 포인트
                                </label>
                                <input
                                    className="input input-bordered w-full"
                                    id="points"
                                    type="number"
                                    placeholder="충전할 포인트를 입력하세요"
                                    min="0"
                                    step="1"
                                    value={request.amount}
                                    onChange={(e) => setRequest({...request, amount: parseInt(e.target.value)})}
                                />
                            </div>
                            <div className="flex items-center justify-center">
                                <button
                                    className="btn btn-primary w-full bg-[#62CBC6] border-none hover:bg-[#4FA6A3]"
                                    type="button"
                                    onClick={requestChargePoints}
                                >
                                    충전하기
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default ChargePointPage;