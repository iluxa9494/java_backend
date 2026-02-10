import storage from '@/requests/storage';

export default {
  namespaced: true,
  state: {
    fileName: null,
  },
  getters: {
    getStoragePostPhoto: (state) => state.fileName,
  },
  mutations: {
    setStoragePostPhoto: (state, value) => {
      state.fileName = value;
    },
  },
  actions: {
    async apiStoragePostPhoto({ commit }, file) {
      const data = new FormData();
      data.append('file', file);
      const response = await storage.api(data);
      commit('setStoragePostPhoto', response.data.fileName);
    },
  },
};
