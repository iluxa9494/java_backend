<template>
  <div>
    <button
      class="search-showmore-filter"
      @click.prevent="showBlock = !showBlock"
      :class="{ 'open-filter': showBlock }"
    >
      <span v-if="!showBlock">{{
        translationsLang.searchNewsShowMoreButton
      }}</span>
      <span v-else>{{ translationsLang.searchNewsCloseMoreButton }}</span>
      <arrow-bottom />
    </button>
    <transition name="fade">
      <div class="search-filter--users" v-show="showBlock">
        <div class="search-filter">
          <div class="search-filter__block name">
            <label class="search__label" for="search-people-name"
              >{{ translationsLang.createAccNameField1 }}:</label
            >
            <input
              class="search__input"
              :placeholder="translationsLang.searchUserNameInput"
              type="text"
              id="search-people-name"
              v-model="firstName"
            />
          </div>
          <div class="search-filter__block lastname">
            <label class="search__label" for="search-people-lastname">{{
              translationsLang.createAccNameField2
            }}</label>
            <input
              class="search__input"
              :placeholder="translationsLang.searchUserSurnameInput"
              type="text"
              id="search-people-lastname"
              v-model="lastName"
            />
          </div>
          <div class="search-filter__block age">
            <label class="search__label">{{
              translationsLang.friendsFindYearTitle
            }}</label>
            <div class="search__row">
              <select
                class="select search-filter__select"
                v-model.number="ageFrom"
              >
                <option :value="null" :disabled="disabled">
                  {{ translationsLang.friendsFindFrom }}
                </option>
                <option v-for="age in ageFromArray" :value="age" :key="age">
                  {{ translationsLang.friendsFindFrom }} {{ age }}
                </option>
              </select>
              <span class="search__age-defis">—</span>
              <select
                class="select search-filter__select"
                v-model.number="ageTo"
              >
                <option :value="null" :disabled="disabled">
                  {{ translationsLang.friendsFindTo }}
                </option>
                <option v-for="age in ageToArray" :value="age" :key="age">
                  {{ translationsLang.friendsFindTo }} {{ age }}
                </option>
              </select>
            </div>
          </div>
          <div class="search-filter__block region">
            <label class="search__label">{{
              translationsLang.friendsFindRegion
            }}</label>
            <div class="search__row">
              <v-select
                :placeholder="translationsLang.settingMainPlaceholderCountry"
                class="country"
                v-model="country"
                :options="countryNames"
                :key="countries.id"
              />
              <v-select
                :placeholder="translationsLang.settingMainPlaceholderCity"
                class="country"
                v-model="city"
                :options="cityNames"
                :key="cities.id"
              />
            </div>
          </div>
        </div>
        <div class="search-filter__block btn-news">
          <button class="search-filter__btn" @click.prevent="onSearchUsers()">
            {{ translationsLang.searchNewsBtnComplited }}
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { computed, onMounted, ref, watch } from "vue";
import { useStore } from "vuex";
import useTranslations from "@/composables/useTranslations";
import axios from "axios";
import VSelect from "vue-select";
import "vue-select/dist/vue-select.css";
import ArrowBottom from "@/Icons/ArrowBottom.vue";

