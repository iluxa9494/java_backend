<template>
  <div class="form-layout__main">
    <div class="form-layout__logo">
      <p>NETWORK 58</p>
    </div>
    <h1 class="form-layout__title">{{ translationsInfo.title }}</h1>
    <p class="form-layout__text">{{ translationsInfo.text }}</p>
    <p class="form-layout__descr" v-if="info.descr">{{ translationsInfo.descr }}</p>

    <router-link v-if="info.btn" :to="buttonLink">
      <button class="form-layout__btn">{{ translationsInfo.btn.text }}</button>
    </router-link>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import useTranslations from '@/composables/useTranslations';

export default {
  name: 'FormLayoutInfo',
  setup() {
    const store = useStore();
    const route = useRoute();
    const language = ref('');
    const isOpenLanguageBlock = ref(false);
    const { translationsInfo } = useTranslations();

    const info = computed(() => store.getters['auth/info/getInfoById'](route.path.split('/')[1]));

    const buttonLink = computed(() => {
      if (info.value.btn && info.value.btn.href) {
        return info.value.btn.href;
      }
      return { name: info.value.btn.link };
    });

    return {
      info,
      buttonLink,
      language,
      isOpenLanguageBlock,
      translationsInfo,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.form-layout__main
  margin auto 0
  margin-bottom 30px

.form-layout__logo
  position absolute
  top 30px
  left 30px
  z-index 1000
  display inline-flex
  border-radius border-small
  user-select none
  pointer-events none
  gap 10px
  max-width unset
  p
    color ui-cl-color-white-theme
    font-size 25px
    font-weight font-weight-light
    text-transform uppercase

.form-layout__title
  color ui-cl-color-white-theme
  font-style normal
  font-weight font-weight-light
  font-size 40px
  line-height 53px
  letter-spacing -0.01em
  margin-bottom 20px

.form-layout__text
  font-size font-size-updefault
  line-height 30px
  letter-spacing 0.01em
  color ui-cl-color-jumbo
  margin-bottom 20px

.form-layout__descr
  font-size font-size-downdefault
  letter-spacing 0.01em
  color ui-cl-color-scarpa-flow
  margin-bottom 15px
  margin-top 30px
</style>
