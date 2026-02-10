import axios from 'axios';

export default {
  // Получение текущего пользователя
  get() {
    return axios.get('account/me');
  },
  // Редактирование текущего аккаунта
  edit(data) {
    return axios.put('account/me', data);
  },
  // Удаление текущего пользователя
  delete() {
    return axios.delete('account/me');
  },
  // Получение пользователя по id
  getUser(id) {
    return axios.get(`account/${id}`);
  },
};
