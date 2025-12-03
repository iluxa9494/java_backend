<template>
  <div class="error">
    <span class="error__title">Ошибка: </span>
    <span class="error__message">
      {{ message }}
    </span>
  </div>
</template>

<script>
export default {
  props: {
    message: {
      type: String,
      default: '',
    },
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';
.error
  font-size 20px
  &__title
    font-weight font-weight-bold
  &__message
    color ui-cl-color-wild-watermelon
</style>
