<template>
  <div class="admin-panel">
    <h2 class="admin__title">Люди ({{ countUsers }})</h2>
    <div class="admin-panel__content">
      <div class="admin-panel__left">
        <search-input v-on:input-change="handleInputChange" />
        <div class="admin-panel__result">
          <div class="admin-panel__result-block" v-if="filter === 'active'">
            <friends-block admin />
          </div>
          <div class="admin-panel__result-block" v-else-if="filter === 'blocked'">
            <friends-block admin blocked />
          </div>
        </div>
      </div>
      <div>
        <admin-sidebar v-model="filter" @change-filter="onChangeFilter" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
// import FriendsBlock from '@/components/Friends/Block.vue';
// import SearchInput from '@/pages/Admin/SearchInput.vue';
// import AdminSidebar from './Sidebar.vue';

export default {
  name: 'UsersPanel',
  // components: { SearchInput, FriendsBlock, AdminSidebar },

  setup() {
    const filter = ref('active');
    const inputValue = ref('');
    const countUsers = ref(0);

    const onChangeFilter = (id) => {
      filter.value = id;
    };

    const handleInputChange = (value) => {
      inputValue.value = value;
    };

    return {
      filter,
      inputValue,
      countUsers,
      onChangeFilter,
      handleInputChange,
    };
  },
};
</script>
