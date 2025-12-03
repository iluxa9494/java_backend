<template>
  <div class="form-layout">
    <div class="wrapper form-layout__container">
      <div class="form-layout_right">
        <form-layout-info />
        <form-layout-footer />
      </div>
      <div class="form-layout__right">
        <router-view />
      </div>
    </div>
    <div class="footer__waves-container">
      <svg
        class="footer__waves"
        xmlns="http://www.w3.org/2000/svg"
        xmlns:xlink="http://www.w3.org/1999/xlink"
        viewBox="0 24 150 28"
        preserveAspectRatio="none"
        shape-rendering="auto"
      >
        <defs>
          <path
            id="gentle-wave"
            d="M-160 44c30 0 58-18 88-18s 58 18 88 18 58-18 88-18 58 18 88 18 v44h-352z"
          />
        </defs>
        <g class="footer__parallax">
          <use
            xlink:href="#gentle-wave"
            x="48"
            y="0"
            fill="rgba(33, 164, 93, 1)"
          />
          <use
            xlink:href="#gentle-wave"
            x="48"
            y="3"
            fill="rgba(27, 129, 73, 0.8)"
          />
          <use
            xlink:href="#gentle-wave"
            x="48"
            y="5"
            fill="rgba(13, 157, 78, 0.4)"
          />
          <use
            xlink:href="#gentle-wave"
            x="48"
            y="7"
            fill="rgba(22, 246, 123, 0.2)"
          />
        </g>
      </svg>
    </div>
  </div>
</template>

<script>
import { computed } from "vue";
import { useStore } from "vuex";
import FormLayoutFooter from "@/components/FormLayout/Footer.vue";
import FormLayoutInfo from "@/components/FormLayout/FormLayoutInfo.vue";

export default {
  name: "FormLayout",
  components: {
    FormLayoutInfo,
    FormLayoutFooter,
  },

  setup() {
    const store = useStore();

    const getStatusOpenBlock = computed(
      () => store.getters["auth/languages/getStatusOpenBlock"]
    );
    const isAuthenticated = computed(
      () => store.getters["auth/api/isAuthenticated"]
    );
    const routeName = computed(() =>
      isAuthenticated.value ? "News" : "Login"
    );

    return {
      getStatusOpenBlock,
      isAuthenticated,
      routeName,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.footer__waves
  position relative
  width 100%
  height 21vh
  margin-bottom -7px
  min-height 100px
  max-height 200px
  opacity 0.25
  &-container
    width 100%
    position absolute
    bottom 0
    left 0

.footer__parallax>use
  animation move-forever 25s cubic-bezier(.55, .5, .45, .5) infinite

.footer__parallax>use:nth-child(1)
  animation-delay -2s
  animation-duration 7s

.footer__parallax>use:nth-child(2)
  animation-delay: -3s
  animation-duration: 10s

.footer__parallax>use:nth-child(3)
  animation-delay -4s
  animation-duration 13s

.footer__parallax>use:nth-child(4)
  animation-delay -5s
  animation-duration 20

@keyframes move-forever {
0% {
transform translate3d(-90px, 0, 0)
}

100% {
transform translate3d(85px, 0, 0)
}
}

.form-layout
  overflow hidden
  position relative
  z-index 10

.form-layout__btn
  background-color ui-cl-color-eucalypt
  border-radius border-super-small
  color ui-cl-color-white-theme
  padding 13px
  transition all .2s ease-in-out
  &:disabled
    background-color ui-cl-color-light-grey
    &:hover
      background-color ui-cl-color-light-grey
  @media (any-hover: hover)
    &:hover
      background-color ui-cl-color-comet

  &.btn__login
    &:hover
      background-color ui-cl-color-full-black
      &:disabled
        background-color ui-cl-color-light-grey

.form-layout
  background ui-cl-color-steel-gray
  height 100%
  display flex

  .wrapper
    width 1250px
    margin 0 auto

  &__right
    position relative
    padding 30px
    background ui-cl-color-comet
    border-radius border-small
    box-shadow box-shadow-main
    z-index 100
    &:before
      content ""
      border solid transparent
      position absolute
      right 100%
      top 50%
      border-right-color ui-cl-color-comet
      border-width 16px
      margin-top -14px

.form-layout__info
  width 100%
  background-color ui-cl-color-steel-gray
  color ui-cl-color-white-theme
  flex none
  display flex
  flex-direction column
  transition all 0.2s

.form-layout__container
  display grid
  grid-template-columns 1fr 1fr
  align-items center
  gap 100px

.form-layout__form
  width 100%
  color ui-cl-color-white-theme
  background-color ui-cl-color-eucalypt
  padding 50px 5.3%
  overflow-y auto
  height 100vh
</style>
