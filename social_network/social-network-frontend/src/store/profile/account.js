import auth from '@/requests/auth';
import notifications from '@/requests/notifications';

export default {
  namespaced: true,
  state: {
    notifications: [
      {
        icon: 'post',
        name: 'О новых публикациях',
        nameEng: 'About new publications',
        type: 'POST',
        enable: false,
      },
      {
        icon: 'comments',
        name: 'О новых комментариях к моим публикациям',
        nameEng: 'About new comments to my publications',
        type: 'POST_COMMENT',
        enable: false,
      },
      {
        icon: 'reviews',
        name: 'О ответах на мои комментарии',
        nameEng: 'About the responses to my comments',
        type: 'COMMENT_COMMENT',
        enable: false,
      },
      {
        icon: 'friends',
        name: 'О заявках в друзья',
        nameEng: 'About friend requests',
        type: 'FRIEND_REQUEST',
        enable: false,
      },
      {
        icon: 'messages',
        name: 'О новых личных сообщениях',
        nameEng: 'About new private messages',
        type: 'MESSAGE',
        enable: false,
      },
      {
        icon: 'birthdays',
        name: 'О дне рождения друга',
        nameEng: `About a friend's birthday`,
        type: 'FRIEND_BIRTHDAY',
        enable: false,
      },
      // {
      //   icon: "phone",
      //   name: "Отправлять уведомления на телефон",
      //   type: "SEND_PHONE_MESSAGE",
      //   enable: false,
      // },
      {
        icon: 'email',
        name: 'Отправлять уведомления на e-mail',
        nameEng: 'Send notifications by e-mail',
        type: 'SEND_EMAIL_MESSAGE',
        enable: false,
      },
    ],
  },

  getters: {
    getNotificationsSettings: (state) => state.notifications,
  },

mutations: {
  setNotificationsSettings(state, notifications) {
    state.notifications.forEach((el) => {
      switch (el.type) {
        case 'POST': el.enable = notifications.enablePost; break;
        case 'POST_COMMENT': el.enable = notifications.enablePostComment; break;
        case 'COMMENT_COMMENT': el.enable = notifications.enableCommentComment; break;
        case 'FRIEND_REQUEST': el.enable = notifications.enableFriendRequest; break;
        case 'MESSAGE': el.enable = notifications.enableMessage; break;
        case 'FRIEND_BIRTHDAY': el.enable = notifications.enableFriendBirthday; break;
        case 'SEND_EMAIL_MESSAGE': el.enable = notifications.enableSendEmailMessage;
      }
    });
  },
},

  actions: {
    passwordRecovery(context, email) {
      return auth.recoveryPassword(email);
    },

    passwordSet(context, { password, secret }) {
      return auth.setPassword({ password }, secret);
    },

    async changeEmail(context, data) {
      await auth.changeEmail(data);
    },

    async changePassword(context, data) {
      await auth.changePassword(data);
    },

    async changeNotifications({ dispatch }, data) {
      try {
        notifications.changeSettings(data);

        dispatch(
          'global/alert/setAlert',
          { status: 'success', text: 'Настройки уведомления изменены' },
          { root: true }
        );
      } finally {
        dispatch('apiNotificationsSettings');
      }
    },

    async apiNotificationsSettings({ commit }) {
      const response = await notifications.getSettings();
      commit('setNotificationsSettings', response.data);
    },
  },
};
