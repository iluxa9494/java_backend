<template>
  <div class="settings inner-page">
    <h1 class="settings__title">
      {{ currentTranslations === 'Русский' ? activeComponent.text : activeComponent.textEng }}
    </h1>
    <div class="settings__wrap">
      <div class="inner-page__main">
        <component :is="activeComponent.component" />
      </div>
      <div class="inner-page__aside">
        <settings-sidebar v-model="activeComponent.component" @change-component="onChange" />
      </div>
    </div>
  </div>
</template>

<script>
import { computed, reactive, defineAsyncComponent } from 'vue';
import { useStore } from 'vuex';

export default {
  name: 'UserSettings',
  components: {
    SettingsMain: defineAsyncComponent(() => import('@/components/Settings/Main.vue')),
    SettingsPush: defineAsyncComponent(() => import('@/components/Settings/Push.vue')),
    SettingsSecurity: defineAsyncComponent(() => import('@/components/Settings/Security.vue')),
    SettingsDelete: defineAsyncComponent(() => import('@/components/Settings/Delete.vue')),
    SettingsSidebar: defineAsyncComponent(() => import('@/components/Settings/Sidebar.vue')),
  },

  setup() {
    const store = useStore();
    const activeComponent = reactive({ component: 'settings-main', text: 'Основные', textEng: 'Main' });

    const currentTranslations = computed(() => store.state.auth.languages.language.name);

    const onChange = (item) => {
      Object.assign(activeComponent, item);
    };

    return {
      activeComponent,
      currentTranslations,
      onChange,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.settings
  display block

  @media (max-width breakpoint-xl)
    .inner-page__aside
      display block
      max-width 100%

    .inner-page__main
      margin-right 0

.settings__title
  font-family font-exo
  font-weight 200
  font-size 30px
  margin-bottom 20px
  color #000

  @media (max-width breakpoint-xl)
    display none

.settings__wrap
  display grid
  gap 20px
  grid-template-columns 1fr 430px

  @media (max-width breakpoint-xl)
    flex-direction column-reverse
</style>
