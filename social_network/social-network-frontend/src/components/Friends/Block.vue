<template>
  <div class="friends-block" ref="dropdown" v-if="userInfo">
    <div class="friends-block__img">
      <img
        v-if="userInfo.photo"
        :src="userInfo.photo"
        :alt="userInfo.firstName"
      />
      <img v-else src="/static/img/avatar.png" :alt="userInfo.firstName" />
    </div>
    <div class="friends-block__info">
      <div class="friends-block-top-name">
        <router-link
          class="friends-block__name"
          :to="{ name: 'ProfileId', params: { id: userInfo.id } }"
        >
          {{ userInfo.firstName }}
          {{ userInfo.lastName }}
        </router-link>
        <span
          class="user-status isonline-lasttime"
          v-if="userInfo.lastOnlineTime === null"
          >был(а) в сети давно</span
        >
        <span
          class="user-status isonline-online"
          v-else-if="userInfo.isOnline"
          >{{ translationsLang.profileInfoStatusOnline }}</span
        >
        <span class="user-status isonline-lasttime" v-else
          >был(а) в сети {{ formatLastTime(userInfo.lastOnlineTime) }}</span
        >
      </div>
      <span class="friends-block__age-city" v-if="moderator">модератор</span>
      <span
        class="friends-block__age-city"
        v-else-if="userInfo.birthDate && userInfo.country"
      >
        {{ formatBirthDate(userInfo.birthDate) }},
        {{
          userInfo.city
            ? userInfo.city
            : userInfo.country
            ? userInfo.country
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
              class="filter-green"
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
          <!-- Подписка/отписка/принимаем запрос -->
          <div
            v-if="
              (info.statusCode === 'SUBSCRIBED' ||
                info.statusCode === 'WATCHING') &&
              info.statusCode !== 'FRIEND'
            "
            class="friends-block__actions-block message subscribe__icon"
            @click="openModal('deleteSubscribe')"
          >
            <span v-if="info.statusCode === 'SUBSCRIBED'"
              >Удалить подписчика</span
            >
            <span v-if="info.statusCode === 'WATCHING'">{{
              translationsLang.profileAccountUnsubscribe
            }}</span>
            <img src="@/assets/static/img/delete.svg" alt="img.svg" />
          </div>
          <div
            v-else-if="
              info.statusCode !== 'FRIEND' &&
              info.statusCode !== 'REQUEST_TO' &&
              info.statusCode !== 'BLOCKED' &&
              info.statusCode !== 'REQUEST_FROM' &&
              info.statusCode !== 'SUBSCRIBED'
            "
            class="friends-block__actions-block message subscribe__icon"
            @click="subscribe(info.friendId)"
          >
            <span>{{ translationsLang.profileAccountSubscribe }}</span>
            <img
              src="@/assets/static/img/sidebar/admin/comments.svg"
              alt="img.svg"
            />
          </div>
          <div
            v-if="info.statusCode === 'REQUEST_FROM'"
            class="friends-block__actions-block message"
            @click="acceptFriendRequest(info.friendId)"
          >
            <span>{{ translationsLang.profileAccountAcceptRequests }}</span>
            <img
              class="accept"
              src="@/assets/static/img/add.svg"
              alt="img.svg1"
            />
          </div>
          <div
            v-if="info.statusCode === 'REQUEST_FROM'"
            class="friends-block__actions-block message"
            @click="openModal('cancelFriend')"
          >
            <span>Отклонить запрос</span>
            <img src="@/assets/static/img/delete.svg" alt="img.svg" />
          </div>
          <!-- Отправка сообщений -->
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
          <!-- Добавление в друзья/отмена -->
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
            @click="addToFriend(info.friendId)"
            v-else-if="
              info.statusCode !== 'WATCHING' &&
              info.statusCode !== 'REQUEST_TO' &&
              info.statusCode !== 'BLOCKED' &&
              info.statusCode !== 'REQUEST_FROM' &&
              info.statusCode !== 'SUBSCRIBED'
            "
          >
            <span>{{ translationsLang.profileAccountAddFriend }}</span>
            <img src="@/assets/static/img/friend-add.svg" alt="img.svg" />
          </div>
          <!-- Блокировка/разблокировка -->
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
import { ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import vClickOutside from "click-outside-vue3";
import useTranslations from "@/composables/useTranslations";
import axios from "axios";
import dayjs from "dayjs";
import Modal from "@/components/Modal";
import ActionsShow from "@/Icons/ActionsShow.vue";

export default {
  name: "FriendsBlock",
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
    allowManualAddition: {
      type: Boolean,
      default: true,
    },
  },

  setup(props) {
    const { getters, dispatch } = useStore();
    const router = useRouter();
    const modalShow = ref(false);
    const modalType = ref("delete");
    const showDropdown = ref(false);
    const userInfo = ref(null);
    const blocked = ref(props.blocked);
    const { translationsLang } = useTranslations();

    const dialogs = computed(() => getters["profile/dialogs/dialogs"]);

    const statusText = computed(() => {
      return props.info.isOnline
        ? translationsLang.profileInfoStatusOnline
        : translationsLang.profileInfoStatusOffline;
    });
    const online = computed(() => {
      return props.info.isOnline;
    });
    const getInfo = computed(() => {
      return props.info.friendId;
    });
    const currentUser = computed(() => {
      return getters.getUser;
    });
    const targetId = computed(() => {
      if (props.info.friendId) {
        if (props.info.friendId === currentUser.value) {
          return props.info.friendId;
        } else {
          return props.info.friendId;
        }
      } else {
        return props.info.id;
      }
    });
    const modalText = computed(() => {
      let text = "";
      if (modalType.value === "delete") {
        text = `${translationsLang.value.friendsModalTextDelete} ${
          userInfo.value.firstName + " " + userInfo.value.lastName
        } ${translationsLang.value.friendsModalLastText}`;
      } else if (modalType.value === "deleteSubscribe") {
        text = `${translationsLang.value.friendsModalTextDeleteSub} ${
          userInfo.value.firstName + " " + userInfo.value.lastName
        }?`;
      } else if (modalType.value === "cancelFriend") {
        text = `${translationsLang.value.friendsModalTextCancelFriend} ${
          userInfo.value.firstName + " " + userInfo.value.lastName
        }?`;
      } else if (modalType.value === "deleteModerator") {
        text = `${translationsLang.value.friendsModalTextDeteleModerator} ${
          userInfo.value.firstName + " " + userInfo.value.lastName
        } ${translationsLang.value.friendsModalLastTextSecond}`;
      } else if (modalType.value === "block") {
        text = `${translationsLang.value.friendsModalTextBlocked} ${
          userInfo.value.firstName + " " + userInfo.value.lastName
        }?`;
      } else if (modalType.value === "unblock") {
        text = `${translationsLang.value.friendsModalTextUnlock} ${
          userInfo.value.firstName + " " + userInfo.value.lastName
        }?`;
      }
      return text;
    });
    const messageId = computed(() => {
      if (userInfo.value.toAccountId) {
        return userInfo.value.toAccountId;
      } else return userInfo.value.id;
    });

    onMounted(() => {
      fetchUserInfo();
    });

    const acceptFriendRequest = (id) => {
      if (props.info.statusCode === "FRIEND") {
        dispatch("global/alert/setAlert", {
          status: "action",
          text: "Этот пользователь уже является вашим другом!",
        });
        return;
      }
      dispatch("profile/friends/apiAddFriends", { id, isApprove: true });

      setTimeout(() => {
        location.reload();
      }, 500);
    };

    const addToFriend = (id) => {
      if (props.info.statusCode === "REQUEST_TO") {
        dispatch("global/alert/setAlert", {
          status: "action",
          text: "Вы уже отправляли запрос на добавления в друзья этому пользователю!",
        });
        return;
      }
      if (props.info.statusCode === "FRIEND") {
        dispatch("global/alert/setAlert", {
          status: "action",
          text: "Этот пользователь уже является вашим другом!",
        });
        return;
      }
      if (props.info.statusCode === "BLOCKED") {
        dispatch("global/alert/setAlert", {
          status: "action",
          text: "Вы заблокировали этого пользователя!",
        });
        return;
      }
      if (props.info.statusCode === "SUBSCRIBED") {
        dispatch("global/alert/setAlert", {
          status: "action",
          text: "Этот пользователь среди ваших подписчиков, для добавления в друзья необходимо удалить его из списка подписчиков!",
        });
        return;
      }
      if (
        props.info.statusCode !== "NONE" &&
        props.info.statusCode !== null &&
        props.info.statusCode == "WATCHING"
      ) {
        dispatch("global/alert/setAlert", {
          status: "action",
          text: "У вас уже есть отношения с этим пользователем, чтобы подписаться необходимо остановить другие отношения!",
        });
        return;
      }
      dispatch("profile/friends/apiAddFriends", { id });
    };

    const subscribe = (id) => {
      if (props.info.statusCode === "WATCHING") {
        dispatch("global/alert/setAlert", {
          status: "action",
          text: "Вы уже подписаны на этого пользователя!",
        });
        return;
      }
      if (
        props.info.statusCode !== "NONE" &&
        props.info.statusCode !== null &&
        props.info.statusCode !== "WATCHING"
      ) {
        dispatch("global/alert/setAlert", {
          status: "action",
          text: "У вас уже есть отношения с этим пользователем, чтобы подписаться необходимо остановить другие отношения!",
        });
        return;
      }
      dispatch("profile/friends/apiSubscribe", {id});
    };

    const fetchUserInfo = () => {
      if (!props.allowManualAddition) return;
      axios
        .get(`/account/${getInfo.value}`)
        .then((response) => {
          userInfo.value = response.data;
        })
        .catch((error) => {
          console.log(error);
        });
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
      if (modalType.value === "delete") {
        dispatch("profile/friends/apiDeleteFriends", { id }).then(() => {
          closeModal();
        });
      }

      if (modalType.value === "deleteSubscribe") {
        dispatch("profile/friends/apiDeleteFriends", {id}).then(() => {
          closeModal();
        });
      }

      if (modalType.value === "cancelFriend") {
        dispatch("profile/friends/apiDeleteFriends", {id}).then(() => {
          closeModal();
        });
      }

      if (modalType.value === "deleteModerator") {
        closeModal();
      }

      if (modalType.value === "block") {
        dispatch("users/actions/apiBlockedUser", {id}).then(() => {
          blocked.value = true;
          closeModal();
        });
      }

      if (modalType.value === "unblock") {
        dispatch("users/actions/apiUnblockUser", {id}).then(() => {
          blocked.value = false;
          closeModal();
        });
      }

      setTimeout(() => {
        location.reload();
      }, 500);
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

    const formatBirthDate = (date) => {
      return dayjs(date).fromNow(true);
    };

    const openDialog = () => {
      dispatch("profile/dialogs/openDialog");
    };

    return {
      userInfo,
      modalShow,
      showDropdown,
      translationsLang,
      dialogs,
      statusText,
      online,
      getInfo,
      currentUser,
      targetId,
      modalText,
      messageId,
      acceptFriendRequest,
      addToFriend,
      subscribe,
      fetchUserInfo,
      closeModal,
      openModal,
      sendMessage,
      onConfrim,
      closePopup,
      toggleDropdown,
      formatLastTime,
      formatBirthDate,
      openDialog,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.friends-block-top-name
  display flex
  align-items center
  gap 10px

.isonline-online
  background unset
  color ui-cl-color-eucalypt

.isonline-lasttime
  color #6c6c6c

.status-isonline
  font-size font-size-super-medium-small
  background-color ui-cl-color-white-theme
  padding 3px
  border-radius border-super-small
  box-shadow box-shadow-main

.v-enter-active,
.v-leave-active
  transition opacity .3s ease-in-out

.v-enter-from
.v-leave-to
  opacity 0

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
