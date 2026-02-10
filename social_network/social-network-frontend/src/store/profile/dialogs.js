import dialogsApi from '@/requests/dialogs';

export default {
  namespaced: true,
  state: {
    dialogs: [],
    unreadedMessages: 0, // total unreaded
    lastMessages: {
      messages: [],
      totalPages: null,
    },
    nextMessages: [],
    newMessage: {},
    mySubmitMessage: {},
    allMessages: [],
    totalNextMessages: null,
    totalLastMessages: null,
    dialogsLoaded: false,
    activeDialogId: null,
    oldLastKnownMessageId: null,
    isHistoryEndReached: false,
  },
  getters: {
    getDialogs: (state) => state.dialogs,
    getNewMessage: (state) => state.newMessage,
    getSubmitMessage: (state) => state.mySubmitMessage,
    getNextMessages: (state) => state.nextMessages,
    getLastMessages: (state) => state.lastMessages,
    oldestKnownMessageId: (state) => (state.messages),
    activeDialog: (state) => state.dialogs.find((el) => el.id == state.activeDialogId),
    getActiveDialogId: (state) => state.activeDialogId,
    dialogsLoaded: (state) => state.dialogsLoaded,
    unreadedMessages: (state) => state.unreadedMessages,
    hasOpenedDialog: (state) => (dialogId) => !!state.dialogs.find((el) => el.id == dialogId),
    isHistoryEndReached: (state) => state.isHistoryEndReached,
  },
  mutations: {
    clearLastMessages(state) {
      state.lastMessages.messages = [];
      state.lastMessages.totalPages = null;
    },

    setUnreadedMessages: (state, unread) => (state.unreadedMessages = unread),
    setDialogs: (state, dialogs) => (state.dialogs = dialogs),
    setNewMessage: (state, newMessage) => {
      state.newMessage = newMessage;
      state.allMessages = [...state.allMessages, newMessage];
    },
    setSubmitMessage: (state, submitMessage) => {
      state.mySubmitMessage = submitMessage;
      state.allMessages = [...state.allMessages, submitMessage];
    },
    dialogsLoaded: (state) => (state.dialogsLoaded = true),
    setActiveDialogId: (state, value) => (state.activeDialogId = value),
    addNextMessages: (state, {
      nextMessages,
      total
    }) => {
      state.nextMessages = nextMessages;
      state.nextMessages.sort((a, b) => a.time - b.time);
      state.allMessages = [...state.nextMessages, ...state.allMessages];
      state.totalNextMessages = total;
    },
    addLastMessages: (state, {
      lastMessages,
      total
    }) => {
      state.lastMessages.totalPages = lastMessages.totalPages;
      state.lastMessages.messages = lastMessages.content;
      state.lastMessages.messages.sort((a, b) => a.time - b.time);
      state.allMessages = state.lastMessages.messages;
      state.totalLastMessages = total;
    },

    selectDialog: (state, dialogId) => {
      state.activeId = dialogId;
      state.messages = [];
      state.isHistoryEndReached = false;
    },
    markEndOfHistory: (state) => (state.isHistoryEndReached = true),
  },

  actions: {
    // Создание диалога. Если нет диалога - создаём, если есть - получаем;
    // Запрос: axios.get(`/dialogs/recipientId/${id}`);
    async newDialogs({
      commit
    }, recipientId) {
      try {
        const response = await dialogsApi.newDialogs(recipientId);
        if (!response.data) return;
        const {
          data
        } = response.data || {}; // добавляем проверку на существование content
        commit('selectDialog', data);
      } catch (err) {
        console.log(err);
      }
    },

    // Получаем все диалоги:
    // Запрос: /dialogs?page=0&sort=unreadCount,desc
    async fetchDialogs({
      commit
    }) {
      try {
        const response = await dialogsApi.getDialogs();
        if (response.data?.content?.length === 0) return;

        const data = response.data.content;
        commit('setDialogs', data);
      } catch (err) {
        console.log(err);
      }
    },

    async apiUnreadedMessages({
      commit,
      dispatch
    }) {
      const {
        data
      } = await dialogsApi.unreadedMessages();
      commit('setUnreadedMessages', data);
      await dispatch('fetchDialogs');
    },

    async markReadedMessages({
      dispatch
    }, _, id) {
      await dialogsApi.markReaded(id);
      await dispatch('fetchDialogs');
    },

    // Получаем сообщения диалога по dialogId
    // Запрос: dialogs/messages?recipientId=${id}&page=${countPage}&size=1&sort=time,desc
    async loadLastMessages({
      commit
    }, payload) {
      const {
        id,
        countPage,
        direction,
      } = payload;

      const response = await dialogsApi.getMessages(id, countPage, direction);
      if (!response.data.content) return;
      const lastMessages = response.data;
      commit('clearLastMessages');
      commit('addLastMessages', {
        lastMessages,
        total: response.unreadCount,
      });
    },

    // Запрос: dialogs/messages?recipientId=${id}&page=${countPage}&size=1&sort=time,asc
    async loadNextMessages({
      commit
    }, payload) {
      const {
        id,
        countPage,
        direction,
      } = payload;

      const response = await dialogsApi.getMessages(id, countPage, direction);
      if (!response.data.content) return;
      const nextMessages = response.data.content;
      commit('addNextMessages', {
        nextMessages,
        total: response.unreadCount,
      });
    },
  },
};