import {create} from 'zustand';

const useStore = create(set => ({
    id: null,
    setId: (newId) => set({ id: newId }),
    myAccount: null,
    setMyAccount: (newMyAccount) => set({ myAccount: newMyAccount }),
}));

export default useStore;