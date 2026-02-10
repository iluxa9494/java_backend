import auth from '@/requests/auth';

export default {
  namespaced: true,
  state: {
    captcha: { secret: '', imgCode: '' },
  },
  getters: {
    getCaptcha: (state) => {
      return state.captcha;
    },
  },
  mutations: {
    setCaptcha: (state, value) => {
      state.captcha = value;
    },
  },
  actions: {
    async fetchCaptcha({ commit }) {
      const response = await auth.captcha();
      commit('setCaptcha', {
        secret: response.data.secret,
        imgCode: response.data.image,
      });
    },
  },
};