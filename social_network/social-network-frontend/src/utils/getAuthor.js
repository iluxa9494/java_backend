import account from '@/requests/account';

export default async (id, authors, dispatch) => {
  let author = authors.find((currentAuthor) => currentAuthor.id === id);
  if (!author) {
    const response = await account.getUser(id);
    author = response.data;
    dispatch('global/authors/addAuthor', author, { root: true });
  }
  return author;
};
