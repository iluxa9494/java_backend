import geo from '@/requests/geo';

export default {
  namespaced: true,

  state: {
    countries: [],
    cities: {},
  },

  mutations: {
    setCountries: (state, payload) => {
      state.countries = payload;
    },
    setCity: (state, payload) => {
      state.cities = {
        ...state.cities,
        [payload[0].countryId]: payload,
      };
    },
  },

  actions: {
    async apiGeo({ commit }) {
      const countriesRequest = await geo.getCountries();
      const countries = countriesRequest.data;
      commit('setCountries', countries);

      await Promise.all(
        countries.map(async (country) => {
          const citiesRequest = await geo.getSities(country.id);
          const city = citiesRequest.data;
          commit('setCity', city);
        })
      );
    },
  },
};
