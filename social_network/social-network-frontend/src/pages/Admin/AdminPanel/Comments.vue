<template>
  <div class="admin-panel">
    <h2 class="admin__title">Комментарии ({{ countComments }})</h2>
    <div class="panel-publications admin-panel__content">
      <div class="admin-panel__left">
        <search-input v-on:input-change="handleInputChange" />
        <div class="admin-panel__result">
          <div class="admin-panel__result-block" v-if="filter === 'all' || filter === 'active'">
            <comments-block admin />
          </div>
          <div class="admin-panel__result-block" v-if="filter === 'blocked'">
            <comments-block admin blocked />
          </div>
        </div>
      </div>
      <admin-sidebar v-model="filter" @change-filter="onChangeFilter" />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
// import CommentsBlock from '@/components/Comments/Block.vue';
// import SearchInput from '@/pages/Admin/SearchInput.vue';
// import AdminSidebar from './Sidebar.vue';

export default {
  name: 'CommentsPanel',
  // components: { SearchInput, AdminSidebar, CommentsBlock },

  setup() {
    const filter = ref('all');
    const inputValue = ref('');
    const countComments = ref(0);

    const onChangeFilter = (id) => {
      filter.value = id;
    };

    const handleInputChange = (value) => {
      inputValue.value = value;
    };

    return {
      filter,
      inputValue,
      countComments,
      onChangeFilter,
      handleInputChange,
    };
  },
};
</script>
