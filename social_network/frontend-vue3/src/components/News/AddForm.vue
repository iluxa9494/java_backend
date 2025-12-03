<template>
  <div class="news-add-form">
    <button
      class="news-hint__show"
      v-if="showHintButton"
      @click.prevent="showHintButton()"
    >
      ?
    </button>
    <transition name="fade">
      <div v-if="showHint" class="news-hint">
        <transition name="fade">
          <div class="steps__news news-hint__step-first" v-if="step === 1">
            <div class="steps_step">1.</div>
            <p>{{ translationsLang.newsStepFirst }}</p>
          </div>
        </transition>
        <transition name="fade">
          <div class="steps__news news-hint__step-second" v-if="step === 2">
            <div class="steps_step">2.</div>
            <p>{{ translationsLang.newsStepSecond }}</p>
          </div>
        </transition>
        <transition name="fade">
          <div class="steps__news news-hint__step-third" v-if="step === 3">
            <div class="steps_step">3.</div>
            <p>{{ translationsLang.newsStepThird }}</p>
          </div>
        </transition>
        <transition name="fade">
          <div class="steps__news news-hint__step-four" v-if="step === 4">
            <div class="steps_step">4.</div>
            <p>{{ translationsLang.newsStepFour }}</p>
          </div>
        </transition>
      </div>
    </transition>
    <div
      class="news-hint__overlay"
      @keydown="hideHints()"
      @click="hideHints()"
      v-if="showHint"
    ></div>
    <div class="news-add__actions-buttons">
      <button class="close_modal bold" @click.prevent="closeAddForm()">
        x
      </button>
    </div>

    <form class="news-add__main" action="#">
      <div class="news-add__text">
        <textarea
          class="news-add__text-title"
          :placeholder="translationsLang.newsAddTitle"
          v-model="title"
          maxlength="200"
          v:pattern="200"
          type="textarea"
        />
        <div v-if="src">
          <img :src="src" :alt="'photo'" class="post-image" />
        </div>

        <div>
          <input
            class="user-info-form__input_stylus-photo"
            type="file"
            ref="photoRef"
            id="photo"
            @change="processFile($event)"
            accept="image/*"
            max-size="3MB"
          />
        </div>

        <editor-content class="news-add__text-main" :editor="editor" />

        <div class="news-add__actions" :editor="editor">
          <div class="news-add__actions-buttons">
            <button
              class="bold"
              :class="{ 'is-active': editor && editor.isActive('bold') }"
              @click.prevent="editor.chain().focus().toggleBold().run()"
            >
              ж
            </button>

            <button
              class="italic"
              :class="{ 'is-active': editor && editor.isActive('italic') }"
              @click.prevent="editor.chain().focus().toggleItalic().run()"
            >
              к
            </button>

            <button
              class="underline"
              :class="{ 'is-active': editor && editor.isActive('underline') }"
              @click.prevent="
                editor.chain().focus().toggleMark('underline').run()
              "
            >
              ч
            </button>

            <div
              class="news-add__block photo"
              @keydown="loadPhoto()"
              @click.prevent="loadPhoto()"
            >
              <img src="@/assets/static/img/photo.svg" alt="img.svg" />
            </div>

            <div class="news-add__block" v-if="src">
              <button class="bold" @click.prevent="deletePhoto()">x</button>
            </div>
          </div>
        </div>
      </div>

      <div class="news-add__settings">
        <h4 class="news-add__settings-title">
          {{ translationsLang.newsAddSettings }}
        </h4>

        <add-tags :tags="tags" @change-tags="onChangeTags" />

        <div class="is_planing" v-if="isPlaning">
          <h6>{{ translationsLang.newsAddSettingsTimePublished }}</h6>

          <p>
            {{ day }} {{ monthNames[month] }} {{ year }} г. в {{ defaultTime }}
          </p>
        </div>

        <div class="plaining-list__btns">
          <button
            v-if="!edit || deffered"
            class="post-btn-planing plaining-hole"
            @click.prevent="openModal()"
          >
            {{ translationsLang.newsAddQueued }}
          </button>
          <button class="post-btn-planing" @click.prevent="submitForm()">
            {{ translationsLang.newsAddPosted }}
          </button>
        </div>
      </div>
    </form>

    <modal class="news-add__modal" v-model="modalShow">
      <div class="news-add__modal-selects deferred-post">
        <div class="alert-deferred-post">
          <span>{{ translationsLang.newsAddQueuedTimeToPosted }}</span>
          <span
            >{{ day || "01" }} {{ monthNames[month] || "январь" }}
            {{ year || "1970" }} г. в {{ defaultTime || "00:00" }}</span
          >
        </div>
        <div class="data-pickers-list">
          <vue-date-picker
            v-model="date"
            type="date"
            :placeholder="translationsLang.newsAddDataPickedData"
            :enable-time-picker="false"
            :locale="translation.desc"
            :cancelText="translationsLang.newsAddDataPickedButtonClose"
            :selectText="translationsLang.newsAddDataPickedButtonSelect"
            :dark="selectedTheme"
          >
          </vue-date-picker>
          <vue-date-picker
            v-model="dateTime"
            :hour-options="hours"
            format="HH:mm"
            value-type="format"
            type="time"
            :placeholder="translationsLang.newsAddDataPickedTime"
            time-picker
            :locale="translation.desc"
            :cancelText="translationsLang.newsAddDataPickedButtonClose"
            :selectText="translationsLang.newsAddDataPickedButtonSelect"
            :dark="selectedTheme"
          >
          </vue-date-picker>
        </div>
      </div>

      <template v-slot:actions>
        <div class="plaining-list__btns on_plaining">
          <button class="post-btn-planing" @click.prevent="onPlaning()">
            {{ translationsLang.newsAddQueued }}
          </button>
          <button
            class="post-btn-planing plaining-hole"
            @click.prevent="onCancelPlaning()"
          >
            {{ translationsLang.cancel }}
          </button>
        </div>
      </template>
    </modal>
  </div>
