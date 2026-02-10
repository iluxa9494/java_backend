import friends from '@/requests/friends';

export default {
  namespaced: true,
  actions: {
    async apiBlockedUser({
      dispatch
    }, data) {
      const {
        id,
        searchQuery = {}
      } = data;
      const response = await friends.blockedUser(id);
      dispatch(
        'global/alert/setAlert', {
          status: 'success',
          text: 'Пользователь заблокирован'
        }, {
          root: true
        }
      );
      dispatch('profile/friends/apiFriends', "", {
        root: true
      });
      dispatch('global/search/searchUsers', {
        payload: searchQuery
      }, {
        root: true
      });
      dispatch('global/search/getLastSearchUsersRequest', {
        root: true,
      });
      console.log('TCL: apiBlockedUser -> response', response);
    },

    async apiUnblockUser({
      dispatch
    }, data) {
      const {
        id,
        searchQuery = {}
      } = data;
      const response = await friends.unblockUser(id);
      dispatch(
        'global/alert/setAlert', {
          status: 'success',
          text: 'Пользователь разблокирован'
        }, {
          root: true
        }
      );
      dispatch('profile/friends/apiFriends', "", {
        root: true
      });
      dispatch('global/search/searchUsers', {
        payload: searchQuery
      }, {
        root: true
      });
      dispatch('global/search/getLastSearchUsersRequest', {
        root: true,
      });
      console.log('TCL: apiUnblockUser -> response', response);
    },
  },
};
