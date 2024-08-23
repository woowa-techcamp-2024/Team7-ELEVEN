import {ChargePointsRequest} from "./type";

async function chargePointsApi(
    baseUrl: string,
    data: ChargePointsRequest,
    onSuccess: () => void,
    onFailure: () => void
) {
    try {
        const response = await fetch(`${baseUrl}/payments/points/charge`, {
            method: 'POST',
            mode: 'cors',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
                'Accept': 'application/json',
            },
            body: JSON.stringify(data),
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

export {chargePointsApi};
