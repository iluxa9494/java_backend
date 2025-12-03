import {
  createApp
} from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import './settings';
import chat from '@/plugins/websocket';
import vClickOutside from 'click-outside-vue3';
import useVuelidate from '@vuelidate/core';
import vTooltip from 'v-tooltip';
import Vue3Toastify from 'vue3-toastify';
import scroll from './directives/scroll';
import pattern from './directives/pattern';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'
import VueSelect from "vue-select";
import EmojiPicker from "vue3-emoji-picker";
import 'vue3-emoji-picker/css'
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/ru';
import 'dayjs/locale/en';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';

dayjs.extend(relativeTime);
dayjs.locale('ru');
dayjs.extend(utc)
dayjs.extend(timezone)
dayjs.tz.guess() 

const app = createApp(App);

app
  .use(router)
  .use(store)
  .use(chat, {
    // server: 'http://89.111.155.206'
    // server: 'localhost:8080'
    server: '/ws'
  })
  .use(vClickOutside)
  .use(useVuelidate)
  .use(vTooltip)
  .use(Vue3Toastify, )
  .directive('scroll', scroll)
  .directive('pattern', pattern)
  .component('VueDatePicker', VueDatePicker)
  .component("v-select", VueSelect)
  .component("EmojiPicker", EmojiPicker)
  .mount('#app')