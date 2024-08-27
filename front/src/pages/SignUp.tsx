import {useState} from 'react';
import {signUpApi} from "../api/user/api";
import {SignUpRequest} from "../api/user/type";
import {usePageStore} from "../store/PageStore";
import useAlert from "../hooks/useAlert";

function SignUpPage() {

    const {currentPage, setPage} = usePageStore();
    const baseUrl = process.env.REACT_APP_API_URL || '';
    const {showAlert} = useAlert();

    const [request, setRequest] = useState<SignUpRequest>({
        signUpId: '',
        password: '',
        userRole: 'BUYER',
    });
    const [idError, setIdError] = useState<string>(" ");
    const [passwordError, setPasswordError] = useState<string>(" ");
    const [isFormValid, setIsFormValid] = useState<boolean>(false);

    const handleUserTypeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setRequest({...request, userRole: e.target.value,});
    };

    const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newSignUpId = e.target.value;
        setRequest({...request, signUpId: newSignUpId});
        const idError = validateSignUpId(newSignUpId);
        setIdError(idError);
        validateForm(idError, passwordError);
    };

    const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newPassword = e.target.value;
        setRequest({...request, password: newPassword});
        const passwordError = validatePassword(newPassword);
        setPasswordError(passwordError);
        validateForm(idError, passwordError);
    };

    const requestSignUp = () => {
        signUpApi(
            baseUrl,
            request,
            () => {
                setPage('login');
            },
            (message) => {
                showAlert(message);
            }
        );
    }

    const validateForm = (idError: string, passwordError: string) => {
        if (idError === "" && passwordError === "") {
            setIsFormValid(true);
        } else {
            setIsFormValid(false);
        }
    };

    const validateSignUpId = (signUpId: string): string => {
        if (signUpId.length < 2 || signUpId.length > 20) {
            return "아이디는 2자 이상, 20자 이하로 입력해야 합니다.";
        }
        return "";
    };

    const validatePassword = (password: string): string => {
        if (password.length < 8 || password.length > 20) {
            return "비밀번호는 8자 이상, 20자 이하로 입력해야 합니다.";
        }
        if (!/[0-9]/.test(password)) {
            return "비밀번호에는 최소한 하나의 숫자가 포함되어야 합니다.";
        }
        if (!/[a-z]/.test(password)) {
            return "비밀번호에는 최소한 하나의 소문자가 포함되어야 합니다.";
        }
        if (!/^[a-zA-Z0-9]*$/.test(password)) {
            return "비밀번호는 영문 대소문자와 숫자만 사용할 수 있습니다.";
        }
        return "";
    };

    return (
        <div className="grow mb-[64px]">
            <div className="container mx-auto px-4 py-8 max-w-lg">
                <h1 className="text-3xl font-bold text-center mb-6 text-[#62CBC6]">환영합니다!</h1>

                <form className="bg-white p-6 rounded-lg shadow-md border border-gray-300">
                    <div className="mb-4">
                        <label className="block text-gray-700 font-bold mb-2">사용자 유형</label>
                        <div className="flex items-center space-x-4">
                            <label className="flex items-center">
                                <input
                                    type="radio"
                                    name="userType"
                                    value="BUYER"
                                    className="radio radio-primary"
                                    checked={request.userRole === 'BUYER'}
                                    onChange={handleUserTypeChange}
                                />
                                <span className="ml-2 text-gray-700">구매자</span>
                            </label>
                            <label className="flex items-center">
                                <input
                                    type="radio"
                                    name="userType"
                                    value="SELLER"
                                    className="radio radio-primary"
                                    checked={request.userRole === 'SELLER'}
                                    onChange={handleUserTypeChange}
                                    disabled
                                />
                                <span className="ml-2 text-gray-700">판매자</span>
                            </label>
                        </div>
                    </div>

                    <div className="mb-4">
                        <label htmlFor="name" className="block text-gray-700 font-bold mb-2">아이디</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            className="input input-bordered w-full"
                            placeholder="아이디를 입력하세요"
                            value={request.signUpId}
                            onChange={handleNameChange}
                            required
                        />
                        {idError && <p className="text-red-500 text-xs mt-2">{idError}</p>}
                    </div>

                    <div className="mb-4">
                        <label htmlFor="password" className="block text-gray-700 font-bold mb-2">비밀번호</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            className="input input-bordered w-full"
                            placeholder="비밀번호를 입력하세요"
                            value={request.password}
                            onChange={handlePasswordChange}
                            required
                        />
                        {passwordError && <p className="text-red-500 text-xs mt-2">{passwordError}</p>}
                    </div>


                    <div className="text-center">
                        <button
                            className="btn btn-primary w-full bg-[#62CBC6] text-white"
                            type="button"
                            onClick={requestSignUp}
                            disabled={!isFormValid} // Disable the button when the form is not valid
                        >
                            회원가입
                        </button>
                    </div>

                </form>
            </div>
        </div>
    );
}

export default SignUpPage;
