<!--<template>-->
<!--  <div v-if="weather" class="weather">-->
<!--    <h4 class="weather__city">{{ weather.name }}</h4>-->
<!--    <div class="weather__top">-->
<!--      <div class="weather__clouds">-->
<!--        <img-->
<!--          :src="`https://openweathermap.org/img/wn/${weather.icon}.png`"-->
<!--          :alt="weather.icon"-->
<!--        />-->
<!--        <span class="weather__temp">{{ weather.temp }}°C</span>-->
<!--      </div>-->
<!--      <div>-->
<!--        <div class="weather__window">{{ getNameByIdWeather[0].text }}</div>-->
<!--        <div class="weather__humidity">-->
<!--          {{ translationsLang.weatherHumidity }} {{ weather.humidity }}%-->
<!--        </div>-->
<!--        <div class="weather__humidity">-->
<!--          {{ translationsLang.weatherWind }} {{ weather.windSpeed }}м/с-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
<!--    <div class="weather__minmax">-->
<!--      <div>-->
<!--        {{ translationsLang.weatherMinTemp }}-->
<!--        <span>{{ weather.temp_min }}°C</span>-->
<!--      </div>-->
<!--      <div>-->
<!--        {{ translationsLang.weatherMaxTemp }}-->
<!--        <span>{{ weather.temp_max }}°C</span>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--  <div class="weather" v-else>-->
<!--    <p>{{ translationsLang.weatherLoading }}</p>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import { ref, computed, onMounted } from "vue";-->
<!--import useTranslations from "@/composables/useTranslations";-->
<!--import translateWeather from "@/utils/translateWeather";-->

