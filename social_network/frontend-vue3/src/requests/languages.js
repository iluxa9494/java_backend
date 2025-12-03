import axios from 'axios';

export default {
  get() {
    return axios.get('platform/languages');
  },
};
