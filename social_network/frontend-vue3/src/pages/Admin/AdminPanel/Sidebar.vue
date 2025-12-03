<!-- eslint-disable max-len -->
<template>
  <div class="admin-sidebar">
    <nav class="admin-sidebar__nav">
      <ul class="admin-sidebar__list">
      <li class="admin-sidebar__item" v-for="item in list" :key="item.id" :class="{ active: item.id === value }">
        <button class="admin-sidebar__item-button" @click="changeFilter(item.id)">{{ item.text }}</button>
      </li>
    </ul>
    </nav>
  </div>
</template>

<script>
export default {
  name: 'AdminSidebar',
  props: {
    value: String,
    list: {
      type: Array,
      default: () => [
        { text: 'Все', id: 'all' },
        { text: 'Активные', id: 'active' },
        { text: 'Заблокированные', id: 'blocked' },
      ],
    },
  },

  setup(_, { emit }) {
    const changeFilter = (id) => {
      emit('change-filter', id);
    };

    return {
      changeFilter,
    };
  },
};
</script>

<style lang="stylus">
@import '../../../assets/stylus/base/vars.styl'

.admin-sidebar
  background #fff
  box-shadow 0px 2px 8px rgba(0, 0, 0, 0.08)
  border-radius border-small
  overflow hidden

  @media (max-width breakpoint-xl)
    background transparent
    box-shadow none
    padding 0

.admin-sidebar__list
  @media (max-width breakpoint-xl)
    display flex

.admin-sidebar__item
  color ui-cl-color-gun-powder
  padding 20px 25px
  cursor pointer
  transition all .2s ease-in-out

  &+&
    border-top 1px solid ui-cl-color-e6e6e6

  &:hover
    background ui-cl-color-gun-powder
    color ui-cl-color-white-theme

  &.active
    cursor default
    color ui-cl-color-white-theme
    background ui-cl-color-gun-powder

  @media (max-width breakpoint-xl)
    padding 0
    color gray
    display block
    padding 20px
    flex auto

    &+&
      border-top none
</style>
