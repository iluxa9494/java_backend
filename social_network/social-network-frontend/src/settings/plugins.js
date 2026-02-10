import moment from 'moment';

export default {
  install() {
    const language = JSON.parse(localStorage.getItem('selectedLanguage'))
    if (language) {
      if (language.name === 'Русский') {
        moment.locale('ru')
      } else {
        moment.locale('en')
      }
    } else {
      moment.locale('ru')
    }
  }
}
// import VueSocketIO from 'vue-socket.io';
// Vue.use(chat, { server: 'localhost:8080' }); // <- для локальной разработки. При выгрузке на стэнд закомментировать.
// Vue.use(chat, { server: 'localhost:8099' });