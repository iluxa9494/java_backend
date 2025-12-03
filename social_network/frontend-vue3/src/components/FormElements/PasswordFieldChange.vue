<template>
  <div class="change-password__group" :class="{ fill: password.length > 0 }">
    <input
      class="change-password__input"
      name="password"
      :id="id"
      :type="passwordFieldType"
      :value="modelValue"
      @input="updateValue($event.target.value)"
      @change="passwordBlur"
      :class="{
        invalid: (v.$dirty && !v.required) || (v.$dirty && !v.minLength),
      }"
      placeholder="Введите новый пароль"
      pattern="[A-Za-z0-9!@#$%^&*]+"
    />

    <span class="change-password__error" v-if="v.$dirty && !v.required"
      >Обязательно для заполнения</span
    >

    <div class="change-password__error-block_">
      <template>
        <span class="change-password__error" v-if="v.$dirty && !v.minLength">
          {{ translationsLang.validatePass1 }}
          {{ v.$params.minLength.min }}
          {{ translationsLang.validatePass2 }}
          {{ password.length }}
        </span>
      </template>
    </div>

    <template v-if="!password">
      <div class="change-password__password-icon active">
        <img src="@/assets/static/img/password-info.svg" alt="svg.img" />
      </div>

      <p class="change-password__password-info">
        {{ translationsLang.passInfoChange }}
      </p>
    </template>

    <div
      class="change-password__password-icon"
      :class="{ active: password.length > 0 }"
      @click="switchVisibility"
    >
      <img src="@/assets/static/img/password-eye.svg" alt="svg.img" />
    </div>
  </div>
</template>

<script>
import { computed, ref, watch } from "vue";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "PasswordField",
  props: {
    modelValue: {
      type: String,
      default: "",
    },
    v: {
      type: Object,
      required: true,
    },
    info: Boolean,
    registration: Boolean,
    id: {
      type: String,
      required: true,
    },
  },

  setup(props, { emit }) {
    const passwordFieldType = ref("password");
    const passwordHelperShow = ref(true);
    const { translationsLang } = useTranslations();

    const password = computed({
      get() {
        return props.modelValue;
      },
      set(value) {
        emit("input", value);
      },
    });

    const updateValue = (Value) => {
      password.value = Value;
      emit("update:modelValue", Value);
    };

    const switchVisibility = () => {
      passwordFieldType.value =
        passwordFieldType.value === "password" ? "text" : "password";
    };

    const passwordBlur = () => {
      passwordHelperShow.value = false;
      props.v.$touch();
    };

    watch(password, (newVal) => {
      emit("update:modelValue", newVal);
    });

    return {
      passwordFieldType,
      passwordHelperShow,
      translationsLang,
      updateValue,
      password,
      switchVisibility,
      passwordBlur,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.change-password__group
  position relative
  margin-bottom 15px

.change-password__input
  display block
  width 100%
  color ui-cl-color-767676
  border-radius border-small
  background ui-cl-color-white-theme
  border 1px solid ui-cl-color-ababab
  font-size font-size-small-medium
  padding 10px 15px
  margin-bottom 15px

.change-password__input.invalid
  border 1px solid #ff4d00

.change-password__error
  position absolute
  top -20px
  right 0
  font-size 11px
  line-height 14px
  color #ff4d00

.change-password__password-icon
  position absolute
  right 10px
  bottom 35px
  opacity 0
  visibility hidden
  width 19px
  cursor pointer
  height 12px
  transition all .2s
  &.active
    opacity 1
    visibility visible
  &:hover
    & + .change-password__password-info
      opacity 1
      visibility visible

.change-password__password-icon
  & rect
    fill black
  & circle
    stroke black
  & path
    fill black

.change-password__password-info
  font-weight font-weight-medium
  font-size font-size-super-upsmall
  border-radius border-super-small
  position absolute
  top 35px
  left 0
  right 0
  background-color ui-cl-color-white-bright-second
  display block
  padding 10px
  z-index 100
  opacity 0
  visibility hidden
  transition all .2s

.form__error-block_
    width 100%
    overflow hidden
</style>
