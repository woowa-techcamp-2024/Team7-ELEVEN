interface SignUpRequest {
    signUpId: string;
    password: string;
    userRole: string;
}

interface SignInRequest {
    signInId: string;
    password: string;
}

export type {SignInRequest, SignUpRequest};
