<template>
  <div>
    <div v-if="queued" class="news-block queued-news__block">
      <add-form
        v-if="isEditNews"
        :info="info"
        edit="edit"
        :deffered="deffered"
        @submit-complete="toggleEditNews"
        @close-form="toggleEditNews"
      />
      <template v-else>
        <div class="post-block__top">
          <div class="post-block__top-author">
            <div class="post-block__top-avatar">
              <img
                v-if="info.author.photo"
                :src="info.author.photo"
                :alt="info.author.firstName[0] + ' ' + info.author.lastName[0]"
              />
              <div v-else>
                <unknow-user />
              </div>
            </div>
            <div class="post-block__info">
              <router-link :to="routerLink(info.author.id)">
                {{ info.author.firstName + " " + info.author.lastName }}
              </router-link>
              <span v-if="!info.timeChanged">
                {{ translationsLang.newsBlockPublishedTitle }}
                {{ formatTime(info.time) }}
              </span>
              <span v-if="info.timeChanged">
                {{ translationsLang.newsBlockEditedTime }}
                {{ formatTimeChange(info.timeChanged) }}
              </span>
            </div>
          </div>
          <div
            v-if="(!admin && edit) || deleted"
            class="post-block__top-actions"
          >
            <div class="button-top__actions" @click="actionsShow">
              <show-more />
            </div>
            <transition name="fade">
              <div
                v-if="showActions"
                class="post-block__showmore-actions"
                v-click-outside="closeActions"
              >
                <button
                  class="post-block__btn-edit"
                  v-if="edit"
                  @click="toggleEditNews"
                >
                  {{ translationsLang.friendsBlockEdit }}
                </button>
                <button
                  class="post-block__btn-edit"
                  v-if="deleted"
                  @click="deleteNews"
                >
                  {{ translationsLang.newsBlockDeleted }}
                </button>
                <button class="post-block__btn-edit" v-if="blocked">
                  {{ translationsLang.profileAccountUnblocking }}
                </button>
                <button class="post-block__btn-edit" v-if="blocked">
                  {{ translationsLang.profileAccountBlocking }}
                </button>
              </div>
            </transition>
          </div>
        </div>
        <div class="news-block__deffered" v-if="queued">
          <span class="news-block__deffered-text">
            {{ translationsLang.newsBlockQueuedTime }}
            {{ formatTimeLLLL(info.time) }}
            ({{ translationsLang.newsBlockQueuedDatatime }}
            {{ formatPublisheLLLL(info.publishDate) }}
            )
          </span>
        </div>
        <div class="post-block__text">
          <div v-if="info.imagePath">
            <img class="post-image" :src="info.imagePath" :alt="'photo'" />
          </div>
          <h3 class="post-block__title">{{ info.title }}</h3>
          <p
            class="post-block__text-content"
            ref="textRef"
            v-html="displayedText"
          />
          <div
            v-if="currentPostText > 65 && !openText"
            @click.prevent="toggleText"
          >
            <button class="post-block__showmore">
              {{ translationsLang.newsBlockReadedMore }}
            </button>
          </div>
          <div v-if="openText" @click.prevent="toggleText">
            <button class="post-block__showmore">
              {{ translationsLang.newsBlockReadedHide }}
            </button>
          </div>
        </div>
      </template>
    </div>
    <div
      v-if="!queued"
      class="news-block"
      :class="{
        deffered,
        'news-block--admin': admin,
        'news-block--edited': isEditNews,
      }"
    >
      <add-form
        v-if="isEditNews"
        :info="info"
        edit="edit"
        :deffered="deffered"
        @submit-complete="toggleEditNews"
        @close-form="toggleEditNews"
      />
      <template v-else>
        <div class="post-block__top">
          <div class="post-block__top-author">
            <div class="post-block__top-avatar">
              <img
                v-if="info.author.photo"
                :src="info.author.photo"
                :alt="info.author.firstName[0] + ' ' + info.author.lastName[0]"
              />
              <div v-else>
                <unknow-user />
              </div>
            </div>
            <div class="post-block__info">
              <router-link :to="routerLink(info.author.id)">{{
                info.author.firstName + " " + info.author.lastName
              }}</router-link>
              <span v-if="!info.timeChanged">
                {{ translationsLang.newsBlockPublishedTitle }}
                {{ formatTimeFrom(info.time) }}
              </span>
              <span v-if="info.timeChanged">
                {{ translationsLang.newsBlockEditedTime }}
                {{ formatTimeChangeFrom(info.timeChanged) }}
              </span>
            </div>
          </div>
          <div
            v-if="(!admin && edit) || deleted"
            class="post-block__top-actions"
          >
            <div class="button-top__actions" @click="actionsShow">
              <show-more />
            </div>
            <transition name="fade">
              <div
                v-if="showActions"
                class="post-block__showmore-actions"
                v-click-outside="closeActions"
              >
                <button
                  class="post-block__btn-edit"
                  v-if="edit"
                  @click="toggleEditNews"
                >
                  {{ translationsLang.friendsBlockEdit }}
                </button>
                <button
                  class="post-block__btn-edit"
                  v-if="deleted"
                  @click="deleteNews"
                >
                  {{ translationsLang.newsBlockDeleted }}
                </button>
                <button class="post-block__btn-edit" v-if="blocked">
                  {{ translationsLang.profileAccountUnblocking }}
                </button>
                <button class="post-block__btn-edit" v-if="blocked">
                  {{ translationsLang.profileAccountBlocking }}
                </button>
              </div>
            </transition>
          </div>
        </div>
        <h3 class="post-block__title">{{ info.title }}</h3>
        <div class="post-block__timer">
          <post-timer />
          <span
            @mouseover="showInfoTimer = true"
            @mouseout="showInfoTimer = false"
          >
            {{ timeToRead }}
            {{ translationsLang.newsBlockReadedTime }}
          </span>
          <transition name="fade">
            <div v-if="showInfoTimer" class="post-block__timer-more">
              {{ translationsLang.newsBlockReadedDisclamer }}
            </div>
          </transition>
        </div>
        <ul class="post-block__tags" v-if="info.tags && info.tags.length > 0">
          <li
            class="post-block__tags-item"
            v-for="(tag, index) in info.tags"
            :key="index"
          >
            <router-link
              :to="{ name: 'Search', query: { tab: 'news', tags: tag.name } }"
            >
              {{ "#" + tag.name }}
            </router-link>
          </li>
        </ul>

        <div class="post-block__text">
          <div v-if="info.imagePath">
            <img class="post-image" :src="info.imagePath" :alt="'photo'" />
          </div>
          <p
            class="post-block__text-content"
            ref="textRef"
            v-html="displayedText"
          />
          <div
            v-if="currentPostText > 65 && !openText"
            @click.prevent="toggleText"
          >
            <button class="post-block__showmore">
              {{ translationsLang.newsBlockReadedMore }}
            </button>
          </div>
          <div v-if="openText" @click.prevent="toggleText">
            <button class="post-block__showmore">
              {{ translationsLang.newsBlockReadedHide }}
            </button>
          </div>
        </div>

        <div
          class="post-block__actions"
          v-if="!queued && !admin"
          :class="{ 'open-comment': openCommnets }"
        >
          <div
            class="news-block__actions-block news-block__comments-btn"
            @click="toggleComments()"
            :title="
              !openCommnets
                ? translationsLang.newsBlockCommentTitleFirst
                : translationsLang.newsBlockCommentTitleSecond
            "
          >
            <like-comment
              :quantity="info.commentsCount"
              width="16px"
              height="16px"
              font-size="15px"
              color="ui-cl-color-eucalypt"
              comment="comment"
            />
          </div>
          <post-reactions
            @reaction-deleted="deleteReaction"
            @reaction-added="newlikeAction"
            :active="info.myLike"
            :reaction="info.myReaction"
            :quantity="info.likeAmount"
            :reactions-info="info.reactionType"
          />

          <!-- <div class="news-block__actions-block">
            <like-comment
              :quantity="info.likeAmount"
              width="16px"
              height="16px"
              font-size="15px"
              @liked="likeAction"
              :active="info.myLike"
              :id="info.id"
            />
          </div> -->
        </div>
        <transition name="fade">
          <div class="post-block__comments" v-if="!queued && openCommnets">
            <comments
              :admin="admin"
              :info="currentComments"
              :id="info.id"
              :edit="edit"
              :deleted="deleted"
            />
            <button
              v-if="
                currentComments &&
                currentComments.totalPages !== 0 &&
                currentComments.page + 1 !== currentComments.totalPages
              "
              class="post-block__comment-more"
              @click.prevent="showMore()"
            >
              {{ translationsLang.newsBlockCommentMore }}
            </button>
          </div>
        </transition>
      </template>
    </div>
  </div>
