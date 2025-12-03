import axios from 'axios';

export default {
  get(query) {
    if (Array.isArray(query)) return axios.get(`friends?${query.join('&')}`);
    if (typeof query === 'string') return axios.get(`friends?${query}`);

    throw new TypeError('Недопустимое значение запроса');
  },

  delete(id) {
    return axios.delete(`friends/${id}`);
  },

  addFriends(id, isApprove) {
    if (isApprove) {
      return axios.put(`friends/${id}/approve`);
    }
    return axios.post(`friends/${id}/request`);
  },

  addSubscribe(id) {
    return axios.post(`friends/subscribe/${id}`);
  },

  getRequest(query) {
    if (Array.isArray(query)) return axios.get(`friends/request?${query.join('&')}`);
    if (typeof query === 'string') return axios.get(`friends/request?${query}`);

    throw new TypeError('Недопустимое значение запроса');
  },

  getRecommendations(query) {
    if (Array.isArray(query)) return axios.get(`friends/recommendations?${query.join('&')}`);
    if (typeof query === 'string') return axios.get(`friends/recommendations?${query}`);

    throw new TypeError('Недопустимое значение запроса');
  },

  blockedUser(id) {
    return axios.put(`friends/block/${id}`);
  },

  unblockUser(id) {
    return axios.put(`friends/unblock/${id}`);
  },

  friendRequestsCount() {
    return axios.get('/friends/count');
  },

  friendSearch(firstName, statusCode) {
    return axios.get(`account/search/statusCode?firstName=${firstName}&statusCode=${statusCode}`);
  }
};
