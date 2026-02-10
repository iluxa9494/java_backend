import search from '@/requests/search';
import router from '@/router';
import createPagination from '@/utils/createPagination';
import createQuery from '@/utils/createQuery';
import getAuthor from '@/utils/getAuthor';

export default {
  namespaced: true,
  state: {
    lastSearchUsersRequest: '',
    lastSearchNewsRequest: '',
    usersQueryParams: {},
    usersQueryParamsRecomend: {},
    newsQueryParams: {},
    searchText: null,
    usersPagination: { total: 20, page: 1, size: 5 },
    usersPaginationRecomend: { total: 20, page: 1, size: 5 },
    newsPagination: { total: 20, page: 1, size: 5 },
    tabs: [
      // {
      //   id: "all",
      //   text: "Всё",
      // },
      {
        id: 'users',
        text: 'Люди',
        textEng: 'People',
      },
      {
        id: 'news',
        text: 'Новости',
        textEng: 'News',
      },
    ],
    tabSelect: 'users',
    result: {
      users: [],
      news: [],
    },
    recomendationUsers: [],
    status: '',
  },
  getters: {
    searchText: (state) => state.searchText,
    tabs: (state) => state.tabs,
    tabSelect: (state) => state.tabSelect,
    getResult: (state) => state.result,
    getResultByIdSearch: (state) => (id) => state.result[id],
    getRecomendationUsers: (state) => state.recomendationUsers,
    getStatus: (state) => state.status,
    getLastSearchUsersRequest: (state) => state.lastSearchUsersRequest,
    getLastSearchNewsRequest: (state) => state.lastSearchNewsRequest,
    getUsersQueryParams: (state) => state.usersQueryParams,
    getUsersQueryParamsRecomend: (state) => state.usersQueryParamsRecomend,
    getNewsQueryParams: (state) => state.newsQueryParams,
    getUsersPagination: (state) => state.usersPagination,
    getUsersPaginationRecomend: (state) => state.usersPaginationRecomend,
    getNewsPagination: (state) => state.newsPagination,
    authors: (state, getters, rootState) => rootState.global.authors.authors,
  },
  mutations: {
    setUsersPagination: (state, pagination) => (state.usersPagination = pagination),
    setUsersPaginationRecomend: (state, pagination) => (state.usersPaginationRecomend = pagination),
    setNewsPagination: (state, pagination) => (state.newsPagination = pagination),
    setSearchText: (state, value) => (state.searchText = value),
    setLastSearchUsersRequest: (state, value) => (state.lastSearchUsersRequest = value),
    setLastSearchNewsRequest: (state, value) => (state.lastSearchNewsRequest = value),
    setUsersQueryParams: (state, value) => (state.usersQueryParams = value),
    setUsersQueryParamsRecomend: (state, value) => (state.usersQueryParamsRecomend = value),
    setNewsQueryParams: (state, value) => (state.newsQueryParams = value),
    setTabSelect: (state, id) => (state.tabSelect = id),
    routePushWithQuery(state, id) {
      const query = {};
      if (id !== 'all') query.tab = id;
      if (state.searchText) query.text = state.searchText;
      router.push({
        name: 'Search',
        query,
      });
    },
    setResult(state, result) {
      state.result[result.id] = result.value;
    },
    setRecomendations(state, data) {
      state.recomendationUsers = data;
    },
  },
  actions: {
    clearSearch({ commit }) {
      commit('setSearchText', '');
      commit('setResult', {
        id: 'users',
        value: [],
      });
      commit('setResult', {
        id: 'news',
        value: [],
      });
    },
    changeTab({ commit }, id) {
      commit('setTabSelect', id);
      commit('routePushWithQuery', id);
    },

    async searchRecomendations({ commit }, { payload, }) {
      const query = createQuery(payload);

      const response = await search.recomendationFrends(query);
      commit('setRecomendations', response.data);
      commit('setUsersQueryParamsRecomend', payload);
      commit('setLastSearchUsersRequest', payload);

      const pagination = createPagination(response);
      commit('setUsersPaginationRecomend', pagination);
    },

    async searchUsers({commit}, { payload }) {
      const query = createQuery(payload);

      const response = await search.users(query);
      commit('setResult', {
        id: 'users',
        value: response.data.content,
      });
      commit('setUsersQueryParams', payload);
      commit('setLastSearchUsersRequest', payload);

      const pagination = createPagination(response);
      commit('setUsersPagination', pagination);
    },

    async searchNews({ commit, getters, dispatch }, payload) {
      const query = createQuery(payload);
      const response = await search.posts(query);

      const { content } = response.data;

      const fullData = await content.reduce(async (acc, post) => {
        const accumulator = await acc;
        const author = await getAuthor(post.authorId, getters.authors, dispatch);

        post = {
          ...post,
          author,
        };

        accumulator.push(post);
        return accumulator;
      }, []);

      const result = fullData.filter((feed) => feed.author !== undefined);

      commit('setResult', {
        id: 'news',
        value: result,
      });
      commit('setNewsQueryParams', payload);
      commit('setLastSearchNewsRequest', `post?${query.join('&')}`);

      const pagination = createPagination(response);
      commit('setNewsPagination', pagination);
    },

    async searchAll({ dispatch, state }, text) {
      const searchQueryUsers = Object.assign({}, state.usersQueryParams, {
        author: text || '',
        size: state.usersPagination.size,
        page: state.usersPagination.page - 1,
      });
      await dispatch('searchUsers', { payload: searchQueryUsers });
      const searchQueryNews = Object.assign({}, state.newsQueryParams, {
        text: text || '',
        size: state.newsPagination.size,
        page: state.newsPagination.page - 1,
      });
      await dispatch('searchNews', searchQueryNews);
    },
  },
};
