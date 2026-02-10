import {
  defineComponent,
  ref,
  computed,
} from 'vue';

export default defineComponent({
  name: 'ButtonHover',

  props: {
    tag: {
      type: String,
      default: 'a',
    },
    variant: {
      type: String,
      default: 'green',
    },
    bordered: {
      type: Boolean,
    },
    to: {
      type: Object,
    },
    disable: {
      type: Boolean,
    },
  },

  setup(props) {
    const x = ref(null);
    const y = ref(null);
    const isAnimated = ref(true);

    const classObject = computed(() => ({
      'btn--white': props.variant === 'white',
      'btn--dark': props.variant === 'dark',
      'btn--red': props.variant === 'red',
      'btn--warning': props.variant === 'warning',
      'btn--fill': props.variant === 'fill',
      'btn--bordered': props.bordered,
      'btn--disable': props.disable,
    }));

    const btnHandler = (e) => {
      if (!isAnimated.value) return;
      isAnimated.value = false;
      x.value = `${e.offsetX}px`;
      y.value = `${e.offsetY}px`;
    };
    const btnMouseLeave = (e) => {
      x.value = `${e.offsetX}px`;
      y.value = `${e.offsetY}px`;
      isAnimated.value = true;
      setTimeout(() => {
        x.value = 0;
        y.value = 0;
      }, 200);
    };

    return {
      x,
      y,
      isAnimated,
      classObject,
      btnHandler,
      btnMouseLeave,
    };
  },
  template: `<router-link :to="to" class="btn" :class="classObject" @mousemove="btnHandler($event)" @mouseleave="btnMouseLeave($event)" v-if="to">
    <span class="helper" :style="{left: x, top: y}"></span>
    <slot></slot>
  </router-link>
  <component :is="tag" class="btn" :class="classObject" @mousemove="btnHandler($event)" @mouseleave="btnMouseLeave($event)" v-else>
    <span class="helper" :style="{left: x, top: y}"></span>
    <slot></slot>
  </component>`,
});
