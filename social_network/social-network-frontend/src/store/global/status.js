export default {
  namespaced: true,
  state: {
    loading: false,
    error: false,
    errorMessage: '',
  },

  mutations: {
    setLoading(state) {
      state.loading = true;
    },

    setError(state, message = '') {
      state.error = true;
      state.errorMessage = message;
    },

    removeLoading(state) {
      state.loading = false;
    },

    removeError(state) {
      state.error = false;
      state.errorMessage = '';
    },
  },
};
