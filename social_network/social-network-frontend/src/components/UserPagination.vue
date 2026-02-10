<template>
  <ul class="catalog__pagination pagination">
    <li class="pagination__item">
      <button
        class="pagination__link pagination__link--arrow"
        :class="{ 'pagination__link--disabled': page === prevPage }"
        @click.prevent="paginate(prevPage)"
        aria-label="Предыдущая страница"
      >
        <svg width="8" height="14" fill="black">
          <use xlink:href="#icon-arrow-left" />
        </svg>
      </button>
    </li>

    <li class="pagination__item" v-for="pageNumber in pages" :key="pageNumber">
      <a
        href="#"
        class="pagination__link"
        :class="{
          'pagination__link--current': pageNumber === page,
          'pagination__link--disabled': pageNumber === page,
        }"
        @click.prevent="paginate(pageNumber)"
      >
        {{ pageNumber }}
      </a>
    </li>

    <li class="pagination__item">
      <button
        class="pagination__link pagination__link--arrow"
        :class="{ 'pagination__link--disabled': page === nextPage }"
        @click.prevent="paginate(nextPage)"
        aria-label="Следующая страница"
      >
        <svg width="8" height="14" fill="currentColor">
          <use xlink:href="#icon-arrow-right" />
        </svg>
      </button>
    </li>
  </ul>
</template>

<script>
import { computed } from "vue";

export default {
  props: ["page", "count", "perPage"],

  setup(props, { emit }) {
    const pages = computed(() => Math.ceil(props.count / props.perPage));
    const prevPage = computed(() => {
      if (props.page === 1) {
        return props.page;
      }
      return props.page - 1;
    });
    const nextPage = computed(() => {
      if (props.page >= pages.value) {
        return props.page;
      }
      return props.page + 1;
    });

    const paginate = (selectPage) => {
      emit("update:page", selectPage);
    };

    return {
      pages,
      prevPage,
      nextPage,
      paginate,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.catalog__pagination
  margin-top 30px

.pagination__link--arrow
  border-radius border-small

.pagination__link
  padding: 10px 3px
</style>
