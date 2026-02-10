<template>
  <div class="search-news" v-if="news.length > 0">
    <div>
      <search-block :title="translationsLang.searchNewsTitle" id="news">
        <news-block
          v-for="n in filteredWall.posted"
          :key="n.id"
          :info="n"
          :queued="false"
        />
      </search-block>
    </div>
    <pagination
      v-if="getNewsPagination.totalElements > 5"
      :count="getNewsPagination.totalElements"
      v-model:page="page"
      :per-page="size"
    />
  </div>
  <div class="search-news__nonews" v-else>
    {{ translationsLang.searchNewsEmpty }}
  </div>
</template>

<script>
import { computed, onMounted, ref, watch } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import useTranslations from "@/composables/useTranslations";
import SearchBlock from "@/components/Search/Block";
import NewsBlock from "@/components/News/Block";
import Pagination from "@/components/UserPagination.vue";

export default {
  name: "SearchNews",
  components: { SearchBlock, NewsBlock, Pagination },

  setup() {
    const { getters, dispatch } = useStore();
    const route = useRoute();
    const page = ref(1);
    const size = ref(5);
    const total = ref(20);
    const { translationsLang } = useTranslations();

    const news = computed(() =>
      getters["global/search/getResultByIdSearch"]("news")
    );
    const getNewsQueryParams = computed(
      () => getters["global/search/getNewsQueryParams"]
    );
    const getNewsPagination = computed(
      () => getters["global/search/getNewsPagination"]
    );
    const filteredWall = computed(() => {
      const wall = news.value;
      const posted = wall.filter((item) => item.type === "POSTED");
      const queued = wall.filter((item) => item.type === "QUEUED");
      return { posted, queued };
    });

    watch(page, () => {
      const searchQuery = {
        ...getNewsQueryParams.value,
        page: page.value - 1,
        size: size.value,
      };
      dispatch("global/search/searchNews", searchQuery);
    });

    watch(
      () => getNewsPagination,
      () => {
        // page.value = getNewsPagination.value.page;
        // perPage.value = getNewsPagination.value.perPage;
        total.value = getNewsPagination.value.total;
      }
    );

    onMounted(() => {
      const { tags } = route.query;
      const searchQuery = {
        ...getNewsQueryParams.value,
        page: page.value - 1,
        size: size.value,
      };
      if (tags) searchQuery.tags = tags;
      if (news.value.length === 0) {
        dispatch("global/search/searchNews", searchQuery);
      }
    });

    return {
      page,
      size,
      total,
      translationsLang,
      news,
      getNewsPagination,
      filteredWall,
    };
  },
};
</script>
