<template>
  <div class="settings-main">
    <div
      v-if="srcCover"
      :style="{ 'background-image': `url(${srcCover})` }"
      class="settings-main__cover"
    ></div>
    <div class="settings-main-photoblock">
      <div class="user-info-form__block user-info-form__block--photo">
        <div class="settings-main__top">
          <div class="main-layout__user-pic" v-if="src">
            <img :src="src" :alt="firstName[0]" />
          </div>
          <div
            class="main-layout__user-pic"
            style="background-color: #333"
            v-else
          >
            <img
              src="https://yastatic.net/s3/yandex-id-static/yandex-id/_/_next/static/media/avatar-placeholder.f27a38d6.png"
              :alt="translationsLang.settingMainPhotoAlt"
            />
          </div>
        </div>

        <div class="user-info-form__wrap">
          <div class="user-info-form__photo-wrap">
            <input
              class="user-info-form__input_stylus-photo"
              type="file"
              ref="photoRef"
              id="photo"
              @change="processFile($event)"
              accept="image/*"
            />
            <input
              class="user-info-form__input_stylus-photo"
              type="file"
              ref="photoCoverRef"
              id="photoCover"
              @change="processFileCoverProfile($event)"
              accept="image/*"
            />
            <!-- Аватарка -->
            <button class="setting-main__buttons" @click.prevent="loadPhoto">
              <load-photo />
              <span v-if="src">{{
                translationsLang.settingsMainEditPhoto
              }}</span>
              <span v-else>{{ translationsLang.settingMainAddPhoto }}</span>
            </button>
            <div
              v-if="src"
              class="settings-main__top--delete"
              @click="deletePhoto"
            >
              <div class="settings-main__top--delete-icon">
                <delete-icon />
              </div>
            </div>
            <!-- Обложка профиля -->
            <div class="settings-main-actions__cover">
              <button
                class="settings-main-actions-load__cover"
                @click.prevent="loadPhotoCover"
              >
                <load-photo />
                <span v-if="srcCover">{{
                  translationsLang.settingsMainEditCover
                }}</span>
                <span v-else>{{ translationsLang.settingsMainAddCover }}</span>
              </button>
              <button
                v-if="srcCover"
                class="settings-main-actions-delete__cover"
                @click.prevent="deletePhotoCover"
              >
                <div>
                  <delete-icon />
                </div>
              </button>
            </div>
            <div>
              <div class="settings-emoji__buttons">
                <button
                  class="settings-emoji__btn"
                  @click.prevent="toggleEmojiStatus"
                >
                  <span v-if="emojiStatus">{{
                    translationsLang.settingsUnknowEmojiStatus
                  }}</span>
                  <span v-else>{{
                    translationsLang.settingsSetEmojiStatus
                  }}</span>
                </button>
              </div>
              <div
                class="overlay-emoji"
                v-if="showEmojiStatus"
                @click="showEmojiStatus = false"
              ></div>
              <transition name="fade">
                <div
                  class="settings-emoji"
                  v-if="showEmojiStatus"
                  v-click-outside="closeEmojiList"
                >
                  <div class="settings-emoji-top">
                    <div class="settings-emoji-top__left">
                      <h2 class="settings-emoji__title">
                        {{ translationsLang.settingsEmojiTitle }}
                      </h2>
                      <span class="settings-emoji__desclamer">{{
                        translationsLang.settingsEmojiDisclamer
                      }}</span>
                    </div>
                    <div class="settings-emoji-top__right">
                      <button
                        class="settings-emoji-top__btn settings-main-actions-delete__cover"
                        @click.prevent="deleteEmojiStatus"
                      >
                        <delete-icon />
                        {{ translationsLang.settingsEmojiDelete }}
                      </button>
                    </div>
                  </div>
                  <ul class="settings-emoji__list">
                    <li
                      class="settings-emoji__item"
                      :class="{ selected: index.toString() === emojiStatus }"
                      v-for="(item, index) in itemsEmojiStatus"
                      :key="index"
                      @click="currentEmojiStatus(index)"
                    >
                      <div v-if="typeof item === 'string'">{{ item }}</div>
                      <div
                        v-else-if="
                          typeof item === 'object' && item.type === 'image'
                        "
                      >
                        <p class="settings-emoji__subtitle">
                          {{
                            currentTranslations === "Русский"
                              ? item.text
                              : item.textEng
                          }}
                        </p>
                        <img
                          class="settings-emoji__img"
                          :src="item.src"
                          :alt="
                            currentTranslations === 'Русский'
                              ? item.alt
                              : item.altEng
                          "
                        />
                        <p class="settings-emoji__desc">
                          {{
                            currentTranslations === "Русский"
                              ? item.desc
                              : item.descEng
                          }}
                        </p>
                      </div>
                    </li>
                  </ul>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>
    </div>

    <user-info-form-block
      :label="translationsLang.createAccNameField1"
      :placeholder="translationsLang.searchUserNameInput"
      text="firstName"
      v-model="firstName"
      ref="firstNameRef"
    />

    <user-info-form-block
      :label="translationsLang.createAccNameField2"
      :placeholder="translationsLang.searchUserSurnameInput"
      text="lastName"
      v-model="lastName"
      ref="lastNameRef"
    />

    <user-info-form-block
      :label="translationsLang.profileInfoPhone"
      :placeholder="translationsLang.settingMainPlaceholderPhone"
      v-model="phone"
      phone="phone"
    />

    <div class="settings-main-input">
      <span class="user-info-form__label_stylus">{{
        translationsLang.settingMainCountry
      }}</span>

      <div class="user-info-form__wrap">
        <v-select
          :placeholder="translationsLang.settingMainPlaceholderCountry"
          class="country"
          v-model="country"
          :options="countryNames"
          :key="countries.id"
          @open="openCountrySelect = !openCountrySelect"
        />
      </div>
    </div>

    <div class="settings-main-input">
      <span class="user-info-form__label_stylus">{{
        translationsLang.settingMainCity
      }}</span>

      <div class="user-info-form__wrap">
        <v-select
          :placeholder="translationsLang.settingMainPlaceholderCity"
          class="country"
          v-model="city"
          :options="cityNames"
          :key="cities.id"
        />
      </div>
    </div>

    <div class="settings-main-input">
      <span class="user-info-form__label_stylus">{{
        translationsLang.profileInfoBirthday
      }}</span>

      <div class="user-info-form__wrap">
        <select class="select user-info-form__select day" v-model="day">
          <option :value="null">
            {{ translationsLang.settingMainUnknow }}
          </option>
          <option v-for="d in days" :key="d">{{ d }}</option>
        </select>

        <select
          class="select user-info-form__select month"
          v-model="selectedMonth"
        >
          <option :value="null">none</option>
          <option v-for="month in months" :key="month.val" :value="month">
            {{ currentTranslations === "Русский" ? month.text : month.textEng }}
          </option>
        </select>

        <select class="select user-info-form__select year" v-model="year">
          <option :value="null">
            {{ translationsLang.settingMainUnknow }}
          </option>
          <option v-for="i in years" :key="i">{{ i }}</option>
        </select>
      </div>
    </div>

    <user-info-form-block
      :label="translationsLang.settingMainAbout"
      v-model="about"
      about="about"
    />

    <div class="user-info-form__block user-info-form__block--actions">
      <div class="user-info-form__wrap">
        <button
          class="setting-main__buttons button-submit"
          @click.prevent="submitHandler"
        >
          {{ translationsLang.settingMainSave }}
        </button>
        <router-link :to="{ name: 'Profile' }">
          <button class="setting-main__buttons canceled">
            {{ translationsLang.settingMainCancel }}
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import useTranslations from "@/composables/useTranslations";
import vClickOutside from "click-outside-vue3";
import vSelect from "vue-select";
import "vue-select/dist/vue-select.css";
import axios from "axios";
import DeleteIcon from "@/Icons/DeleteIcon.vue";
import LoadPhoto from "@/Icons/LoadPhoto.vue";
import UserInfoFormBlock from "@/components/Settings/UserInfoForm/Block.vue";

