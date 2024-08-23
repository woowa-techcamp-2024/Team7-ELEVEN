import create from 'zustand';

interface ReceiptStore {
    receiptId?: number;
    setReceiptId: (id: number) => void;
}

export const useReceiptStore = create<ReceiptStore>((set) => ({
    setReceiptId: (id: number) => set({receiptId: id}),
}));
