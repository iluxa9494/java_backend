export default {
  namespaced: true,
  state: {
    status: 'Успешно',
    text: 'Сделано!',
    show: false,
    timeout: 500,
    notificationsQueue: [],
    theme: '',
  },
  getters: {
    getState: (state) => state,
    isNotificationQueueEmpty: (state) => state.notificationsQueue.length === 0,
    getTheme: (state) => state.theme,
  },
  mutations: {
    setInfo(state, value) {
      state.status = value.status;
      state.text = value.text;
    },
    toggleShow: (state) => (state.show = !state.show),
    PUSH_NOTIFICATION(state, notification) {
      state.notificationsQueue.push(notification);
    },
    REMOVE_NOTIFICATION(state) {
      state.notificationsQueue.shift();
    },
    changeTheme(state, theme) {
      state.theme = theme;
    },
  },
  actions: {
    setAlert({
      commit,
      state,
      dispatch
    }, value) {
      commit('PUSH_NOTIFICATION', value);
      if (!state.show) {
        dispatch('showNotification');
      }
    },
    showNotification({
      state,
      commit,
      dispatch
    }) {
      if (state.notificationsQueue.length > 0) {
        const currentNotification = state.notificationsQueue[0];
        commit('setInfo', currentNotification);
        commit('toggleShow');

        setTimeout(() => {
          commit('toggleShow');
          commit('REMOVE_NOTIFICATION');
          if (state.notificationsQueue.length > 0);
          dispatch('showNotification');
        }, state.timeout);
      }
    },
  },
};