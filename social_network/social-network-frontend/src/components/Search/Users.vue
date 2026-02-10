<template>
  <div class="search-users" v-if="users.length > 0">
    <search-block :title="translationsLang.searchUserTitle" id="users">
      <div class="friends__list">
        <block-search
          v-for="user in users"
          :key="user.id"
          :info="user"
          subscribe-button
          :page="page"
          :size="size"
        />
      </div>

      <pagination
        :count="getUsersPagination.totalElements"
        v-model:page="page"
        :per-page="size"
      />
    </search-block>
  </div>
  <div class="search-news__nonews" v-else>
    {{ translationsLang.searchUserEmpty }}
  </div>
</template>

<script>
import { computed, onMounted, ref, watch } from "vue";
import { useStore } from "vuex";
import useTranslations from "@/composables/useTranslations";
import SearchBlock from "@/components/Search/Block";
import BlockSearch from "@/components/Friends/BlockSearch.vue";
import Pagination from "@/components/UserPagination.vue";

export default {
  name: "SearchUsers",
  components: { SearchBlock, BlockSearch, Pagination },

  setup() {
    const { getters, dispatch, commit } = useStore();
    const page = ref(1);
    const size = ref(5);
    const total = ref(20);
    const { translationsLang } = useTranslations();

    const users = computed(() =>
      getters["global/search/getResultByIdSearch"]("users")
    );
    const getUsersQueryParams = computed(
      () => getters["global/search/getUsersQueryParams"]
    );
    const getUsersPagination = computed(
      () => getters["global/search/getUsersPagination"]
    );

    watch(page, () => {
      const searchQuery = {
        ...getUsersQueryParams.value,
        page: page.value - 1,
        size: size.value,
      };
      dispatch("global/search/searchUsers", { payload: searchQuery });
    });

    watch(getUsersPagination, () => {
      total.value = getUsersPagination.value.total;
    });

    onMounted(() => {
      const searchQuery = {
        ...getUsersQueryParams.value,
        page: page.value - 1,
        size: size.value,
      };
      if (users.value.length === 0) {
        dispatch("global/search/searchUsers", { payload: searchQuery });
      }
    });

    const setUsersPagination = () => {
      commit("global/search/setUsersPagination");
    };

    return {
      page,
      size,
      total,
      translationsLang,
      users,
      getUsersPagination,
      setUsersPagination,
    };
  },
};
</script>
