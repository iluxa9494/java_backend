<template>
  <div class="friends-block" ref="dropdown">
    <div class="friends-block__img">
      <img v-if="info.photo" :src="info.photo" :alt="info.firstName" />
      <img v-else src="/static/img/avatar.png" :alt="info.firstName" />
    </div>

    <div class="friends-block__info">
      <div class="friends-block-top-name">
        <router-link
          class="friends-block__name"
          :to="{ name: 'ProfileId', params: { id: info.id } }"
        >
          {{ info.firstName }}
          {{ info.lastName }}
        </router-link>
        <span
          class="user-status isonline-lasttime"
          v-if="info.lastOnlineTime === null"
          >был(а) в сети давно</span
        >
        <span class="user-status isonline-online" v-else-if="info.isOnline">{{
          translationsLang.profileInfoStatusOnline
        }}</span>
        <span class="user-status isonline-lasttime" v-else
          >был(а) в сети {{ formatLastTime(info.lastOnlineTime) }}</span
        >
      </div>

      <span class="friends-block__age-city" v-if="moderator">модератор</span>

      <span
        class="friends-block__age-city"
        v-else-if="info.birthDate && info.country"
      >
        {{ formatBirhDate(info.birthDate) }},
        {{
          info.city
            ? info.city
            : info.country
            ? info.country
            : translationsLang.profileNotFilled
        }}
      </span>

      <span class="friends-block__age-city" v-else>{{
        translationsLang.profileNotFilledAll
      }}</span>
    </div>

    <div>
      <button
        :class="{ 'friends-block__showmore': true, active: showDropdown }"
        @click.prevent="toggleDropdown"
      >
        <actions-show />
      </button>
      <div
        class="friends-block__actions"
        v-if="showDropdown"
        v-click-outside="closePopup"
      >
        <template v-if="moderator">
          <div
            class="friends-block__actions-block"
            @click="openModal('deleteModerator')"
          >
            <span>{{ translationsLang.friendsBlockEdit }}</span>
            <img src="@/assets/static/img/edit.svg" alt="img.svg" />
          </div>
          <div
            class="friends-block__actions-block"
            @click="openModal('deleteModerator')"
          >
            <span>{{ translationsLang.friendsBlockDelete }}</span>
            <img src="@/assets/static/img/delete.svg" alt="img.svg" />
          </div>
        </template>
        <template v-else-if="admin">
          <div
            class="friends-block__actions-block"
            v-if="blocked || info.isBlocked"
          >
            <span>{{ translationsLang.profileAccountBlocking }}</span>
            <img
              class="filter-green svg-width"
              src="@/assets/static/img/security-system-unlock.svg"
              alt="img.svg"
            />
          </div>
          <div class="friends-block__actions-block" v-else>
            <span>{{ translationsLang.profileAccountUnblocking }}</span>
            <img src="@/assets/static/img/unblocked.svg" alt="img.svg" />
          </div>
        </template>
        <template v-else>
          <div
            v-if="
              info.statusCode === 'WATCHING' && info.statusCode !== 'FRIEND'
            "
            class="friends-block__actions-block message subscribe__icon"
            @click="openModal('deleteSubscribe')"
          >
            <span>{{ translationsLang.profileAccountUnsubscribe }}</span>
            <img src="@/assets/static/img/delete.svg" alt="img.svg" />
          </div>
          <div
            v-else-if="
              info.statusCode !== 'FRIEND' &&
              info.statusCode !== 'REQUEST_TO' &&
              info.statusCode !== 'BLOCKED' &&
              info.statusCode !== 'REQUEST_FROM'
            "
            class="friends-block__actions-block message subscribe__icon"
            @click="subscribe(info.id)"
          >
            <span>{{ translationsLang.profileAccountSubscribe }}</span>
            <img src="@/assets/static/img/add.svg" alt="img.svg" />
          </div>
          <div
            v-if="info.statusCode === 'REQUEST_FROM'"
            class="friends-block__actions-block message"
            @click="acceptFriendRequest(info.id)"
          >
            <span>{{ translationsLang.profileAccountAcceptRequests }}</span>
            <img
              class="accept"
              src="@/assets/static/img/add.svg"
              alt="img.svg"
            />
          </div>
          <div
            class="friends-block__actions-block message"
            @click="sendMessage(messageId)"
            v-if="info.statusCode !== 'BLOCKED'"
          >
            <span>{{ translationsLang.profileAccountSendMessage }}</span>
            <img
              class="svg-width"
              src="@/assets/static/img/sidebar/im.svg"
              alt="img.svg"
            />
          </div>
          <div
            class="friends-block__actions-block delete"
            @click="openModal('delete')"
            v-if="info.statusCode === 'FRIEND'"
          >
            <span>{{ translationsLang.profileAccountDeleteFriend }}</span>
            <img src="@/assets/static/img/delete.svg" alt="img.svg" />
          </div>
          <div
            class="friends-block__actions-block delete"
            @click="openModal('cancelFriend')"
            v-else-if="info.statusCode === 'REQUEST_TO'"
          >
            <span>{{ translationsLang.profileAccountCancelFriend }}</span>
            <img src="@/assets/static/img/delete.svg" alt="img.svg" />
          </div>
          <div
            class="friends-block__actions-block add"
            @click="addToFriend(info.id)"
            v-else-if="
              info.statusCode !== 'WATCHING' &&
              info.statusCode !== 'REQUEST_TO' &&
              info.statusCode !== 'BLOCKED' &&
              info.statusCode !== 'REQUEST_FROM'
            "
          >
            <span>{{ translationsLang.profileAccountAddFriend }}</span>
            <img src="@/assets/static/img/friend-add.svg" alt="img.svg" />
          </div>
          <div
            class="friends-block__actions-block"
            v-if="blocked || info.isBlocked || info.statusCode === 'BLOCKED'"
            @click="openModal('unblock')"
          >
            <span>{{ translationsLang.profileAccountUnblocking }}</span>
            <img
              class="filter-green svg-width"
              src="@/assets/static/img/security-system-unlock.svg"
              alt="img.svg"
            />
          </div>
          <div
            v-else
            class="friends-block__actions-block"
            @click="openModal('block')"
          >
            <span>{{ translationsLang.profileAccountBlocking }}</span>
            <img src="@/assets/static/img/unblocked.svg" alt="img.svg" />
          </div>
        </template>
      </div>
    </div>

    <modal v-model="modalShow">
      <p v-if="modalText">{{ modalText }}</p>
      <template v-slot:actions>
        <button class="btn" @click="onConfrim(targetId)">
          <span class="helper"></span>
          {{ translationsLang.yes }}
        </button>
        <button
          class="btn btn--red btn--bordered"
          variant="red"
          bordered="bordered"
          @click="closeModal()"
        >
          <span class="helper"></span>
          {{ translationsLang.cancel }}
        </button>
      </template>
    </modal>
  </div>
