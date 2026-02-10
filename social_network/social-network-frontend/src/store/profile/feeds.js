import posts from '@/requests/posts';
import createPagination from '@/utils/createPagination';
import getAuthor from '@/utils/getAuthor';

export default {
  namespaced: true,
  state: {
    feeds: [],
    feedsPagination: {},
  },

  getters: {
    getFeeds: (state) => state.feeds,
    getPagitnation: (state) => state.feedsPagination,
    authors: (state, getters, rootState) => rootState.global.authors.authors,
    getloading: (state, getters, rootstate) => rootstate.global.status.loading,
  },

  mutations: {
    setFeeds: (state, feeds) => {
      const currentFeeds = feeds.filter((currentFeed) => {
        return !state.feeds.find((feed) => feed.id === currentFeed.id);
      });

      state.feeds = [...state.feeds, ...currentFeeds];
    },

    clearFeeds: (state) => {
      state.feeds = [];
    },

    deleteFeeds: (state, id) => {
      const currentFeeds = state.feeds.filter((feed) => feed.id !== id);
      state.feeds = currentFeeds;
    },

    putFeeds: (state, payload) => {
      const postIndex = state.feeds.indexOf(state.feeds.find((feed) => feed.id === payload.postId));
      state.feeds[postIndex] = {
        ...state.feeds[postIndex],
        imagePath: payload.imagePath,
        postText: payload.postText,
        tags: payload.tags.map(tag => ({ id: tag.id, name: tag.name })),
        title: payload.title,
        timeChanged: new Date(),
      };
      state.feeds = [...state.feeds];
    },

    setFeedsPagination: (state, pagination) => (state.feedsPagination = pagination),

    setFeedsById: (state, payload) =>
      (state.feeds[state.feeds.indexOf(state.feeds.find((el) => el.id === payload.id))] = payload),
  },

  actions: {
    async apiFeeds({ commit, dispatch, getters }, payload) {
      if (getters.getloading) return;
      commit('global/status/removeError', null, { root: true });
      commit('global/status/setLoading', null, { root: true });
      try {
        const response = await posts.getFeeds(payload);
        const { content } = response.data;

        const feeds = await content.reduce(async (acc, post) => {
          const accumulator = await acc;
          const author = await getAuthor(post.authorId, getters.authors, dispatch);

          post = { ...post, author };
          accumulator.push(post);

          return accumulator;
        }, []);

        commit('setFeeds', feeds);
        const pagination = createPagination(response);
        commit('setFeedsPagination', pagination);
      } catch (error) {
        const errorMessage = error.message || '';
        commit('global/status/setError', errorMessage, { root: true });
      } finally {
        commit('global/status/removeLoading', null, { root: true });
      }
    },

    async apiFeedsById({ commit }, postId) {
      const response = await posts.getById(postId);
      commit('setFeedsById', response.data);
    },

    async actionsFeed({ dispatch, commit, rootGetters }, payload) {
      const isPUT = payload.edit && !!payload.edit;
      // const publishDate = payload.publishDate ? '?publishDate=' + payload.publishDate : '';
      const data = {
        title: payload.title,
        postText: payload.postText,
        publishDate: payload.publishDate,
        tags: payload.tags == null ? console.log('пусто') : payload.tags.map(tag => ({ id: tag.id, name: tag.name })),
        imagePath: payload.imagePath,
        id: payload.postId,
      };

      if (isPUT) {
        await posts.push(data, isPUT);
      } else {
        await posts.push(data, isPUT, payload.postId);
      }

      // await posts.push(data, isPUT, payload.postId);

      // if (isPUT) {
      //   dispatch('users/info/apiWallById', payload.postId, { root: true });
      // }

      if (payload.route === 'News') {
        if (isPUT) commit('putFeeds', payload);
        else {
          commit('clearFeeds');
          dispatch('apiFeeds');
        }
      } else {
        if (isPUT) commit('users/info/putPost', payload, { root: true });
        else {
          console.log(rootGetters['profile/info/getInfo']);
          commit('users/info/clearWall', null, { root: true });
          dispatch(
            'users/info/apiWall',
            { accountIds: payload.id, author: rootGetters['profile/info/getInfo'] },
            { root: true }
          );
        }
      }
    },

    async deleteFeeds({ commit }, payload) {
      await posts.delete(payload.postId);

      if (payload.route === 'News') {
        commit('deleteFeeds', payload.postId);
      } else commit('users/info/deletePost', payload.postId, { root: true });
    },
  },
};
