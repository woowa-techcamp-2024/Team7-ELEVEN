import React, {useEffect} from 'react';
import {useAlertStore} from '../store/AlertStore';
import alertCircleIcon from '../img/alert-circle.svg';

const AlertPage = () => {
    const {isShow, message, setShow, setMessage} = useAlertStore();

    useEffect(() => {
        if (isShow) {
            const timer = setTimeout(() => {
                setShow(false);
            }, 1500);

            return () => clearTimeout(timer);
        }
    }, [isShow, setShow]);

    const onClick = () => {
        setShow(false);
        setMessage('');
    }

    if (!isShow) {
        return null;
    }

    return (
        <div
            className={`fixed inset-0 w-screen h-screen bg-slate-50/50 z-[9999] transition-opacity duration-200 ease-in-out ${isShow ? 'opacity-100' : 'opacity-0'}`}
            onClick={onClick}
        >
            <div className={`p-4 fixed w-full top-0 z-[10000] transition-opacity duration-200 ease-in-out ${isShow ? 'opacity-100' : 'opacity-0'}`}>
                <div role="alert" className="alert">
                    <img
                        src={alertCircleIcon}
                        alt=""
                    />
                    <span>{message}</span>
                </div>
            </div>
        </div>
    );
};

export default AlertPage;
