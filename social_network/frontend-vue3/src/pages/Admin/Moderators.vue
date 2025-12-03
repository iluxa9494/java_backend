<!-- eslint-disable max-len -->
<template>
  <div class="admin-moderators inner-page admin">
    <h2 class="admin__title">Администраторы и модераторы</h2>
    <div class="admin__wrap">
      <div class="inner-page__main">
        <button @click="addModerator" class="admin-moderators__link">Добавить</button>
        <friends-block v-for="moderator in filteredModerators" :key="moderator.id" :moderator="moderator" />
      </div>
      <div class="inner-page__aside">
        <admin-sidebar v-model="filter" :list="list" @change-filter="onChangeFilter" />
      </div>
    </div>
  </div>
</template>

<script>
import { computed, reactive, ref } from 'vue';
import FriendsBlock from '@/components/Friends/Block.vue';
import AdminSidebar from './AdminPanel/Sidebar.vue';

export default {
  name: 'AdminModerators',
  components: { AdminSidebar, FriendsBlock },

  setup() {
    const filter = ref('all');
    const list = reactive([
      { text: 'Все', id: 'all' },
      { text: 'Модераторы', id: 'moderators' },
      { text: 'Администраторы', id: 'admins' },
    ]);
    const moderators = ref([]);

    const filteredModerators = computed(() => {
      if (filter.value === 'all') {
        return moderators;
      }
      return moderators.value.filter((moderator) => moderator.type === filter.value);
    });
    // Добавить модератора
    const addModerator = () => {};
    const onChangeFilter = (id) => {
      filter.value = id;
    };

    return {
      filter,
      list,
      filteredModerators,
      addModerator,
      onChangeFilter,
    };
  },
};
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl'

.admin-moderators
  .inner-page__main
    position relative

.admin-moderators__link
  font-size font-size-small
  color ui-cl-color-eucalypt
  position absolute
  right 15px
  top -40px
</style>
