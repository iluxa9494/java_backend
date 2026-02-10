<template>
  <div class="success-register">
    <h4 class="success-register__title">{{ translationsLang.registerSucces }}</h4>
    <img src="@/assets/static/img/success-register.svg" alt="img.svg" />
  </div>
</template>

<script>
import useTranslations from '@/composables/useTranslations';

export default {
  name: 'RegisterSuccess',
  setup() {
    const { translationsLang } = useTranslations();

    return {
      translationsLang,
    };
  },
};
</script>

<style lang="stylus">
.success-register
  height 100%
  display flex
  flex-direction column
  justify-content center
  align-items center
  &__title
    color #fff
    font-weight 500
    margin-bottom 30px
    font-size 40px

  .simple-svg-wrapper
    width 130px
</style>
