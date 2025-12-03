<template>
  <div class="form__group last-block_reg">
    <input
      type="checkbox"
      name="confirm"
      class="form__checkbox"
      v-model="confirm"
      :id="id"
      :class="{ invalid: v.$dirty && !v.sameAs }"
    />

    <label :for="id" class="form__checkbox-label">
      <span>{{ translationsLang.confirmComponent1 }}</span>
      <a href="#">{{ translationsLang.confirmComponent2 }}</a>
      <span>{{ translationsLang.confirmComponent3 }}</span>
      <a href="">{{ translationsLang.confirmComponent4 }}</a>
    </label>
  </div>
</template>

<script>
import { ref, watch } from "vue";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "ConfirmField",
  props: {
    modelValue: {
      type: Boolean,
      default: false,
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
    const confirm = ref(props.modelValue);
    const { translationsLang } = useTranslations();

    watch(confirm, (newVal) => {
      emit("update:modelValue", newVal);
    });

    return {
      confirm,
      translationsLang,
    };
  },
};
</script>
