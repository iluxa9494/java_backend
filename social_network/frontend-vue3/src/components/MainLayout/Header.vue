<template>
  <header
    class="main-layout__header"
    :class="{ admin: isAdminPage }"
    v-if="!isAdminPage"
  >
    <div class="wrapper wrapper__header">
      <template v-if="!isAdminPage">
        <div class="main-layout__header-left">
          <div class="main-layout__logotype">
            <p>NETWORK 51</p>
          </div>
          <form
            class="main-layout__search"
            action="#"
            v-if="getInfo && !getInfo.isDeleted"
            @submit.prevent="onSearch()"
          >
            <button class="main-layout__search-btn">
              <search-icon />
            </button>
            <input
              class="main-layout__search-input"
              type="text"
              :placeholder="translationsLang.searchPlaceholder"
              :value="searchText"
              @input="setSearchText($event.target.value)"
            />
          </form>
        </div>
        <div class="main-layout__header-right">
          <div class="main-layout__push" v-if="getInfo && !getInfo.isDeleted">
            <span class="indicator" v-if="getNotificationsLength">{{
              getNotificationsLength
            }}</span>
            <span @click="togglePush()">
              <push-icon
                :isNotEmpty="getNotificationsLength > 0 && !isOpenPush"
              />
            </span>
            <push :isOpen="isOpenPush" @close-push="togglePush" />
          </div>
          <div
            @click="toggleActionsProfile"
            class="main-layout__user"
            :class="{ 'active__profile-actions': showActionsProfile }"
            v-if="getInfo"
          >
            <div
              class="main-layout__user-pic header__pic"
              style="background-color: #8bc49e"
            >
              <img
                v-if="getInfo?.photo && getInfo && !getInfo.isDeleted"
                :src="getInfo?.photo"
                :alt="getInfo?.firstName[0] + ' ' + getInfo.lastName[0]"
              />
              <div v-else>
                <unknow-user />
              </div>
            </div>
            <arrow-bottom />
          </div>
          <transition name="fade">
            <div
              v-show="showActionsProfile"
              v-click-outside="closeActionsProfile"
              class="main-layout__actions-profile"
            >
              <router-link
                :to="{ name: 'Profile' }"
                v-if="getInfo && !getInfo.isDeleted"
                class="main-layout-profile__actions"
              >
                <div
                  class="main-layout__user-pic header__pic"
                  style="background-color: #8bc49e"
                >
                  <img
                    v-if="getInfo?.photo"
                    :src="getInfo?.photo"
                    :alt="getInfo?.firstName[0] + ' ' + getInfo?.lastName[0]"
                  />
                  <div v-else>
                    <unknow-user />
                  </div>
                </div>
                <span
                  v-if="getInfo && !getInfo.isDeleted"
                  class="main-layout__user-name"
                  >{{ getInfo?.fullName }}</span
                >
                <span class="main-layout__user-post" v-if="isAdminPage"
                  >- администратор</span
                >
              </router-link>
              <ul class="main-layout__actions-profile-list">
                <li
                  v-if="getInfo && !getInfo.isDeleted"
                  class="main-layout__actions-profile-item"
                >
                  <div class="simple-svg-wrapper">
                    <sidebar-icons :name="'settings'" />
                  </div>
                  <router-link v-if="!isAdminPage" :to="{ name: 'Settings' }">{{
                    translationsLang.sidebarSetting
                  }}</router-link>
                </li>
                <li class="main-layout__actions-profile-item">
                  <language-icon />
                  <language-block
                    :show-language="false"
                    :no-use-content="false"
                  />
                </li>
                <li class="main-layout__actions-profile-item">
                  <theme-icon />
                  <change-theme />
                </li>
                <li class="main-layout__actions-profile-item">
                  <div class="simple-svg-wrapper">
                    <sidebar-icons :name="'exit'" />
                  </div>
                  <a href="#" @click.prevent="onLogout()">
                    {{ translationsLang.logout }}
                  </a>
                </li>
              </ul>
            </div>
          </transition>
        </div>
      </template>
    </div>
  </header>
