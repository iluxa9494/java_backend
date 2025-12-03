<template>
  <div class="form__group" :class="{ fill: email.length > 0 }">
    <input
      class="form__input_stylus"
      :id="id"
      :value="modelValue"
      @input="updateValue($event.target.value)"
      name="email"
      :class="{
        invalid:
          (v.$dirty && !v.required) ||
          (v.$dirty && !v.email) ||
          /\s/.test(email),
      }"
      @change="v.$touch()"
      autocomplete="email"
    />

    <label class="form__label_stylus" :for="id">{{ placeholder }}</label>

    <span class="form__error" v-if="v.$dirty && !v.required">
      {{ translationsLang.enterEmail }}
    </span>

    <span class="form__error" v-else-if="v.$dirty && !v.email">
      {{ translationsLang.correctEmail }}
    </span>
  </div>
</template>

<script>
import { ref } from "vue";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "EmailField",
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
    placeholder: {
      type: String,
      default: "E-mail",
    },
  },

  setup(props, { emit }) {
    const email = ref(props.modelValue);
    const { translationsLang } = useTranslations();

    const updateValue = (value) => {
      email.value = value;
      emit("update:modelValue", value);
    };

    return {
      email,
      translationsLang,
      updateValue,
    };
  },
};
</script>
