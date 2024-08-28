import React from 'react';

const WaitingPage: React.FC = () => {
    return (
        <>
            <style>
                {`
          @keyframes progress {
            0% { width: 0%; }
            50% { width: 70%; }
            100% { width: 100%; }
          }
        `}
            </style>
            <div
                className="fixed inset-0 w-full h-full bg-white flex flex-col items-center justify-center p-4 z-[9999]">
                <div className="text-center">
                    {/*<div className="w-20 h-20 bg-[#37DBCF] rounded-full flex items-center justify-center mb-6">*/}
                    {/*    <Loader2 className="w-12 h-12 animate-spin text-white" />*/}
                    {/*</div>*/}
                    <h1 className="text-2xl font-bold mb-2 text-gray-800">처리 중</h1>
                    <p className="text-sm text-gray-600 mb-8">잠시만 기다려 주세요...</p>
                    <div className="w-64 h-2 bg-gray-200 rounded-full overflow-hidden">
                        <div
                            className="h-full bg-[#37DBCF] rounded-full"
                            style={{
                                width: '0%',
                                animation: 'progress 2s ease-in-out infinite'
                            }}
                        ></div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default WaitingPage;
