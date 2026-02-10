<!--<template>-->
<!--  <form action="#" class="friends-possible" @submit.prevent="onSearchUsers">-->
<!--    <h4 class="friends-possible__title">-->
<!--      {{ translationsLang.friendsFindParamsTitle }}-->
<!--    </h4>-->

<!--    <div class="friends-search">-->
<!--      <div class="friends-search__row">-->
<!--        <div class="friends-search__block">-->
<!--          <label for="friends-search-name" class="search__label"-->
<!--            >{{ translationsLang.createAccNameField1 }}:</label-->
<!--          >-->
<!--          <input-->
<!--            type="text"-->
<!--            class="search__input"-->
<!--            id="friends-search-name"-->
<!--            v-model="firstName"-->
<!--          />-->
<!--        </div>-->
<!--      </div>-->

<!--      <div class="friends-search__block">-->
<!--        <label class="search__label">{{-->
<!--          translationsLang.friendsFindYearTitle-->
<!--        }}</label>-->
<!--        <div class="search__row">-->
<!--          <select-->
<!--            class="select friends-search__select"-->
<!--            v-model.number="ageFrom"-->
<!--          >-->
<!--            <option value="null" disabled>-->
<!--              {{ translationsLang.friendsFindFrom }}-->
<!--            </option>-->
<!--            <option v-for="age in ageFromArray" :value="age" :key="age">-->
<!--              {{ translationsLang.friendsFindFrom }} {{ age }}-->
<!--            </option>-->
<!--          </select>-->

<!--          <span class="search__age-defis">—</span>-->

<!--          <select class="select friends-search__select" v-model.number="ageTo">-->
<!--            <option value="null" disabled>-->
<!--              {{ translationsLang.friendsFindTo }}-->
<!--            </option>-->
<!--            <option v-for="age in ageToArray" :value="age" :key="age">-->
<!--              {{ translationsLang.friendsFindTo }} {{ age }}-->
<!--            </option>-->
<!--          </select>-->
<!--        </div>-->
<!--      </div>-->

<!--      <div class="friends-search__block">-->
<!--        <label class="search__label">{{-->
<!--          translationsLang.friendsFindRegion-->
<!--        }}</label>-->
<!--        <div class="search__row">-->
<!--          <select class="select friends-search__select" v-model="country">-->
<!--            <option value="null" disabled>-->
<!--              {{ translationsLang.friendsFindCountry }}-->
<!--            </option>-->
<!--            <option v-for="c in countries" :key="c.id" :value="c">-->
<!--              {{ c.title }}-->
<!--            </option>-->
<!--          </select>-->
<!--          <select class="select friends-search__select" v-model="city">-->
<!--            <option value="null" disabled>-->
<!--              {{ translationsLang.friendsFindCity }}-->
<!--            </option>-->
<!--            <option v-for="c in currentCities" :key="c.id" :value="c">-->
<!--              {{ c.title }}-->
<!--            </option>-->
<!--          </select>-->
<!--        </div>-->
<!--      </div>-->

<!--      <div class="friends-search__block">-->
<!--        <label class="search__label checkbox-wrapper">-->
<!--          <span>{{ translationsLang.friendsFindCheckbox }}</span>-->
<!--          <input type="checkbox" class="checkbox" v-model="allUsers" />-->
<!--        </label>-->
<!--      </div>-->
<!--    </div>-->

<!--    <button class="recommend-block__search" type="submit">-->
<!--      {{ translationsLang.recommendBlockBtn }}-->
<!--    </button>-->
<!--  </form>-->
<!--</template>-->

<!--<script>-->
<!--import { ref, computed, watch, onBeforeUnmount } from "vue";-->
<!--import { useStore } from "vuex";-->
<!--import useTranslations from "@/composables/useTranslations";-->

