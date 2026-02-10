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
        invalid: v.$dirty && !v.required,
      }"
      placeholder="Введите текущий пароль"
      pattern="[A-Za-z0-9!@#$%^&*]+"
      autocomplete="new-password"
    />

    <span class="change-password__error" v-if="v.$dirty && !v.required"
      >Обязательно для заполнения</span
    >

    <div
      class="change-password__password-icon"
      :class="{ active: password.length > 0 }"
      @click="switchVisibility"
    >
      <img src="@/assets/static/img/password-eye.svg" alt="img.svg" />
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
