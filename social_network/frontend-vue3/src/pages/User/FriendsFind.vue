<template>
  <div class="friends friends-find inner-page">
    <div class="inner-page__main">
      <div class="friends__header">
        <h2 class="friends__title">
          <template v-if="searchUsers.length === 0">{{
            translationsLang.friendsFindTitle
          }}</template>
          <template v-else>
            {{ translationsLang.friendsFindFindFirstText }}
            {{ searchUsers.length }}
            {{ translationsLang.friendsFindFindSecondText }}</template
          >
        </h2>
      </div>

      <div class="friends__content">
        <div class="friends__list-wrapper">
          <div class="friends__list" v-if="searchUsers.length > 0">
            <friends-block
              v-for="user in searchUsers"
              :key="user.id"
              :info="user"
            />
          </div>
        </div>

        <div class="inner-page__aside">
          <friends-search />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted } from "vue";
import { useStore } from "vuex";
import useTranslations from "@/composables/useTranslations";
import FriendsBlock from "@/components/Friends/BlockSearch";
import FriendsSearch from '@/components/Friends/Search';

export default {
  name: "FriendsFind",
  components: {
    FriendsBlock,
    FriendsSearch
  },

  setup() {
    const store = useStore();
    const { getters, dispatch } = store;
    const isPossible = ref(true);
    const friends = ref([]);
    const statusCode = ref("");
    const { translationsLang } = useTranslations();

    const possibleFriends = computed(() =>
      getters["profile/friends/getResultById"]("recommendations")
    );
    const searchUsers = computed(() =>
      getters["global/search/getResultByIdSearch"]("users")
    );

    watch(searchUsers, (newVal) => {
      if (newVal.length === 0) {
        dispatch("global/alert/setAlert", {
          status: "response",
          text: "Пользователи с такими параметрами не найдены!",
        });
        return;
      }
    });

    onMounted(() => {
      if (possibleFriends.value.length === 0) {
        dispatch("profile/friends/apiRecommendations");
      }
      if (friends.value.length === 0) {
        dispatch("profile/friends/apiFriends", statusCode.value);
      }
    });

    // const apiAddFriends = () => {
    //   dispatch("profile/friends/apiFriends");
    // };

    return {
      isPossible,
      friends,
      translationsLang,
      possibleFriends,
      searchUsers,
      // apiAddFriends,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.friends-possible
  border-radius border-small
  .friends-possible__btn
    color
.friends-find
  display grid
  width 100%
  grid-template-columns 1fr
  gap 20px
</style>

<style lang="stylus" scoped>
@import '../../assets/stylus/base/vars.styl';
.inner-page__main, .friends-block
  max-width none

.friends__content
  position relative
  display flex
  justify-content space-between

.friends__list-wrapper
  width 100%
  margin-right 40px

.friends__list
  margin-right 0

.friends-block
  margin-right 0

.friends-search__select
  border-radius border-super-small

.friends-possible
  margin-top 0

.inner-page__aside
  position sticky
  top 85px

@media (max-width 892px)
  .friends__content
    flex-direction column
    align-items stretch
  .inner-page__aside
    position static
    max-width none
    width 100%
    margin-bottom 40px
    order -1
  .friends__list-wrapper
    margin-right 0
</style>


