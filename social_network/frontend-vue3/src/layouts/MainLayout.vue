<template>
  <div class="main-layout">
    <div class="wrapper">
      <main-layout-header />
      <main-layout-sidebar />
      <main class="main-layout__page">
        <router-view />
      </main>
    </div>
    <real-time-updater />
    <a href="#" class="scroll-to-top" v-if="showButton" @click.prevent="scrollToTop()">
      <scroll-button />
    </a>
  </div>
</template>

<script>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import MainLayoutHeader from '@/components/MainLayout/Header.vue';
import MainLayoutSidebar from '@/components/MainLayout/Sidebar.vue';
import RealTimeUpdater from '@/components/RealTimeUpdater.vue';
import ScrollButton from '@/Icons/ScrollIcon.vue';

export default {
  components: {
    MainLayoutHeader,
    MainLayoutSidebar,
    RealTimeUpdater,
    ScrollButton
  },

  setup() {
    const showButton = ref(false);

    const handleScroll = () => {
      showButton.value = window.scrollY > 85;
    };

    const scrollToTop = () => {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    onMounted(() => {
      window.addEventListener('scroll', handleScroll);
    });

    onBeforeUnmount(() => {
      window.removeEventListener('scroll', handleScroll);
    });

    return {
      showButton,
      scrollToTop,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.scroll-to-top
  position fixed
  bottom 20px
  right 30px
  background #fff
  border-radius border-half
  border 1px solid ui-cl-color-border-dark
  z-index 100

  svg
    stroke #000


.main-layout
  position relative
  display flex
  height 100%

.wrapper
  width 1280px
  padding 0 15px
  margin 0 auto

.main-layout__main
  width 100%
  margin-left sidebar-width
  height 100%

.main-layout__page
  display flex
  gap 15px
  align-items flex-start
  padding-top 170px
  min-height 100%
  position relative
  z-index 1

@media (min-width: 320px) and (max-width: 768px)
  .main-layout
    flex-direction column
    &__main
      margin-left 0
    &__sidebar
      display flex
      align-items center
      flex-direction row
      justify-content space-between
      position unset
      width 100%
      overflow unset
      padding 15px
    &__logo
      margin-bottom 0
    &__logotype
      flex-direction row-reverse
      background transparent
      padding 0
      z-index unset
      p
        font-size font-size-downdefault
    &__link
      margin-left 0
      margin-right 0
    &__header
      position unset
      left 0
    &__page
      padding  30px 15px
      .inner-page
        padding 0
    .recommend-block
      display none
</style>
