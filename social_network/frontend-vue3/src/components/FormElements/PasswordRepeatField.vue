<template>
  <div class="form__group" :class="{ fill: password.length > 0 }">
    <label :for="id" class="form__label_stylus">{{
      translationsLang.repeatPassword
    }}</label>

    <input
      type="password"
      class="form__input_stylus"
      :id="id"
      v-model.trim="password"
      name="password"
      :class="{
        invalid:
          (v.$dirty && !v.required) ||
          (v.$dirty && !v.minLength) ||
          (v.$dirty && !v.sameAsPassword),
      }"
      @change="v.$touch()"
    />

    <span class="form__error" v-if="v.$dirty && !v.sameAsPassword">{{
      translationsLang.matchPassword
    }}</span>

    <span class="form__error" v-if="v.$dirty && !v.minLength">
      {{ translationsLang.validatePass1 }} {{ v.$params.minLength.min }}
      {{ translationsLang.validatePass2 }}
      {{ password.length }}
    </span>
  </div>
</template>

<script>
import { watch, ref } from "vue";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "PasswordRepeatField",
  props: {
    modelValue: {
      type: String,
      default: "",
    },
    v: {
      type: Object,
      required: true,
    },
    id: {
      type: String,
      required: true,
    },
  },

  setup(props, { emit }) {
    const password = ref(props.modelValue);
    const { translationsLang } = useTranslations();

    watch(password, (newVal) => {
      emit("update:modelValue", newVal);
    });

    return {
      translationsLang,
      password,
    };
  },
};
</script>
