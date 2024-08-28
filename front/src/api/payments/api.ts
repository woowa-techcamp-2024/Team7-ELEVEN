import {ChargePointsRequest} from "./type";

async function chargePointsApi(
    baseUrl: string,
    data: ChargePointsRequest,
    onSuccess: () => void,
    onFailure: (message: string) => void
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
            const errorMessage = await response.text();
            onFailure(errorMessage);
        }
    } catch (error) {
        onFailure("CHARGE POINT ERROR.");
    }
}

export {chargePointsApi};
