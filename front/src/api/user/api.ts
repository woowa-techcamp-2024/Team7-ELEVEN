import { SignInRequest, SignUpRequest } from "./type";

async function signUpApi(
    baseUrl: string,
    data: SignUpRequest,
    onSuccess: () => void,
    onFailure: (message: string) => void
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
            const errorMessage = await response.text();
            onFailure(`Sign up failed: ${errorMessage}`);
        }
    } catch (error) {
        console.error(error);
        onFailure("An unexpected error occurred during sign-up.");
    }
}

async function signInApi(
    baseUrl: string,
    data: SignInRequest,
    onSuccess: () => void,
    onFailure: (message: string) => void
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
            const errorMessage = await response.text();
            onFailure(`Sign in failed: ${errorMessage}`);
        }
    } catch (error) {
        console.error(error);
        onFailure("An unexpected error occurred during sign-in.");
    }
}

async function signOut(
    baseUrl: string,
    onSuccess: () => void,
    onFailure: (message: string) => void
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
            const errorMessage = await response.text();
            onFailure(`Sign out failed: ${errorMessage}`);
        }
    } catch (error) {
        console.error(error);
        onFailure("An unexpected error occurred during sign-out.");
    }
}

export { signUpApi, signInApi, signOut };
