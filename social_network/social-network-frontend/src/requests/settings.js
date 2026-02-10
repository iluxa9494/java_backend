import axios from 'axios';

export default {
  setDefaultHeader(key, value) {
    axios.defaults.headers.common[key] = value;
  },

  deleteDefaultHeader(key) {
    delete axios.defaults.headers.common[key];
  },
};
