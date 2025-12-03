<template>
  <transition name="fade">
    <div
      class="modal"
      v-show="modelValue"
      tabindex="-1"
      @click.self="close"
      @keyup.esc="close"
      ref="modalRef"
    >
      <div class="modal__wrapper">
        <div class="modal__body">
          <slot />
        </div>

        <div class="modal__actions">
          <slot name="actions" />
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
import { ref, computed, onMounted, watch } from "vue";

export default {
  name: "ComponentsModal",
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
  },

  setup(props, { emit }) {
    const DELAY_EFFECT = ref(0);
    const modalRef = ref(null);
    const isBodyOverflowing = ref(null);
    const value = ref(props.value);

    const scrollBarWidth = computed(
      () => window.innerWidth - document.documentElement.clientWidth
    );

    watch(value, () => {
      setTimeout(() => {
        emit(props.value ? "onOpen" : "onClose");
      }, DELAY_EFFECT.value);
      if (props.value) {
        setScrollPadding();
        document.body.classList.add("overflow-hidden");
      } else {
        setTimeout(() => {
          removeScrollPadding();
          document.body.classList.remove("overflow-hidden");
        }, DELAY_EFFECT.value);
      }
    });

    onMounted(() => {
      DELAY_EFFECT.value = 300;
    });

    const close = () => {
      emit('update:modelValue', false);
    };

    const checkBodyOverflowing = () => {
      const rect = document.body.getBoundingClientRect();
      return rect.left + rect.right < window.innerWidth;
    };

    const setScrollPadding = () => {
      isBodyOverflowing.value = checkBodyOverflowing();
      if (isBodyOverflowing.value) {
        document.body.style.paddingRight = `${scrollBarWidth.value}px`;
      }
    };

    const removeScrollPadding = () => {
      document.body.style.paddingRight = 0;
    };

    return {
      modalRef,
      close,
    };
  },
};
</script>
