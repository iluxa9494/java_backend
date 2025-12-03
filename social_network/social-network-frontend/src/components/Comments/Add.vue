<template>
  <form
    action="#"
    class="comment-add"
    :class="{ 'is-subcomment': isSubcomment }"
    @submit.prevent="onSubmitComment()"
  >
    <div class="comment-add__pic" v-if="getInfo.photo">
      <img :src="getInfo.photo" :alt="getInfo.firstName" />
    </div>

    <div class="comment-add__pic" v-else>
      <unknow-user />
    </div>

    <div class="comment-add__input-container">
      <input
        ref="addInput"
        type="text"
        minlength="5"
        class="comment-add__input"
        :placeholder="
          isSubcomment
            ? translationsLang.commentAddPlaceholderFirst
            : translationsLang.commentAddPlaceholderSecond
        "
        v-model="commentText"
        v-on:keydown.ctrl.enter="onSubmitComment()"
      />

      <EmojiIconComment
        class="emoji-icon-comment"
        @click.prevent="openEmojiPicker()"
      />

      <emoji-picker
        v-if="isOpenEmojiPicker"
        :native="true"
        @select="onSelectEmoji"
        hide-search
      >
      </emoji-picker>
    </div>
    <button class="comment-add__submit-btn" type="submit" useStore>
      <submit-icon />
    </button>
  </form>
</template>

<script>
import { ref, computed } from "vue";
import { useStore } from "vuex";
import useTranslations from "@/composables/useTranslations";
import EmojiPicker from "vue3-emoji-picker";
import "vue3-emoji-picker/css";
import EmojiIconComment from "@/Icons/EmojiIconComment.vue";
import SubmitIcon from "@/Icons/SubmitIcon.vue";
import UnknowUser from "@/Icons/UnknowUser.vue";

export default {
  name: "CommentAdd",
  components: {
    EmojiPicker,
    EmojiIconComment,
    SubmitIcon,
    UnknowUser,
  },
  props: {
    modelValue: String,
    id: [Number, String],
    parentId: String,
    isSubcomment: {
      type: Boolean,
      default: false,
    },
  },

  setup(props, { emit }) {
    const { getters } = useStore();
    const input = ref("");
    const search = ref("");
    const addInput = ref(null);
    const isOpenEmojiPicker = ref(false);
    const { translationsLang } = useTranslations();

    const getInfo = computed(() => getters["profile/info/getInfo"]);

    const commentText = computed({
      get: () => props.modelValue,
      set: (value) => emit("update:modelValue", value),
    });

    const onSubmitComment = () => {
      emit("submited");
    };

    const onSelectEmoji = (emoji) => {
      commentText.value += emoji.i;
    };

    const openEmojiPicker = () => {
      isOpenEmojiPicker.value = !isOpenEmojiPicker.value;
    };

    const focusInput = () => {
      if (addInput.value) {
        addInput.value.focus();
      }
    };

    const setFocusInput = () => {
      if (addInput.value) {
        addInput.value.focus();
      }
    };

    return {
      input,
      search,
      addInput,
      isOpenEmojiPicker,
      translationsLang,
      getInfo,
      commentText,
      onSubmitComment,
      onSelectEmoji,
      openEmojiPicker,
      focusInput,
      setFocusInput,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';
.emoji-invoker
  position absolute
  top 18%
  right 10px
  width 1.5rem
  height 1.5rem
  border-radius border-half
  cursor pointer
  transition all 0.2s
  padding 0
  background transparent
  border 0

.emoji-invoker:hover
  transform scale(1.1)

.emoji-picker
  position absolute
  bottom 100%
  left 100%
  z-index 1
  border 1px solid #ccc
  width 386px
  height 400px
  overflow-y scroll
  padding 20px
  box-sizing border-box
  border-radius 0.5rem
  background ui-cl-color-white-theme
  box-shadow 1px 1px 8px #c7dbe6

.emoji-picker__search
  display flex
  margin-bottom 15px

.emoji-invoker > svg
  fill #bdcdd6

.emoji-picker__search > input
  flex 1
  border-radius 10rem
  border 1px solid #ccc
  padding 0.5rem 1rem
  outline none

.emoji-picker h5
  margin-bottom 0
  color #b1b1b1
  text-transform uppercase
  font-size 0.8rem
  cursor default

.emoji-picker .emojis
  display flex
  flex-wrap wrap
  justify-content space-between

.emoji-picker .emojis:after
  content ""
  flex auto

.emoji-picker .emojis span
  padding 0.2rem
  cursor pointer
  border-radius border-super-small

.emoji-picker .emojis span:hover
  background #ececec
  cursor pointer

.v3-emoji-picker
  height 210px
  width 260px
  position absolute
  top -125px
  right -267px

.v3-emoji-picker .v3-header
  padding: 10px 10px 10px;

.v3-emoji-picker .v3-body
  padding-bottom 6px

.v3-emoji-picker .v3-footer
  padding-top 7px
  padding-bottom 7px

.v3-emoji-picker .v3-footer .v3-tone
  display none

.v3-emoji-picker .v3-body .v3-body-inner .v3-group h5.v3-sticky
  font-size 10px

.comment-add__input-container
  position relative
  display flex
  width 100%
  margin-right 10px
  align-items center
  justify-content space-between

.emoji-icon-comment
  position absolute
  right 10px

.open-comment
  border-color #575757

.comment-add
  display flex
  max-width 515px
  padding-top 20px
  align-items center
  margin-bottom 30px

  &.is-subcomment
    padding-top 10px !important
    margin-bottom 10px !important
    border-top 1px solid ui-cl-color-white-bright-second
    border-bottom none
    margin-top 20px

.comment-add__submit-btn
  background-color transparent
  color #bdcdd6

.comment-add__pic
  width 34px
  height 34px
  border-radius border-half
  overflow hidden
  margin-right 15px
  flex none
  background #9eaab0

  img
    display flex
    align-items center
    justify-content center
    width 100%
    height 100%
    object-fit cover

  div
    display flex
    align-items center
    justify-content center
    width 100%
    height 100%
    object-fit cover

.comment-add__input
  font-size font-size-small
  width 100%
  line-height 17px
  color #6A6A80
  padding 7px 90px 7px 13px
  height 40px
  max-height none
  border 1px solid #d3d9de
  border-radius 6px
  display block

  &::placeholder
    color #B0B0BC

.comment-add__icon
  cursor pointer

  &+&
    margin-left: 10px

  &.photo
    .simple-svg-wrapper
      width: 22px
      height: 22px

  &.add
    margin-top: -5px

    .simple-svg-wrapper
      width: 15px
      height: 15px
</style>
