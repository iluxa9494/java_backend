import axios from 'axios';

export default {
  get(postId, currentPage = null, commentId = null) {
    if (commentId) {
      return axios.get(
        `post/${postId}/comment/${commentId}/subcomment?sort=time,desc&&isDeleted=false&size=5&page=${
          currentPage === null ? 0 : currentPage + 1
        }`
      );
    }

    return axios.get(
      `post/${postId}/comment?sort=time,desc&size=5&isDeleted=false&page=${
        currentPage === null ? 0 : currentPage + 1
      }`
    );
  },

  add(postId, data) {
    return axios.post(`post/${postId}/comment`, data);
  },

  edit(postId, data) {
    return axios.put(`post/${postId}/comment`, data);
  },

  delete(postId, commentId) {
    return axios.delete(`post/${postId}/comment/${commentId}`);
  },

  recover(postId, commentId) {
    return axios.put(`post/${postId}/comment/${commentId}/recover`);
  },
};
