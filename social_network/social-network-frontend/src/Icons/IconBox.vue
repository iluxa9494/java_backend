<template>
  <svg xmlns="http://www.w3.org/2000/svg" :width="width" :height="height" :viewBox="viewBox" :x="x" :y="y"
    role="presentation" :fill="fill" :stroke="stroke">
    <slot />
  </svg>
</template>

<script>
export default {
  props: {
    width: {
      type: [Number, String],
      default: 18,
    },
    height: {
      type: [Number, String],
      default: 18,
    },
    fill: {
      type: String,
      default: 'currentColor',
    },
    stroke: {
      type: String,
      default: 'none',
    },
    viewBox: {
      type: String,
      default: '0 0 18 18',
    },
    x: {
      type: [Number, String],
    },
    y: {
      type: [Number, String],
    },
  },
};
</script>
