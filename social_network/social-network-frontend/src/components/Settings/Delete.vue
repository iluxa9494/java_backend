<template>
  <div class="settings-delete">
    <h2 class="settings-delete__title">
      {{ translationsLang.settingDeleteTitle }}
    </h2>

    <div class="settings-delete__confirm">
      <input
        class="settings-delete__confirm-input"
        type="checkbox"
        id="confirm"
        v-model="confirm"
      />

      <label for="confirm" class="settings-delete__confirm-label">
        {{ translationsLang.settingDeleteLabel }}
      </label>
    </div>

    <div class="settings-delete__actions">
      <button
        :disable="!confirm"
        variant="warning"
        @click.prevent="onDelete()"
      >
        {{ translationsLang.settingDeleteButton }}
      </button>

      <router-link
        class="settings-delete__actions-link"
        :to="{ name: 'Profile' }"
      >
        {{ translationsLang.settingDeleteCancel }}
      </router-link>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "SettingsDelete",

  setup() {
    const store = useStore();
    const router = useRouter();
    const confirm = ref(false);
    const { translationsLang } = useTranslations();

    const onDelete = () => {
      store.dispatch("profile/info/deleteInfo").then(() => {
        store.dispatch("auth/api/logout").then(() => {
          router.push({ name: "Login" });
        });
      });
    };

    return {
      confirm,
      translationsLang,
      onDelete,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.settings-delete
  background ui-cl-color-white-theme
  box-shadow box-shadow-main
  padding 30px
  border-radius border-big-radius

  @media (max-width breakpoint-xl)
    padding 20px

.settings-delete__title
  margin-bottom 20px
  color ui-cl-color-0A0A0A
  font-size font-size-updefault
  line-height 27px
  max-width 666px

.settings-delete__confirm
  margin-bottom 30px

.settings-delete__confirm-input
  display none

  &:checked
    & + .settings-delete__confirm-label
      &:before
        border-color ui-cl-color-eucalypt

      &:after
        opacity 1

.settings-delete__confirm-label
  position relative
  display flex
  align-items center
  color ui-cl-color-santas-gray
  font-size font-size-default
  cursor pointer

  &:before
    content ''
    display block
    border 2px solid ui-cl-color-BCBCC7
    width 26px
    height 26px
    margin-right 24px
    transition all 0.2s

  &:after
    content ''
    display block
    width 8px
    height 13px
    border 2px solid transparent
    border-bottom-color ui-cl-color-eucalypt
    border-right-color ui-cl-color-eucalypt
    position absolute
    left 9px
    top 4px
    transform rotate(45deg)
    opacity 0
    transition all 0.2s

.settings-delete__actions
  display flex
  align-items center

.settings-delete__actions-link
  font-size font-size-small
  color ui-cl-color-eucalypt
  margin-left 20px
</style>
