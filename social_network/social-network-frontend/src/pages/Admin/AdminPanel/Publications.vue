<template>
  <div class="admin-panel">
    <h2 class="admin__title">Посты ({{ countPosts }})</h2>
    <div class="panel-publications admin-panel__content">
      <div class="admin-panel__left">
        <search-input v-on:input-change="handleInputChange" />
        <div class="admin-panel__result">
          <div class="admin-panel__result-block" v-if=" filter === 'all' || filter === 'active'">
            <news-block admin />
          </div>
          <div class="admin-panel__result-block" v-if="filter === 'blocked'">
            <news-block admin blocked />
          </div>
        </div>
      </div>
      <admin-sidebar v-model="filter" @change-filter="onChangeFilter" />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import NewsBlock from '@/components/News/Block.vue';
import SearchInput from '@/pages/Admin/SearchInput.vue';
import AdminSidebar from './Sidebar.vue';

export default {
  name: 'PublicationPanel',
  components: { SearchInput, AdminSidebar, NewsBlock },

  setup() {
    const filter = ref('all');
    const inputValue = ref('');
    const countPosts = ref(0);

    const onChangeFilter = (id) => {
      filter.value = id;
    };

    const handleInputChange = (value) => {
      inputValue.value = value;
    };

    return {
      filter,
      inputValue,
      countPosts,
      onChangeFilter,
      handleInputChange,
    };
  },
};
</script>
