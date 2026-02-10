// import axios from 'axios';
//
// function serializeQueryParams(params) {
//   return Object.keys(params)
//     .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
//     .join('&');
// }
//
// export default {
//   // Поиск пользователей по запросу
//   users(query) {
//     if (Array.isArray(query)) return axios.get(`account/search?${serializeQueryParams(query)}&isDeleted=false`);
//     if (typeof query === 'string') return axios.get(`account/search?${query}`);
//     if (Array.isArray(query)) return axios.get(`friends?statusCode=FRIEND&${serializeQueryParams(query)}`);
//     if (typeof query === 'string') return axios.get(`friends?statusCode=FRIEND&${query}`);
//
//     throw new TypeError('Недопустимое значение запроса');
//   },
//
//   recomendationFrends(query) {
//     if (Array.isArray(query)) return axios.get(`account/search?${query.join('&')}&isDeleted=false&showFriends=false`)
//   },
//   // Поиск постов по запросу
//   posts(query) {
//     if (Array.isArray(query))
//       return axios.get(`post?${query.join('&')}&sort=time,desc&isDeleted=false`);
//     if (typeof query === 'string') return axios.get(`post?${query}`);
//
//     throw new TypeError('Недопустимое значение запроса');
//   },
// };

import axios from 'axios';

function serializeQueryParams(params) {
  return Object.keys(params)
      .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
      .join('&');
}

export default {
  // Поиск пользователей по запросу
  users(query) {
    console.log('Query: ', query);
    if (Array.isArray(query)) return axios.get(`account/search?${serializeQueryParams(query)}&isDeleted=false`);
    if (typeof query === 'string') return axios.get(`account/search?${query}`);
    if (Array.isArray(query)) return axios.get(`friends?statusCode=FRIEND&${serializeQueryParams(query)}`);
    if (typeof query === 'string') return axios.get(`friends?statusCode=FRIEND&${query}`);

    throw new TypeError('Недопустимое значение запроса');
  },

  recomendationFrends(query) {
    if (Array.isArray(query)) return axios.get(`account/search?${query.join('&')}&isDeleted=false&showFriends=false`)
  },
  // Поиск постов по запросу
  posts(query) {
    if (Array.isArray(query))
      return axios.get(`post?${query.join('&')}&sort=time,desc&isDeleted=false`);
    if (typeof query === 'string') return axios.get(`post?${query}`);

    throw new TypeError('Недопустимое значение запроса');
  },
};



