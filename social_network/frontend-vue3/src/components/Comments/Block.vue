<template>
  <div class="comment-block" :class="{ 'show-comments': isShowSubComments }" >
    <template v-if="admin">
      <div class="edit" v-tooltip.bottom="'Разблокировать'" v-if="blocked">
        <img src="@/assets/static/img/unblocked.svg" alt="img.svg" />
      </div>

      <div class="edit" v-tooltip.bottom="'Заблокировать'" v-else>
        <img src="@/assets/static/img/blocked.svg" alt="img.svg" />
      </div>
    </template>

    <comment-main
      :admin="admin"
      :info="info"
      :edit="edit"
      :deleted="deleted"
      @answer-comment="onAnswerMain"
      @edit-comment="onEditMain"
      @delete-comment="onDeleteComment"
      @recover-comment="onRecoverComment"
    />

    <div class="comment-block__reviews" v-if="!info.is_deleted">
      <a
        class="comment-block__reviews-show"
        href="#"
        v-if="!currentSubComents && info.commentsCount"
        @click.prevent="showSubComments()"
      >
        {{ translationsLang.commentAnswerShow }} {{ info.commentsCount }}
        {{ answerText }}
      </a>

      <div class="comment-block__reviews-list" v-show="currentSubComents" >
        <transition name="fade">
          <div v-if="currentSubComents" class="subcomments" >
            <comment-main
              :isSubcomment="true"
              :admin="admin"
              v-for="i in currentSubComents.currentComments"
              :key="i.id"
              :info="i"
              :edit="getInfo.id === i.author.id"
              :deleted="getInfo.id === i.author.id"
              @answer-comment="onAnswerSub"
              @edit-comment="onEditSub"
              @delete-comment="onDeleteSubComment"
              @recover-comment="onRecoverSubComment"
            />
          </div>
        </transition>

        <comment-add
          :isSubcomment="true"
          v-if="!admin"
          ref="addCommentRef"
          :id="info.postId"
          :parent-id="info.parentId"
          v-model="commentText"
          @submited="onSubmitComment"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref } from "vue";
import { useStore } from "vuex";
import CommentMain from "@/components/Comments/Main";
import CommentAdd from "@/components/Comments/Add";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "CommentBlock",
  components: { CommentMain, CommentAdd },
  props: {
    admin: Boolean,
    blocked: Boolean,
    info: Object,
    edit: Boolean,
    deleted: Boolean,
  },
  setup(props, { emit }) {
    const { getters, state, dispatch } = useStore();
    const isShowSubComments = ref(false);
    const commentText = ref("");
    const commentEdit = ref(false);
    const commentEditId = ref(null);
    const commentEditParentId = ref(null);
    const addCommentRef = ref(null);
    const { translationsLang } = useTranslations();

    const getInfo = computed(() => getters["profile/info/getInfo"]);
    const subComments = computed(() => state.profile.comments.subComments);

    const answerText = computed(() => {
      if (!props.info) return translationsLang.commentAnswerTextSecond;
      return props.info.commentsCount && props.info.commentsCount > 1
        ? translationsLang.commentAnswerTextFirst
        : translationsLang.commentAnswerTextSecond;
    });

    const currentSubComents = computed(() => subComments.value[props.info.id]);

    const showSubComments = async () => {
      if (isShowSubComments.value) return;
      await dispatch("profile/comments/commentsById", {
        postId: props.info.postId,
        commentId: props.info.id,
      });
      isShowSubComments.value = true;
    };

    const onAnswerSub = () => {
      if (addCommentRef.value) {
        addCommentRef.value.setFocusInput();
      }
    };

    const onAnswerMain = async () => {
      await showSubComments();
      onAnswerSub();
    };

    const onEditMain = ({ commentText }) => {
      emit("edit-comment", {
        commentInfo: props.info,
        commentText,
      });
    };

    const onDeleteComment = (id) => {
      dispatch("profile/comments/deleteComment", {
        id,
        postId: props.info.postId,
      });
    };

    const onDeleteSubComment = (id, parentId) => {
      dispatch("profile/comments/deleteComment", {
        id,
        postId: props.info.postId,
        parentId,
      });
    };

    const onRecoverComment = (id) => {
      dispatch("profile/comments/recoverComment", {
        id,
        postId: props.info.postId,
      });
    };

    const onRecoverSubComment = (id) => {
      dispatch("profile/comments/recoverComment", {
        id,
        postId: props.info.postId,
        isSubComment: true,
      });
    };

    const onEditSub = ({ parentId, id, commentVal }) => {
      commentEdit.value = true;
      commentText.value = commentVal;
      commentEditId.value = id;
      commentEditParentId.value = parentId;
      onAnswerSub();
    };

    const onSubmitComment = async () => {
      try {
        await dispatch("profile/comments/commentActions", {
          isSubcomment: true,
          edit: commentEdit.value,
          postId: props.info.postId,
          parentId: commentEdit.value
            ? commentEditParentId.value
            : props.info.id,
          text: commentText.value,
          id: commentEditId.value,
        });

        resetCommentState();
      } catch (error) {
        console.error("Ошибка при отправке комментария:", error);
        // Опционально: обработка ошибок, например, показать сообщение пользователю
      }
    };

    const resetCommentState = () => {
      commentText.value = "";
      commentEdit.value = false;
      commentEditParentId.value = null;
      isShowSubComments.value = true;
    };

    return {
      isShowSubComments,
      commentText,
      addCommentRef,
      translationsLang,
      getInfo,
      subComments,
      answerText,
      currentSubComents,
      showSubComments,
      onAnswerSub,
      onAnswerMain,
      onEditMain,
      onDeleteComment,
      onDeleteSubComment,
      onRecoverComment,
      onRecoverSubComment,
      onEditSub,
      onSubmitComment,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.comment-block
  position relative

    &:after
      display block

  &:not(:last-child)
    margin-bottom 20px

  &.show-comments
    & + .comment-block
      margin-top 0

      &:after
        width 100%

  .comment-add

    &.is-subcomment
      padding 15px 0
      margin-top 0
      border-top 0

  .comment-main
    padding 15px 0


.comment-block__reviews
  max-width calc(100% - 50px)
  margin-left auto

.comment-block__reviews-show
  color ui-cl-color-eucalypt
  font-size font-size-small
  font-weight font-weight-bold
  display inline-flex
  align-items flex-start

  &:before
    content '↳'
    display block
    width 7px
    margin-right 5px
    font-size font-size-small

  .comment-main__pic
    width 30px
    height 30px
.subcomments
  &.fade-enter-active,
  &.fade-leave-active
    transition all .2s ease-in-out
  &.fade-enter,
  &.fade-leave-to
    opacity 0
  .comment-main
    padding-top 15px !important
    margin-bottom 0
  .comment-add__pic
    width 30px
    height 30px
    font-size font-size-super-small
  .comment-main__pic
    margin-right 0
</style>
