<template>
  <div class="form-layout__language" ref="language">
    <h4 class="form-layout__language-title">Выберите язык</h4>

    <div class="form-layout__language-search">
      <simple-svg :filepath="'/static/img/search.svg'" />

      <input
        class="form-layout__language-input"
        type="text"
        v-model="language"
        placeholder="Начните вводить название языка"
      />
    </div>

    <ul class="form-layout__language-list">
      <li
        class="form-layout__language-item"
        v-for="lang in filterLanguages"
        :key="lang.id"
        @keydown="setActiveHandler"
        @click="setActiveHandler(lang)"
      >
        {{ lang.title }}
      </li>
    </ul>
  </div>
</template>

<script>
import { ref, computed } from "vue";
import { useStore } from "vuex";

export default {
  name: "FormLayoutLanguage",
  setup() {
    const store = useStore();
    const language = ref("");

    const getLanguages = computed(
      () => store.getters["auth/languages/getLanguages"]
    );

    const filterLanguages = () => {
      if (language.value.length > 0) {
        getLanguages.value.filter(
          (el) =>
            el.text.toLowerCase().indexOf(language.value.toLowerCase()) >= 0
        );
      }
      return getLanguages.value;
    };

    // onMounted() {
    //   if (getLanguages.value.length === 0)
    // store.dispatch('auth/languages/apiLanguages');
    // };

    const setActiveHandler = (lang) => {
      store.dispatch("auth/languages/toggleLanguageBlock");
      store.dispatch("auth/languages/setActiveLanguage", lang);
    };

    return {
      getLanguages,
      filterLanguages,
      setActiveHandler,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.form-layout__language
  height 0
  width 100%
  background ui-cl-color-white-theme
  padding 35px 20px 20px 11.82%
  color ui-cl-color-charade
  transition all 0.2s

  &.active
    height auto

.form-layout__language-title
  font-family font-exo
  font-weight 200
  font-size 30px
  letter-spacing -0.01em
  color ui-cl-color-full-black
  margin-bottom 15px

.form-layout__language-search
  margin-bottom 30px
  max-width 345px
  position relative

  .simple-svg-wrapper
    width 16px
    height 16px
    position absolute
    left 0
    top 0

  .simple-svg
    stroke ui-cl-color-steel-gray
    fill transparent

.form-layout__language-input
  width 100%
  border-bottom 1px solid #BBBBC7
  padding 0 20px 5px
  cursor pointer
  font-weight font-weight-light
  font-size font-size-default
  color ui-cl-color-manatee

.form-layout__language-list
  display inline-flex
  flex-direction column
  flex-wrap wrap
  height auto
  max-height 350px

.form-layout__language-item
  font-weight font-weight-light
  font-size font-size-small
  line-height 25px
  color ui-cl-color-charade
  width 190px
  transition all 0.2s
  cursor pointer

  &:hover
    color ui-cl-color-eucalypt
</style>
