<template>
  <div class="form__group" :class="{ fill: number.length > 0 }">
    <input
      type="text"
      class="form__input_stylus"
      :id="id"
      v-model="number"
      name="number"
      :class="{ invalid: (v.$dirty && !v.required) || (v.$dirty && !v.isCode) }"
      @change="v.$touch()"
    />

    <label :for="id" class="form__label_stylus">Цифры</label>

    <span class="form__error" v-if="v.$dirty && !v.required"
      >Обязательное поле</span
    >

    <span class="form__error" v-else-if="v.$dirty && !v.isCode"
      >Цифры не совпадают</span
    >
  </div>
</template>

<script>
import { ref, watch } from "vue";

export default {
  name: "NumberField",
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
    const number = ref(props.modelValue);

    watch(number, (newVal) => {
      emit("update:modelValue", newVal);
    });

    return {
      number,
    };
  },
};
</script>
