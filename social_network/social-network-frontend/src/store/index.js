import { createStore } from 'vuex';

import global from './global';
import auth from './auth';
import profile from './profile';
import users from './users';

export default createStore({
  namespaced: true,
  modules: {
    global,
    auth,
    profile,
    users,
  },
  state: {
    captchaCode: Math.floor(Math.random() * 10000)
      .toString()
      .padStart(4, '0'),
    currentUser: null,
  },
  getters: {
    getCode: (state) => state.captchaCode,
    getUser: (state) => state.currentUser,
  },
  actions: {
    loadUser(contex, payload) {
      contex.commit('loadUser', payload);
    },
  },
  mutations: {
    loadUser(state, payload) {
      state.currentUser = payload;
    },
  },
  strict: process.env.NODE_ENV !== 'production',
});
