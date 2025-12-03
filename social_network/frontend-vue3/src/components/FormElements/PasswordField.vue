<template>
  <div class="form__group" :class="{ fill: password.length > 0 }">
    <label class="form__label_stylus" :for="id">{{
      translationsLang.passLogin
    }}</label>

    <input
      class="form__input_stylus"
      name="password"
      :id="id"
      :type="passwordFieldType"
      :value="modelValue"
      @input="updateValue($event.target.value)"
      @change="passwordBlur()"
      :class="{
        invalid: (v.$dirty && !v.required) || (v.$dirty && !v.minLength),
      }"
      pattern="[A-Za-z0-9!@#$%^&*]+"
      autocomplete="current-password"
    />

    <span class="form__error" v-if="v.$dirty && !v.required">{{
      translationsLang.passLoginEnter
    }}</span>

    <div class="form__error-block_">
      <div v-if="registration">
        <span class="form__password-helper" :class="levelInfo.class" />
        <span class="form__error" v-if="password.length >= 3">
          {{ levelInfo.text }}
        </span>
      </div>

      <div v-else>
        <span class="form__error" v-if="v.$dirty && !v.minLength">
          {{ translationsLang.validatePass1 }}
          {{ v.$params.minLength.min }}
          {{ translationsLang.validatePass2 }}
          {{ password.length }}
        </span>
      </div>
    </div>

    <div v-if="info">
      <div class="form__password-icon active">
        <img src="@/assets/static/img/password-info.svg" alt="img.svg" />
      </div>

      <p class="form__password-info">
        {{ translationsLang.passInfo }}
      </p>
    </div>

    <div
      class="form__password-icon"
      :class="{ active: password.length > 0 }"
      @keydown="switchVisibility"
      @click="switchVisibility"
      v-if="!registration"
    >
      <img src="@/assets/static/img/password-eye.svg" alt="img.svg" />
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from "vue";
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
    const password = ref(props.modelValue);
    const { translationsLang } = useTranslations();

    const levelInfo = computed(() => {
      if (!passwordHelperShow.value) {
        return { text: null, class: null };
      }
      if (password.value.length >= 3 && password.value.length < 7) {
        return { text: translationsLang.passLevelFirst, class: "easy" };
      }
      if (password.value.length >= 7 && password.value.length < 11) {
        return { text: translationsLang.passLevelSecond, class: "midle" };
      }
      if (password.value.length > 11) {
        return { text: translationsLang.passLevelTree, class: "hard" };
      }
      return { text: null, class: null };
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
      password,
      translationsLang,
      switchVisibility,
      passwordBlur,
      updateValue,
      levelInfo,
      passwordFieldType,
    };
  },
};
</script>
