<template>
  <div class="im">
    <div class="im__dialogs" v-if="dialogs.length !== 0">
      <div
        class="im-dialog"
        v-for="dialog in dialogs"
        :key="dialog.id"
        @click="clickOnDialog(dialog)"
        :class="{
          active:
            dialog.conversationPartner1 !== info.id
              ? activeDialogId === dialog.conversationPartner1
              : activeDialogId === dialog.conversationPartner2,
        }"
      >
        <a class="im-dailog__pic" href="#">
          <div class="main-layout__user-pic">
            <img
              v-if="
                dialog.conversationPartner1 === info.id
                  ? users?.find(
                      (user) => user.id === dialog.conversationPartner2
                    )?.photo
                  : dialog.conversationPartner2 === info.id
                  ? users?.find(
                      (user) => user.id === dialog.conversationPartner1
                    )?.photo
                  : null
              "
              :src="
                dialog.conversationPartner1 === info.id
                  ? users?.find(
                      (user) => user.id === dialog.conversationPartner2
                    )?.photo
                  : dialog.conversationPartner2 === info.id
                  ? users?.find(
                      (user) => user.id === dialog.conversationPartner1
                    )?.photo
                  : null
              "
              alt="Аватар"
            />

            <div v-else>
              <unknow-user />
            </div>
          </div>
        </a>

        <div class="im-dialog-contents">
          <div class="im-dialog__info" v-if="users">
            <a class="im-dialog__name" href="#">
              {{
                dialog.conversationPartner1 === info.id
                  ? (users?.find(
                      (user) => user.id === dialog.conversationPartner2
                    )?.firstName || "...") +
                    " " +
                    (users.find(
                      (user) => user.id === dialog.conversationPartner2
                    )?.lastName || "...")
                  : dialog.conversationPartner2 === info.id
                  ? (users?.find(
                      (user) => user.id === dialog.conversationPartner1
                    )?.firstName || "...") +
                    " " +
                    (users.find(
                      (user) => user.id === dialog.conversationPartner1
                    )?.lastName || "...")
                  : null
              }}
            </a>
            <div v-for="(message, index) in dialog.lastMessage" :key="index">
              <span
                v-if="
                  index + 1 === dialog.unreadCount &&
                  message.conversationPartner1 !== info.id &&
                  message.readStatus === 'SENT'
                "
                :data-push="
                  message.conversationPartner1 !== info.id &&
                  message.readStatus === 'SENT'
                    ? index + 1
                    : null
                "
                :class="{
                  'im-dialog__unread-count--count': dialog.unreadCount > 0,
                }"
              >
              </span>
            </div>
          </div>
          <div class="im-dialog__content">
            <p class="im-dialog__last" v-if="dialog.lastMessage">
              <span v-if="dialog.lastMessage">{{
                dialog.lastMessage && dialog.lastMessage[0]?.messageText
              }}</span>
              <span class="im-dialog__last-time">{{
                formattedLastTime(dialog.lastMessage[0]?.time)
              }}</span>
            </p>
          </div>
          <!-- <span class="im-dialog__push" v-if="push > 0">{{ push }}</span> -->
        </div>
      </div>
    </div>

    <div class="im__chat" v-if="activeDialog && messagesLoaded">
      <im-chat :user-info="users" :info="activeDialog" />
    </div>

    <div v-else class="no-dialog">
      {{ translationsLang.messageDialogNotSelected }}
    </div>
  </div>
</template>

<script>
import { computed, ref, watch, onMounted, onBeforeMount } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
// import ImDialog from '@/components/Im/Dialog';
import UnknowUser from "@/Icons/UnknowUser.vue";
import ImChat from "@/components/Im/Chat";
import dialogsApi from "@/requests/dialogs";
import useTranslations from "@/composables/useTranslations";
import dayjs from "dayjs";

