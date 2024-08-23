import {SignInRequest, SignUpRequest} from "./type";

async function signUpApi(
    baseUrl: string,
    data: SignUpRequest,
    onSuccess: () => void,
    onFailure: () => void
) {
    try {
        const response = await fetch(`${baseUrl}/auth/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
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

async function signInApi(
    baseUrl: string,
    data: SignInRequest,
    onSuccess: () => void,
    onFailure: () => void
) {
    try {
        const response = await fetch(`${baseUrl}/auth/signin`, {
            method: 'POST',
            mode: 'cors',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });
        if (response.ok) {
            onSuccess();
        } else {
            onFailure();
        }
    } catch (error) {
        console.error(error);
        onFailure();
    }
}

async function signOut(
    baseUrl: string,
    onSuccess: () => void,
    onFailure: () => void
) {
    try {
        const response = await fetch(`${baseUrl}/auth/signout`, {
            method: 'POST',
            mode: 'cors',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
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

export {signUpApi, signInApi, signOut};
