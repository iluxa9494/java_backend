<template>
  <div class="profile inner-page" v-if="getUsersInfo">
    <div class="inner-page__main">
      <div class="profile__info">
        <profile-info
          :info="getUsersInfo"
          :blocked="getUsersInfo.isBlocked"
          :friend="getUsersInfo.isFriend"
          :online="getUsersInfo.isOnline"
          :me="myProfile"
          :infoFriend="
            getStatusSub ||
            getStatusFriend ||
            getStatusRequestTo ||
            getStatusRequestFrom ||
            getStatusRequestWatching ||
            getStatusRequestBlocked ||
            getUsersInfo
          "
        />
      </div>

      <div class="profile__news" id="mypublications">
        <div class="profile__tabs">
          <span class="profile__tab active">
            Публикации {{ getUsersInfo.firstName }} ({{
              getWallPagination.totalElements
            }})
          </span>
        </div>

        <div v-if="getWall && getWall.length > 0" class="profile__news-list">
          <error-block v-if="!loading && error" :message="errorMessage" />

          <news-block
            v-for="news in filteredWall.posted"
            :key="news.id"
            :info="news"
          />

          <div class="spinner-wrapper" v-if="loading">
            <spinner />
          </div>

          <auto-paginator
            :page="getWallPagination.page"
            :loading="loading"
            :totalPage="getWallPagination.total"
            :action="setThisWall"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, watch, onMounted } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import ProfileInfo from "@/components/Profile/Info";
import NewsBlock from "@/components/News/Block";
import Spinner from "@/components/SpinnerComponents.vue";
import ErrorBlock from "@/components/ErrorBlock.vue";
import AutoPaginator from "@/components/AutoPaginator.vue";

export default {
  name: "ProfileId",
  components: { ProfileInfo, NewsBlock, Spinner, ErrorBlock, AutoPaginator },

  setup() {
    const { getters, state, dispatch, commit } = useStore();
    const route = useRoute();
    const localFriends = ref([]);
    const statusCodes = reactive([
      "REQUEST_FROM",
      "REQUEST_TO",
      "FRIEND",
      "BLOCKED",
      "SUBSCRIBED",
      "WATCHING",
    ]);

    const getUsersInfo = computed(() => getters["users/info/getUsersInfo"]);
    const getWall = computed(() => getters["users/info/getWall"]);
    const getWallPagination = computed(
      () => getters["users/info/getWallPagination"]
    );
    const getInfo = computed(() => getters["profile/info/getInfo"]);

    const friends = computed(() => state.profile.friends.friends);
    const loading = computed(() => state.global.status.loading);
    const error = computed(() => state.global.status.error);
    const errorMessage = computed(() => state.global.status.errorMessage);

    const myProfile = computed(
      () => getInfo.value?.id === getUsersInfo.value?.id
    );
    const filteredWall = computed(() => {
      const wall = getWall.value;
      const posted = wall.filter((item) => item.type === "POSTED");
      const queued = wall.filter((item) => item.type === "QUEUED");
      return { posted, queued };
    });
    const getStatusSub = computed(() => {
      const friends = localFriends.value?.SUBSCRIBED;
      if (friends === undefined) return;
      let friend;
      friends.forEach((i) => {
        if (i.friendId === getUsersInfo.value.id) {
          friend = i;
        }
      });
      return friend;
    });

    const getStatusFriend = computed(() => {
      const friends = localFriends.value?.FRIEND;
      if (friends === undefined) return;
      let friend;
      friends.forEach((i) => {
        if (i.friendId === getUsersInfo.value.id) {
          friend = i;
        }
      });
      return friend;
    });

    const getStatusRequestTo = computed(() => {
      const friends = localFriends.value?.REQUEST_TO;
      if (friends === undefined) return;
      let friend;
      friends.forEach((i) => {
        if (i.friendId === getUsersInfo.value.id) {
          friend = i;
        }
      });
      return friend;
    });

    const getStatusRequestFrom = computed(() => {
      const friends = localFriends.value?.REQUEST_FROM;
      if (friends === undefined) return;
      let friend;
      friends.forEach((i) => {
        if (i.friendId === getUsersInfo.value.id) {
          friend = i;
        }
      });
      return friend;
    });

    const getStatusRequestWatching = computed(() => {
      const friends = localFriends.value?.WATCHING;
      if (friends === undefined) return;
      let friend;
      friends.forEach((i) => {
        if (i.friendId === getUsersInfo.value.id) {
          friend = i;
        }
      });
      return friend;
    });

    const getStatusRequestBlocked = computed(() => {
      const friends = localFriends.value?.BLOCKED;
      if (friends === undefined) return;
      let friend;
      friends.forEach((i) => {
        if (i.friendId === getUsersInfo.value.id) {
          friend = i;
        }
      });
      return friend;
    });

    watch(
      () => friends,
      () => {
        localFriends.value = friends.value;
      }
    );

    onMounted(async () => {
      window.scroll(0, 0);
      statusCodes.forEach((statusCode) => {
        dispatch("profile/friends/apiFriends", { statusCode });
      });
      localFriends.value = friends.value;
      commit("users/info/clearWall");
      commit("users/info/clearPagination");
      await dispatch("users/info/userInfoId", route.params.id);
    });

    const setThisWall = ({ page }) => {
      return dispatch("users/info/apiWall", {
        accountIds: getUsersInfo.value.id,
        author: getUsersInfo.value,
        page,
      });
    };

    const apiFriendSearch = () => {
      dispatch("profile/friends/apiFriendSearch");
    };

    return {
      getUsersInfo,
      getWall,
      getWallPagination,
      friends,
      loading,
      error,
      errorMessage,
      myProfile,
      filteredWall,
      getStatusSub,
      getStatusFriend,
      getStatusRequestTo,
      getStatusRequestFrom,
      getStatusRequestWatching,
      getStatusRequestBlocked,
      setThisWall,
      apiFriendSearch,
    };
  },
};
</script>
