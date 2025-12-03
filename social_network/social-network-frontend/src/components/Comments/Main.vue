<template>
  <div class="comment-main">
    <template v-if="info.is_deleted">
      <p class="comment-main__text">
        <span>{{ translationsLang.commentIsDeleted }}</span>

        <a href="#" @click="onRecoverComment">{{
          translationsLang.commentIsRecovery
        }}</a>
      </p>
    </template>

    <template v-else>
      <router-link
        class="comment-main__pic"
        :to="{ name: 'ProfileId', params: { id: info.authorId } }"
      >
        <img
          class="comment-add__pic"
          v-if="info.author.photo"
          :src="info.author.photo"
          :alt="info.author.firstName"
        />

        <div v-else class="comment-add__pic">
          <unknow-user />
        </div>
      </router-link>

      <div class="comment-main__main">
        <div class="comment-main__top">
          <div class="comment__author">
            <router-link
              class="comment-main__author"
              :to="{ name: 'ProfileId', params: { id: info.author.id } }"
            >
              {{ info.author.firstName + " " + info.author.lastName }}
            </router-link>
          </div>
          <transition name="fade">
            <div class="comment-main__allactions-top" v-if="edit || deleted">
              <div class="comment-main__actions">
                <div
                  class="comment-main__icons-top"
                  v-if="edit"
                  @click="editComment()"
                >
                  <edit-icon />
                </div>
                <div
                  class="comment-main__icons-top"
                  v-if="deleted"
                  @click="onDeleteComment()"
                >
                  <delete-comment />
                </div>
              </div>
            </div>
          </transition>
        </div>

        <div class="comment-main__text">
          {{ info.commentText }}
        </div>

        <div class="comment-main__actions">
          <div class="comment-main__actions-button">
            <span class="comment-main__time">{{ formatTime(info.time) }}</span>
            <a
              class="comment-main__review"
              href="#"
              @click.prevent="answerComment()"
            >
              {{ translationsLang.commentAddAnswer }}
            </a>
          </div>
          <div class="show__like">
            <like-comment
              fill="fill"
              :quantity="info.likeAmount"
              :active="info.myLike || info.likeAmount"
              :id="info.id"
              @liked="likeAction"
            />
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { ref } from "vue";
import { useStore } from "vuex";
import LikeComment from "@/components/LikeComment";
import EditIcon from "@/Icons/EditIcon.vue";
import DeleteComment from "@/Icons/DeleteNewsIcon.vue";
import UnknowUser from "@/Icons/UnknowUser.vue";
import useTranslations from "@/composables/useTranslations";
import dayjs from "dayjs";

export default {
  name: "CommentMain",
  components: { LikeComment, EditIcon, DeleteComment, UnknowUser },
  props: {
    admin: Boolean,
    info: Object,
    edit: Boolean,
    deleted: Boolean,
    isSubcomment: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, { emit }) {
    const { dispatch } = useStore();
    const like = ref(props.info.myLike);
    const { translationsLang } = useTranslations();

    const likeAction = () => {
      if (like.value) {
        dispatch("global/likes/deleteLike", {
          itemId: props.info.id,
          postId: props.info.postId,
          type: "COMMENT",
        });
        like.value = !like.value;
        return;
      }

      dispatch("global/likes/putLike", {
        itemId: props.info.id,
        postId: props.info.postId,
        type: "COMMENT",
      });
      like.value = !like.value;
    };

    const onDeleteComment = () => {
      if (props.isSubcomment)
        emit("delete-comment", props.info.id, props.info.parentId);
      else emit("delete-comment", props.info.id);
    };

    const answerComment = () => {
      emit("answer-comment");
    };

    const editComment = () => {
      emit("edit-comment", {
        id: props.info.id,
        commentText: props.info.commentText,
        parentId: props.info.parentId,
      });
    };

    const onRecoverComment = () => {
      emit("recover-comment", props.info.id);
    };

    const formatTime = (time) => {
      return dayjs(time).fromNow();
    };

    return {
      like,
      translationsLang,
      likeAction,
      onDeleteComment,
      answerComment,
      editComment,
      onRecoverComment,
      formatTime,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.comments
  font-family 'Open Sans', sans-serif !important
.comment-main__actions-button
  display flex
  gap 7px
.comment__author
  display flex
  gap 5px

.comment-block .comment-main
  padding 0 !important

.comment-main
  display flex
  font-size font-size-small
  position relative

  .like-comment
    background-color transparent
    padding 0
    height unset !important
    svg
      color #bdcdd6
      width 19px
      height 19px
      &:hover
        color ui-cl-color-wild-watermelon

  &__top
    display inline-flex
    align-items center
    justify-content space-between
    gap 10px

  &__allactions-top
    display inline-flex
    align-items center

  &__icons-top
    color ui-cl-color-full-black
    cursor pointer
    svg
      width 14px
      height 14px
      path
        fill #bdcdd6
    &:hover
      svg path
        fill #6E7B82


.comment-main__pic
  flex none
  align-self flex-start
  width 40px
  height 40px
  border-radius border-half
  overflow hidden
  margin-right 10px

  img
    width 100%
    height 100%
    object-fit cover

.comment-main__main
  display flex
  flex-direction column
  gap 3px
  justify-content center
  width 100%

.comment-main__author
  font-weight font-weight-bold
  color #444444
  font-size font-size-small
  display block

.comment-main__text
  display flex
  align-items center
  justify-content space-between
  gap 5px
  line-height 21px
  color #6A6A80

.comment-main__actions
  display flex
  align-items center
  gap 5px
  justify-content space-between
  &.fade-enter-active,
  &.fade-leave-active
    transition all .2s ease-in-out
  &.fade-enter,
  &.fade-leave-to
    opacity 0

.show__like
  display flex
  align-items center
  gap 10px

  &.fade-enter-active,
  &.fade-leave-active
    transition all .2s ease-in-out
  &.fade-enter,
  &.fade-leave-to
    opacity 0

.comment-main__time
  color #777777
  display block
  margin-right 10px

.comment-main__review
  color ui-cl-color-eucalypt
  margin-right auto

.comment-add__pic
  display flex
  align-items center
  justify-content center
  width 100%
  height 100%
  object-fit cover
</style>
