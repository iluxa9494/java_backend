<!-- eslint-disable max-len -->
<template>
  <div>
    <ul v-if="statsDataAccount || statsDataPosts || statsDataComment || statsDataLikes" class="admin__pages-list">
      <li class="admin__pages-item">
        <div class="admin__pages-img">
          <img src="/public/static/img/admin/admin_1.png" alt="Users.png">
        </div>
        <div class="admin__pages-info">
          <h4 class="admin__pages-title">Пользователей зарегестрировано</h4>
          <span class="admin__pages-statistic">{{ statsDataAccount?.count }}</span>
        </div>
      </li>
      <li class="admin__pages-item">
        <div class="admin__pages-img">
          <img src="/public/static/img/admin/admin_3.png" alt="Publication.png">
        </div>
        <div class="admin__pages-info">
          <h4 class="admin__pages-title">Публикаций создано</h4>
          <span class="admin__pages-statistic">{{ statsDataPosts?.count }}</span>
        </div>
      </li>
      <li class="admin__pages-item">
        <div class="admin__pages-img">
          <img src="/public/static/img/admin/admin_2.png" alt="Comments.png">
        </div>
        <div class="admin__pages-info">
          <h4 class="admin__pages-title">Комментариев оставлено</h4>
          <span class="admin__pages-statistic">{{ statsDataComment?.count }}</span>
        </div>
      </li>
      <li class="admin__pages-item">
        <div class="admin__pages-img">
          <img src="/public/static/img/admin/admin_4.png" alt="Likes.png">
        </div>
        <div class="admin__pages-info">
          <h4 class="admin__pages-title">Лайков поставлено</h4>
          <span class="admin__pages-statistic">{{ statsDataLikes?.count }}</span>
        </div>
      </li>
    </ul>
    <div v-else-if="statisticsError" class="statistics-error">
      <h3 class="statistics-error__title">404. Данные не найдены.</h3>
      <p class="statistics-error__status">В данный момент сервер не может получить данные.</p>
      <div class="statistics-error__image"></div>
      <button class="statistics-error__button" @click.prevent="loadData">Попробовать снова</button>
    </div>
    <div v-else-if="!statsDataAccount || !statsDataPosts || !statsDataComment || !statsDataLikes"
      class="admin__pages-users panel loading__info">
      Загружаем данные статистики...
    </div>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue';
import axios from 'axios';
import moment from 'moment';

export default {
  name: 'AdminStatistics',

  setup() {
    const statsDataAccount = ref(null);
    const statsDataPosts = ref(null);
    const statsDataComment = ref(null);
    const statsDataLikes = ref(null);
    const statisticsError = ref(false);
    const statisticsLoading = ref(false);

    const dataMapping = {
      account: statsDataAccount,
      post: statsDataPosts,
      comment: statsDataComment,
      like: statsDataLikes,
    };

    const loadStatistics = (type) => {
      const today = moment();
      const formattedToday = today.format('YYYY-MM-DD');
      const lastDayOfYear = today.endOf('year').format('YYYY-MM-DD');
      const firstMonthOfYear = today.startOf('year').format('YYYY-MM-DD');
      const url = `/admin-console/statistic/${type}?date=${formattedToday}T00:00:00.735Z&firstMonth=${firstMonthOfYear}T00:00:00.735Z&lastMonth=${lastDayOfYear}T00:00:00.735Z`;

      setTimeout(() => {
        axios.get(url)
          .then((response) => {
            dataMapping[type].value = response.data;
          })
          .catch(() => {
            statisticsError.value = true;
            statisticsLoading.value = false;
          })
          .then(() => {
            statisticsLoading.value = false;
          });
      }, 1000);
    };

    onMounted(() => {
      loadStatistics('account');
      loadStatistics('post');
      loadStatistics('comment');
      loadStatistics('like');
    });

    return {
      statsDataAccount,
      statsDataPosts,
      statsDataComment,
      statsDataLikes,
      statisticsError,
      statisticsLoading,
    };
  },
};
</script>
