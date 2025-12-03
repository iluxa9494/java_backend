<template>
  <div class="settings-push">
    <ul class="settings-push__list">
      <li
        class="settings-push__item"
        v-for="item in getNotificationsSettings"
        :key="item.type"
      >
        <div class="settings-push__icon">
          <img
            :src="require(`@/assets/static/img/settings/push/${item.icon}.svg`)"
            alt="img.png"
          />
        </div>

        <h2 class="settings-push__name">
          {{ currentTranslations === "Русский" ? item.name : item.nameEng }}
        </h2>

        <div class="settings-push__check">
          <input
            class="settings-push__check-input"
            type="checkbox"
            :id="item.icon"
            @change="onChecked(item)"
            :checked="item.enable"
          />

          <label class="settings-push__check-label" :for="item.icon" />
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import { computed, onMounted } from "vue";
import { useStore } from "vuex";

export default {
  name: "SettingsPush",

  setup() {
    const store = useStore();

    const getNotificationsSettings = computed(
      () => store.getters["profile/account/getNotificationsSettings"]
    );
    const currentTranslations = computed(
      () => store.state.auth.languages.language.name
    );

    onMounted(() => {
      store.dispatch("profile/account/apiNotificationsSettings");
    });

    const onChecked = (item) => {
      console.log(item);
      store.dispatch("profile/account/changeNotifications", {
        notificationType: item.type,
        enable: !item.enable,
      });
    };

    return {
      getNotificationsSettings,
      currentTranslations,
      onChecked,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.settings__wrap
  .inner-page__main
    max-width 100%


.settings-push
  background ui-cl-color-white-theme
  box-shadow box-shadow-main
  padding 30px
  border-radius border-big-radius
  width 100%

.settings-push__item
  display flex
  align-items center
  padding 25px 0

  &+&
    border-top 1px solid ui-cl-color-e6e6e6

.settings-push__icon
  margin-right 15px
  width 23px
  height 23px

.settings-push__name
  margin-right auto
  font-size font-size-small
  font-weight font-weight-regular
  font-family 'Open Sans'
  color ui-cl-color-comet

.settings-push__check
  width 43px

.settings-push__check-input
  display none

  &:checked
    & + .settings-push__check-label
      &:before
        background-color ui-cl-color-eucalypt
        left 50%

      &:after
        background-color ui-cl-color-a5cfb1

.settings-push__check-label
  display block
  width 100%
  position relative
  cursor pointer

  &:before
    content ''
    display block
    width 22px
    height 22px
    border-radius border-half
    background-color ui-cl-color-BCBCC7
    position absolute
    left 0
    top -11px
    z-index 2
    transition all 0.2s

  &:after
    content ''
    display block
    background-color ui-cl-color-d6d6e2
    border-radius 13px
    width 43px
    height 13px
    position absolute
    left 0
    top 50%
    transform translateY(-50%)
    transition all 0.2s
</style>
