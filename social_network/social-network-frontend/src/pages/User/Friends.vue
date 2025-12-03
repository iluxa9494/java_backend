<template>
  <div class="friends inner-page">
    <div class="inner-page__main">
      <div class="friends__header">
        <div class="friends__header-left">
          <h2
            class="friends__title"
            :style="
              friends.length === 0 ||
              activeTab === 'no-data' ||
              activeTab === 'no-data-users'
                ? 'margin-bottom: 0'
                : ''
            "
          >
            {{ translationsLang.friendsTitle }}
          </h2>
          <div
            class="friends__search"
            v-if="
              friends.length !== 0 &&
              activeTab !== 'no-data' &&
              activeTab !== 'no-data-users'
            "
          >
            <div class="friends__search-icon">
              <search-icon />
            </div>

            <input
              class="friends__search-input"
              :placeholder="translationsLang.friendsSearchPlaceholder"
              v-model="firstName"
              @keydown.enter="searchFriends"
            />
          </div>
        </div>
        <ul class="friends__tabs">
          <li class="friends__tabs__item" v-if="localFriends.REQUEST_FROM">
            <button
              :class="{
                'friends__tabs__link active': activeTab === 'REQUEST_FROM',
              }"
              @click.prevent="setActive('REQUEST_FROM')"
            >
              {{
                `${translationsLang.friendsRequestsFrom}` +
                " " +
                `(${paginations.REQUEST_FROM.totalElements || 0})`
              }}
            </button>
          </li>

          <li class="friends__tabs__item" v-if="localFriends.REQUEST_TO">
            <button
              :class="{
                'friends__tabs__link active': activeTab === 'REQUEST_TO',
              }"
              @click.prevent="setActive('REQUEST_TO')"
            >
              {{
                `${translationsLang.friendsRequestsTo}` +
                " " +
                `(${paginations.REQUEST_TO.totalElements || 0})`
              }}
            </button>
          </li>

          <li class="friends__tabs__item" v-if="localFriends.FRIEND">
            <button
              :class="{ 'friends__tabs__link active': activeTab === 'FRIEND' }"
              @click.prevent="setActive('FRIEND')"
            >
              {{
                `${translationsLang.sidebarFriend}` +
                " " +
                `(${paginations.FRIEND.totalElements || 0})`
              }}
            </button>
          </li>

          <li class="friends__tabs__item" v-if="localFriends.SUBSCRIBED">
            <button
              :class="{
                'friends__tabs__link active': activeTab === 'SUBSCRIBED',
              }"
              @click.prevent="setActive('SUBSCRIBED')"
            >
              {{
                `${translationsLang.friendsMySubscribe}` +
                " " +
                `(${paginations.SUBSCRIBED.totalElements || 0})`
              }}
            </button>
          </li>

          <li class="friends__tabs__item" v-if="localFriends.BLOCKED">
            <button
              :class="{ 'friends__tabs__link active': activeTab === 'BLOCKED' }"
              @click.prevent="setActive('BLOCKED')"
            >
              {{
                `${translationsLang.friendsBlocked}` +
                " " +
                `(${paginations.BLOCKED.totalElements || 0})`
              }}
            </button>
          </li>

          <li class="friends__tabs__item" v-if="localFriends.WATCHING">
            <button
              :class="{
                'friends__tabs__link active': activeTab === 'WATCHING',
              }"
              @click.prevent="setActive('WATCHING')"
            >
              {{
                `Подписки` +
                " " +
                `(${paginations.WATCHING.totalElements || 0})`
              }}
              <!-- {{ `${translationsLang.friendsMyPesonalSubscribed}` + ' ' + `(${paginations.WATCHING.totalElements || 0})` }} -->
            </button>
          </li>
        </ul>
      </div>

      <div class="friends__content">
        <div class="friends__list">
          <!-- Запросы в друзья / поиск реализован -->
          <div v-if="activeTab === 'REQUEST_FROM'" class="friends_group active">
            <h3 class="friends_group_title friends__title">
              {{ translationsLang.friendsRequestsFrom }}
            </h3>

            <div
              v-if="friendSearch !== null && friendSearch.content.length !== 0"
            >
              <div class="friend__search__resultats">
                <p class="friend__search-title">
                  {{ translationsLang.friendsSearchDescription }} "{{
                    localFirstName
                  }}":
                </p>
                <button
                  class="friend__search-clear"
                  @click.prevent="resetFriendSearch"
                >
                  {{ translationsLang.friendsSearchResultClear }}
                  <span>✕</span>
                </button>
              </div>

              <search-block
                v-for="user in friendSearch.content"
                :key="user.id"
                :info="user"
              />
            </div>

            <div
              v-if="friendSearch === null || friendSearch.content.length === 0"
            >
              <friends-block
                friend="friend"
                v-for="friend in localFriends?.REQUEST_FROM"
                :key="friend.id"
                :info="friend"
                accept-button
              />
              <button
                class="friends-btn__more"
                @click.prevent="loadMoreFriends('REQUEST_FROM')"
                v-if="
                  localFriends?.REQUEST_FROM.length !==
                    paginations.REQUEST_FROM.totalElements ||
                  paginations.REQUEST_FROM.totalElements === 0
                "
              >
                {{ translationsLang.friendsShowMore }}
              </button>
            </div>
          </div>
          <!-- Исходящие запросы / поиск реализован -->
          <div v-if="activeTab === 'REQUEST_TO'" class="friends_group active">
            <h3 class="friends_group_title friends__title">
              {{ translationsLang.friendsRequestsTo }}
            </h3>

            <div
              v-if="friendSearch !== null && friendSearch.content.length !== 0"
            >
              <div class="friend__search__resultats">
                <p class="friend__search-title">
                  {{ translationsLang.friendsSearchDescription }} "{{
                    localFirstName
                  }}":
                </p>
                <button
                  class="friend__search-clear"
                  @click.prevent="resetFriendSearch"
                >
                  {{ translationsLang.friendsSearchResultClear }}
                  <span>✕</span>
                </button>
              </div>

              <search-block
                v-for="user in friendSearch.content"
                :key="user.id"
                :info="user"
              />
            </div>

            <div
              v-if="friendSearch === null || friendSearch.content.length === 0"
            >
              <friends-block
                friend="friend"
                v-for="friend in localFriends?.REQUEST_TO"
                :key="friend.id"
                :info="friend"
              />
              <button
                class="friends-btn__more"
                @click.prevent="loadMoreFriends('REQUEST_TO')"
                v-if="
                  localFriends?.REQUEST_TO.length !==
                    paginations.REQUEST_TO.totalElements ||
                  paginations.REQUEST_TO.totalElements === 0
                "
              >
                {{ translationsLang.friendsShowMore }}
              </button>
            </div>
          </div>
          <!-- Друзья / поиск реализован -->
          <div v-if="activeTab === 'FRIEND'" class="friends_group active">
            <h3 class="friends_group_title friends__title">
              {{ translationsLang.sidebarFriend }}
            </h3>

            <div
              v-if="friendSearch !== null && friendSearch.content.length !== 0"
            >
              <div class="friend__search__resultats">
                <p class="friend__search-title">
                  {{ translationsLang.friendsSearchDescription }} "{{
                    localFirstName
                  }}":
                </p>
                <button
                  class="friend__search-clear"
                  @click.prevent="resetFriendSearch"
                >
                  {{ translationsLang.friendsSearchResultClear }}
                  <span>✕</span>
                </button>
              </div>

              <search-block
                v-for="user in friendSearch.content"
                :key="user.id"
                :info="user"
              />
            </div>

            <div
              v-if="friendSearch === null || friendSearch.content.length === 0"
            >
              <friends-block
                friend="friend"
                v-for="friend in localFriends?.FRIEND"
                :key="friend.id"
                :info="friend"
              />
              <button
                class="friends-btn__more"
                @click.prevent="loadMoreFriends('FRIEND')"
                v-if="
                  localFriends?.FRIEND.length !==
                    paginations.FRIEND.totalElements ||
                  paginations.FRIEND.totalElements === 0
                "
              >
                {{ translationsLang.friendsShowMore }}
              </button>
            </div>
          </div>
          <!-- Подписчики / поиск реализован -->
          <div v-if="activeTab === 'SUBSCRIBED'" class="friends_group active">
            <h3 class="friends_group_title friends__title">
              {{ translationsLang.friendsMySubscribe }}
            </h3>

            <div
              v-if="friendSearch !== null && friendSearch.content.length !== 0"
            >
              <div class="friend__search__resultats">
                <p class="friend__search-title">
                  {{ translationsLang.friendsSearchDescription }} "{{
                    localFirstName
                  }}":
                </p>
                <button
                  class="friend__search-clear"
                  @click.prevent="resetFriendSearch"
                >
                  {{ translationsLang.friendsSearchResultClear }}
                  <span>✕</span>
                </button>
              </div>

              <search-block
                v-for="user in friendSearch.content"
                :key="user.id"
                :info="user"
              />
            </div>

            <div
              v-if="friendSearch === null || friendSearch.content.length === 0"
            >
              <friends-block
                friend="friend"
                v-for="friend in localFriends?.SUBSCRIBED"
                :key="friend.id"
                :info="friend"
              />
              <button
                class="friends-btn__more"
                @click.prevent="loadMoreFriends('SUBSCRIBED')"
                v-if="
                  localFriends?.SUBSCRIBED.length !==
                    paginations.SUBSCRIBED.totalElements ||
                  paginations.SUBSCRIBED.totalElements === 0
                "
              >
                {{ translationsLang.friendsShowMore }}
              </button>
            </div>
          </div>
          <!-- Заблокированные пользователи / поиск реализован -->
          <div v-if="activeTab === 'BLOCKED'" class="friends_group active">
            <h3 class="friends_group_title friends__title">
              {{ translationsLang.friendsBlocked }}
            </h3>

            <div
              v-if="friendSearch !== null && friendSearch.content.length !== 0"
            >
              <div class="friend__search__resultats">
                <p class="friend__search-title">
                  {{ translationsLang.friendsSearchDescription }} "{{
                    localFirstName
                  }}":
                </p>
                <button
                  class="friend__search-clear"
                  @click.prevent="resetFriendSearch"
                >
                  {{ translationsLang.friendsSearchResultClear }}
                  <span>✕</span>
                </button>
              </div>

              <search-block
                v-for="user in friendSearch.content"
                :key="user.id"
                :info="user"
              />
            </div>

            <div
              v-if="friendSearch === null || friendSearch.content.length === 0"
            >
              <friends-block
                friend="friend"
                v-for="friend in localFriends?.BLOCKED"
                :key="friend.id"
                :info="friend"
                :blocked="true"
              />
              <button
                class="friends-btn__more"
                @click.prevent="loadMoreFriends('BLOCKED')"
                v-if="
                  localFriends?.BLOCKED.length !==
                    paginations.BLOCKED.totalElements ||
                  paginations.BLOCKED.totalElements === 0
                "
              >
                {{ translationsLang.friendsShowMore }}
              </button>
            </div>
          </div>
          <!-- Подписан(а) / поиск реализован -->
          <div v-if="activeTab === 'WATCHING'" class="friends_group active">
            <h3 class="friends_group_title friends__title">
              Подписки
              <!-- {{ translationsLang.friendsMyPesonalSubscribed }} -->
            </h3>

            <div
              v-if="friendSearch !== null && friendSearch.content.length !== 0"
            >
              <div class="friend__search__resultats">
                <p class="friend__search-title">
                  {{ translationsLang.friendsSearchDescription }} "{{
                    localFirstName
                  }}":
                </p>
                <button
                  class="friend__search-clear"
                  @click.prevent="resetFriendSearch"
                >
                  {{ translationsLang.friendsSearchResultClear }}
                  <span>✕</span>
                </button>
              </div>

              <search-block
                v-for="user in friendSearch.content"
                :key="user.id"
                :info="user"
              />
            </div>

            <div
              v-if="friendSearch === null || friendSearch.content.length === 0"
            >
              <friends-block
                v-for="friend in localFriends?.WATCHING"
                :key="friend.id"
                :info="friend"
              />
              <button
                class="friends-btn__more"
                @click.prevent="loadMoreFriends('WATCHING')"
                v-if="
                  localFriends?.WATCHING.length !==
                    paginations.WATCHING.totalElements ||
                  paginations.WATCHING.totalElements === 0
                "
              >
                {{ translationsLang.friendsShowMore }}
              </button>
            </div>
          </div>
          <!-- Нет информации -->
          <div
            v-if="activeTab === 'no-data-users'"
            class="friends_group-nodata active"
          >
            <h3 class="friends_group_title no-data">
              {{ translationsLang.friendsNoResult }}
            </h3>
            <router-link
              class="friends_group__search"
              href="#"
              :to="{ name: 'FriendsFind' }"
            >
              {{ translationsLang.recommendBlockBtn }}
            </router-link>
          </div>
          <!-- Нет информации -->
          <div
            v-if="activeTab === 'no-data'"
            class="friends_group-nodata active"
          >
            <h3 class="friends_group_title no-data">
              {{ translationsLang.friendsNoActiveTab }}
            </h3>
          </div>
        </div>

        <div class="inner-page__aside">
          <recommend-friend />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useStore } from "vuex";