export default {
  name: "UserIm",
  components: { ImChat, UnknowUser },
  props: {
    activeDialogId: {
      type: String,
      required: false,
    },
  },

  setup(props) {
    const { getters, state, dispatch, commit } = useStore();
    const route = useRoute();
    const router = useRouter();
    const activeDialog = ref(null);
    const activeDialogId = ref(props.activeDialogId);
    const numberPage = ref(0);
    const defaultDirection = ref("desc");
    const messagesLoaded = ref(false);
    const { translationsLang } = useTranslations();

    const dialogs = computed(() => state.profile.dialogs.dialogs);
    const newMessage = computed(() => state.profile.dialogs.newMessage);
    const info = computed(() => state.profile.info.info);

    const users = computed(() =>
      getters["global/search/getResultByIdSearch"]("users")
    );
    // const getMessages = computed(() => getters["profile/dialogs/getMessages"]);
    const getUsersQueryParams = computed(
      () => getters["global/search/getUsersQueryParams"]
    );

    const currentActiveDialogId = computed(() => route.params.activeDialogId);
    const conversationPartners = computed(() => {
      return dialogs.value
        .filter((dialog) => {
          return (
            dialog.conversationPartner1 === info.value.id ||
            dialog.conversationPartner2 === info.value.id
          );
        })
        .map((dialog) => {
          return dialog.conversationPartner1 === info.value.id
            ? dialog.conversationPartner2
            : dialog.conversationPartner1;
        });
    });

    watch(
      activeDialogId,
      async (newVal) => {
        if (newVal) {
          await dispatch("profile/dialogs/newDialogs", newVal);
          messagesLoaded.value = false;
          await dispatch("profile/dialogs/loadLastMessages", {
            id: newVal,
            countPage: numberPage.value,
            direction: defaultDirection.value,
          });
          messagesLoaded.value = true;
          const newActiveDialog = dispatch("profile/dialogs/fetchDialogs")
            .length
            ? dispatch("profile/dialogs/fetchDialogs").filter(
                (d) => d.id === newVal
              )
            : [];
          if (newActiveDialog.length > 0) {
            [activeDialog.value] = newActiveDialog;
            activeDialog.value.unreadCount = 0;
          } else {
            const response = await dialogsApi.newDialogs(newVal);
            const dialogData = response.data;
            activeDialog.value = generateNewDialog(dialogData);
          }
        }
      },
      { immediate: true }
    );

    watch(
      () => newMessage,
      (message) => {
        console.log(message);
        if (
          activeDialogId.value &&
          message.conversationPartner1 === activeDialogId.value
        ) {
          message.isSentByMe = false;
          commit("profile/dialogs/addOneMessage", message);
          dispatch(
            "profile/dialogs/markReadedMessages",
            message.conversationPartner1
          );
        }
      }
    );

    onMounted(() => {
      console.log(activeDialogId.value);
    });

    onBeforeMount(() => {
      setTimeout(() => {
        const searchQuery = {
          ...getUsersQueryParams.value,
          ids: conversationPartners.value,
        };
        dispatch("global/search/searchUsers", { payload: searchQuery });
      }, 1000);
    });

    const generateNewDialog = (dialogData) => {
      return {
        id: dialogData.id,
        unreadCount: dialogData.unreadCount,
        conversationPartner1: info.value.id,
        conversationPartner2: currentActiveDialogId.value,
        lastMessage: {
          time: dialogData.lastMessage && dialogData.lastMessage[0]?.time,
          messageText:
            dialogData.lastMessage && dialogData.lastMessage[0]?.messageText,
          authorId:
            dialogData.lastMessage && dialogData.lastMessage[0]?.authorId,
        },
      };
    };

    const countPush = () => {
      // return unread > 0 ? unread : null;
    };

    const clickOnDialog = (dialog) => {
      const partnerId =
        dialog.conversationPartner1 !== info.value.id
          ? dialog.conversationPartner1
          : dialog.conversationPartner2;
      router.push({
        name: "ImChat",
        params: { activeDialogId: partnerId },
      });
      dispatch("profile/dialogs/apiUnreadedMessages");
      activeDialogId.value = partnerId;
    };

    const formattedLastTime = (time) => {
      return dayjs(time).fromNow();
    };

    // ...mapMutations("profile/dialogs", [
    //   "setUnreadedMessages",
    //   "setNewMessage",
    //   "setActiveDialogId",
    // ]),
    // ...mapMutations("profile/dialogs", ["setDialogs"]),
    // ...mapGetters("users/info", ["getUsersInfo"]),
    // ...mapGetters("profile/dialogs", ["getDialogs"]),
    // ...mapGetters("profile/info", ["getInfo"]),

    return {
      activeDialog,
      messagesLoaded,
      translationsLang,
      dialogs,
      // getMessages,
      newMessage,
      info,
      users,
      getUsersQueryParams,
      currentActiveDialogId,
      conversationPartners,
      countPush,
      clickOnDialog,
      formattedLastTime,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';


.im-dialog__last
  font-size font-size-super-medium-small
  display flex
  align-items center
  gap 5px
  &-time
    color #979797

.im-dialog__info
  margin-bottom 5px

.im-dialog
  display flex
  align-items center
  padding 15px 10px
  cursor pointer
  border-radius border-super-small
  border 1px solid #b9b9b9
  background-color #c9c9c9
  transition all .2s ease-in-out
  &:not(:last-child)
    margin-bottom 10px
  @media (any-hover: hover)
    &:hover
      background-color #c3c3c3
  &.active
    background-color #e1e1e1
    outline 3px solid #21a45d;

.im-dialog__name
  margin-right 5px

.im-dialog-online
  font-size font-size-super-upsmall
  background-color ui-cl-color-gun-powder
  border-radius 3px
  color ui-cl-color-white-theme
  padding 3px

.no-dialog
  display flex
  width 100%
  height 100%
  justify-content:center
  align-items center
  color #666

.im
  display flex
  width 100%
  height calc(100vh - 175px)
  margin-bottom 30px

.im__dialogs
  width 100%
  max-width 35%
  overflow-y auto
  max-height 100%
  padding 15px
  height 100%
  border-radius 10px 0 0 10px
  background-color #d2d4d7

.im__chat
  width 100%
  flex auto
  height 100%
  background-color ui-cl-color-white-theme
  border-radius 0 10px 10px 0
  overflow hidden

.im-dialog__info
  display flex
  align-items center
  justify-content flex-start


.im-dialog__unread-count
  &--count
    &:after
      content attr(data-push)
      font-weight font-weight-regular
      font-size font-size-super-small
      width 15px
      height 15px
      color ui-cl-color-white-theme
      background-color #E65151
      border-radius border-half
      display flex
      align-items center
      justify-content center
      position relative
      right 0px
      bottom -7px
      transform translateY(-50%)
</style>
