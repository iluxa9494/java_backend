<template>
  <div class="search-all">
    <search-block
      :title="translationsLang.searchUserTitle"
      id="users"
      all="all"
    >
      <div class="friends__list">
        <friends-block
          v-for="user in users"
          :key="user.id"
          :info="user"
          subscribeButton
        />
      </div>
    </search-block>

    <search-block :title="translationsLang.searchNewsTitle" id="news" all="all">
      <news-block v-for="n in news" :key="n.id" :info="n" />
    </search-block>
  </div>
</template>

<script>
import { computed } from "vue";
import { useStore } from "vuex";
import useTranslations from "@/composables/useTranslations";
import SearchBlock from "@/components/Search/Block";
import FriendsBlock from "@/components/Friends/Block";
import NewsBlock from "@/components/News/Block";

export default {
  name: "SearchAll",
  components: { SearchBlock, FriendsBlock, NewsBlock },

  setup() {
    const store = useStore();
    const { translationsLang } = useTranslations();

    const news = computed(() =>
      store.getters["global/search/getResultById"]("news")
    );
    const users = computed(() =>
      store.getters["global/search/getResultById"]("users")
    );

    return {
      translationsLang,
      news,
      users,
    };
  },
};
</script>
