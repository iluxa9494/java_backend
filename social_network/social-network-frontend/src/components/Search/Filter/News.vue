<template>
  <div>
    <button
      class="search-showmore-filter"
      @click.prevent="showBlock = !showBlock"
      :class="{ 'open-filter': showBlock }"
    >
      <span v-if="!showBlock">{{ translationsLang.searchNewsShowMoreButton }}</span>
      <span v-else>{{ translationsLang.searchNewsCloseMoreButton }}</span>
      <arrow-bottom />
    </button>
    <transition name="fade">
      <div class="search-filter--news" v-show="showBlock">
        <div class="search-filter">
          <div class="search-filter__block">
            <label class="search__label" for="search-news-author">{{
              translationsLang.searchNewsAuthor
            }}</label>
            <input
              class="search__input search-news-author"
              type="text"
              :placeholder="translationsLang.searchNewsInputPlaceholder"
              id="search-news-author"
              v-model="author"
            />
          </div>
          <div class="search-filter__block time">
            <label class="search__label">{{
              translationsLang.searchNewsTimePost
            }}</label>
            <select class="select search-filter__select" v-model="dateFrom">
              <option value="null">{{ translationsLang.searchNewsAllTime }}</option>
              <option value="year">
                {{ translationsLang.searchNewsLastYear }}
              </option>
              <option value="month">
                {{ translationsLang.searchNewsLastMonth }}
              </option>
              <option value="week">
                {{ translationsLang.searchNewsLastWeek }}
              </option>
            </select>
          </div>
          <div class="search-filter__block tags">
            <label class="search__label">{{
              translationsLang.searchNewsTag
            }}</label>
            <add-tags
              :allowManualAddition="false"
              :tags="updateTags"
              @change-tags="onChangeTags"
            />
          </div>
        </div>
        <div class="search-filter__block btn-news">
          <button class="search-filter__btn" @click.prevent="onSearchNews">
            {{ translationsLang.searchNewsBtnComplited }}
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, computed } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import useTranslations from "@/composables/useTranslations";
import dayjs from "dayjs";
import ArrowBottom from "@/Icons/ArrowBottom.vue";
import AddTags from "@/components/News/AddTags";

export default {
  name: "SearchFilterNews",
  components: { AddTags, ArrowBottom },

  setup() {
    const { getters, dispatch } = useStore();
    const route = useRoute();
    const router = useRouter();
    const updateTags = ref([]);
    const dateFrom = ref("null");
    const dateTo = ref(0);
    const page = ref(1);
    const itemPerPage = ref(5);
    const author = ref("");
    const showBlock = ref(false);
    const { translationsLang } = useTranslations();

    const getNewsQueryParams = computed(
      () => getters["global/search/getNewsQueryParams"]
    );
    const searchText = computed(() => getters["global/search/searchText"]);

    // watch(
    //   "$route.query.tags",
    //   (newVal) => {
    //     if (newVal) {
    //       updateTags.value = newVal.split(",");
    //     }
    //   },
    //   { immediate: true }
    // );

    const onChangeTags = (tags) => {
      updateTags.value = tags;
      const dataTagsSplit = tags.map((tag) => tag.name);
      router.replace({
        query: {
          ...route.query,
          tags: dataTagsSplit.join(","),
        },
      });
    };

    const onSearchNews = () => {
      const dataTags = updateTags.value;
      const dataTagsSplit = dataTags.map((tag) => tag.name);
      const dateFromNew =
        dateFrom.value === "null" ? 0 : dayjs().subtract(1, dateFrom.value).valueOf();
      const dateToNew = (dateTo.value = dayjs().format(
        "YYYY-MM-DDTHH:mm:ss.SSS[Z]"
      ));
      const formattedDateFrom = dayjs(dateFromNew).format(
        "YYYY-MM-DDTHH:mm:ss.SSS[Z]"
      );
      let searchQuery = {
        ...getNewsQueryParams.value,
        dateFrom: formattedDateFrom,
        dateTo: dateToNew,
        author: author.value,
        tags: dataTagsSplit.join(","),
        text: searchText.value,
        withFriends: true,
      };
      console.log(formattedDateFrom);
      console.log(dateToNew);
      dispatch("global/search/searchNews", searchQuery);
    };

    return {
      updateTags,
      dateFrom,
      dateTo,
      page,
      itemPerPage,
      author,
      showBlock,
      translationsLang,
      onChangeTags,
      onSearchNews,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

  .search-filter--news
    &.fade-enter-active,
    &.fade-leave-active
      transition all .2s ease-in-out
    &.fade-enter,
    &.fade-leave-to
      opacity 0

  .search-news-author
    width 200px
  .search-filter__block .add-tags__search-results
    top 100%
    border-radius border-super-small
    max-width 200px
</style>
