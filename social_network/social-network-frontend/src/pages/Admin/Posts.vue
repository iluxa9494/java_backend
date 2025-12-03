<!-- eslint-disable max-len -->
<template>
  <div>
    <div v-if="statsDataPosts" class="admin__pages-posts panel">
      <div class="panel-publications__top">
        <img src="/public/static/img/admin/admin_3.png" alt="image.png">
        <div class="panel-publications__info">
          <span>Публикаций за все время:</span>
          <span>{{ statsDataPosts?.count }}</span>
        </div>
      </div>
      <div class="panel__graphics first__graph">
        <div class="panel__graphics-left">
          <span class="panel-dynamics__title">Динамика прироста (год)</span>
          <div class="panel-dynamics__graph">
            <v-chart class="chart" :option="dynamics" autoresize />
          </div>
        </div>
      </div>
      <div class="panel__graphics">
        <div class="panel__graphics-left">
          <span class="panel-dynamics__title">Время публикации (суточная диаграмма)</span>
          <div class="panel-dynamics__graph">
            <v-chart class="chart" :option="dynamicsPosts" autoresize />
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="statisticsError" class="statistics-error">
      <h3 class="statistics-error__title">404. Данные не найдены.</h3>
      <p class="statistics-error__status">В данный момент сервер не может получить данные.</p>
      <div class="statistics-error__image"></div>
      <button class="statistics-error__button" @click.prevent="loadData">Попробовать снова</button>
    </div>
    <div v-else-if="!statsDataPosts" class="admin__pages-users panel loading__info">
      Загружаем данные статистики...
    </div>
  </div>
</template>

<script>
import {
  ref, computed, defineComponent, onMounted, 
  // provide,
} from 'vue';
// import { use } from 'echarts/core';
// import { CanvasRenderer } from 'echarts/renderers';
// import { PieChart, BarChart, LineChart } from 'echarts/charts';
// import {
//   TitleComponent,
//   TooltipComponent,
//   LegendComponent,
//   GridComponent,
// } from 'echarts/components';
// import VChart, { THEME_KEY } from 'vue-echarts';
import axios from 'axios';
import moment from 'moment';

// use([
//   CanvasRenderer,
//   PieChart,
//   BarChart,
//   LineChart,
//   TitleComponent,
//   TooltipComponent,
//   LegendComponent,
//   GridComponent,
// ]);

export default defineComponent({
  name: 'AdminPosts',
  components: {
    // VChart,
  },

  setup() {
    const statsDataPosts = ref(null);
    const statisticsError = ref(false);
    const statisticsLoading = ref(false);

    const dynamics = computed(() => {
      const months = ['Янв.', 'Фев.', 'Март', 'Апр.', 'Май', 'Июнь', 'Июль', 'Авг.', 'Сент.', 'Окт.', 'Нояб.', 'Дек.'];
      const xAxisData = statsDataPosts.value?.countPerMonth.map((item) => {
        const date = new Date(item.date);
        return months[date.getMonth()];
      });
      const seriesData = statsDataPosts.value?.countPerMonth.map((item) => item.count);

      return {
        tooltip: {
          trigger: 'axis',
        },
        xAxis: {
          data: xAxisData,
          splitLine: {
            show: true,
          },
        },
        yAxis: {
          splitLine: {
            show: true,
          },
        },
        series: [
          {
            data: seriesData,
            type: 'line',
            smooth: true,
            color: '#434257',
            position: 'right',
          },
        ],
      };
    });
    const dynamicsPosts = computed(() => ({
      tooltip: {
        trigger: 'axis',
      },
      xAxis: {
        type: 'category',
        data: statsDataPosts.value?.countPerHours.map(({ date }) => date.slice(11, 13)),
        splitLine: {
          show: true,
        },
      },
      yAxis: {},
      series: [
        {
          data: statsDataPosts.value?.countPerHours.map(({ count }) => count),
          type: 'bar',
          smooth: true,
          barWidth: '13px',
          color: '#434257',
        },
      ],
    }));

    const loadData = () => {
      const today = moment();
      const formattedToday = today.format('YYYY-MM-DD');
      const lastDayOfYear = today.endOf('year').format('YYYY-MM-DD');
      const firstMonthOfYear = today.startOf('year').format('YYYY-MM-DD');

      setTimeout(() => {
        axios.get(`/admin-console/statistic/post?date=${formattedToday}T00:00:00.735Z&firstMonth=${firstMonthOfYear}T00:00:00.735Z&lastMonth=${lastDayOfYear}T00:00:00.735Z`)
          .then((response) => {
            statsDataPosts.value = response.data;
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

    const provideTheme = () => {
      // provide(THEME_KEY, 'default');
    };

    onMounted(() => {
      loadData();
      provideTheme();
    });

    return {
      statsDataPosts,
      statisticsError,
      statisticsLoading,
      dynamics,
      dynamicsPosts,
      provideTheme,
    };
  },
});
</script>
