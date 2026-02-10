import axios from 'axios';

export default {
  put({ itemId, data, postId = null }) {
    if (postId) return axios.post(`post/${postId}/comment/${itemId}/like`);
    return axios.post(`post/${itemId}/like`, data);
  },

  delete({ itemId, data, postId = null }) {
    if (postId) return axios.delete(`post/${postId}/comment/${itemId}/like`);
    return axios.delete(`post/${itemId}/like`, data);
  },
};
