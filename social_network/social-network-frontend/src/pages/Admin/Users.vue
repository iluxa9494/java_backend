<template>
  <div>
    <div class="admin__pages-users panel" v-if="statsDataUsers">
      <div class="panel-publications__top">
        <img src="/public/static/img/admin/admin_1.png" alt="Users.png">
        <div class="panel-publications__info">
          <span>Пользователей зарегистрировано:</span>
          <span v-show="statsDataUsers.count">{{ statsDataUsers.count }}</span>
        </div>
      </div>
      <div class="panel__graphics">
        <div class="panel__graphics-left first__graph">
          <span class="panel-dynamics__title">Динамика прироста (в год)</span>
          <div class="panel-dynamics__graph" v-show="dynamics">
            <v-chart class="chart" :option="dynamics" autoresize />
          </div>
        </div>
      </div>
      <div class="panel__graphics-circle">
        <div class="panel__graphics-circle-left first__circle" v-show="circleAge">
          <span class="panel-dynamics__title circle__title">Распределение по возрасту</span>
          <v-chart class="chart" :option="circleAge" />
        </div>
        <!-- <div class="panel__graphics-circle-left">
          <span class="panel-dynamics__title">Распределение по полу</span>
          <v-chart class="chart" :option="gender" />
        </div> -->
      </div>
    </div>
    <div v-else-if="statisticsError" class="statistics-error">
      <h3 class="statistics-error__title">404. Данные не найдены.</h3>
      <p class="statistics-error__status">В данный момент сервер не может получить данные.</p>
      <div class="statistics-error__image"></div>
      <button class="statistics-error__button" @click.prevent="loadData">Попробовать снова</button>
    </div>
    <div v-else-if="!statsDataUsers" class="admin__pages-users panel loading__info">
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
// import SpinnerVue from '@/components/Spinner.vue';

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
  name: 'AdminUsers',

  components: {
    // VChart,
    // SpinnerVue,
  },

  setup() {
    const statsDataUsers = ref(null);
    const statisticsError = ref(false);
    const statisticsLoading = ref(false);

    const dynamics = computed(() => {
      const months = ['Янв.', 'Фев.', 'Март', 'Апр.', 'Май', 'Июнь', 'Июль', 'Авг.', 'Сент.', 'Окт.', 'Нояб.', 'Дек.'];
      const xAxisData = statsDataUsers.value?.countPerMonth.map((item) => {
        const date = new Date(item.date);
        return months[date.getMonth()];
      });
      const seriesData = statsDataUsers.value?.countPerMonth.map((item) => item.count);

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
    const circleAge = computed(() => {
      const ageRanges = [
        { min: 13, max: 23 },
        { min: 26, max: 32 },
        { min: 37, max: 48 },
        { min: 51, max: 59 },
        { min: 60, max: 67 },
        { min: 70, max: 89 },
      ];
      const groupedData = [];
      ageRanges.forEach((range) => {
        // eslint-disable-next-line max-len
        const ageGroup = statsDataUsers.value?.countPerAge?.filter((item) => item.age >= range.min && item.age <= range.max);
        const totalCount = ageGroup?.reduce((acc, item) => acc + item.count, 0);
        groupedData.push({
          name: `${range.min}-${range.max} лет`,
          value: totalCount,
        });
      });

      // Группируем данные для неизвестного возраста
      // eslint-disable-next-line max-len
      const unknownAgeGroup = statsDataUsers.value?.countPerAge?.filter((item) => item.age === null);
      if (unknownAgeGroup.length > 0) {
        const totalCount = unknownAgeGroup?.reduce((acc, item) => acc + item.count, 0);
        groupedData.push({
          name: 'не указан',
          value: totalCount,
        });
      }
      return {
        tooltip: {
          trigger: 'item',
        },
        legend: {
          top: '0',
          left: 'left',
        },
        series: [
          {
            type: 'pie',
            radius: ['60%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center',
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 13,
                fontWeight: 'bold',
                formatter: '{b}: ({d}%)',
              },
            },
            labelLine: {
              show: false,
            },
            data: groupedData,
          },
        ],
      };
    });

    const loadData = () => {
      const today = moment();
      const formattedToday = today.format('YYYY-MM-DD');
      const lastDayOfYear = today.endOf('year').format('YYYY-MM-DD');
      const firstMonthOfYear = today.startOf('year').format('YYYY-MM-DD');

      setTimeout(() => {
        axios.get(`/admin-console/statistic/account?date=${formattedToday}T00:00:00.735Z&firstMonth=${firstMonthOfYear}T00:00:00.735Z&lastMonth=${lastDayOfYear}T00:00:00.735Z`)
          .then((response) => {
            statsDataUsers.value = response.data;
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

    // const provideTheme = () => {
    //   provide(THEME_KEY, 'default');
    // };

    onMounted(() => {
      loadData();
      // provideTheme();
    });

    return {
      statsDataUsers,
      statisticsError,
      statisticsLoading,
      dynamics,
      circleAge,
    };
  },
});
</script>
