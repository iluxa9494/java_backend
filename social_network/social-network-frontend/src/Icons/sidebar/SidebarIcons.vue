<template>
  <exit-icon v-if="name === 'exit'" />
  <profile-icon v-else-if="name === 'profile'" />
  <settings-icon v-else-if="name === 'settings'" />
  <friends-icon v-else-if="name === 'friends'" />
  <im-icon v-else-if="name === 'im'" />
  <news-icon v-else-if="name === 'news'" />
  <comments-icon v-else-if="name === 'comments'" />
  <moderators-icon v-else-if="name === 'moderators'" />
  <posts-icon v-else-if="name === 'posts'" />
  <statistics-icon v-else-if="name === 'statistics'" />
  <users-icon v-else-if="name === 'users'" />
  <span v-else />
</template>

<script>
import { defineAsyncComponent } from 'vue';

export default {
  components: {
    ExitIcon: defineAsyncComponent(() => import('./ExitIcon.vue')),
    ProfileIcon: defineAsyncComponent(() => import('./ProfileIcon.vue')),
    SettingsIcon: defineAsyncComponent(() => import('./SettingsIcon.vue')),
    FriendsIcon: defineAsyncComponent(() => import('./FriendsIcon.vue')),
    ImIcon: defineAsyncComponent(() => import('./ImIcon.vue')),
    NewsIcon: defineAsyncComponent(() => import('./NewsIcon.vue')),
    CommentsIcon: defineAsyncComponent(() => import('./CommentsIcon.vue')),
    ModeratorsIcon: defineAsyncComponent(() => import('./ModeratorsIcon.vue')),
    PostsIcon: defineAsyncComponent(() => import('./PostsIcon.vue')),
    StatisticsIcon: defineAsyncComponent(() => import('./StatisticsIcon.vue')),
    UsersIcon: defineAsyncComponent(() => import('./UsersIcon.vue')),
  },
  props: {
    name: {
      type: String,
      required: true,
      validator(value) {
        return [
          'exit', 'profile', 'settings', 'friends', 'im', 'news', 'comments',
          'moderators', 'posts', 'statistics', 'users',
        ].includes(value);
      },
    },
  },

};
</script>
