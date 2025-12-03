<template>
  <div class="search">
    <div class="search__tabs">
      <search-tabs />
      <component
        :is="`search-filter-${tabSelect}`"
        v-if="tabSelect !== 'all'"
      />
    </div>

    <div class="search__main" :class="{ high: tabSelect !== 'all' }">
      <component :is="`search-${tabSelect}`" />
    </div>
  </div>
</template>

<script>
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import SearchTabs from "@/components/Search/Tabs";
import SearchAll from "@/components/Search/All";
import SearchUsers from "@/components/Search/Users";
import SearchNews from "@/components/Search/News";
import SearchFilterUsers from "@/components/Search/Filter/Users";
import SearchFilterNews from "@/components/Search/Filter/News";

export default {
  name: "ComponentSearch",
  components: {
    SearchTabs,
    SearchAll,
    SearchUsers,
    SearchNews,
    SearchFilterUsers,
    SearchFilterNews,
  },

  setup() {
    const { getters, dispatch, commit } = useStore();
    const route = useRoute();
    const hasSearchText = ref(true);

    const searchText = computed(() => getters["global/search/searchText"]);
    const tabSelect = computed(() => getters["global/search/tabSelect"]);
    // const tabs = computed(() => getters["global/search/tabs"]);

    watch(searchText, () => {
      commit("global/search/routePushWithQuery", tabSelect.value);
      // const newSelectTab = tabs.value.find((tab) => tab.text === newVal || tab.textEng === newVal);
      // if (newSelectTab === undefined) {
      //   dispatch("global/alert/setAlert", {
      //     status: "error",
      //     text: "Неверное слово или с заглавной буквы!",
      //   });
      //   return;
      // }
      // console.log(newSelectTab.id);
      // commit("global/search/routePushWithQuery", newSelectTab.id);
    });

    onMounted(() => {
      if (route.query.tab)
        commit("global/search/setTabSelect", route.query.tab);
      if (route.query.text || route.query.text == "")
        dispatch("global/search/searchAll", route.query.text);
      else hasSearchText.value = false;
      document.body.onkeydown = (e) => {
        if (e.key === "Enter") hasSearchText.value = true;
      };
    });
    onBeforeUnmount(() => {
      dispatch("global/search/clearSearch");
    });

    return {
      searchText,
      tabSelect,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.search
  width 100%

.search__tabs
  background #fff
  box-shadow box-shadow-main
  border-radius border-small
  padding 20px

  @media (max-width breakpoint-xxl)
    left sidebar-width-xl

.search__main
  padding 100px 40px 50px
  height 100%

  &.high
    padding-top 20px

@media (max-width 1024px)
  .search__tabs
    left 120px
</style>