</template>

<script>
import { computed, onMounted, ref } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import PostTimer from "@/Icons/PostTimer.vue";
import ShowMore from "@/Icons/ShowMore.vue";
import UnknowUser from "@/Icons/UnknowUser.vue";
import AddForm from "@/components/News/AddForm";
import LikeComment from "@/components/LikeComment";
import Comments from "@/components/Comments/Index.vue";
import vClickOutside from "click-outside-vue3";
import useTranslations from "@/composables/useTranslations";
import PostReactions from "@/components/PostReactions.vue";
import dayjs from "dayjs";

export default {
  name: "NewsBlock",
  directives: {
    clickOutside: vClickOutside.directive,
  },
  components: {
    Comments,
    LikeComment,
    AddForm,
    PostTimer,
    ShowMore,
    UnknowUser,
    PostReactions,
  },
  props: {
    info: {
      type: Object,
      default: () => ({
        title: "Дизайн привычных вещей",
        post_text:
          "А вот и «Книга недели от Skillbox и МИФ»." +
          " Сегодня делимся с вами книгой «Дизайн привычных вещей» автора Дональда Нормана." +
          " В ней Дональд рассказывает об основополагающих принципах, которым нужно следовать" +
          " чтобы избежать проблем и превратить привычные вещи в приятные товары, доставляющие" +
          " нам удовольствие А вот и «Книга недели от Skillbox и МИФ». Сегодня делимся с вами" +
          " книгой «Дизайн привычных вещей» автора Дональда Нормана. В ней Дональд рассказывает" +
          " об основополагающих принципах, которым нужно следовать, чтобы избежать проблем и" +
          " превратить привычные вещи в приятные товары, доставляющие нам удовольствие",
        time: 1559751301818,
        likes: 44,
        id: 1,
        tags: [],
        comments: {},
        author: "",
      }),
    },
    edit: Boolean,
    deffered: Boolean,
    admin: Boolean,
    blocked: Boolean,
    deleted: Boolean,
    queued: {
      type: Boolean,
      default: false,
    },
  },

  setup(props) {
    const { getters, state, dispatch } = useStore();
    const route = useRoute();
    const openCommnets = ref(false);
    const isLotText = ref(false);
    const openText = ref(false);
    const isEditNews = ref(false);
    const commnets = ref("");
    const showInfoTimer = ref(false);
    const showActions = ref(false);
    const textRef = ref(null);
    const { translationsLang } = useTranslations();

    const getInfo = computed(() => getters["profile/info/getInfo"]);
    const comments = computed(() => state.profile.comments.comments);

    const currentComments = computed(() => comments.value[props.info.id]);

    const displayedText = computed(() => {
      const words = props.info.postText.split(" ");
      if (words.length > 100 && !openText.value) {
        return words.slice(0, 100).join(" ") + "...";
      }
      return props.info.postText;
    });
    const currentPostText = computed(() => {
      const words = props.info.postText.split(" ").length;
      return words;
    });
    const timeToRead = computed(() => {
      const wordsCount = props.info.postText.split(" ").length;
      const averageReadingSpeed = 150;
      const timeInMinutes = Math.ceil(wordsCount / averageReadingSpeed);
      return timeInMinutes;
    });

    onMounted(() => {
      isLotText.value = textRef.value.offsetHeight > 150;
    });

    const toggleText = () => {
      openText.value = !openText.value;
    };

    const actionsShow = () => {
      showActions.value = !showActions.value;
    };

    const closeActions = () => {
      showActions.value = false;
    };

    const toggleComments = async () => {
      const isSetComments = !!currentComments.value;
      const currentPage = isSetComments ? currentComments.value.page : null;
      if (!isSetComments) {
        try {
          await dispatch("profile/comments/commentsById", {
            postId: props.info.id,
            currentPage,
          });
          openCommnets.value = !openCommnets.value;
        } catch (error) {
          console.log(error);
        }
      } else {
        openCommnets.value = !openCommnets.value;
      }

    };

    const showMore = async () => {
      await dispatch("profile/comments/commentsById", {
        postId: props.info.id,
        currentPage: currentComments.value.page,
      });
    };

    const deleteReaction = () => {
      dispatch("global/likes/deleteLike", {
        itemId: props.info.id,
        data: { type: "DELETE", reactionType: null },
      });
    };

    const newlikeAction = (reactionType) => {
      dispatch("global/likes/putLike", {
        itemId: props.info.id,
        data: { type: "POST", reactionType: reactionType },
      });
    };

    const likeAction = (active) => {
      if (active)
        dispatch("global/likes/deleteLike", {
          itemId: props.info.id,
          type: "DELETE",
        });
      else
        dispatch("global/likes/putLike", {
          itemId: props.info.id,
          data: { type: "POST", reactionType: null },
        });
    };

    const toggleEditNews = () => {
      isEditNews.value = !isEditNews.value;
    };

    const routerLink = (infoAuthorId) => {
      return getInfo.value?.id === infoAuthorId
        ? { name: "Profile" }
        : { name: "ProfileId", params: { id: infoAuthorId } };
    };

    const deleteNews = () => {
      dispatch("profile/feeds/deleteFeeds", {
        id: getInfo.value.id,
        postId: props.info.id,
        route: route.name,
      });
    };

    const formatTime = (time) => {
      return dayjs(time).fromNow();
    };
    const formatTimeChange = (timeChange) => {
      return dayjs(timeChange).fromNow();
    };
    const formatTimeLLLL = (time) => {
      return dayjs(time).fromNow("LLLL");
    };
    const formatPublisheLLLL = (publishe) => {
      return dayjs(publishe).fromNow("LLLL");
    };
    const formatTimeFrom = (time) => {
      return dayjs(time).fromNow("from");
    };
    const formatTimeChangeFrom = (timeChange) => {
      return dayjs(timeChange).fromNow("from");
    };

    // const deletePost = () => {
    //   dispatch('users/info/deletePost');
    // };

    return {
      openCommnets,
      openText,
      isEditNews,
      commnets,
      showInfoTimer,
      showActions,
      textRef,
      translationsLang,
      comments,
      currentComments,
      displayedText,
      currentPostText,
      timeToRead,
      toggleText,
      actionsShow,
      closeActions,
      toggleComments,
      showMore,
      deleteReaction,
      newlikeAction,
      likeAction,
      toggleEditNews,
      routerLink,
      deleteNews,
      formatTime,
      formatTimeChange,
      formatTimeLLLL,
      formatPublisheLLLL,
      formatTimeFrom,
      formatTimeChangeFrom,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.open-comment
  padding-bottom 30px
  border-bottom 1px solid #BDCDD6
.button-top__actions
  cursor pointer
  svg
    pointer-events none

.post-block__comment-more
  background-color transparent
  margin-top 20px
  font-size font-size-downdefault
  font-weight font-weight-medium
  color ui-cl-color-eucalypt

.post-block
  max-width 910px
  padding 30px
  background-color ui-cl-color-white-theme
  border-radius border-small
  box-shadow box-shadow-main
  &__comments
    &.fade-enter-active,
    &.fade-leave-active
      transition all .2s ease-in-out
    &.fade-enter,
    &.fade-leave-to
      opacity 0
  &__top
    position relative
    display flex
    align-items center
    justify-content space-between
    margin-bottom 25px
    &-author
      display flex
      align-items center
    &-avatar
      display flex
      align-items center
      justify-content center
      background #BDCDD6
      width 40px
      height 40px
      border-radius border-half
      overflow hidden
      margin-right 15px
      margin-right 10px
      user-select none
      pointer-events none
      img
        display flex
        align-items center
        justify-content center
        width 100%
        height 100%
        object-fit cover
    &-actions
      display flex
      gap 10px
  &__showmore-actions
    position absolute
    display flex
    flex-direction column
    top 40px
    right 0
    background-color ui-cl-color-white-theme
    border-radius border-small
    padding 10px 0
    min-width 150px
    max-width 250px
    box-shadow 0px 15px 40px rgba(0,0,0,10%)
    z-index 12

    &.fade-enter-active,
    &.fade-leave-active
      transition all .2s ease-in-out
    &.fade-enter,
    &.fade-leave-to
      opacity 0
  &__info
    a:nth-child(1)
      font-weight font-weight-bold
      font-size font-size-default
      line-height 19px
      color #444444
      margin-right 10px
    span:nth-child(2)
      font-weight font-weight-regular
      color #777777
      font-size font-size-small
      line-height 15px
  &__btn-edit
    display flex
    align-items center
    gap 8px
    padding 8px 15px
    background-color transparent
    font-size font-size-small
    line-height 18px
    color ui-cl-color-full-black
    transition all .2s ease-in-out
    &:hover
      background-color rgba(174, 183, 194, 0.12)

  &__title
    font-weight font-weight-bold
    font-size 20px
    line-height 23px
    margin-bottom 15px
  &__timer
    position relative
    display inline-flex
    align-items center
    gap 3px
    font-weight font-weight-regular
    font-size font-size-small
    line-height 16px
    color #BDCDD6
    margin-bottom 15px

    &.fade-enter-active,
    &.fade-leave-active
      transition all .2s ease-in-out
    &.fade-enter,
    &.fade-leave-to
      opacity 0
    &-more
      position absolute
      white-space nowrap
      top -35%
      font-weight font-weight-regular
      left 100% + 15px
      background ui-cl-color-grey-color
      color ui-cl-color-white-theme
      border-radius border-super-small
      padding 5px 10px
      z-index 10
      &::after
        content " "
        position absolute
        top 50%
        right 100%;
        margin-top -5px
        border-width 5px
        border-style solid
        border-color transparent ui-cl-color-grey-color transparent transparent
  &__tags
    display flex
    align-items center
    flex-wrap wrap
    gap 10px
    margin-bottom 25px
    &-item
      background-color #efefef
      border-radius border-super-small
      padding 5px
      color #696767
      font-size font-size-small
      transition color, background-color .2s ease-in-out
      a
        color #696767
      &:hover
        background-color #9eaab0
        a
          color ui-cl-color-grey-color

  &__text
    &.fade-enter-active,
    &.fade-leave-active
      transition all 5s ease-in-out
    &.fade-enter,
    &.fade-leave-to
      opacity 0
    &-content
      font-size font-size-default
      line-height 24px
      color #444
      margin-bottom 25px
      p:not(:first-child)
        margin-bottom 10px
      p:last-child
        margin-bottom 0

  &__showmore
    padding 10px
    background-color #9eaab0
    color ui-cl-color-white-theme
    font-size font-size-small
    border-radius border-super-small
    margin-bottom 35px
    transition background-color .2s ease-in-out
    @media (any-hover: hover)
      &:hover
        background-color #6d7579

  &__actions
    display flex
    justify-content space-between
    align-items center
    gap 15px
</style>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.post-image
  width 400px

.queued-news__block .post-block__top
  margin-bottom 10px

.news-block
  background ui-cl-color-white-theme
  box-shadow box-shadow-main
  padding 30px
  position relative
  border-radius border-small
  margin-bottom 20px

.news-block__deffered
  margin-bottom 20px
  display flex

.news-block__deffered-text
  color ui-cl-color-comet
  font-size font-size-default

@media (min-width: 320px) and (max-width: 768px)
  .news-block
    padding 25px 15px 15px 15px
    .post-image
      margin-bottom 15px
    &__changed-time
      left 10px
      top 10px
      padding 3px !important
      font-size 9px
    &__deffered
      max-width 230px
      top 5px
      left 5px
      &-text
        font-size 9px

    &--edited
      padding 0
    &__author-pic
      width 40px
      height 40px
      .main-layout__user-pic
        width 40px
        height 40px
    &__author-name
      margin-bottom 0
      font-size font-size-small
    &__author-time
      font-size 11px
    &__author-info
      font-size font-size-small
    &__content-title
      font-size font-size-default
    &__content-text
      font-size font-size-small
    &__content-main
      padding-bottom 10px
    &__content
      display flex
      flex-direction column
      &-tags
        padding 15px 0 0 0
        margin-left 0
      &-tag a
        font-size 11px
        padding 5px
    .comment-main
      &__time
        font-size 9px
      &__author
        margin-bottom 0
        font-size font-size-super-medium-small
      &__text
        margin-bottom 0
      &__review
        font-size font-size-super-upsmall
        margin-right auto
      &__reviews
        max-width calc(100% - 25px)
        margin-top 0

    &__actions
      margin 15px 0
    .edit
      top 10px !important
    .edit__icon
      width 10px
      svg
        width 15px
</style>