import RecommendFriend from "@/components/RecommendFriend.vue";
import FriendsBlock from "@/components/Friends/Block";
import SearchBlock from "@/components/Friends/BlockSearch";
import SearchIcon from "@/Icons/SearchIcon.vue";
import useTranslations from "@/composables/useTranslations";
import { onBeforeRouteUpdate } from "vue-router";

export default {
  name: "UserFriends",
  components: { RecommendFriend, FriendsBlock, SearchIcon, SearchBlock },

  setup() {
    const { state, dispatch, commit } = useStore();
    const firstName = ref("");
    const localFirstName = ref("");
    const localFriends = ref([]);
    const searchResult = ref(null);
    const statusCodes = reactive([
      "REQUEST_FROM",
      "REQUEST_TO",
      "FRIEND",
      "BLOCKED",
      "SUBSCRIBED",
      "WATCHING",
    ]);
    const activeTab = ref("no-data");
    const { translationsLang } = useTranslations();

    const friends = computed(() => state.profile.friends.friends);
    const paginations = computed(() => state.profile.friends.paginations);
    const friendSearch = computed(() => state.profile.friends.friendSearch);

    const calculateTabSelect = computed(() => {
      if (
        localFriends.value.REQUEST_FROM &&
        localFriends.value.REQUEST_FROM.length > 0
      ) {
        return "REQUEST_FROM";
      } else if (
        localFriends.value.REQUEST_TO &&
        localFriends.value.REQUEST_TO.length > 0
      ) {
        return "REQUEST_TO";
      } else if (
        localFriends.value.FRIEND &&
        localFriends.value.FRIEND.length > 0
      ) {
        return "FRIEND";
      } else if (
        localFriends.value.BLOCKED &&
        localFriends.value.BLOCKED.length > 0
      ) {
        return "BLOCKED";
      } else if (
        localFriends.value.SUBSCRIBED &&
        localFriends.value.SUBSCRIBED.length > 0
      ) {
        return "SUBSCRIBED";
      } else if (
        localFriends.value.WATCHING &&
        localFriends.value.WATCHING.length > 0
      ) {
        return "WATCHING";
      } else if (localFriends.value && localFriends.value.length === 0) {
        return "no-data";
      } else {
        return "no-data-users";
      }
    });
// try new way to update localFriends from AI serafum 20241201 07-08 - upd: not work
    watch(friends, () => {
      localFriends.value = friends.value;
    });
//     watch(
//         () => state.profile.friends.friends,
//         (newFriends) => {
//           localFriends.value = Array.isArray(newFriends) ? newFriends : [newFriends];
//         },
//         { immediate: true }
//     );

    onMounted(() => {
      activeTab.value = calculateTabSelect.value;
      statusCodes.forEach((statusCode) => {
        dispatch("profile/friends/apiFriends", { statusCode });
      });
      localFriends.value = friends.value;
    });

    onBeforeRouteUpdate((next) => {
      activeTab.value = calculateTabSelect.value;
      next();
    });

    const setActive = (tabName) => {
      activeTab.value = tabName;
      firstName.value = "";
      localFirstName.value = "";
      commit("profile/friends/resetFriendSearch");
    };

    const loadMoreFriends = (statusCode) => {
      dispatch("profile/friends/apiFriends", { statusCode, loadMore: true });
    };

    const resetFriendSearch = () => {
      commit("profile/friends/resetFriendSearch");
      localFirstName.value = "";
    };

    const searchFriends = () => {
      dispatch("profile/friends/apiFriendSearch", {
        firstName: firstName.value,
        statusCode: activeTab.value,
      }).then((response) => {
        searchResult.value = response.data;
      });
      localFirstName.value = firstName;
      firstName.value = "";
    };

    return {
      firstName,
      localFirstName,
      localFriends,
      searchResult,
      statusCodes,
      activeTab,
      translationsLang,
      friends,
      paginations,
      friendSearch,
      setActive,
      loadMoreFriends,
      resetFriendSearch,
      searchFriends,
    };
  },
};
</script>

