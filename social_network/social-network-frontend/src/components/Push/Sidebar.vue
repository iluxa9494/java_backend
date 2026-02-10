<template>
  <div class="aside-filter-notes">
    <h2 class="aside-filter-notes__title">Оповещения</h2>

    <ul class="aside-filter-notes__list">
      <li
        class="aside-filter-notes__item"
        v-for="item in list"
        :key="item"
        :class="{ active: item === value }"
        @click="changePushSidebar(item)"
      >
        {{ item }}
      </li>
    </ul>
  </div>
</template>

<script>
import { reactive } from "vue";

export default {
  name: "PushSidebar",
  props: {
    value: String,
  },

  setup({ emit }) {
    const list = reactive(["Все", "Комментарии", "Друзья"]);

    const changePushSidebar = (item) => {
      emit("update:change-push-sidebar", item);
    };

    return {
      list,
      changePushSidebar,
    };
  },
};
</script>
