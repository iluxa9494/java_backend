<template>
  <div class="form__group" :class="{ fill: captchaCode.length > 0 }">
    <input
      type="text"
      class="form__input_stylus"
      :id="id"
      v-model="captchaCode"
      name="code"
      :class="{ invalid: (v.$dirty && !v.required) || (v.$dirty && !v.isCode) }"
      @change="v.$touch()"
      autocomplete="off"
    />

    <label :for="id" class="form__label_stylus">{{
      translationsLang.codeTitle
    }}</label>

    <span class="form__error" v-if="v.$dirty && !v.required">{{
      translationsLang.requiredField
    }}</span>

    <span class="form__error" v-else-if="v.$dirty && !v.isCode">{{
      translationsLang.codeNoMatch
    }}</span>
  </div>
</template>

<script>
import { ref, watch } from "vue";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "CodeField",
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
    const captchaCode = ref(props.modelValue);
    const { translationsLang } = useTranslations();

    watch(captchaCode, (newVal) => {
      emit("update:modelValue", newVal);
    });

    return {
      captchaCode,
      translationsLang,
    };
  },
};
</script>
