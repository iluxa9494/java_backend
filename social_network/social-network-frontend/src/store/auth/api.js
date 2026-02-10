import auth from '@/requests/auth';
import requestSettings from '@/requests/settings';


export default {
  namespaced: true,
  state: {
    captchaSecret: localStorage.getItem('user-token') || '',
    jwt: '',
    status: '',
    pollingToken: null,
    refreshAttempts: 0,
    isCode: true,
  },
  getters: {
    apiToken: (state) => state.captchaSecret,
    isAuthenticated: (state) => !!state.captchaSecret,
    authStatus: (state) => state.status,
    getIsCode: (state) => state.isCode,
  },
  mutations: {
    setJwt: (state) => {
      const cookie = document.cookie.split('; ').reduce((acc, string) => {
        const [key, value] = string.split('=');
        acc[key] = value;
        return acc;
      }, {});

      state.jwt = cookie.jwt || '';
      console.log("state.jwt="+state.jwt)
    },

    setToken: (state, token) => (state.captchaSecret = token),
    setIsCode: (state, isCode) => (state.isCode = isCode),
    setStatus: (state, status) => (state.status = status),
    addAttempts: (state) => (state.refreshAttempts = state.refreshAttempts + 1),
    resetAttempts: (state) => (state.resetAttmpts = 0),
    setPollingInterval(state, interval) {
      state.pollingToken = interval;
    },
  },
  actions: {
    async register({ dispatch }, user) {
      await auth.register(user);

      dispatch(
        'global/alert/setAlert',
        { status: 'success', text: 'Зарегистрирован, делаю логин' },
        { root: true }
      );
    },
    

    pollingToken({ commit, dispatch }) {
      const interval = setInterval(() => {
        dispatch("refreshToken");
      }, 1000 * 600 * 1.5);//1000*600*1.5
      commit("setPollingInterval", interval);
    },

    async refreshToken({ commit, state, dispatch }) {
      if (state.refreshAttempts > 3) {
        console.warn('Cannot refresh token. Logging out...', state.pollingToken);
        dispatch('logout');
        clearInterval(state.pollingToken);
        return;
      }
      // localStorage.removeItem('user-token');
      const refreshToken = localStorage.getItem('refresh-token');

      try {
        const response = await auth.refreshToken(refreshToken);
        // response.data
        const newAccessToken = response.data.accessToken;
        const newRefreshToken = response.data.refreshToken;


        localStorage.setItem('user-token', newAccessToken);
        localStorage.setItem('refresh-token', newRefreshToken);
        
        document.cookie = `jwt=${newAccessToken}`;

        requestSettings.setDefaultHeader('Authorization', `Bearer ${newAccessToken}`);

        commit('resetAttempts');
        commit('setJwt');
      } catch {
        console.warn('Cannot get new access token', state.refreshAttempts);
        commit('addAttempts');
      }
    },

    async login({ commit, dispatch }, user) {
      commit('setStatus', 'loading');
      try {
        const response = await auth.login(user);
        const { accessToken, refreshToken } = response.data;
        localStorage.setItem('user-token', accessToken);
        localStorage.setItem('refresh-token', refreshToken);

        document.cookie = `jwt=${accessToken}`;
        requestSettings.setDefaultHeader('Authorization', `Bearer ${accessToken}`);

        commit('setJwt');
        commit('setToken', accessToken);
        commit('setStatus', 'success');
        commit('profile/info/setInfo', response.data, {
          root: true,
        });

        dispatch('pollingToken');
      } catch (error) {
        commit('setStatus', 'error');

        localStorage.removeItem('user-token');
        localStorage.removeItem('refresh-token');

        document.cookie = 'jwt=';
        commit('setJwt');
      }
    },

    async logout({ commit, dispatch, state }) {
      await auth.logout();

      commit('setToken', '');
      commit('setStatus', 'logout');

      dispatch(
        'global/alert/setAlert',
        { status: 'success', text: 'Вы вышли из системы' },
        { root: true }
      );

      localStorage.removeItem('user-token');
      localStorage.removeItem('refresh-token');

      document.cookie = 'jwt=';
      clearInterval(state.pollingToken);
      commit('setJwt');
      requestSettings.deleteDefaultHeader('Authorization');
    },
  },
};
