<template>
  <ul class="search-tabs">
    <li
      class="search-tabs__item"
      v-for="(item, index) in tabs"
      :key="index"
      :class="{ active: item.id === tabSelect }"
      @click="changeTab(item.id)"
    >
      {{ currentTranslations === "Русский" ? item.text : item.textEng }}
    </li>
  </ul>
</template>

<script>
import { computed } from "vue";
import { useStore } from "vuex";

export default {
  name: "SearchTabs",

  setup() {
    const { getters, state, dispatch } = useStore();

    const tabs = computed(() => getters["global/search/tabs"]);
    const tabSelect = computed(() => getters["global/search/tabSelect"]);
    const currentTranslations = computed(
      () => state.auth.languages.language.name
    );

    const changeTab = (id) => {
      dispatch("global/search/changeTab", id);
    };

    return {
      tabs,
      tabSelect,
      currentTranslations,
      changeTab,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.search-tabs
  display flex
  align-items center
  gap 15px
  margin-bottom 15px

.search-tabs__item
  display inline-block
  text-align center
  color ui-cl-color-eucalypt
  padding 8px 13px
  border 2px solid #21a45d
  transition all 0.2s ease-in-out
  cursor pointer
  &:hover
    background #333
    border-color #333
    color #fff

  &.active
    color ui-cl-color-white-theme
    background ui-cl-color-eucalypt
    border-color ui-cl-color-eucalypt
    &:hover
      color ui-cl-color-white-theme
      background ui-cl-color-eucalypt
      border-color ui-cl-color-eucalypt
</style>