<!--export default {-->
<!--  name: "WeatherBlock",-->

<!--  setup() {-->
<!--    const weather = ref(null);-->

<!--    const { translationsLang } = useTranslations();-->

<!--    const getNameByIdWeather = computed(() =>-->
<!--        translateWeather.filter((item) => item.id === weather.value.id)-->
<!--    );-->

<!--    const OPENCAGE_API_KEY = '4e527b718a0d492799d21247c6af9701';-->

<!--    const getWeather = () => {-->
<!--      fetch('https://api.ipify.org?format=json')-->
<!--          .then(response => response.json())-->
<!--          .then(data => {-->
<!--            const ipAddress = data.ip;-->

<!--            fetch(`https://api.opencagedata.com/geocode/v1/json?q=${ipAddress}&key=${OPENCAGE_API_KEY}`)-->
<!--                .then(response => response.json())-->
<!--                .then(locationData => {-->
<!--                  const coordinates = locationData.results[0]?.geometry;-->
<!--                  if (coordinates) {-->
<!--                    const lat = coordinates.lat;-->
<!--                    const lon = coordinates.lng;-->

<!--                    const weatherUrl = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=metric&appid=5308edc68a042a4788ed4de34ae08480`;-->

<!--                    fetch(weatherUrl)-->
<!--                        .then(response => response.json())-->
<!--                        .then(weatherData => {-->
<!--                          weather.value = {-->
<!--                            temp: Math.round(weatherData.main.temp),-->
<!--                            temp_min: Math.round(weatherData.main.temp_min),-->
<!--                            temp_max: Math.round(weatherData.main.temp_max),-->
<!--                            cloudiness: weatherData.clouds.all,-->
<!--                            windSpeed: weatherData.wind.speed,-->
<!--                            name: weatherData.name,-->
<!--                            icon: weatherData.weather[0].icon,-->
<!--                            humidity: weatherData.main.humidity,-->
<!--                            id: weatherData.weather[0].id,-->
<!--                          };-->
<!--                        })-->
<!--                        .catch(error => console.log(error));-->
<!--                  } else {-->
<!--                    console.error('Нет данных о координатах');-->
<!--                  }-->
<!--                })-->
<!--                .catch(error => console.log(error));-->
<!--          })-->
<!--          .catch(error => console.log(error));-->
<!--    };-->


<!--    onMounted(() => {-->
<!--      getWeather();-->
<!--    });-->

<!--    return {-->
<!--      weather,-->
<!--      translationsLang,-->
<!--      getNameByIdWeather,-->
<!--      getWeather,-->
<!--    };-->
<!--  },-->
<!--};-->
<!--</script>-->

<!--<style lang="stylus">-->
<!--@import '@/assets/stylus/base/vars.styl';-->

<!--.weather-->
<!--  display flex-->
<!--  align-items center-->
<!--  flex-direction column-->
<!--  justify-content center-->
<!--  color ui-cl-color-grey-color-->
<!--  &__city-->
<!--    font-size 20px-->
<!--    text-transform uppercase-->
<!--    font-weight 900-->
<!--    margin-bottom 10px-->
<!--  &__title-->
<!--    color ui-cl-color-grey-color-->
<!--  &__top-->
<!--    display flex-->
<!--    align-items center-->
<!--    justify-content flex-start-->
<!--    gap 5px-->
<!--    font-size font-size-small-medium-->
<!--    margin-bottom 10px-->
<!--  &__humidity-->
<!--    color #a4a4a4-->
<!--  &__temp-->
<!--    font-size 24px-->
<!--    font-weight font-weight-bold-->
<!--  &__clouds-->
<!--    display flex-->
<!--    align-items center-->
<!--    gap 5px-->
<!--    margin-right 15px-->
<!--  &__minmax-->
<!--    display flex-->
<!--    align-items center-->
<!--    gap 10px-->
<!--    font-size font-size-small-medium-->
<!--    color #000-->
<!--    span-->
<!--      display block-->
<!--      color #a4a4a4-->

<!--@media (min-width: 320px) and (max-width: 768px)-->
<!--  .weather-->
<!--    display none-->
<!--</style>-->

<template>
  <div v-if="weather" class="weather">
    <h4 class="weather__city">{{ weather.name }}</h4>
    <div class="weather__top">
      <div class="weather__clouds">
        <img
            :src="`https://openweathermap.org/img/wn/${weather.icon}.png`"
            :alt="weather.description"
        />
        <span class="weather__temp">{{ weather.temp }}°C</span>
      </div>
      <div>
        <div class="weather__window">{{ getNameByIdWeather[0]?.text || weather.description }}</div>
        <div class="weather__humidity">
          {{ translationsLang.weatherHumidity }} {{ weather.humidity }}%
        </div>
        <div class="weather__humidity">
          {{ translationsLang.weatherWind }} {{ weather.windSpeed }}м/с
        </div>
      </div>
    </div>
    <div class="weather__minmax">
      <div>
        {{ translationsLang.weatherMinTemp }}
        <span>{{ weather.temp_min }}°C</span>
      </div>
      <div>
        {{ translationsLang.weatherMaxTemp }}
        <span>{{ weather.temp_max }}°C</span>
      </div>
    </div>
  </div>
  <div class="weather" v-else>
    <p>{{ translationsLang.weatherLoading }}</p>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import useTranslations from "@/composables/useTranslations";
import translateWeather from "@/utils/translateWeather";

export default {
  name: "WeatherBlock",

  setup() {
    const weather = ref(null);
    const { translationsLang } = useTranslations();

    const getNameByIdWeather = computed(() =>
        translateWeather.filter((item) => item.id === weather.value?.id)
    );

    const getWeather = () => {
      fetch("http://ip-api.com/json/")
          .then((response) => response.json())
          .then((locationData) => {
            if (locationData.status === "success") {
              const { lat, lon, city } = locationData;

              const weatherUrl = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=metric&appid=5308edc68a042a4788ed4de34ae08480`;

              fetch(weatherUrl)
                  .then((response) => response.json())
                  .then((weatherData) => {
                    weather.value = {
                      temp: Math.round(weatherData.main.temp),
                      temp_min: Math.round(weatherData.main.temp_min),
                      temp_max: Math.round(weatherData.main.temp_max),
                      humidity: weatherData.main.humidity,
                      windSpeed: weatherData.wind.speed,
                      icon: weatherData.weather[0].icon,
                      description: weatherData.weather[0].description,
                      id: weatherData.weather[0].id,
                      name: city || weatherData.name,
                    };
                  })
                  .catch((error) => console.error("Ошибка получения данных о погоде:", error));
            } else {
              console.error("Не удалось определить местоположение");
            }
          })
          .catch((error) => console.error("Ошибка при запросе местоположения:", error));
    };

    onMounted(() => {
      getWeather();
    });

    return {
      weather,
      translationsLang,
      getNameByIdWeather,
    };
  },
};
</script>

<style lang="stylus">
@import "@/assets/stylus/base/vars.styl";

.weather
  display flex
  align-items center
  flex-direction column
  justify-content center
  color ui-cl-color-grey-color
  &__city
    font-size 20px
    text-transform uppercase
    font-weight 900
    margin-bottom 10px
  &__title
    color ui-cl-color-grey-color
  &__top
    display flex
    align-items center
    justify-content flex-start
    gap 5px
    font-size font-size-small-medium
    margin-bottom 10px
  &__humidity
    color #a4a4a4
  &__temp
    font-size 24px
    font-weight font-weight-bold
  &__clouds
    display flex
    align-items center
    gap 5px
    margin-right 15px
  &__minmax
    display flex
    align-items center
    gap 10px
    font-size font-size-small-medium
    color #000
    span
      display block
      color #a4a4a4

@media (min-width: 320px) and (max-width: 768px)
  .weather
    display none
</style>
