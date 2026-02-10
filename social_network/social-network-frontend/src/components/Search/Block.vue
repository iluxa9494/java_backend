<template>
  <div class="search-block">
    <div class="search-block__header">
      <h2 class="search-block__title">{{ title }}</h2>

      <a class="search-block__more" href="#" @click.prevent="changeTab(id)" v-if="all">
        Смотреть всех
      </a>
    </div>

    <div class="search-block__list">
      <slot />
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex';

export default {
  name: 'SearchBlock',
  props: {
    title: {
      type: String,
      required: true,
    },
    id: {
      type: String,
      required: true,
    },
    all: Boolean,
  },

  setup() {
    const store = useStore();

    const changeTab = (id) => {
      store.dispatch('global/search/changeTab', id);
    };

    return {
      changeTab,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.search-block
  width 100%

  &+&
    margin-top 70px

.search-block__header
  display flex
  align-items center
  justify-content space-between
  margin-bottom 25px

.search-block__title
  font-family font-exo
  font-weight font-weight-light
  font-size 30px
  color ui-cl-color-full-black

.search-block__more
  font-size font-size-small
  text-align right
  color ui-cl-color-eucalypt
</style>
