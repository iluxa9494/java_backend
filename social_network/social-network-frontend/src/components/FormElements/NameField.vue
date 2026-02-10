<template>
  <div class="form__group" :class="{ fill: name.length > 0 }">
    <input
      type="text"
      class="form__input_stylus"
      :id="id"
      v-model="name"
      name="name"
      :class="{
        invalid: (v.$dirty && !v.required) || (v.$dirty && !v.minLength),
      }"
      @change="v.$touch()"
      @input="onInput"
    />

    <label :for="id" class="form__label_stylus">{{ label }}</label>

    <span class="form__error" v-if="v.$dirty && !v.required">{{
      translationsLang.requiredField
    }}</span>

    <span class="form__error" v-else-if="v.$dirty && !v.minLength">
      {{ translationsLang.minimumNumberOfCharacters }} {{ v.minLength }}
    </span>
  </div>
</template>

<script>
import { ref, watch } from "vue";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "NameField",
  props: {
    modelValue: {
      type: String,
      default: "",
    },
    v: {
      type: Object,
      required: true,
    },
    label: {
      type: String,
      default: "Имя",
    },
    id: {
      type: String,
      required: true,
    },
  },

  setup(props, { emit }) {
    const name = ref(props.modelValue);
    const { translationsLang } = useTranslations();

    watch(name, (newVal) => {
      emit("update:modelValue", newVal);
    });

    const onInput = (event) => {
      const sourceValue = event.target.value;
      const newValue = sourceValue.replace(/[^a-zA-Zа-яА-ЯёЁ_]/g, "");
      if (sourceValue !== newValue) {
        name.value = newValue;
      }
    };

    return {
      name,
      translationsLang,
      onInput,
    };
  },
};
</script>