export default {
  name: "SearchFilterUsers",

  components: {
    ArrowBottom,
    VSelect,
  },

  setup() {
    const { getters, dispatch } = useStore();
    const firstName = ref(null);
    const lastName = ref(null);
    const ageFrom = ref(null);
    const ageTo = ref(null);

    const country = ref("");
    const city = ref("");
    const countries = ref([]);
    const cities = ref([]);
    const currentCountry = ref({});

    const disabled = ref(false);
    const ageFromArray = ref([...Array(100).keys()].slice(5));
    const ageToArray = ref([...Array(121).keys()].slice(5));

    const showBlock = ref(false);

    const { translationsLang } = useTranslations();

    const getUsersQueryParams = computed(
      () => getters["global/search/getUsersQueryParams"]
    );
    const searchText = computed(() => getters["global/search/searchText"]);

    const countryNames = computed(() =>
      countries.value.map((country) => country.title)
    );
    const cityNames = computed(() => cities.value?.map((city) => city.title));

    watch(
      country,
      (newVal) => {
        if (newVal && newVal !== "") {
          setTimeout(() => {
            currentCountry.value = countries.value.find(
              (country) => country.title === newVal
            );
            loadCities(currentCountry.value.id);

            if (
              city.value &&
              currentCountry.value &&
              city.value !== "" &&
              currentCountry.value.id !==
                cities.value.find((city) => city.title === city).countryId
            ) {
              city.value = "";
              country.value = "";
            }
          }, 500);
        } else {
          city.value = "";
          cities.value = [];
          country.value = "";
        }
      },
      { immediate: true }
    );

    onMounted(() => {
      loadCountries();
    });

    // const onSearchUsers = () => {
    //   if (
    //     ageFrom.value !== null &&
    //     ageTo.value !== null &&
    //     ageFrom.value >= ageTo.value
    //   ) {
    //     dispatch("global/alert/setAlert", {
    //       status: "action",
    //       text: "Неправильный выбор возрастного интервала!",
    //     });
    //     return;
    //   }
    //   const countryName = country.value ? country : null;
    //   const cityName = city.value ? city : null;
    //   const searchQuery = {
    //     ...getUsersQueryParams.value,
    //     firstName,
    //     lastName,
    //     ageFrom,
    //     ageTo,
    //     country: countryName.value,
    //     city: cityName.value,
    //     author: searchText.value,
    //   };
    //   dispatch("global/search/searchUsers", { payload: searchQuery });
    // };

    const onSearchUsers = () => {
      if (
          ageFrom.value !== null &&
          ageTo.value !== null &&
          ageFrom.value >= ageTo.value
      ) {
        dispatch("global/alert/setAlert", {
          status: "action",
          text: "Неправильный выбор возрастного интервала!",
        });
        return;
      }
      const searchQuery = {
        ...getUsersQueryParams.value,
        firstName: firstName.value,
        lastName: lastName.value,
        ageFrom: ageFrom.value,
        ageTo: ageTo.value,
        country: country.value,
        city: city.value,
        author: searchText.value,
      };
      dispatch("global/search/searchUsers", { payload: searchQuery });
    };


    const loadCountries = () => {
      axios
        .get("/geo/country")
        .then((response) => {
          countries.value = response.data;
        })
        .catch(() => {});
      return;
    };

    const loadCities = (countryId) => {
      if (!countryId) {
        city.value = null;
        return;
      }
      axios.get(`/geo/country/${countryId}/city`).then((response) => {
        cities.value = response.data;
      });
      return countryId;
    };

    return {
      firstName,
      lastName,
      ageFrom,
      ageTo,
      country,
      city,
      countries,
      cities,
      disabled,
      ageFromArray,
      ageToArray,
      showBlock,
      translationsLang,
      countryNames,
      cityNames,
      onSearchUsers,
    };
  },
};
</script>

<style lang="stylus">

.search-filter--users
  &.fade-enter-active,
  &.fade-leave-active
    transition all .2s ease-in-out
  &.fade-enter,
  &.fade-leave-to
    opacity 0

.search-showmore-filter
  background-color transparent
  transition all .2s ease-in-out
  svg path
    stroke #000
  &:hover
    opacity 0.5
  &.open-filter
    margin-bottom 15px
    transition all .2s ease-in-out
    svg
      transform rotate(180deg)
      transition all .2s ease-in-out


.search-filter__btn
  display inline-block
  text-align center
  color #fff
  padding 8px
  border 2px solid #21a45d
  transition all 0.2s ease-in-out
  background #21a45d
  &:hover
    background #333
    border-color #333
    color #fff

.v-select
  width 100%
  height 41px
.vs__search
  font-size 14px
  margin-top 0
.vs--searchable .vs__dropdown-toggle
  padding 5px
  border 1px solid #e3e3e3
  height 100%
.vs__selected-options
  padding 0
.vs__search, .vs__search:focus
  font-size 14px
  margin-top 0
.vs__selected
  font-size 14px
  margin-top 0
</style>
