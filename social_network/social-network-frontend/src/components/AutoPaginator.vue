<template>
  <div ref="observerRef" />
</template>

<script>
import { onMounted, ref } from "vue";

export default {
  props: {
    loading: {
      type: Boolean,
      required: true,
    },
    page: {
      type: Number,
      default: 0,
    },
    totalPage: {
      type: Number,
      default: 200,
    },
    action: {
      type: Function,
      required: true,
    },
  },

  setup(props) {
    const pageCount = ref(0);
    const observerRef = ref(null);

    onMounted(() => {
      pageCount.value = props.page;

      const loadingObserver = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting && !props.loading) {
            if (props.page >= props.totalPage) return;

            if (pageCount.value === 0) {
              props.action({ page: props.page - 1 || 0 });
              pageCount.value += 1;
            } else {
              props.action({ page: props.page || 0 });
              pageCount.value += 1;
            }
          }
        });
      });

      loadingObserver.observe(observerRef.value);
    });

    return {
      observerRef,
    };
  },
};
</script>
