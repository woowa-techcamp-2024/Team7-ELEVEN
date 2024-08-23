import {usePageStore} from "../store/PageStore";
import React from "react";
import {useLoginStore} from "../store/LoginStatusStore";
import {signOut} from "../api/user/api";

function Footer() {

    const {currentPage, setPage} = usePageStore();
    const {isLogin, setIsLogin} = useLoginStore();
    const baseUrl = process.env.REACT_APP_API_URL || '';

    const changePage = (page: string) => {
        setPage(page);
    }

    const logout = () => {
        signOut(
            baseUrl,
            () => {
                setIsLogin(false);
                setPage('home');
            },
            () => {
                console.log('Failed to sign out.');
            }
        )
    }

    return (
        <div className="btm-nav">
            <button
                className={`text-[#62CBC6] ${currentPage === 'home' ? "active" : ""}`}
                onClick={() => changePage('home')}
            >
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="h-6 w-6"
                    fill="currentColor"
                    viewBox="0 0 24 24"
                >
                    <path
                        d="M12 3L4 9v12h5v-7h6v7h5V9l-8-6zM12 0L0 9h4v15h8v-7h2v7h8V9h4L12 0z"
                    />
                </svg>
                <span className="btm-nav-label">경매</span>
            </button>
            {
                isLogin ?
                    <button
                        className={`text-[#62CBC6] ${currentPage === 'charge' ? "active" : ""}`}
                        onClick={() => changePage('charge')}
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-6 w-6"
                            fill="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                d="M21.71 20.29L16.9 15.5A8.48 8.48 0 0018 10.5 8.5 8.5 0 109.5 19a8.48 8.48 0 005-.6l4.79 4.79a1 1 0 001.42-1.42zM10.5 17A6.5 6.5 0 1117 10.5 6.51 6.51 0 0110.5 17z"
                            />
                        </svg>
                        <span className="btm-nav-label">포인트 충전</span>
                    </button>
                    : ''
            }

            {
                isLogin ?

                    <button
                        className={`text-[#62CBC6] ${currentPage === 'receiptList' ? "active" : ""}`}
                        onClick={() => changePage('receiptList')}
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-6 w-6"
                            fill="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                d="M12 3L2 9v12h20V9l-10-6zm0 2.18L19 9l-7 4.09L5 9l7-3.82zM4 20V9.36l8 4.64 8-4.64V20H4z"
                            />
                        </svg>
                        <span className="btm-nav-label">구매 내역</span>
                    </button>
                    : ''
            }

            {
                isLogin ?
                    <button
                        className={`text-[#62CBC6] ${currentPage === 'login' ? "active" : ""}`}
                        onClick={() => logout()}
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-6 w-6"
                            fill="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                d="M12 12a5 5 0 11-5 5 5 5 0 015-5m0-2a7 7 0 107 7 7 7 0 00-7-7zm0-4a4 4 0 11-4 4 4 4 0 014-4m0-2a6 6 0 106 6 6 6 0 00-6-6zm0 6a2 2 0 112-2 2 2 0 01-2 2z"
                            />
                        </svg>
                        <span className="btm-nav-label">로그아웃</span>
                    </button>
                    :
                    <button
                        className={`text-[#62CBC6] ${currentPage === 'login' ? "active" : ""}`}
                        onClick={() => changePage('login')}
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            className="h-6 w-6"
                            fill="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                d="M12 12a5 5 0 11-5 5 5 5 0 015-5m0-2a7 7 0 107 7 7 7 0 00-7-7zm0-4a4 4 0 11-4 4 4 4 0 014-4m0-2a6 6 0 106 6 6 6 0 00-6-6zm0 6a2 2 0 112-2 2 2 0 01-2 2z"
                            />
                        </svg>
                        <span className="btm-nav-label">로그인</span>
                    </button>
            }
        </div>
    );
}

export default Footer