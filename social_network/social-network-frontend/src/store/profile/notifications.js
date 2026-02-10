import notifications from '@/requests/notifications';
import axios from 'axios';

export default {
  namespaced: true,
  state: {
    lastNotifications: [],
    nextNotifications: [],
    notificationsLength: 0,
    userInfo: {},
    size: 0,
    allNotifications: {
      notifications: [],
      totalPages: null,
      totalElements: null,
    },
  },
  getters: {
    getLastNotifications: (state) => state.lastNotifications,
    getNextNotifications: (state) => state.nextNotifications,
    getAllNotifications: (state) => state.allNotifications,
    getNotificationsLength: (state) => state.notificationsLength,
    getNotificationsTextType: () => (type) => {
      switch (type) {
        case 'POST':
          return 'опубликовал новую запись';
        case 'POST_COMMENT':
          return 'оставил комментарий';
        case 'COMMENT_COMMENT':
          return 'ответил на ваш комментарий';
        case 'FRIEND_REQUEST':
          return '';
        case 'FRIEND_BIRTHDAY':
          return '';
        case 'MESSAGE':
          return 'прислал сообщение';
        case 'FRIEND_APPROVE':
          return '';
        case 'FRIEND_BLOCKED':
          return '';
        case 'FRIEND_UNBLOCKED':
          return '';
        case 'FRIEND_SUBSCRIBE':
          return '';
        case 'USER_BIRTHDAY':
          return '';
        case 'LIKE':
          return '';
        case 'SEND_EMAIL_MESSAGE':
          return '';
      }
    },
  },
  mutations: {
    clearLastNotifications: (state) => {
      state.lastNotifications.notifications = [];
      state.lastNotifications.totalPages = null;
    },

    setNextNotifications: (state, value) => {
      state.nextNotifications = value.notifications;
      state.allNotifications.totalPages = value.totalPages;
      state.allNotifications.totalElements = value.totalElements;
      state.allNotifications.notifications = [
        ...value.notifications,
        ...state.allNotifications.notifications,
      ];
    },

    setLatsNotifications: (state, value) => {
      state.lastNotifications = value.notifications;
      state.allNotifications.totalPages = value.totalPages;
      state.allNotifications.totalElements = value.totalElements;
      state.allNotifications.notifications = value.notifications;
    },

    setReadedNotifications: (state, value) => (state.notifications = value),
    setNotificationsLength: (state, value) => (state.notificationsLength = value),

    addNotification(state, notification) {
      state.notifications.unshift(notification);
    },

    addNotificationsLength(state, notification) {
      if (notification.notificationType) {
        state.notificationsLength = state.notificationsLength + 1;
      }
    },

    setUserInfo(state, userInfo) {
      state.userInfo = userInfo;
    },

    incrementOffset(state, count) {
      state.size += count;
    },
  },

  actions: {
    async fetchNotifications({ commit, state }, payload = {}) {
      // дефолты, чтобы не падать, когда dispatch('fetchNotifications') вызывается без аргументов
      const {
        countPage = 0,
        direction = 'desc',
      } = payload;

      // Отправка запроса на сервер с использованием текущей длины списка уведомлений
      const response = await notifications.get(countPage, direction);

      // Получение списка уведомлений
      const notificationsList = response.data.content;
      const totalPages = response.data.totalPages;
      const totalElements = response.data.totalElements;

      // Получение списка авторов
      const authorIds = notificationsList
        .filter((notification) => notification.data && notification.data.authorId)
        .map((notification) => notification.data.authorId);

      // Обращение к API для получения информации об авторах
      const userInfoRequestPromises = Array.from(new Set(authorIds)).map(async (authorId) => {
        try {
          const { data } = await axios.get(`/account/${authorId}`);
          return data;
        } catch (error) {
          console.error(`Не нашёл аккаунт с id: ${authorId}:`, error);
          return {};
        }
      });

      // Ожидание завершения всех запросов
      const userInfoResponses = await Promise.all(userInfoRequestPromises);

      // Создание объекта для хранения информации об авторах
      const userInfo = userInfoResponses.reduce((acc, user) => {
        if (user && user.id) {
          acc[user.id] = user;
        }
        return acc;
      }, {});

      // Обновление объекта состояния Vuex
      commit('setUserInfo', userInfo);

      // Обработка уведомлений
      const mergedNotifications = notificationsList.map(async (notification) => {
        const { authorId } = notification.data;
        if (authorId) {
          const authorInfo = userInfo[authorId];
          return {
            ...notification,
            data: {
              ...notification.data,
              author: authorInfo || {},
            },
          };
        }
        return notification;
      });

      // Ожидание завершения обработки уведомлений
      if (state.lastNotifications.length === 0) {
        const lastNotifications = await Promise.all(mergedNotifications);
        commit('clearLastNotifications');
        commit('setLatsNotifications', {
          notifications: lastNotifications,
          totalPages,
          totalElements,
        });
      } else {
        const nextNotifications = await Promise.all(mergedNotifications);
        commit('setNextNotifications', {
          notifications: nextNotifications,
          totalPages,
          totalElements,
        });
      }
    },

    async fetchNotificationsLength({ commit }) {
      const response = await notifications.getLength();
      commit('setNotificationsLength', response.data.data.count);
    },

    async readedNotifications({ commit, dispatch }) {
      const response = await notifications.readed();
      commit('setReadedNotifications', response);
      await dispatch('fetchNotifications'); // теперь не падает, т.к. у fetchNotifications есть дефолтный payload
    },
  },
};