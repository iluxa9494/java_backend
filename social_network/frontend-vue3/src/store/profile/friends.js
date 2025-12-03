import friends from '@/requests/friends';
import createQuery from '@/utils/createQuery';

const dispatchSetAlert = (dispatch, text) =>
  dispatch('global/alert/setAlert', {
    status: 'success',
    text
  }, {
    root: true
  });

const dispatchSearchUsers = (dispatch, users, searchQuery) => {
  if (!users) {
    users = {
      firstName: null,
      lastName: null,
      ageFrom: null,
      ageTo: null,
      country: null,
      city: null,
    };
  }
  dispatch('global/search/searchUsers', { users, payload: searchQuery }, {
    root: true,
  });
};

export default {
  namespaced: true,
  state: {
    result: {
      friends: [],
      request: [],
      recommendations: [],
    },
    friends: {},
    paginations: {},
    requestsCount: null,
    friendSearch: null,
    loadedCounts: {
      REQUEST_FROM: 0,
      REQUEST_TO: 0,
      FRIEND: 0,
      BLOCKED: 0,
      SUBSCRIBED: 0,
      WATCHING: 0,
    },
    statusCode: null
  },

  getters: {
    getResult: (state) => state.result,
    getResultById: (state) => (id) => state.result[id],
  },

  mutations: {
    setResult: (state, payload) => (state.result[payload.id] = payload.value),
// trying a new way to update localFriends from AI serafum 20241201 19-14
    setFriends: (state, payload) => {
      payload.forEach((user) => {
        const {
          statusCode
        } = user;
        if (!state.friends[statusCode]) {
          state.friends[statusCode] = [];
        }
        const isDouble = state.friends[statusCode].find((currentUser) => currentUser.id === user.id);
        if (!isDouble) {
          state.friends[statusCode].push(user);
        }
      });
    },

    // setFriends(state, payload) {
    //   state.localFriends = { ...state.localFriends, [payload.statusCode]: payload.data };
    // },



    setRequestsCount: (state, payload) => {
      state.requestsCount = payload;
    },

    setFriendSearch: (state, payload) => {
      state.friendSearch = payload;
    },

    resetFriendSearch(state) {
      state.friendSearch = null;
    },

    setPaginations: (state, {
      totalElements,
      totalPages,
      field
    }) => {
      state.paginations[field] = {
        totalElements,
        totalPages
      };
    },

    setLoadedCount: (state, {
      statusCode,
      count
    }) => {
      state.loadedCounts = {
        ...state.loadedCounts,
        [statusCode]: count,
      };
    },
  },

  actions: {
    async apiFriends({
      commit,
      state
    }, {
      statusCode,
      loadMore = false
    }) {
      const query = createQuery({
        statusCode,
        size: loadMore ? state.loadedCounts[statusCode] + 6 : 3
      });
      const {
        data
      } = await friends.get(query);
      const {
        content
      } = data;
      const {
        totalElements,
        totalPages
      } = data;
      commit('setResult', {
        id: 'friends',
        value: content || []
      });
      commit('setFriends', content);
      commit('setPaginations', {
        totalElements,
        totalPages,
        field: statusCode
      });
      if (loadMore) {
        commit('setLoadedCount', {
          statusCode,
          count: state.loadedCounts[statusCode] + 3
        });
      }
    },

    async apiDeleteFriends({
      dispatch
    }, data) {
      const {
        id,
        searchQuery = {},
      } = data;
      await friends.delete(id);
      dispatch('global/search/searchUsers', {
        payload: searchQuery
      }, {
        root: true
      });
      dispatch('global/search/getLastSearchUsersRequest', {
        root: true,
      });
    },

    async apiAddFriends({
      dispatch,
      rootGetters,
    }, {
      id,
      statusCode = "",
      isApprove = false,
      searchQuery = {},
    }) {
      // const _friend = getters.getResult.friends.find((fr) => fr.id === id);
      if (statusCode === 'REQUEST_TO') {
        dispatchSetAlert(dispatch, 'Вы уже отправляли запрос этому пользователю!');
        return;
      }

      if (statusCode === 'REJECTING') {
        dispatchSetAlert(dispatch, 'Этот пользователь заблокировал Вас!');
        return;
      }

      if (statusCode === 'BLOCKED') {
        dispatchSetAlert(dispatch, 'Вы заблокировали этого пользователя!');
        return;
      }

      if (statusCode === 'FRIEND') {
        dispatchSetAlert(dispatch, 'Вы уже друзья с этим пользователем!');
        return;
      }
      const {
        data
      } = await friends.addFriends(id, isApprove);

      if (data && data.message === 'request already sent!') {
        dispatchSetAlert(dispatch, 'Вы уже отправили запрос этому пользователю!');
      } else if (data && data.message === 'Blocked!') {
        dispatchSetAlert(dispatch, 'Этот пользователь заблокировал Вас!');
      } else dispatchSetAlert(dispatch, 'Заявка отправлена');
      dispatch('apiFriends', statusCode);
      dispatchSearchUsers(dispatch, rootGetters['global/search/getLastSearchUsersRequest'], searchQuery);
    },

    async apiSubscribe({
      dispatch,
      rootGetters
    }, data) {
      const { id } = data;
      await friends.addSubscribe(id);
      dispatchSetAlert(dispatch, 'Заявка отправлена');
      dispatch('apiFriends');
      dispatchSearchUsers(dispatch, rootGetters['global/search/getLastSearchUsersRequest']);
    },

    async apiRequest({
      commit
    }, payload) {
      const query = createQuery(payload);
      const {
        data
      } = await friends.getRequest(query);
      commit('setResult', {
        id: 'request',
        value: data
      });
    },

    async apiRecommendations({
      commit
    }, payload) {
      const query = createQuery(payload);
      const {
        data
      } = await friends.getRecommendations(query);
      commit('setResult', {
        id: 'recommendations',
        value: data || []
      });
    },

    async apiRequestsCount({
      commit
    }) {
      const {
        data
      } = await friends.friendRequestsCount();
      commit('setRequestsCount', data);
    },

    async apiFriendSearch({
      commit
    }, {
      firstName,
      statusCode
    }) {
      const {
        data
      } = await friends.friendSearch(firstName, statusCode);
      commit('setFriendSearch', data);
    },
  },
};