<!--export default {-->
<!--  name: "FriendsSearch",-->

<!--  setup() {-->
<!--    const store = useStore();-->
<!--    const {-->
<!--      state,-->
<!--      dispatch,-->
<!--    } = store;-->
<!--    const firstName = ref(null);-->
<!--    const lastName = ref(null);-->
<!--    const ageFrom = ref(null);-->
<!--    const ageTo = ref(null);-->
<!--    const country = ref(null);-->
<!--    const city = ref(null);-->
<!--    const currentCities = ref([]);-->
<!--    const disabled = ref(false);-->
<!--    const page = ref(1);-->
<!--    const itemSize = ref(5);-->
<!--    const ageFromArray = ref([...Array(100).keys()].slice(5));-->
<!--    const ageToArray = ref([...Array(121).keys()].slice(5));-->
<!--    const allUsers = ref(false);-->

<!--    const { translationsLang } = useTranslations();-->

<!--    const countries = computed(() => state.global.geo.countries);-->
<!--    const cities = computed(() => state.global.geo.cities);-->

<!--    watch(-->
<!--      () => country,-->
<!--      (newVal) => {-->
<!--        if (newVal && newVal.id) currentCities.value = cities[country.value.id];-->
<!--        // else cities.value = [];-->
<!--        city.value = null;-->
<!--      },-->
<!--      { immediate: true }-->
<!--    );-->

<!--    onBeforeUnmount(() => {-->
<!--      dispatch("global/search/clearSearch");-->
<!--    });-->

<!--    const onSearchUsers = () => {-->
<!--      if (-->
<!--        ageFrom.value !== null &&-->
<!--        ageTo.value !== null &&-->
<!--        ageFrom.value >= ageTo.value-->
<!--      ) {-->
<!--        dispatch("global/alert/setAlert", {-->
<!--          status: "error",-->
<!--          text: "Неправильный выбор возрастного интервала!",-->
<!--        });-->
<!--        return;-->
<!--      }-->


<!--      const countryName = country.value ? country.value.title : null;-->
<!--      const cityName = city.value ? city.value.title : null;-->
<!--      dispatch("global/search/searchUsers", {-->
<!--        payload: {-->
<!--          firstName,-->
<!--          lastName,-->
<!--          ageFrom,-->
<!--          ageTo,-->
<!--          country: countryName,-->
<!--          city: cityName,-->
<!--        },-->
<!--        myFriends: !allUsers.value,-->
<!--      });-->
<!--    };-->

<!--    return {-->
<!--      firstName,-->
<!--      lastName,-->
<!--      ageFrom,-->
<!--      ageTo,-->
<!--      country,-->
<!--      city,-->
<!--      currentCities,-->
<!--      disabled,-->
<!--      page,-->
<!--      itemSize,-->
<!--      ageFromArray,-->
<!--      ageToArray,-->
<!--      allUsers,-->
<!--      translationsLang,-->
<!--      countries,-->
<!--      cities,-->
<!--      onSearchUsers,-->
<!--    };-->
<!--  },-->
<!--};-->

<!--</script>-->

<!--<style lang="stylus">-->
<!--@import '@/assets/stylus/base/vars.styl'-->

<!--.checkbox-->
<!--  position relative-->
<!--  border 0-->
<!--  padding 0-->
<!--  height 1px-->
<!--  width 1px-->
<!--  clip rect(1px, 1px, 1px, 1px)-->
<!--  cursor pointer-->

<!--  &::before-->
<!--    content ''-->
<!--    position absolute-->
<!--    right 0-->
<!--    width 20px-->
<!--    height 20px-->
<!--    border 2px solid ui-cl-color-eucalypt-->

<!--  &::after-->
<!--    content: ''-->
<!--    transform scale(1.3)-->
<!--    opacity 0-->
<!--    transition transform 200ms linear, opacity  100ms linear-->

<!--  &:checked::after-->
<!--    content '\2714'-->
<!--    position absolute-->
<!--    right -2px-->
<!--    top -11px-->
<!--    font-size 27px-->
<!--    color ui-cl-color-comet-->
<!--    transform scale(1)-->
<!--    opacity 1-->

<!--.checkbox-wrapper-->
<!--  position relative-->
<!--  padding-top 20px-->
<!--  display flex-->
<!--  justify-content space-between-->

<!--.friends-search-->
<!--  margin-top 25px-->
<!--  padding-top 20px-->
<!--  margin-bottom 30px-->
<!--  border-top 1px solid ui-cl-color-e6e6e6-->

<!--.friends-search__row-->
<!--  @media (max-width breakpoint-xl)-->
<!--    display flex-->

<!--    .friends-search__block-->
<!--      flex auto-->

<!--    .friends-search__block + .friends-search__block-->
<!--      margin-top 0-->
<!--      margin-left 12px-->

<!--.friends-search__row + .friends-search__block-->
<!--  margin-top 15px-->

<!--.friends-search__block-->
<!--  &+&-->
<!--    margin-top 15px-->

<!--.friends-search__select-->
<!--  display block-->
<!--  width 100%-->
<!--  border-radius border-super-small-->
<!--  padding 13px 10px-->
<!--  height unset-->

<!--  &+&-->
<!--    margin-left 12px-->
<!--</style>-->


