import { usePageStore } from "../store/PageStore";
import React from "react";
import { useLoginStore } from "../store/LoginStatusStore";
import { signOut } from "../api/user/api";
import shoppingBagIcon from '../img/shopping-bag.svg';
import creditCardIcon from '../img/credit-card.svg';
import listIcon from '../img/list.svg';
import logInIcon from '../img/log-in.svg';
import logOutIcon from '../img/log-out.svg';

function Footer() {
    const { currentPage, setPage } = usePageStore();
    const { isLogin, setIsLogin } = useLoginStore();
    const baseUrl = process.env.REACT_APP_API_URL || '';

    const changePage = (page: string) => {
        setPage(page);
    };

    const logout = () => {
        if(!window.confirm("정말로 로그아웃할까요?")) {
            return;
        }
        signOut(
            baseUrl,
            () => {
                setIsLogin(false);
                setPage('home');
            },
            () => {
                console.log('Failed to sign out.');
            }
        );
    };

    // Function to determine the appropriate filter for each tab
    const getFilter = (page: string) => {
        if (currentPage === page) {
            // Activate color #61CBC6
            return 'invert(43%) sepia(48%) saturate(5533%) hue-rotate(167deg) brightness(99%) contrast(101%)'; // #61CBC6
        } else {
            // Inactive color gray
            return 'invert(60%) brightness(97%)'; // Gray out effect
        }
    };

    return (
        <div className="btm-nav border-t border-gray-300">
            <button
                className={`flex flex-col items-center ${currentPage === 'home' ? "relative" : ""}`}
                onClick={() => changePage('home')}
            >
                <img
                    src={shoppingBagIcon}
                    alt="Home"
                    className={`h-6 w-6 ${currentPage === 'home' ? '' : 'filter'}`}
                    style={{ filter: getFilter('home') }}
                />
                <span className={`btm-nav-label text-sm ${currentPage === 'home' ? 'text-[#61CBC6]' : 'text-gray-400'}`}>
                    경매
                </span>
                {currentPage === 'home' && (
                    <div className="absolute top-0 left-0 w-full h-1 bg-[#61CBC6] rounded-t-lg"></div>
                )}
            </button>
            {
                isLogin &&
                <button
                    className={`flex flex-col items-center ${currentPage === 'charge' ? "relative" : ""}`}
                    onClick={() => changePage('charge')}
                >
                    <img
                        src={creditCardIcon}
                        alt="Charge"
                        className={`h-6 w-6 ${currentPage === 'charge' ? '' : 'filter'}`}
                        style={{ filter: getFilter('charge') }}
                    />
                    <span className={`btm-nav-label text-sm ${currentPage === 'charge' ? 'text-[#61CBC6]' : 'text-gray-400'}`}>
                        포인트 충전
                    </span>
                    {currentPage === 'charge' && (
                        <div className="absolute top-0 left-0 w-full h-1 bg-[#61CBC6] rounded-t-lg"></div>
                    )}
                </button>
            }
            {
                isLogin &&
                <button
                    className={`flex flex-col items-center ${currentPage === 'receiptList' ? "relative" : ""}`}
                    onClick={() => changePage('receiptList')}
                >
                    <img
                        src={listIcon}
                        alt="Receipt List"
                        className={`h-6 w-6 ${currentPage === 'receiptList' ? '' : 'filter'}`}
                        style={{ filter: getFilter('receiptList') }}
                    />
                    <span className={`btm-nav-label text-sm ${currentPage === 'receiptList' ? 'text-[#61CBC6]' : 'text-gray-400'}`}>
                        구매 내역
                    </span>
                    {currentPage === 'receiptList' && (
                        <div className="absolute top-0 left-0 w-full h-1 bg-[#61CBC6] rounded-t-lg"></div>
                    )}
                </button>
            }
            {
                isLogin ?
                    <button
                        className={`flex flex-col items-center ${currentPage === 'login' ? "relative" : ""}`}
                        onClick={() => logout()}
                    >
                        <img
                            src={logOutIcon}
                            alt="Log Out"
                            className={`h-6 w-6 ${currentPage === 'login' ? '' : 'filter'}`}
                            style={{ filter: getFilter('login') }}
                        />
                        <span className={`btm-nav-label text-sm ${currentPage === 'login' ? 'text-[#61CBC6]' : 'text-gray-400'}`}>
                            로그아웃
                        </span>
                        {currentPage === 'login' && (
                            <div className="absolute top-0 left-0 w-full h-1 bg-[#61CBC6] rounded-t-lg"></div>
                        )}
                    </button>
                    :
                    <button
                        className={`flex flex-col items-center ${currentPage === 'login' ? "relative" : ""}`}
                        onClick={() => changePage('login')}
                    >
                        <img
                            src={logInIcon}
                            alt="Log In"
                            className={`h-6 w-6 ${currentPage === 'login' ? '' : 'filter'}`}
                            style={{ filter: getFilter('login') }}
                        />
                        <span className={`btm-nav-label text-sm ${currentPage === 'login' ? 'text-[#61CBC6]' : 'text-gray-400'}`}>
                            로그인
                        </span>
                        {currentPage === 'login' && (
                            <div className="absolute top-0 left-0 w-full h-1 bg-[#61CBC6] rounded-t-lg"></div>
                        )}
                    </button>
            }
        </div>
    );
}

export default Footer;
