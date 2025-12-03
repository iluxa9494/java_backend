import axios from 'axios';

export default {
  get(countPage, direction) {
    return axios.get(`notifications/page?page=${countPage}&sort=sentTime,${direction}`);
  },

  readed() {
    return axios.put('/notifications/readed');
  },

  getLength() {
    return axios.get('notifications/count');
  },

  changeSettings(data) {
    return axios.put('notifications/settings', data);
  },

  getSettings() {
    return axios.get('notifications/settings');
  },
};