<template>
  <form action="#" class="friends-possible" @submit.prevent="onSearchUsers">
    <h4 class="friends-possible__title">
      {{ translationsLang.friendsFindParamsTitle }}
    </h4>

    <div class="friends-search">
      <div class="friends-search__row">
        <div class="friends-search__block">
          <label for="friends-search-name" class="search__label"
          >{{ translationsLang.createAccNameField1 }}:</label
          >
          <input
              type="text"
              class="search__input"
              id="friends-search-name"
              v-model="firstName"
          />
        </div>
      </div>

      <div class="friends-search__block">
        <label class="search__label">{{
            translationsLang.friendsFindYearTitle
          }}</label>
        <div class="search__row">
          <select
              class="select friends-search__select"
              v-model.number="ageFrom"
          >
            <option value="null" disabled>
              {{ translationsLang.friendsFindFrom }}
            </option>
            <option v-for="age in ageFromArray" :value="age" :key="age">
              {{ translationsLang.friendsFindFrom }} {{ age }}
            </option>
          </select>

          <span class="search__age-defis">—</span>

          <select class="select friends-search__select" v-model.number="ageTo">
            <option value="null" disabled>
              {{ translationsLang.friendsFindTo }}
            </option>
            <option v-for="age in ageToArray" :value="age" :key="age">
              {{ translationsLang.friendsFindTo }} {{ age }}
            </option>
          </select>
        </div>
      </div>

      <div class="friends-search__block">
        <label class="search__label">{{
            translationsLang.friendsFindRegion
          }}</label>
        <div class="search__row">
          <select class="select friends-search__select" v-model="country">
            <option value="null" disabled>
              {{ translationsLang.friendsFindCountry }}
            </option>
            <option v-for="c in countries" :key="c.id" :value="c">
              {{ c.title }}
            </option>
          </select>
          <select class="select friends-search__select" v-model="city">
            <option value="null" disabled>
              {{ translationsLang.friendsFindCity }}
            </option>
            <option v-for="c in currentCities" :key="c.id" :value="c">
              {{ c.title }}
            </option>
          </select>
        </div>
      </div>

      <div class="friends-search__block">
        <label class="search__label checkbox-wrapper">
          <span>{{ translationsLang.friendsFindCheckbox }}</span>
          <input type="checkbox" class="checkbox" v-model="allUsers" />
        </label>
      </div>
    </div>

    <button class="recommend-block__search" type="submit">
      {{ translationsLang.recommendBlockBtn }}
    </button>
  </form>
</template>

<script>
import { ref, computed, watch, onBeforeUnmount } from "vue";
import { useStore } from "vuex";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "FriendsSearch",

  setup() {
    const store = useStore();
    const {
      state,
      dispatch,
    } = store;
    const firstName = ref(null);
    const lastName = ref(null);
    const ageFrom = ref(null);
    const ageTo = ref(null);
    const country = ref(null);
    const city = ref(null);
    const currentCities = ref([]);
    const disabled = ref(false);
    const page = ref(1);
    const itemSize = ref(5);
    const ageFromArray = ref([...Array(100).keys()].slice(5));
    const ageToArray = ref([...Array(121).keys()].slice(5));
    const allUsers = ref(false);

    const { translationsLang } = useTranslations();

    const countries = computed(() => state.global.geo.countries);
    const cities = computed(() => state.global.geo.cities);

    watch(
        () => country,
        (newVal) => {
          if (newVal && newVal.id) currentCities.value = cities[country.value.id];
          // else cities.value = [];
          city.value = null;
        },
        { immediate: true }
    );

    onBeforeUnmount(() => {
      dispatch("global/search/clearSearch");
    });

    const onSearchUsers = () => {
      if (
          ageFrom.value !== null &&
          ageTo.value !== null &&
          ageFrom.value >= ageTo.value
      ) {
        dispatch("global/alert/setAlert", {
          status: "error",
          text: "Неправильный выбор возрастного интервала!",
        });
        return;
      }


      const countryName = country.value ? country.value.title : null;
      const cityName = city.value ? city.value.title : null;
      dispatch("global/search/searchUsers", {
        payload: {
          firstName: firstName.value,
          lastName: lastName.value,
          ageFrom: ageFrom.value,
          ageTo: ageTo.value,
          country: countryName,
          city: cityName,
        },
        myFriends: !allUsers.value,
      });
    };

    return {
      firstName,
      lastName,
      ageFrom,
      ageTo,
      country,
      city,
      currentCities,
      disabled,
      page,
      itemSize,
      ageFromArray,
      ageToArray,
      allUsers,
      translationsLang,
      countries,
      cities,
      onSearchUsers,
    };
  },
};

</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.checkbox
  position relative
  border 0
  padding 0
  height 1px
  width 1px
  clip rect(1px, 1px, 1px, 1px)
  cursor pointer

  &::before
    content ''
    position absolute
    right 0
    width 20px
    height 20px
    border 2px solid ui-cl-color-eucalypt

  &::after
    content: ''
    transform scale(1.3)
    opacity 0
    transition transform 200ms linear, opacity  100ms linear

  &:checked::after
    content '\2714'
    position absolute
    right -2px
    top -11px
    font-size 27px
    color ui-cl-color-comet
    transform scale(1)
    opacity 1

.checkbox-wrapper
  position relative
  padding-top 20px
  display flex
  justify-content space-between

.friends-search
  margin-top 25px
  padding-top 20px
  margin-bottom 30px
  border-top 1px solid ui-cl-color-e6e6e6

.friends-search__row
  @media (max-width breakpoint-xl)
    display flex

    .friends-search__block
      flex auto

    .friends-search__block + .friends-search__block
      margin-top 0
      margin-left 12px

.friends-search__row + .friends-search__block
  margin-top 15px

.friends-search__block
  &+&
    margin-top 15px

.friends-search__select
  display block
  width 100%
  border-radius border-super-small
  padding 13px 10px
  height unset

  &+&
    margin-left 12px
</style>
