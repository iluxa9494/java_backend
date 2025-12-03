<template>
  <div class="form-layout__footer">
    <a href="#" v-if="noUseContent" class="form-layout__footer-support">
      {{ translationsLang.support }}
    </a>

    <div
      class="form-layout__footer-language"
      :class="{ footer__auth: showLanguage }"
    >
      <span>{{ translationsLang.langSelect }}</span>
      <button class="active" @click.prevent="toggleLanguageList()">
        {{ translationsLang.lang }}
        <arrow-bottom />
      </button>
      <transition name="fade">
        <ul
          class="form-layout-list__language"
          v-if="languageShow"
          v-click-outside="closeLanguageList"
        >
          <li
            v-for="lang in languageList"
            :key="lang.id"
            @click="selectLanguage(lang)"
            @keydown.space.prevent="selectLanguage(lang)"
            @keydown.enter.prevent="selectLanguage(lang)"
            class="form-layout-list__language-item"
            :class="{ active__lang: activeLanguage === lang.name }"
          >
            <span>{{ lang.name }}</span>
            <span>{{ lang.desc }}</span>
          </li>
        </ul>
      </transition>
    </div>

    <span v-if="noUseContent" class="form-layout__footer-copyright">
      © Copyright {{ year }} NETWORK 51.
    </span>
  </div>
</template>

<script>
import { ref, computed } from "vue";
import { useStore } from "vuex";
import vClickOutside from 'click-outside-vue3';
import useTranslations from "@/composables/useTranslations";
import changeLocale from "@/utils/changeLocale";
import ArrowBottom from "@/Icons/ArrowBottom.vue";

export default {
  name: "FormLayoutFooter",

  directives: {
    clickOutside: vClickOutside.directive
  },

  components: {
    ArrowBottom,
  },

  props: {
    showLanguage: {
      type: Boolean,
      default: true,
    },

    noUseContent: {
      type: Boolean,
      default: true,
    },
  },

  setup() {
    const store = useStore();
    const { translationsLang } = useTranslations();

    const languageShow = ref(false);
    const selectedLanguage = ref({ id: 1, name: "Русский", desc: "RU" });

    const activeLanguage = computed(
      () => store.state.auth.languages.language.name
    );
    const year = computed(() => new Date().getFullYear());

    const languageList = [
      { id: 1, name: "Русский", desc: "RU" },
      { id: 2, name: "Английский", desc: "EN" },
    ];

    function closeLanguageList() {
      languageShow.value = false;
    }

    function selectLanguage(lang) {
      selectedLanguage.value = lang;
      languageShow.value = false;
      changeLocale(lang);
      store.commit("auth/languages/setLanguage", lang);
    }

    function toggleLanguageList() {
      languageShow.value = !languageShow.value;
    }

    return {
      activeLanguage,
      translationsLang,
      year,
      languageList,
      languageShow,
      closeLanguageList,
      selectLanguage,
      toggleLanguageList,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.form-layout__footer
  display flex
  align-items center
  flex-wrap wrap
  color ui-cl-color-gun-powder
  font-size font-size-small
  line-height 18px

.form-layout__footer-support
  color ui-cl-color-comet
  margin-right 8.3%

.form-layout-list__language
  position absolute
  display flex
  flex-direction column
  gap 3px
  top 35px
  right -35%
  padding 10px
  z-index 10
  background-color ui-cl-color-comet
  border-radius border-super-small
  &.fade-enter-active,
  &.fade-leave-active
    transition all .2s ease-in-out
  &.fade-enter,
  &.fade-leave-to
    opacity 0
  &:before
    content ""
    border solid transparent
    position absolute
    bottom 100%
    left 50%
    border-bottom-color ui-cl-color-comet
    border-width 9px
    margin-right -1px
  &-item
    display flex
    color #afadde
    align-items center
    font-weight font-weight-medium
    border-radius border-super-small
    gap 10px
    justify-content space-between
    padding 8px
    cursor pointer
    transition all .2s ease-in-out
    span:nth-child(2)
      color ui-cl-color-light-grey
    @media (any-hover: hover)
      &:hover
        background-color #4f4f65
        color ui-cl-color-white-theme
        span:nth-child(2)
          color ui-cl-color-white-theme
    &.active__lang
      background-color #4f4f65
      color ui-cl-color-white-theme
      span:nth-child(2)
        color ui-cl-color-white-theme

.form-layout__footer-language
  position relative
  margin-right 5.8%

  .active
    background: none
    border: none
    padding: 0
    margin: 0
    font-family: inherit
    font-size: inherit
    outline: none
    color: #434257
    line-height: 18px

    font-weight font-weight-bold
    cursor pointer
    transition all 0.2s

    &:hover
      color ui-cl-color-white-theme
</style>
