import create from 'zustand';

interface AuctionStore {
    auctionId?: number;
    setAuctionId: (auctionId: number) => void;
}

export const useAuctionStore = create<AuctionStore>((set) => ({
    setAuctionId: (auctionId: number) => set({auctionId: auctionId}),
}));