<style scoped lang="stylus">

.friends_group_title
  margin-bottom 15px
  font-weight font-weight-bold
  font-size 30px
  color #000

.friends_group
  display none
  &.active
    display block
  &:not(:last-child)
    margin-bottom 40px
.friends__content
  position relative
  width 100%
  display grid
  grid-template-columns 1fr 430px
  gap 30px
  padding-bottom 30px

  .inner-page__aside
    max-width 100%
    margin-top 54px

.friends__list
  display flex
.inner-page
  &__main
    max-width none



.friends-possible
  margin-top 0

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
  .friends__list
    margin-right 0

  .friends_group:not(:last-child)
    margin-bottom 40px
</style>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl';

.friends .inner-page__main
  width 100%
.friend__search__resultats
  display flex
  gap 10px
  align-items center
  margin-top 30px
  margin-bottom 20px

.friend__search-clear
  display flex
  align-items center
  gap 10px
  background ui-cl-color-eucalypt
  padding 5px
  color ui-cl-color-white-theme
  border-radius border-super-small
  transition all .2s ease-in-out
  span
    font-size font-size-small-medium
    line-height 0
  @media (any-hover: hover)
    &:hover
      background #2fba6e

.friend__search-title
  font-size 20px
  line-height 0

.friends_group_title.no-data
  font-size font-size-updefault
  font-weight font-weight-light
  text-align center
  margin-bottom 20px

.friends_group-nodata
  display flex
  flex-direction column
  align-items center
  justify-content center

.friends_group__search
  display flex
  justify-content center
  padding 10px 20px
  font-size font-size-updefault
  border 1px solid ui-cl-color-eucalypt
  color ui-cl-color-eucalypt
  border-radius border-small
  transition all .2s ease-in-out
  @media (any-hover: hover)
    &:hover
      background ui-cl-color-eucalypt
      color ui-cl-color-white-theme

.friends-btn__more
  display block
  background transparent
  padding 15px
  font-size font-size-updefault
  user-select none
  border 3px solid ui-cl-color-eucalypt
  color ui-cl-color-eucalypt
  border-radius border-small
  margin 0 auto
  transition all .2s ease-in-out
  @media (any-hover: hover)
    &:hover
      background ui-cl-color-eucalypt
      color ui-cl-color-white-theme
</style>