export default {
  name: "SettingsMain",
  directives: {
    clickOutside: vClickOutside.directive,
  },
  components: { UserInfoFormBlock, DeleteIcon, LoadPhoto, vSelect },

  setup() {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();
    const { getters, state, dispatch, commit } = store;
    const emojiStatus = ref("");
    const firstName = ref("");
    const lastName = ref("");
    const phone = ref("");
    const about = ref("");
    const day = ref(1);
    const days = ref(null);
    const selectedMonth = ref(null);
    const year = ref(2000);
    const months = reactive([
      { val: 0, text: "Января", textEng: "January" },
      { val: 1, text: "Февраля", textEng: "February" },
      { val: 2, text: "Марта", textEng: "March" },
      { val: 3, text: "Апреля", textEng: "April" },
      { val: 4, text: "Мая", textEng: "May" },
      { val: 5, text: "Июня", textEng: "June" },
      { val: 6, text: "Июля", textEng: "July" },
      { val: 7, text: "Августа", textEng: "August" },
      { val: 8, text: "Сентября", textEng: "September" },
      { val: 9, text: "Октября", textEng: "October" },
      { val: 10, text: "Ноября", textEng: "November" },
      { val: 11, text: "Декабря", textEng: "December" },
    ]);
    const fileName = ref("");
    const src = ref("");

    const showEmojiStatus = ref(false);
    const itemsEmojiStatus = reactive([
      {
        type: "image",
        src: "/static/img/user/status_guru.png",
        alt: "Гуру кода",
        altEng: "Code guru",
        text: "Гуру кода",
        textEng: "Code guru",
        desc: "Вы уже обладаете высоким уровнем знаний и опыта в программировании.",
        descEng:
          "You already have a high level of knowledge and experience in programming.",
      },
      {
        type: "image",
        src: "/static/img/user/status_new.png",
        alt: "Новичок",
        altEng: "Beginner",
        text: "Новичок",
        textEng: "Beginner",
        desc: "Вы только начинаете изучать программирование и имеете базовые знания в этой области.",
        descEng:
          "You are just starting to learn programming and have basic knowledge in this field.",
      },
      {
        type: "image",
        src: "/static/img/user/status_escp.png",
        alt: "Экспериментатор",
        altEng: "The experimenter",
        text: "Экспериментатор",
        textEng: "The experimenter",
        desc: "Вы любите экспериментировать с новыми технологиями и методами программирования.",
        descEng:
          "You like to experiment with new technologies and programming methods.",
      },
      {
        type: "image",
        src: "/static/img/user/status_teacher.png",
        alt: "Учитель",
        altEng: "Teacher",
        text: "Учитель",
        textEng: "Teacher",
        desc: "Вы уже имеете опыт и знания в программировании и помогаете другим изучать эту область.",
        descEng:
          "You already have experience and knowledge in programming and are helping others to study this field.",
      },
      {
        type: "image",
        src: "/static/img/user/status_student.png",
        alt: "Фрилансер",
        altEng: "Freelancer",
        text: "Фрилансер",
        textEng: "Freelancer",
        desc: "Ваша работа основана на свободе и творчестве, что позволяет вам достигать успеха в своей области и наслаждаться жизнью.",
        descEng:
          "Your work is based on freedom and creativity, which allows you to achieve success in your field and enjoy life.",
      },
      {
        type: "image",
        src: "/static/img/user/status_love.png",
        alt: "Любитель",
        altEng: "Lover",
        text: "Любитель",
        textEng: "Lover",
        desc: "Вы увлекаетесь программированием и имеете базовые знания в этой области, но не являетесь профессионалом.",
        descEng:
          "You are interested in programming and have basic knowledge in this field, but you are not a professional.",
      },
    ]);
    const selectedEmojiStatus = ref(null);
    const srcCover = ref("");
    const profileCover = ref("");
    const country = ref("");
    const city = ref("");
    const countries = ref([]);
    const cities = ref([]);
    const currentCountry = ref({});
    const photoRef = ref(null);
    const photoCoverRef = ref(null);
    const firstNameRef = ref(null);
    const lastNameRef = ref(null);
    const openCountrySelect = ref(false);
    const { translationsLang } = useTranslations();

    const getStorage = computed(() => getters["global/storage/getStorage"]);
    const getInfo = computed(() => getters["profile/info/getInfo"]);
    const currentTranslations = computed(
      () => state.auth.languages.language.name
    );
    const phoneNumber = computed(() => phone.value.replace(/\D+/g, ""));
    const years = computed(() =>
      Array.from({ length: 61 }, (value, index) => 1962 + index)
    );
    const countryNames = computed(() =>
      countries.value.map((country) => country.title)
    );
    const cityNames = computed(() => {
      const itemCities = cities.value.map((city) => city.title);
      return itemCities.sort();
    });

    watch(openCountrySelect, (newVal) => {
      openCountrySelect.value = newVal;
    });

    watch(
      () => getInfo,
      (newVal) => {
        if (!newVal) {
          return;
        }
        setInfo();
      }
    );

    watch(
      country,
      (newVal) => {
        if (newVal && newVal !== "") {
          setTimeout(() => {
            currentCountry.value = countries.value.find(
              (country) => country.title === newVal
            );
            loadCities(currentCountry.value.id);

            countries.value.forEach((itemCountry) => {
              if (itemCountry.id === currentCountry.value.id) {
                const foundCity = itemCountry.cities.find(
                  (item) => item.title === city.value
                );
                if (
                  city.value &&
                  currentCountry.value &&
                  city.value !== "" &&
                  currentCountry.value.id !== foundCity.countryId
                ) {
                  city.value = "";
                  country.value = "";
                }
              }
            });
          }, 500);
        } else {
          city.value = "";
          cities.value = [];
          country.value = "";
        }
      },
      { immediate: true }
    );

    watch(selectedMonth, (newVal) => {
      selectedMonth.value = newVal;
      days.value =
        selectedMonth.value.val === 1
          ? year.value & 3 || (!(year.value % 25) && year.value & 15)
            ? 28
            : 29
          : 30 +
            ((selectedMonth.value.val + (selectedMonth.value.val >> 3)) & 1);
    });

    onMounted(() => {
      loadCountries();

      if (route.params.showEmojiPicker) {
        showEmojiStatus.value = true;
      }
    });

    const loadCountries = () => {
      axios
        .get("/geo/country")
        .then((response) => {
          countries.value = response.data;
          if (getInfo.value) {
            setInfo();
          }
        })
        .catch(() => {});
      return;
    };

    const closeEmojiList = () => {
      showEmojiStatus.value = false;
    };

    const toggleEmojiStatus = () => {
      showEmojiStatus.value = !showEmojiStatus.value;
    };

    const currentEmojiStatus = (index) => {
      selectedEmojiStatus.value = index.toString();
      emojiStatus.value = index.toString();
      showEmojiStatus.value = false;
    };

    const deleteEmojiStatus = () => {
      emojiStatus.value = "";
      selectedEmojiStatus.value = "";
      dispatch("global/alert/setAlert", {
        status: "response",
        text: "Статус удалён, вы всегда сможете поставить его заново",
      });
      showEmojiStatus.value = false;
    };

    const loadCities = (countryId) => {
      if (!countryId) {
        city.value = null;
        return;
      }

      const itemCountry = countries.value.find((itemCountry) => itemCountry.id === countryId);
      cities.value = itemCountry.cities;
      return countryId;
    };

    const submitHandler = async () => {
      let _birthDate = "none";
      if (year.value && selectedMonth.value && day.value) {
        const date = new Date(year.value, selectedMonth.value.val, day.value);

        const offset = date.getTimezoneOffset();
        const localDate = new Date(date.getTime() - offset * 60 * 1000);
        _birthDate = localDate.toISOString();
      }
      if (!firstNameRef.value.validate() && !lastNameRef.value.validate()) {
        dispatch("global/alert/setAlert", {
          status: "error",
          text: 'Поля "Имя" и "Фамилия" являются обязательными',
        });
        return;
      } else if (!firstNameRef.value.validate()) {
        dispatch("global/alert/setAlert", {
          status: "error",
          text: 'Поле "Имя" является обязательным',
        });
        return;
      } else if (!lastNameRef.value.validate()) {
        dispatch("global/alert/setAlert", {
          status: "error",
          text: 'Поле "Фамилия" является обязательным',
        });
        return;
      } else if (lastName.value.length < 3 && firstName.value.length < 3) {
        dispatch("global/alert/setAlert", {
          status: "error",
          text: "Имя и Фамилия не могут быть короче 3-ёх символов",
        });
        return;
      } else if (firstName.value.length < 3) {
        dispatch("global/alert/setAlert", {
          status: "error",
          text: "Имя не может быть короче 3-ёх символов",
        });
        return;
      } else if (lastName.value.length < 3) {
        dispatch("global/alert/setAlert", {
          status: "error",
          text: "Фамилия не может быть короче 3-ёх символов",
        });
        return;
      }

      if (fileName.value) {
        await dispatch("global/storage/apiStorage", fileName.value).then(
          (response) => {
            fileName.value = response.data.fileName;
          }
        );
      }

      if (profileCover.value) {
        await dispatch("global/storage/apiStorage", profileCover.value).then(
          (response) => {
            profileCover.value = response.data.fileName;
          }
        );
      }

      await dispatch("profile/info/apiChangeInfo", {
        firstName: firstName.value,
        lastName: lastName.value,
        birthDate: _birthDate,
        phone: phoneNumber.value,
        about: about.value,
        country: country.value,
        city: city.value,
        emojiStatus:
          selectedEmojiStatus.value === ""
            ? emojiStatus.value
            : selectedEmojiStatus.value,
        photo: fileName.value === "" ? src.value : fileName.value,
        profileCover:
          profileCover.value === "" ? srcCover.value : profileCover.value,
      }).then(() => router.push("/profile"));
    };

    // Аватарка:
    const processFile = (event) => {
      [fileName.value] = event.target.files;
      const reader = new window.FileReader();
      reader.onload = (e) => {
        src.value = e.target.result;
      };
      reader.readAsDataURL(fileName.value);
    };

    const loadPhoto = () => {
      photoRef.value.click();
    };

    const deletePhoto = () => {
      fileName.value = "";
      src.value = "";
      commit("global/storage/setStorage", "");
    };

    // Обложка
    const processFileCoverProfile = (event) => {
      [profileCover.value] = event.target.files;
      const reader = new window.FileReader();
      reader.onload = (e) => {
        srcCover.value = e.target.result;
      };
      reader.readAsDataURL(profileCover.value);
    };

    const loadPhotoCover = () => {
      photoCoverRef.value.click();
    };

    const deletePhotoCover = () => {
      profileCover.value = "";
      srcCover.value = "";
      commit("global/storage/setStorage", "");
    };

    const setInfo = () => {
      firstName.value = getInfo.value.firstName;
      lastName.value = getInfo.value.lastName;
      src.value = getInfo.value.photo;
      srcCover.value = getInfo.value.profileCover;
      emojiStatus.value = getInfo.value.emojiStatus;

      if (getInfo.value.phone) {
        phone.value = getInfo.value.phone.replace(/^[+]?[78]/, "");
      } else phone.value = "";

      if (getInfo.value.birthDate) {
        const birthDate = new Date(getInfo.value.birthDate);
        day.value = birthDate.getDate();
        selectedMonth.value = months[birthDate.getMonth()];
        year.value = birthDate.getFullYear();
      }
      about.value = getInfo.value.about;
      if (getInfo.value.country) {
        country.value = getInfo.value.country;
        currentCountry.value = countries.value.find(
          (country) => country.title === country
        );
        loadCities(currentCountry.value?.id);
      }
      if (getInfo.value.city) {
        city.value = getInfo.value.city;
      }
    };

    return {
      currentCountry,
      emojiStatus,
      firstName,
      lastName,
      phone,
      about,
      day,
      year,
      months,
      src,
      showEmojiStatus,
      itemsEmojiStatus,
      srcCover,
      profileCover,
      country,
      city,
      countries,
      cities,
      photoRef,
      photoCoverRef,
      firstNameRef,
      lastNameRef,
      translationsLang,
      currentTranslations,
      years,
      countryNames,
      cityNames,
      days,
      getStorage,
      selectedMonth,
      closeEmojiList,
      toggleEmojiStatus,
      currentEmojiStatus,
      deleteEmojiStatus,
      submitHandler,
      processFile,
      loadPhoto,
      deletePhoto,
      processFileCoverProfile,
      loadPhotoCover,
      deletePhotoCover,
      openCountrySelect,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.vt-notification-container {
  display block !important
}

.settings-emoji__buttons
  display flex
  gap 10px
  align-items center
  position absolute
  top 20px
  left 20px

.settings-emoji-top
  display flex
  align-items flex-start
  justify-content space-between
  border-bottom 1px solid #e3e3e3
  padding-bottom 20px
  margin-bottom 20px

  &__right
    display flex
    flex-direction column

  &__btn
    padding 5px 10px !important

.settings-emoji__btn
  display flex
  align-items center
  gap 5px
  font-size font-size-small-medium
  color ui-cl-color-white-theme
  font-weight font-weight-regular
  background rgba(0, 0, 0, 0.36)
  padding 5px
  border-radius border-small
  transition all .2s ease-in-out
  &:hover
    background rgba(0, 0, 0, 0.5)

.overlay-emoji
  position fixed
  top 0
  left 0
  width 100%
  height 100%
  background-color rgba(0, 0, 0, 0.5)
  z-index 1

.settings-emoji
  position fixed
  display flex
  flex-direction column
  justify-content center
  max-width 100%
  top 50%
  left 50%
  transform translate(-50%, -50%)
  z-index 10
  background ui-cl-color-white-theme
  border-radius border-big-radius
  box-shadow 0px 0px 33px rgba(0,0,0,0.42)
  padding 20px
  &.fade-enter-active,
  &.fade-leave-active
    transition all .2s ease-in-out
  &.fade-enter,
  &.fade-leave-to
    opacity 0

  &__list
    display grid
    grid-template-columns repeat(6, 180px)
    gap 20px
    align-items flex-start
  &__item

    height 100%
    padding 10px
    border-radius border-small
    transition all .2s ease-in-out
    cursor pointer
    &:hover
      background-color #efefef
    &.selected
      background-color ui-cl-color-eucalypt
      color ui-cl-color-white-theme
  &__img
    max-width 40px
    margin-bottom 10px
  &__title
    font-size 23px
    margin-bottom 5px
  &__desclamer
    font-size font-size-super-medium-small
    color ui-cl-color-medium-grey-light
  &__subtitle
    font-weight font-weight-bold
    margin-top 0
    margin-bottom 10px
  &__desc
    font-size font-size-super-medium-small


.settings-main
  position relative
  background ui-cl-color-white-theme
  width 100%
  box-shadow box-shadow-main
  padding 40px 20px
  border-radius border-big-radius
  overflow hidden
  z-index 10

  &-actions-load__cover,
  &-actions-delete__cover
    display flex
    align-items center
    gap 5px
    font-size font-size-small-medium
    color ui-cl-color-white-theme
    font-weight font-weight-regular
    background rgba(0, 0, 0, 0.36)
    padding 5px
    border-radius border-small
    transition all .2s ease-in-out
    &:hover
      background rgba(0, 0, 0, 0.5)

  &-actions__cover
    display flex
    gap 10px
    align-items center
    position absolute
    top 20px
    right 20px

  &__cover
    position absolute
    top 0
    left 0
    width 100%
    height 230px
    background-size cover
    background-repeat no-repeat
    background-position center center
    z-index -1
    opacity 0.8
    &::after
      content ""
      position absolute
      bottom 0
      left 0
      right 0
      height 150px
      background-image linear-gradient(to bottom, rgba(255,255,255,0), rgba(255,255,255,1))

  .user-info-form__label_stylus
    white-space pre-wrap

.settings-main__back
  margin-left 20px

.setting-main__delete-icon
  position absolute
  right 10px
  top 12px
  width 12px
  height 12px
  transition all .2s ease-in-out

.country
  width 100%

.user-info-form__block
  display flex
  justify-content center
  flex-direction column
  .main-layout__user-pic
    width 96px
    height 96px
    border 2px solid grey
    margin-right 0
  img
    width 100%
    height 100%

.settings-main__top
  display flex
  align-items center
  gap 15px
  justify-content center
  margin-bottom 15px
  &--delete
    display flex
    flex-direction column
    font-size 11px
    cursor pointer
    &-icon
      display inline-flex
      justify-content center
      align-items center
      background ui-cl-color-e9e9e9
      padding 9px 10px
      border-radius border-fullhalf
      margin-left 10px
      transition all .2s ease-in-out
      @media (any-hover: hover)
        &:hover
          background ui-cl-color-c5c5c5

.setting-main__buttons
  display flex
  align-items center
  justify-content center
  gap 5px
  background ui-cl-color-e9e9e9
  padding 10px
  border-radius border-big-radius
  font-size font-size-small-medium
  transition all .2s ease-in-out
  box-shadow box-shadow-main
  &.button-submit
    min-width 150px
  @media (any-hover: hover)
    &:hover
      background ui-cl-color-c5c5c5
  &.canceled
    margin-left 10px
    background transparent
    border: 1px solid ui-cl-color-c5c5c5
    color ui-cl-color-8e8e8e
    @media (any-hover: hover)
      &:hover
        background ui-cl-color-c85252
        border-color ui-cl-color-c85252
        color ui-cl-color-white-theme

.settings-main-photoblock
  margin-bottom 25px

.settings-main-input
  display flex
  flex-direction column
  align-items flex-start

.user-info-form__select
  width 100%
  border 1px solid ui-cl-color-e3e3e3
  padding 15px 20px
  font-size font-size-downdefault
  color ui-cl-color-full-black
  display flex
  height auto
  align-items center
  white-space nowrap
  overflow hidden
  position relative
  border-radius border-small

.vs__dropdown-toggle
  width 100%
  border 1px solid ui-cl-color-e3e3e3
  padding 0
  background transparent
  font-size font-size-downdefault
  color ui-cl-color-full-black
  display flex
  align-items center
  white-space nowrap
  overflow hidden
  position relative
  border-radius border-small
.vs__selected
  margin 0
  padding 0
  color ui-cl-color-full-black
.vs__selected-options
  padding 11px 20px
.vs__search,
.vs__search:focus
  margin 0
  padding 0
.vs__actions
  padding-right 10px
  margin 0
.vs__clear
  vertical-align unset
  svg
    height 10px
    display block
@media (min-width: 320px) and (max-width: 768px)
  .settings
    .aside-filter
      background-color transparent !important
      &__title
        display block
    .aside-filter
      border-radius 0
      background none
      margin-bottom 20px
      &__list
        flex-direction column
      &__item
        padding 10px 0 10px 0
        font-size font-size-small
    &-main
      width 100%
      padding 15px
    &-push
      width 100% !important
      padding 15px !important
      &__name
        font-size font-size-super-upsmall !important
      &__item
        padding 10px 0 !important
      &__icon
        width 14px !important
    .user-info-form__input_stylus
      padding 10px
      font-size font-size-small
    .vs__selected-options
      padding 10px
      font-size font-size-small
    .user-info-form__select
      padding 10px
      font-size font-size-small
    .settings-push__check-label:after
      width 30px
    .settings-push__check-label:before
      width 17px
      height 17px
      top -8px
    .settings-push__check-input:checked + .settings-push__check-label:before
      left 32%
    .settings-security__block
      width 100%
      padding 15px
    .settings-delete__title
      font-size font-size-small-medium
      line-height 130%
      max-width unset
    .settings-delete__confirm-label
      font-size font-size-super-upsmall
    .settings-delete__confirm-label:before
      width 18px
      height 15px
      margin-right 15px
    .settings-delete__confirm-label:after
      width 0
      height 8px
      top 9px
      left 6px
    .settings-delete__confirm
      margin-bottom 15px
    .settings-delete__actions
      .btn.btn--disable
        margin 0
      .btn.btn--warning
        margin 0
    .settings-delete__actions-link
      font-size font-size-super-medium-small
    .settings-security__title
      font-size 20px
</style>