</template>

<script>
import {
  ref,
  reactive,
  computed,
  watch,
  onMounted,
  onBeforeUnmount,
} from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import useTranslations from "@/composables/useTranslations";
import { Editor, EditorContent } from "@tiptap/vue-3";
import StarterKit from "@tiptap/starter-kit";
import Underline from "@tiptap/extension-underline";
import AddTags from "@/components/News/AddTags.vue";
import Modal from "@/components/Modal.vue";
import VueDatePicker from "@vuepic/vue-datepicker";
import "@vuepic/vue-datepicker/dist/main.css";
import pattern from "@/directives/pattern";

export default {
  name: "NewsAddForm",
  components: {
    AddTags,
    Modal,
    VueDatePicker,
    EditorContent,
  },

  directive: {
    pattern,
  },

  props: {
    edit: Boolean,
    deffered: Boolean,
    info: Object,
  },

  setup(props, { emit }) {
    const { getters, state, dispatch, commit } = useStore();
    const route = useRoute();
    const title = ref("");
    const content = ref("");
    const tags = ref([]);
    const linkUrl = ref("");
    const isOpenLinkMenu = ref(false);
    const modalShow = ref(false);
    const isPlaning = ref(null);
    const editor = ref(null);
    const showHint = ref(false);
    const step = ref(1);
    const day = ref("");
    const month = ref("");
    const monthNames = reactive([
      "Январь",
      "Февраль",
      "Март",
      "Апрель",
      "Май",
      "Июнь",
      "Июль",
      "Август",
      "Сентябрь",
      "Октябрь",
      "Ноябрь",
      "Декабрь",
    ]);
    const year = ref("");
    const date = ref("");
    const dateTime = ref("");
    const hours = ref(Array.from({ length: 16 }).map((_, i) => i + 8));
    const lastDate = ref(null);
    const currentUtcDateTime = ref(null);
    const photoRef = ref(null);
    const imagePath = ref("");
    const selectedTheme = ref();

    const { translationsLang } = useTranslations();

    const componentKey = ref(0);
    const photo = ref("");
    const src = ref("");
    const attrs = reactive([
      {
        key: "weekends",
        content: "weekends",
        dates: {
          start: new Date(2018, 0, 1),
          end: new Date(2022, 0, 1),
          weekdays: [1, 7],
        },
      },
    ]);

    const translation = computed(() => state.auth.languages.language);
    const getTheme = computed(() => getters["global/alert/getTheme"]);
    const getInfo = computed(() => getters["profile/info/getInfo"]);
    const getStoragePostPhoto = computed(
      () => getters["global/storagePostPhoto/getStoragePostPhoto"]
    );

    const defaultTime = computed(() => {
      if (dateTime.value) {
        return `${dateTime.value.hours}:${dateTime.value.minutes}`;
      }
      return "";
    });

    const fullDate = computed(() => {
      if (dateTime.value && date.value) {
        const dateNow = new Date(date.value);
        dateNow.setHours(Number(dateTime.value.hours));
        dateNow.setMinutes(Number(dateTime.value.minutes));
        const offset = dateNow.getTimezoneOffset();
        const localDate = new Date(dateNow.getTime() - offset * 60 * 1000);
        return localDate.toISOString();
      }
      return "";
    });

    const currentDate = computed(() => {
      const date = new Date();
      const offset = date.getTimezoneOffset();
      const localDate = new Date(date.getTime() - offset * 60 * 1000);
      return localDate.toISOString();
    });

    const currentDay = computed(() => {
      const day = String(new Date().getDate()).padStart(2, "0");
      return day;
    });

    const currentMonth = computed(() => {
      const month = monthNames[new Date().getMonth()];
      return month;
    });

    const currentYear = computed(() => {
      const year = new Date().getFullYear();
      return year;
    });

    const currentTime = computed(() => {
      let hour = new Date().getHours();
      let minute = new Date().getMinutes();
      if (hour.toString().length == 1) {
        hour = "0" + hour;
      }
      if (minute.toString().length == 1) {
        minute = "0" + minute;
      }
      const time = hour + ":" + minute;
      return time;
    });

    const currentUtc = computed(() => {
      if (dateTime.value) {
        const dateNow = new Date(date.value);
        dateNow.setHours(Number(dateTime.value.hours));
        dateNow.setMinutes(Number(dateTime.value.minutes));
        const offset = dateNow.getTimezoneOffset();
        const localDate = new Date(dateNow.getTime() - offset * 60 * 1000);
        return localDate;
      }
      return "";
    });

    watch(getTheme, (newVal) => {
      changeTheme(newVal);
    });

    watch(dateTime, () => {
      lastDate.value = fullDate.value;
      currentUtcDateTime.value = currentUtc.value;
    });

    watch(lastDate, (newDate) => {
      const dateNow = new Date(newDate);
      day.value = dateNow.getUTCDate();
      month.value = dateNow.getUTCMonth() + 1;
      year.value = dateNow.getUTCFullYear();
    });

    onMounted(() => {
      if (props.info && props.info.imagePath) {
        src.value = props.info.imagePath;
      }
      if (props.edit) {
        title.value = props.info.title;
        tags.value = props.info.tags;
        editor.value = new Editor({
          content: `${props.info.postText}`,
          extensions: [StarterKit, Underline],
        });
      } else {
        editor.value = new Editor({
          content: "",
          extensions: [StarterKit, Underline],
        });
      }
      lastDate.value = fullDate.value;
      currentUtcDateTime.value = currentUtc.value;
      changeTheme(getTheme.value);
    });

    onBeforeUnmount(() => {
      editor.value.destroy();
    });

    const changeTheme = (theme) => {
      if (theme === "light") {
        selectedTheme.value = false;
      } else {
        selectedTheme.value = true;
      }
    };

    const showHintButton = () => {
      if (step.value <= 5) {
        showHint.value = true;
      } else {
        showHint.value = false;
      }
    };

    const hideHints = () => {
      step.value += 1;
      if (step.value >= 5) {
        showHint.value = false;
        step.value = 1;
      }
    };

    const onChangeTags = (newTags) => {
      tags.value = newTags;
    };

    const loadPhoto = () => {
      photoRef.value.click();
    };

    const deletePhoto = () => {
      photo.value = "";
      src.value = "";
      dispatch("global/storagePostPhoto/setStoragePostPhoto", "");
    };

    const processFile = (event) => {
      [photo.value] = event.target.files;
      const reader = new window.FileReader();
      reader.onload = (e) => {
        src.value = e.target.result;
      };
      reader.readAsDataURL(photo.value);
    };

    const openModal = () => {
      modalShow.value = true;
    };

    const closeModal = () => {
      modalShow.value = false;
    };

    const submitForm = async () => {
      if (title.value.length <= 3 || editor.value.getHTML().length <= 7) {
        if (modalShow.value) closeModal();
        dispatch("global/alert/setAlert", {
          status: "error",
          text: "Тема должен состоять не менее трех символов, текст - не менее семи.",
        });
        return;
      }

      if (photo.value) {
        await dispatch(
          "global/storagePostPhoto/apiStoragePostPhoto",
          photo.value
        );

        if (getStoragePostPhoto.value) {
          imagePath.value = getStoragePostPhoto.value;
        }
      } else if (!src.value) {
        imagePath.value = "";
      } else if (props.info && props.info.imagePath) {
        imagePath.value = props.info.imagePath;
      }

      await dispatch("profile/feeds/actionsFeed", {
        imagePath: imagePath.value,
        route: route.name,
        postId: props.info ? props.info.id : null,
        edit: props.edit,
        id: getInfo.value.id,
        title: title.value,
        postText: editor.value.getHTML(),
        tags: tags.value,
        publishDate: isPlaning.value && lastDate.value,
      }).then(() => {
        emit("submit-complete");
        commit("global/storagePostPhoto/setStoragePostPhoto", null);
      });
    };

    const onPlaning = () => {
      if (currentUtcDateTime.value < new Date()) {
        dispatch("global/alert/setAlert", {
          status: "error",
          text: "Запланированное время не может быть в прошлом времени!",
        });
        return;
      }
      isPlaning.value = true;
      modalShow.value = false;
    };

    const onCancelPlaning = () => {
      isPlaning.value = null;
      year.value = 1970;
      month.value = 1;
      day.value = 1;
      defaultTime.value = "00:00";
      modalShow.value = false;
    };

    const closeAddForm = () => {
      emit("close-form");
    };

    return {
      title,
      content,
      tags,
      linkUrl,
      isOpenLinkMenu,
      modalShow,
      isPlaning,
      editor,
      showHint,
      step,
      day,
      month,
      monthNames,
      year,
      date,
      dateTime,
      hours,
      translationsLang,
      componentKey,
      photoRef,
      photo,
      src,
      attrs,
      currentDate,
      currentDay,
      currentMonth,
      currentYear,
      currentTime,
      selectedTheme,
      translation,
      defaultTime,
      showHintButton,
      hideHints,
      onChangeTags,
      loadPhoto,
      deletePhoto,
      processFile,
      openModal,
      closeModal,
      submitForm,
      onPlaning,
      onCancelPlaning,
      closeAddForm,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.steps__news
  position absolute
  width 400px
  display flex
  flex-direction column
  z-index 25
  p
    background-color #333
    color #fff
    border-radius 5px
    font-size 13px
    padding 15px 35px
  &.fade-enter-active,
  &.fade-leave-active
    transition all .2s ease-in-out
  &.fade-enter,
  &.fade-leave-to
    opacity 0

.steps_step
  position absolute
  top 15px
  left 10px
  font-size 18px
  color #fff
  font-weight bold

.news-hint
  position absolute
  width 100%
  height 100%
  top 0
  left 0
  &.fade-enter-active,
  &.fade-leave-active
    transition all .2s ease-in-out
  &.fade-enter,
  &.fade-leave-to
    opacity 0
  &__show
    position absolute
    width 20px
    height 20px
    border-radius 50%
    color ui-cl-color-medium-grey
    border 1px solid ui-cl-color-medium-grey
    background-color transparent
    font-size 14px
    line-height 18px
    text-align center
    top 10px
    left 10px
  &__overlay
    position absolute
    top 0
    left 0
    border-radius 10px
    width 100%
    height 100%
    background-color #3a3a3a
    outline 1px solid #ffffff
    opacity 0.5
    overflow hidden
    z-index 9
  &__step-first
    top 80px
    left 35px
    &:before
      content ""
      border solid transparent
      position absolute
      left 8px
      bottom 100%
      border-bottom-color #333
      border-width 5px
      margin-left 0
  &__step-second
    top 120px
    left 285px
    z-index 10
    &:before
      content ""
      border solid transparent
      position absolute
      right 100%
      top 50%
      border-right-color #333
      border-width 5px
      margin-top -9px
  &__step-third
    top 140px
    right 55px
    z-index 10
    &:before
      content ""
      border solid transparent
      position absolute
      bottom 100%
      right 8px
      border-bottom-color #333
      border-width 5px
      margin-right -1px
  &__step-four
    bottom 30px
    right 220px
    z-index 10
    &:before
      content ""
      border solid transparent
      position absolute
      left 100%
      top 50%
      border-left-color #333
      border-width 5px
      margin-top -9px

@media (min-width: 320px) and (max-width: 768px)
  .news-add
    &__main
      flex-direction column
      padding 0 15px 15px 15px
    &__text
      margin-right 0
      padding-right 0
      border-right 0
      &__title
        padding-bottom 0
        font-size font-size-updefault
    &__actions-buttons
      padding 10px 15px 0 15px
      justify-content flex-end
      .close_modal
        position unset
    &__settings
      max-width unset
      &-title
        font-size font-size-updefault
    .modal__wrapper
      padding 15px
      max-width 300px
  .news-add__actions
    justify-content unset
    padding 15px 0 20px 0
</style>
<style lang="stylus" media="screen">
@import '@/assets/stylus/base/vars.styl';
.close_modal
  position relative
  top 8px
  left 95%
  width 25px

.ProseMirror-focused
  height 200px

.ProseMirror
  height 200px

.is_planing
  font-size font-size-small
  background ui-cl-color-white-bright
  padding 5px
  color #6b6b6b
  display flex
  flex-direction column
  border-radius 10px 10px 0 0

.post-image
  width 250px
  margin-bottom 30px

.news-add__text-main
  cursor text

.deferred-post
  display flex
  flex-direction column
  gap 20px
  align-items center
  justify-content center
  border-top 0
  padding-top 0

.data-pickers-list
  display flex
  align-items center
  gap 15px
  .mx-datepicker
    width auto

.alert-deferred-post
  display flex
  width 100%
  flex-direction column
  align-items center
  justify-content center
  padding 10px
  font-size font-size-default
  background #f6f6f6
  border-radius border-super-small
  border 1px solid #d9d9d9
  span:nth-child(1)
    font-weight font-weight-bold

.news-add__text-title
  border-bottom: 1px solid ui-cl-color-e6e6e6
  padding-bottom 20px

.post-btn-planing
  display block
  text-align center
  color ui-cl-color-white-theme
  width 100%
  padding 10px 5px
  border 2px solid ui-cl-color-eucalypt
  transition all 0.2s ease-in-out
  background ui-cl-color-eucalypt
  @media (any-hover: hover)
    &:hover
      background ui-cl-color-grey-color
      border-color ui-cl-color-grey-color
      color ui-cl-color-white-theme

.post-btn-planing.plaining-hole
  background transparent
  color ui-cl-color-eucalypt
  @media (any-hover: hover)
    &:hover
      background ui-cl-color-eucalypt
      border-color ui-cl-color-eucalypt
      color ui-cl-color-white-theme

.plaining-list__btns
  display flex
  flex-direction column
  gap 10px
  width 100%

.plaining-list__btns.on_plaining
  flex-direction row
</style>