</template>

<script>
import { computed, ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import vClickOutside from "click-outside-vue3";
import useTranslations from "@/composables/useTranslations";
import dayjs from "dayjs";
import Modal from "@/components/Modal";
import ActionsShow from "@/Icons/ActionsShow.vue";

export default {
  name: "FriendsBlockSearch",
  directives: {
    clickOutside: vClickOutside.directive,
  },
  components: { Modal, ActionsShow },
  props: {
    friend: Boolean,
    admin: Boolean,
    blocked: Boolean,
    moderator: Boolean,
    subscribeButton: {
      type: Boolean,
      default: false,
    },
    acceptButton: {
      type: Boolean,
      default: false,
    },
    info: {
      type: Object,
      default: () => ({
        firstName: "Михаил",
        lastName: "Веселов",
        birthDate: 1559751301818,
        town_id: 1,
        photo: "/static/img/user/user_1.jpg",
        id: 124,
      }),
    },
    page: Number,
    size: Number,
  },

  setup(props) {
    const { getters, state, dispatch } = useStore();
    const router = useRouter();
    const modalShow = ref(false);
    const modalType = ref("delete");
    const showDropdown = ref(false);
    const blocked = ref(props.blocked);
    const { translationsLang } = useTranslations();

    const dialogs = computed(() => getters["profile/dialogs/dialogs"]);
    const getUsersQueryParams = computed(
      () => getters["global/search/getUsersQueryParams"]
    );

    const statusText = computed(() =>
      props.info.isOnline
        ? translationsLang.profileInfoStatusOnline
        : translationsLang.profileInfoStatusOffline
    );
    const friends = computed(() => state.friend);
    const online = computed(() => props.info.isOnline);
    const currentUser = computed(() => getters.getUser);
    const targetId = computed(() => {
      if (props.info.id) {
        if (props.info.id === currentUser.value) {
          return props.info.id;
        } else {
          return props.info.id;
        }
      } else {
        return props.info.id;
      }
    });
    const modalText = computed(() => {
      let text = "";
      if (modalType.value === "delete") {
        text = `Вы уверены, что хотите удалить пользователя ${
          props.info.firstName + " " + props.info.lastName
        } из друзей?`;
      } else if (modalType.value === "deleteSubscribe") {
        text = `Вы уверены, что хотите отписаться от пользователя ${
          props.info.firstName + " " + props.info.lastName
        }?`;
      } else if (modalType.value === "cancelFriend") {
        text = `Вы уверены, что хотите отменить запрос в дружбу с ${
          props.info.firstName + " " + props.info.lastName
        }?`;
      } else if (modalType.value === "deleteModerator") {
        text = `Вы уверены, что хотите удалить ${
          props.info.firstName + " " + props.info.lastName
        } из списка модераторов?`;
      } else if (modalType.value === "block") {
        text = `Вы уверены, что хотите заблокировать пользователя ${
          props.info.firstName + " " + props.info.lastName
        }?`;
      } else if (modalType.value === "unblock") {
        text = `Вы уверены, что хотите разблокировать  пользователя ${
          props.info.firstName + " " + props.info.lastName
        }?`;
      }
      return text;
    });
    const messageId = computed(() => {
      if (props.info.toAccountId) {
        return props.info.toAccountId;
      } else return props.info.id;
    });

    const acceptFriendRequest = (id) => {
      const searchQuery = {
        ...getUsersQueryParams.value,
        page: props.page - 1,
        size: props.size,
      };
      dispatch("profile/friends/apiAddFriends", {
        id: id,
        isApprove: true,
        searchQuery: searchQuery,
      });
    };

    const addToFriend = (id) => {
      const searchQuery = {
        ...getUsersQueryParams.value,
        page: props.page - 1,
        size: props.size,
      };
      dispatch("profile/friends/apiAddFriends", {
        id: id,
        searchQuery: searchQuery,
      });
    };

    const subscribe = (id) => {
      const searchQuery = {
        ...getUsersQueryParams.value,
        page: props.page - 1,
        size: props.size,
      };
      dispatch("profile/friends/apiSubscribe", { id, searchQuery });
    };

    const closeModal = () => {
      modalShow.value = false;
    };

    const openModal = (id) => {
      modalType.value = id;
      modalShow.value = true;
    };

    const sendMessage = (userId) => {
      router.push({ name: "ImChat", params: { activeDialogId: userId } });
    };

    const onConfrim = async (id) => {
      const searchQuery = {
        ...getUsersQueryParams.value,
        page: props.page - 1,
        size: props.size,
      };
      if (modalType.value === "delete") {
        dispatch("profile/friends/apiDeleteFriends", { id, searchQuery }).then(
          () => {
            closeModal();
          }
        );
      }

      if (modalType.value === "deleteSubscribe") {
        dispatch("profile/friends/apiDeleteFriends", { id, searchQuery }).then(
          () => {
            closeModal();
          }
        );
      }

      if (modalType.value === "cancelFriend") {
        dispatch("profile/friends/apiDeleteFriends", { id, searchQuery }).then(
          () => {
            closeModal();
          }
        );
      }

      if (modalType.value === "deleteModerator") {
        closeModal();
      }

      if (modalType.value === "block") {
        dispatch("users/actions/apiBlockedUser", { id, searchQuery }).then(
          () => {
            blocked.value = true;
            closeModal();
          }
        );
      }

      if (modalType.value === "unblock") {
        dispatch("users/actions/apiUnblockUser", { id, searchQuery }).then(
          () => {
            blocked.value = false;
            closeModal();
          }
        );
      }
    };

    const closePopup = () => {
      showDropdown.value = false;
    };

    const toggleDropdown = () => {
      showDropdown.value = !showDropdown.value;
    };

    const formatLastTime = (time) => {
      return dayjs(time).fromNow();
    };
    const formatBirhDate = (date) => {
      return dayjs(date).fromNow(true);
    };

    const openDialog = () => {
      dispatch("profile/dialogs/openDialog");
    };

    return {
      modalShow,
      modalType,
      showDropdown,
      translationsLang,
      dialogs,
      statusText,
      friends,
      online,
      currentUser,
      targetId,
      modalText,
      messageId,
      acceptFriendRequest,
      addToFriend,
      subscribe,
      closeModal,
      openModal,
      sendMessage,
      onConfrim,
      closePopup,
      toggleDropdown,
      formatLastTime,
      formatBirhDate,
      openDialog,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.accept path
  fill ui-cl-color-sea-green

.subscribe__icon
    path
      stroke ui-cl-color-sea-green
      stroke-opacity 1
      stroke-width 2

.friends-block
  position relative
  align-items center
  background ui-cl-color-white-theme
  box-shadow box-shadow-main
  padding 20px
  width 100%
  display inline-flex
  border-radius border-small
  &:not(:last-child)
    margin-bottom 20px

  .friends-block__showmore
    background transparent
    padding 0
    svg path:nth-child(1)
      fill none
      stroke ui-cl-color-ababab
    svg path:nth-child(2),
    svg path:nth-child(3),
    svg path:nth-child(4)
      fill ui-cl-color-ababab
    &.active
      svg path:nth-child(1)
        fill none
        stroke ui-cl-color-eucalypt
      svg path:nth-child(2),
      svg path:nth-child(3),
      svg path:nth-child(4)
        fill ui-cl-color-eucalypt

.friends-block__img
  width 65px
  height 65px
  border-radius border-half
  overflow hidden
  margin-right 30px
  flex none

  @media (max-width breakpoint-xxl)
    margin-right 10px

  img
    object-fit cover
    width 100%
    height 100%

.friends-block__info
  margin-right auto

.friends-block__name
  font-weight font-weight-bold
  font-size font-size-updefault
  line-height 27px
  color ui-cl-color-steel-gray
  display block

  @media (max-width breakpoint-xxl)
    font-size font-size-small

.friends-block__age-city
  font-size font-size-default
  line-height 22px
  color #5A5A5A

  @media (max-width breakpoint-xxl)
    font-size font-size-small

.friends-block__actions
  background-color ui-cl-color-white-theme
  position absolute
  right 20px
  top 70%
  display flex
  flex-direction column
  align-items flex-start
  border-radius border-small
  box-shadow box-shadow-main
  transition all 0.3s ease
  z-index 15
  overflow hidden
  &-block
    display flex
    width 100%
    align-items center
    font-size font-size-small
    font-weight font-weight-medium
    justify-content flex-end
    gap 7px
    padding 12px
    flex-direction row-reverse
    cursor pointer
    @media (any-hover: hover)
      &:hover
        background #fbfbfb
        span
          color ui-cl-color-eucalypt

  @media (max-width breakpoint-xxl)
    & + &
      margin-left 5px

  & + &
    margin-left 10px

  &.message
    margin-top 5px

    .simple-svg
      fill ui-cl-color-eucalypt

  &.delete
    margin-top 3px

  &.add
    margin-top 2px
    margin-left 15px

  .simple-svg-wrapper
    font-size 0
    width 15px
    height 15px

  .svg-width
    width 15px
</style>
