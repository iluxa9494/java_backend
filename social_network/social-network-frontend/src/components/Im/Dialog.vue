<template>
  <div
    class="im-dialog"
    :class="{ active, push }"
    v-if="filteredUserInfo.length !== 0"
  >
    <a class="im-dailog__pic" href="#">
      <div class="main-layout__user-pic">
        <img
          v-if="filteredUserInfo && filteredUserInfo[0].photo"
          :src="filteredUserInfo && filteredUserInfo[0].photo"
          :alt="
            filteredUserInfo &&
            filteredUserInfo[0].firstName[0] + ' ' + filteredUserInfo &&
            filteredUserInfo[0].lastName[0]
          "
        />

        <div v-else>
          <unknow-user />
        </div>
      </div>
    </a>

    <div class="im-dialog-contents">
      <div class="im-dialog__info">
        <a class="im-dialog__name" href="#">
          {{ filteredUserInfo && filteredUserInfo[0].firstName }}
          {{ filteredUserInfo && filteredUserInfo[0].lastName }}
        </a>
        <div class="im-dialog-online">
          <span
            v-if="
              filteredUserInfo && filteredUserInfo[0].lastOnlineTime === null
            "
            >был(а) в сети давно</span
          >
          <span
            class="isonline-online"
            v-else-if="filteredUserInfo && filteredUserInfo[0].isOnline"
            >Онлайн</span
          >
          <span v-else
            >Был(а) в сети
            {{
              formatOnlineTime(
                filteredUserInfo && filteredUserInfo[0].lastOnlineTime
              )
            }}</span
          >
        </div>
      </div>
      <div class="im-dialog__content">
        <p class="im-dialog__last">
          <span class="im-dialog__last-me" v-if="!conversationPartner">
            Вы: {{ info.lastMessage && info.lastMessage[0]?.messageText }}</span
          >
          <span v-else>
            {{ info.lastMessage && info.lastMessage[0]?.messageText }}</span
          >
          <span>{{ info.lastMessage && info.lastMessage[0]?.time }}</span>
        </p>
      </div>
      <span class="im-dialog__push" v-if="push > 0">{{ push }}</span>
    </div>
  </div>
</template>

<script>
import { computed, onMounted } from "vue";
import { useStore } from "vuex";
import UnknowUser from "@/Icons/UnknowUser.vue";
import moment from "moment";

export default {
  name: "ImDialog",
  components: { UnknowUser },
  props: {
    active: Boolean,
    push: Number,
    online: Boolean,
    me: Boolean,
    info: Object,
    userInfo: Array,
  },

  setup(props) {
    const { getters, dispatch } = useStore();

    const getInfo = computed(() => getters["profile/info/getInfo"]);

    const conversationPartner = computed(() => {
      return props.info.conversationPartner1 === getInfo.value.id
        ? props.info.conversationPartner2
        : props.info.conversationPartner2 === getInfo.value.id
        ? props.info.conversationPartner1
        : null;
    });

    const filteredUserInfo = computed(() => {
      return props.userInfo.filter(
        (user) => user.id === conversationPartner.value
      );
    });

    onMounted(async () => {
      if (!getInfo.value) {
        await dispatch("profile/info/apiInfo");
      }
    });

    const formatOnlineTime = (time) => {
      return moment(time).fromNow();
    };

    return {
      conversationPartner,
      filteredUserInfo,
      formatOnlineTime,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.im-dialog
  &.active
    background-color #f5fff9
    border 1px solid ui-cl-color-eucalypt
    color ui-cl-color-white-theme
    &:hover
      background-color #f5fff9
      border 1px solid ui-cl-color-eucalypt

.im-dialog-online
  font-size font-size-super-small
  background-color ui-cl-color-eucalypt
  color ui-cl-color-white-theme
  padding 3px
  border-radius border-super-small
  box-shadow box-shadow-main

.im-dialog-contents
  display flex
  flex-direction column
  gap 5px

.im-dialog
  display flex
  gap 10px
  align-items center
  background ui-cl-color-white-bright-second
  border-radius border-small
  position relative
  padding 20px
  z-index 0
  transition all .2s ease-in-out
  cursor pointer

  .user-status
    padding 4px
    background-color ui-cl-color-eucalypt
    font-size font-size-super-upsmall
    line-height 100%

  &:not(:last-child)
    margin-bottom 15px

  &:hover
    background ui-cl-color-c5c5c5


  &.push + &.active, &.active + &.push
    &:after
      display none

  &.active
    &:after
      display none

    &:before
      display block

  &.push
    &:after
      display none

    &:before
      display block
      background-color #E9F5EF

    .im-dialog__time
      color #7D9A8B

.im-dailog__pic
  width 60px
  border-radius border-half
  overflow hidden
  flex none
  .main-layout__user-pic
    width 60px
    height 60px
    margin-right 0

.im-dialog__info
  display flex
  gap 5px
  align-items center
  width 100%

.im-dialog__name
  font-size font-size-default
  color ui-cl-color-grey-color
  font-weight font-weight-medium
  white-space nowrap
  overflow hidden
  text-overflow ellipsis

  @media (max-width breakpoint-xl)
    display none

.im-dialog__last
  white-space nowrap
  overflow hidden
  text-overflow ellipsis
  font-size font-size-small
  line-height 18px
  letter-spacing -0.003em

  &-me
    color ui-cl-color-eucalypt

.im-dialog__time
  color ui-cl-color-santas-gray
  font-size font-size-small
  line-height 19px

.im-dialog__push
  width 27px
  height 27px
  border-radius border-half
  display flex
  align-items center
  justify-content center
  font-size font-size-small
  line-height 27px
  font-weight font-weight-bold
  color ui-cl-color-white-theme
  background-color #E65151
  margin-left auto

.im-dialog__last-me
  color green
</style>
