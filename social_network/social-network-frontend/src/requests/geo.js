import axios from 'axios';

export default {
  getCountries() {
    return axios.get('/geo/country');
  },

  getSities(countryId) {
    return axios.get(`/geo/country/${countryId}/city`);
  },
};
