import axios from 'axios';
import router from '@/router';
import store from '@/store';
// import jwtDecode from "jwt-decode";

axios.defaults.headers['content-type'] = 'application/json';
axios.defaults.withCredentials = true;
// axios.defaults.baseURL = '/api/v1/';
// axios.defaults.baseURL = "http://89.111.155.206:8765/api/v1/";
// axios.defaults.baseURL = "http://localhost:8765/api/v1/";
const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
axios.defaults.baseURL = isLocalhost
  ? `${window.location.protocol}//${window.location.hostname}:8765/api/v1/`
  : `${window.location.origin}/projects/social-network/api/v1/`;

// const setAuthToken = (token) => {
//   axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
// };

const token = localStorage.getItem('user-token');

if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

// console.log(jwtDecode(token));



// axios.interceptors.response.use(null, async (error) => {
//   if (error.response && error.response.status === 401) {
//     try {
//       localStorage.removeItem('user-token');
//       store.dispatch("auth/api/refreshToken");

//       // const refreshToken = localStorage.getItem('refresh-token');
//       // console.log(refreshToken)
//       // const response = await axios.post('/refresh', { refreshToken });
//       // const {
//       //   accessToken
//       // } = response.data;
//       // setAuthToken(accessToken);
//       // localStorage.setItem('user-token', accessToken);

//       return axios(error.config);
//     } catch (refreshError) {
//       console.error('Refresh token error', refreshError);
//       store.dispatch('auth/api/logout');
//       return Promise.reject(refreshError);
//     }
//   }

//   const errorMessage = error.response.data.error_description || '';

//   if (error.response) {
//     // if (error.response.status === 403) {
//     //   console.log("Have to refresh Token");
//     //   store.dispatch("auth/api/refreshToken");
//     // }
//     if (error.response.status === 400) {
//       store.dispatch('global/alert/setAlert', {
//         status: 'error',
//         text: `Ошибка ${error.response.status}: ${errorMessage}`,
//       });
//     }
//     store.dispatch('global/alert/setAlert', {
//       status: 'error',
//       text: `Ошибка ${error.response.status}: ${errorMessage}`,
//     });
//   } else if (error.request) {
//     store.dispatch('global/alert/setAlert', {
//       status: 'error',
//       text: 'Нет ответа от сервера',
//     });
//   } else {
//     store.dispatch('global/alert/setAlert', {
//       status: 'error',
//       text: 'Неизвестная ошибка',
//     });
//   }

//   console.error('Axios error', {
//     error
//   });
//   return Promise.reject(error);
// });

axios.interceptors.response.use(null, (error) => {
  const response = error?.response;
  const status = response?.status;
  const errorMessage = response?.data?.error_description || response?.data?.message || ' ';

  if (response) {
    if (status === 403) {
      console.log("Have to refresh Token");
      store.dispatch("auth/api/refreshToken");
    }
    if (status === 401) {
      localStorage.removeItem('user-token');
      localStorage.removeItem('refresh-token');
      store.commit('auth/api/setToken', null);
      if (router.currentRoute.value.name !== 'Login') {
        router.replace({
          name: 'Login',
          query: {
            redirect: router.currentRoute.value.name || 'News',
          },
        });
      }
    }

    if (status === 400) {
      store.dispatch('global/alert/setAlert', {
        status: 'error',
        text: `Ошибка ${status}: ${errorMessage}`,
      });
    } else if (status && status !== 401) {
      store.dispatch('global/alert/setAlert', {
        status: 'error',
        text: `Ошибка ${status}: ${errorMessage}`,
      });
    }
  } else if (error?.request) {
    store.dispatch('global/alert/setAlert', {
      status: 'error',
      text: 'Нет ответа от сервера',
    });
  } else {
    store.dispatch('global/alert/setAlert', {
      status: 'error',
      text: 'Неизвестная ошибка',
    });
  }

  console.error('Axios error', { error });
  return Promise.reject(error);
});
