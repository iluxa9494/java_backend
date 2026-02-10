<template>
  <div class="page-push inner-page">
    <div class="inner-page__main" v-if="getNotifications.length > 0">
      <push-block
        v-for="info in filterNotifications"
        :key="info.id"
        :info="info"
      />
    </div>

    <div class="inner-page__aside">
      <push-sidebar
        v-model="activeFilter"
        @change-push-sidebar="onChangeFilter"
      />
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import PushSidebar from '@/components/Push/Sidebar';
import PushBlock from '@/components/Push/Block';
export default {
  name: "PagePush",
  components: { PushSidebar, PushBlock },

  setup() {
    const store = useStore();
    const activeFilter = ref("Все");

    const getNotifications = computed(
      () => store.getters["profile/notifications/getNotifications"]
    );

    const filterNotifications = computed(() => {
      switch (activeFilter.value) {
        case "Все":
          return getNotifications;
        case "Комментарии":
          return getNotifications.value.filter(
            (el) =>
              el.notification_type === "POST_COMMENT" ||
              el.notification_type === "COMMENT_COMMENT"
          );
        case "Друзья":
          return getNotifications.value.filter(
            (el) => el.notification_type === "FRIEND_REQUEST"
          );
        default:
          return getNotifications;
      }
    });

    onMounted(() => {
      store.dispatch("profile/notifications/fetchNotifications");
    });

    const onChangeFilter = (item) => {
      activeFilter.value = item;
    };

    return {
      activeFilter,
      getNotifications,
      filterNotifications,
      onChangeFilter,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.page-push
  flex-direction column

  .inner-page__aside
    display block
    max-width 100%
    order -1

    .aside-filter__item
      text-align center

  .inner-page__main
    margin-right 0
    position fixed
    top 230px
    max-height 75vh
    overflow-y scroll
</style>