</template>

<script>
import { computed, onMounted, ref, getCurrentInstance } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import useTranslations from "@/composables/useTranslations";
import vClickOutside from "click-outside-vue3";
import Push from "@/components/MainLayout/Push";
import ChangeTheme from "@/components/Theme/ChangeTheme.vue";
import LanguageBlock from "@/components/FormLayout/Footer.vue";
import SearchIcon from "@/Icons/SearchIcon.vue";
import PushIcon from "@/Icons/PushIcon.vue";
import ThemeIcon from "@/Icons/ThemeIcon.vue";
import UnknowUser from "@/Icons/UnknowUser.vue";
import ArrowBottom from "@/Icons/ArrowBottom.vue";
import SidebarIcons from "@/Icons/sidebar/SidebarIcons.vue";
import LanguageIcon from "@/Icons/LanguageIcon.vue";

export default {
  name: "MainLayoutHeader",
  directives: {
    clickOutside: vClickOutside.directive,
  },
  components: {
    Push,
    SearchIcon,
    PushIcon,
    ChangeTheme,
    LanguageBlock,
    UnknowUser,
    ArrowBottom,
    SidebarIcons,
    LanguageIcon,
    ThemeIcon,
  },

  setup() {
    const { getters, state, dispatch, commit } = useStore();
    const route = useRoute();
    const router = useRouter();
    const isOpenPush = ref(false);
    const isOpenSearch = ref(false);
    const showActionsProfile = ref(false);
    const instance = getCurrentInstance();
    const $socket = instance.appContext.config.globalProperties.$socket;
    const elementsRef = ref(null);
    const { translationsLang } = useTranslations();

    const getInfo = computed(() => getters["profile/info/getInfo"]);
    const searchText = computed(() => getters["global/search/searchText"]);
    const getNotificationsLength = computed(
      () => getters["profile/notifications/getNotificationsLength"]
    );
    const dialogs = computed(() => state.profile.dialogs.dialogs);
    const newMessage = computed(() => state.profile.dialogs.newMessage);

    const isAdminPage = computed(() => route.path.indexOf("admin") !== -1);
    const activeDialogId = computed(() => route.params.activeDialogId);

    // watch(dialogs, (newVal) => {
    //   let unreadCount = 0;
    //   newVal.forEach((item) => {
    //     unreadCount += item.unreadCount;
    //   });
    //   commit('profile/dialogs/setUnreadedMessages', unreadCount);
    // });

    onMounted(async () => {
      // if (route.name !== "Im" && route.name !== "ImChat") {
      //   dispatch("profile/dialogs/fetchDialogs");
      // }

      dispatch("global/geo/apiGeo");
      if (!getInfo.value) {
        await dispatch("profile/info/apiInfo");
      }
      dispatch("loadUser", getInfo.value.id);
      await $socket.connect();
      $socket.subscribe("socket event", (messagePayload) => {
        if (messagePayload.type === "NOTIFICATION") {
          messagePayload.data.sentTime = messagePayload.data.timestamp;
          commit("profile/notifications/addNotification", messagePayload.data);
          if (route.name !== "Push") {
            commit(
              "profile/notifications/addNotificationsLength",
              messagePayload.data
            );
          }
        }

        if (messagePayload.type === "MESSAGE") {
          if (
            route.name === "ImChat" &&
            activeDialogId.value === messagePayload.data.authorId
          ) {
            commit("profile/dialogs/setNewMessage", messagePayload.data);
          } else {
            const result = [];
            dialogs.value.forEach((d) => {
              let dialod = Object.assign({}, d);
              if (
                dialod.id !== activeDialogId.value &&
                dialod.id === messagePayload.data.authorId
              ) {
                dialod.unread_count += 1;
              }
              result.push(dialod);
            });
            commit("profile/dialogs/setDialogs", result);
          }
        }
      });
    });

    const onLogout = () => {
      dispatch("auth/api/logout").finally(() => {
        router.push("/login");
      });
    };

    const toggleActionsProfile = () => {
      showActionsProfile.value = !showActionsProfile.value;
    };

    const closeActionsProfile = (e) => {
      if (elementsRef.value && elementsRef.value.contains(e.target)) {
        showActionsProfile.value = false;
      }
    };

    const onSearch = () => {
      dispatch("global/search/searchAll", searchText.value).then(() => {
        router.push({ name: "Search", query: { text: searchText.value } });
      });
    };

    const togglePush = () => {
      isOpenPush.value = !isOpenPush.value;
    };

    const setSearchText = (event) => {
      return commit("global/search/setSearchText", event);
    };

    return {
      isOpenPush,
      isOpenSearch,
      showActionsProfile,
      translationsLang,
      getInfo,
      searchText,
      getNotificationsLength,
      dialogs,
      newMessage,
      isAdminPage,
      activeDialogId,
      onLogout,
      toggleActionsProfile,
      closeActionsProfile,
      onSearch,
      togglePush,
      setSearchText,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.vt-notification-container {
  display none !important
}

.main-layout__logotype {
  display: inline-flex;
  align-items: center;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
  pointer-events: none;
  gap: 10px;
  width: 100%;
  max-width: unset;
}

.main-layout__logotype p {
  font-size: 25px;
  font-weight: 500;
  text-transform: uppercase
}

.main-layout-profile__actions
  display flex
  padding 10px
  align-items center
  font-size font-size-small-medium
  font-weight font-weight-medium
  background ui-cl-color-white-bright
  border-radius border-super-small
  transition all .2s ease-in-out
  margin-bottom 10px
  gap 10px
  @media (any-hover: hover)
    &:hover
      background ui-cl-color-white-bright-second


.active__profile-actions
  background-color #ffffff2b

.header__pic
  margin-right 0 !important

.main-layout__actions-profile-list
  display flex
  flex-direction column
  gap 5px

.main-layout__actions-profile-item
  display flex
  align-items center
  gap 10px
  white-space nowrap
  cursor pointer
  transition all .2s ease-in-out
  padding 8px
  border-radius border-super-small
  &:nth-child(2),
    cursor default
  &:not(:nth-child(2))
    @media (any-hover: hover)
      &:hover
        background ui-cl-color-white-lilac
    a
      display block
      width 100%
  &:nth-child(3),
    cursor default
  &:not(:nth-child(3))
    @media (any-hover: hover)
      &:hover
        background ui-cl-color-white-lilac
    a
      display block
      width 100%
  .form-layout__footer
    color ui-cl-color-full-black
    font-size font-size-downdefault
  .form-layout__footer-language .active:hover
    color ui-cl-color-eucalypt
  .form-layout__footer-language
    margin-right 0
    .active
      &:hover
        color ui-cl-color-full-black
  .form-layout__footer-language span:nth-child(2)
    background-color ui-cl-color-white-bright
    padding 5px
    font-size font-size-small
    font-weight font-weight-regular
    svg path
      fill ui-cl-color-full-black
  .form-layout-list__language
    background-color ui-cl-color-white-bright
    border-radius unset
    font-size font-size-small
    padding 0
    top 24px
    right -44px
    gap 0
    &::before
      display none
  .form-layout-list__language-item
    color ui-cl-color-full-black
    padding 8px
    border-radius 0
    font-weight font-weight-regular
    .active__lang
      background-color #cdcdcd
    span:nth-child(2)
      padding 0 3px
    @media (any-hover: hover)
      &:hover
        background-color #ebeaea
  .form-layout-list__language-item.active__lang span:nth-child(2)
    color ui-cl-color-full-black
  .form-layout-list__language-item:hover span:nth-child(2)
    color ui-cl-color-full-black
  .form-layout-list__language-item.active__lang
    background-color #ebeaea


.main-layout__actions-profile
  position absolute
  top 57px
  color ui-cl-color-full-black
  font-size font-size-downdefault
  border-radius border-small 0 border-small border-small
  right 0
  padding 10px
  max-width 300px
  min-width 300px
  width 100%
  height auto
  background ui-cl-color-white-theme
  box-shadow box-shadow-main
  &.fade-enter-active,
  &.fade-leave-active
    transition all .2s ease-in-out
  &.fade-enter,
  &.fade-leave-to
    opacity 0

.wrapper__header
  display flex
  align-items center
  justify-content space-between
  width 1280px
  padding 0 15px
  margin 0 auto

.main-layout__header-left,
.main-layout__header-right
  display flex
  align-items center
  gap 15px

.main-layout__header-right
  position relative


.main-layout__header
  background ui-cl-color-eucalypt
  box-shadow box-shadow-main
  height header-height
  position absolute
  top 0
  left 0
  right 0
  display flex
  align-items center
  z-index 8
  color ui-cl-color-white-theme

  &.admin
    background ui-cl-color-white-theme
    color ui-cl-color-steel-gray
    justify-content flex-end

.main-layout__search
  position relative
  display flex
  align-items center
  width 100%
  margin-right auto

.main-layout__search-btn
  position absolute
  left 10px
  top 20%
  margin-right 10px
  background-color transparent
  color ui-cl-color-white-theme
  outline none

  & svg
    transition transform 200ms linear

  &:hover svg
    transform scale(1.2)

.main-layout__search-input
  font-size font-size-downdefault
  width 100%
  background #edeef026
  padding 10px 10px 10px 40px
  border-radius border-small
  color ui-cl-color-white-theme
  transition all 0.2s

  &::placeholder
    color ui-cl-color-white-theme

  &:focus
    background ui-cl-color-full-black29

.indicator
  content ''
  font-weight 400
  font-size 8px
  width 15px
  height 15px
  color #fff
  background-color #e65151
  border-radius 50%
  display flex
  align-items center
  justify-content: center
  position absolute
  right -3px
  top 3px
  transform translateY(-50%)
  z-index 10


.main-layout__push
  display flex
  align-items center
  justify-content center
  cursor pointer
  position relative

  svg
    transition all .2s ease-in-out

    &[data-push]:after
      content attr(data-push)
      font-style normal
      font-weight font-weight-bold
      font-size font-size-super-upsmall
      line-height 15px
      width 16px
      height 16px
      background-color #F9555F
      border-radius border-half
      display flex
      align-items center
      justify-content center
      position relative
      top -25px
      left 7px
      padding-right 1px

  &:hover svg
    opacity 0.7

.main-layout__user
  display flex
  cursor pointer
  padding 5px
  border-radius border-super-small
  gap 5px
  align-items center
  font-weight font-weight-bold
  font-size font-size-downdefault
  color ui-cl-color-white-theme
  transition all .2s ease-in-out
  svg
    width 24px
    height 24px
  @media (any-hover: hover)
    &:hover
      background-color #ffffff2b


.main-layout__user-pic
  width 50px
  height 50px
  border-radius border-half
  overflow hidden
  margin-right 15px
  flex none
  background-color #8bc49e

  div
    display flex
    align-items center
    justify-content center
    width 100%
    height 100%
    object-fit cover

  img
    display flex
    align-items center
    justify-content center
    width 100%
    height 100%
    object-fit cover

.main-layout__user-post
  margin-left 5px

@media (min-width: 320px) and (max-width: 768px)
  .main-layout
    &__header
      display: grid
      grid-template-areas "search push theme avatar"
      grid-template-columns 30px 20px 1fr 120px
      padding: 15px
    &__search
      width unset
      max-width unset
      margin-right unset
      grid-area search
    &__search-input
      display none
    &__push
      display inline-block
      margin-right unset
      height unset
      grid-area push
    &__user
      grid-area avatar
      gap 10px
    &__user-pic
      margin-right 0
    &__user-name
      font-size font-size-small
      font-weight font-weight-regular
    .theme-switch
      grid-area theme
      margin-right 10px
      width 46px
      margin-left auto
</style>
