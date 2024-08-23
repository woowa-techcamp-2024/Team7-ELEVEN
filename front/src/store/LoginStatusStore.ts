import create from 'zustand';

interface LoginStatusStore {
    isLogin: boolean;
    setIsLogin: (isLogin: boolean) => void;
}

export const useLoginStore = create<LoginStatusStore>((set) => ({
    isLogin: false,
    setIsLogin: (isLogin: boolean) => set({isLogin: isLogin}),
}));
