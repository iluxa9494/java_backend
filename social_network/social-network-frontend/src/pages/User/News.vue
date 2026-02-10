<template>
  <div class="news inner-page">
    <div class="inner-page__main">
      <div class="inner-page__main__container">
        <div class="news__add">
          <news-add user="user" />
        </div>

        <template v-if="feeds">
          <div class="news__list" v-if="getInfo">
            <news-block
              v-for="feed in filteredWall.posted"
              :key="feed.id"
              :info="feed"
              :queued="false"
              :edit="getInfo.id === feed.author.id"
              :deleted="getInfo.id === feed.author.id"
            />
          </div>
        </template>

        <div class="spinner-wrapper" v-if="loading">
          <spinner />
        </div>

        <error-block v-if="!loading && error" :message="errorMessage" />
      </div>

      <recommend-friend />

      <auto-paginator
        :page="feedsPagination.page || 0"
        :loading="loading"
        :totalPage="feedsPagination.total"
        :action="apiFeeds"
      />
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import RecommendFriend from "@/components/RecommendFriend.vue";
import NewsBlock from "@/components/News/Block";
import NewsAdd from "@/components/News/Add";
import Spinner from "@/components/SpinnerComponents.vue";
import ErrorBlock from "@/components/ErrorBlock.vue";
import AutoPaginator from "@/components/AutoPaginator.vue";

export default {
  name: "UserNews",
  components: {
    NewsBlock,
    NewsAdd,
    Spinner,
    ErrorBlock,
    AutoPaginator,
    RecommendFriend,
  },

  setup() {
    const { getters, state, dispatch, commit } = useStore();
    const frinedsList = ref(null);

    const getInfo = computed(() => getters["profile/info/getInfo"]);

    const loading = computed(() => state.global.status.loading);
    const error = computed(() => state.global.status.error);
    const errorMessage = computed(() => state.global.status.errorMessage);
    const feeds = computed(() => state.profile.feeds.feeds);
    const feedsPagination = computed(() => state.profile.feeds.feedsPagination);

    const filteredWall = computed(() => {
      const wall = feeds.value;
      const posted = wall.filter((item) => item.type === "POSTED");
      const queued = wall.filter((item) => item.type === "QUEUED");
      return { posted, queued };
    });

    onMounted(async () => {
      commit("profile/feeds/clearFeeds");
    });

    const apiFeeds = () => {
      dispatch("profile/feeds/apiFeeds");
    };
    // const setFeeds = () => {
    //   commit("profile/feeds/setFeeds");
    // };

    return {
      frinedsList,
      getInfo,
      loading,
      error,
      errorMessage,
      feeds,
      feedsPagination,
      filteredWall,
      apiFeeds,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.news-block__changed-time
  top 15px

.news-block .edit
  top 50px

.edit--small
  top 25px !important

.news__add
  border-radius border-small
  margin-bottom 30px

.news-add
  border-radius border-small

.news__list
  display flex
  flex-direction column
  gap 20px
  margin-bottom 30px

@media (min-width: 320px) and (max-width: 768px)
  .inner-page
    &__main__container
      margin-right 0 !important
</style>

<style lang="stylus" scoped>
.spinner-wrapper
  width 100px
  height 100px
  margin 0 auto
  padding 50px
</style>
