import axios from 'axios';

export default {
  api(data) {
    return axios.post('storage', data, {
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 60000,
    });
  },

  postPhoto(data) {
    return axios.post('post/storagePostPhoto?type=IMAGE', data, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};
