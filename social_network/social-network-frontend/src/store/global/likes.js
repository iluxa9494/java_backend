import likes from '@/requests/likes';

export default {
  namespaced: true,
  actions: {
    async putLike(_, data) {
      await likes.put(data);
    },
    async deleteLike(_, data) {
      await likes.delete(data);
    },
  },
};
