import create from 'zustand';

interface AlertStore {
    isShow: boolean;
    message: string;
    setMessage: (message: string) => void;
    setShow: (isShow: boolean) => void;
}

export const useAlertStore = create<AlertStore>((set) => ({
    isShow: false,
    message: '',
    setMessage: (message: string) => set({message: message}),
    setShow: (isShow: boolean) => set({isShow: isShow}),
}));
