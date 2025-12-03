<template>
  <div>
    <!--  Please avoid to use margin between -->
    <!--  messages block or it will glitter on-->
    <!--  scroll-->
    <h5 class="im-chat__message-title" v-if="source.stubDate">
      {{ source.date }}
    </h5>

    <div v-else class="im-chat__message-block" :class="{ me: isSentByMe }">
      <p class="im-chat__message-text">{{ source.messageText }}</p>
      <span class="im-chat__message-time">
        {{ formatTime(source.time) }}
      </span>
    </div>
  </div>
</template>

<script>
import { computed } from "vue";
import { useStore } from "vuex";
import dayjs from "dayjs";

export default {
  name: "InfiniteLoadingItem",
  props: {
    index: {
      // index of current item
      type: Number,
    },
    source: {
      type: Object,
      default() {
        return {};
      },
    },
  },

  setup(props) {
    const { getters, state } = useStore();

    const info = computed(() => state.profile.info.info);

    const messageConversation = computed(() => {
      return props.source?.conversationPartner1 === info.value?.id
        ? props.source?.conversationPartner2
        : props.source?.conversationPartner2 === info.value?.id
        ? props.source?.conversationPartner1
        : null;
    });

    const isSentByMe = computed(() => {
      return props.source?.conversationPartner1 === info.value?.id
        ? true
        : props.source?.conversationPartner2 === info.value?.id
        ? false
        : false;
    });

    const getInfo = () => {
      return getters["profile/info/getInfo"];
    };

    const formatTime = (time) => {
      return dayjs(time).format("YYYY-MM-DD HH:mm");
    };

    return {
      messageConversation,
      isSentByMe,
      getInfo,
      formatTime,
    };
  },
};
</script>

<style lang="stylus" scoped>
@import '@/assets/stylus/base/vars.styl';

.im-chat__message-block:not(:last-child) {
  margin-bottom 20px
}
.im-chat__message-day
  &+&
    margin-top 50px

.im-chat__message-title
  color #8A94A4
  font-size font-size-downdefault
  text-align center
  display block
  width 100%

.im-chat__message-block
  display flex
  align-items center

  &+&
    margin-top 20px

  &.me + &
    margin-top 30px

  &.me
    flex-direction row-reverse
    max-width 650px
    margin-left auto

    .im-chat__message-text
      background-color ui-cl-color-eucalypt
      color ui-cl-color-white-theme
      box-shadow 0px 4px 15px #D4E8DD

    .im-chat__message-time
      margin-right 15px
      margin-left 0

.im-chat__message-text
  word-wrap break-word
  background-color ui-cl-color-white-theme
  box-shadow 0px 4px 14px #e1e1e1
  border-radius border-big-radius
  padding 15px 20px
  color ui-cl-color-steel-gray
  font-size font-size-downdefault
  line-height 23px
  overflow-x hidden

.im-chat__message-time
  min-width 105px
  font-size font-size-small
  color #BAC3D0
  margin-left 15px
</style>
