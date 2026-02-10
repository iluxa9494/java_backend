<template>
  <div class="registration">
    <span class="registration__steps">{{ currentStep }}</span>
    <form class="registration__form" @submit.prevent="submitHandler()">
      <div class="form__block" v-if="step === 1">
        <h4 class="form__subtitle">{{ translationsLang.createAccTitle }}</h4>

        <email-field
          id="register-email"
          v-model="emailValue"
          :v="v$.emailValue"
          :class="{
            checked:
              v$.emailValue.$dirty &&
              v$.emailValue.required &&
              v$.emailValue.email,
          }"
        />

        <password-field
          id="register-password"
          v-model="password1"
          :v="v$.password1"
          info
          registration
          :class="{
            checked:
              v$.password1.$dirty &&
              v$.password1.required &&
              v$.password2.sameAsPassword &&
              v$.password1.minLength &&
              v$.password1.maxLength,
          }"
        />
        <span class="form__error" v-if="password1ServerError">
          {{ password1ServerError }}
        </span>

        <password-repeat-field
          id="register-repeat-password"
          v-model="password2"
          :v="v$.password2"
          :class="{
            checked:
              v$.password2.$dirty &&
              v$.password2.required &&
              v$.password2.sameAsPassword &&
              v$.password2.minLength &&
              v$.password2.maxLength,
          }"
        />
        <span class="form__error" v-if="password2ServerError">
          {{ password2ServerError }}
        </span>

        <button
          class="form__block-nextstep"
          :disabled="isStep1Disabled"
          @click.prevent="nextStep()"
        >
          {{ translationsLang.createAccStepBtn }}
        </button>
      </div>
      <div v-if="step === 2">
        <div class="form__block">
          <h4 class="form__subtitle">
            {{ translationsLang.createAccPersonalTitle }}
          </h4>
          <name-field
            id="register-firstName"
            v-model="firstName"
            :v="v$.firstName"
            :label="translationsLang.createAccNameField1"
          />
          <name-field
            id="register-lastName"
            v-model="lastName"
            :v="v$.lastName"
            :label="translationsLang.createAccNameField2"
          />
        </div>
        <div class="form__block">
          <h4 class="form__captcha-desc">
            {{ translationsLang.createAccCaptchaDescr }}
          </h4>
          <div class="img_captcha">
            <img :src="imgCode" :alt="'image'" />
            <button class="btn__update" @click.prevent="updateCatcha()">
              {{ translationsLang.createAccCaptchaRefresh }}
            </button>
          </div>
          <code-field
            id="register-code"
            v-model="captchaCode"
            :v="v$.captchaCode"
            :class="{
              checked:
                v$.captchaCode.$dirty &&
                v$.captchaCode.required &&
                v$.captchaCode.isCode,
            }"
          />
          <confirm-field
            id="register-confirm"
            v-model="confirm"
            :v="v$.confirm"
          />
        </div>
        <div class="registration__action">
          <button type="submit" class="form-layout__btn btn__login">
            {{ translationsLang.createAccBtnReg }}
          </button>
          <button class="btn__back" @click.prevent="prevStep()">
            {{ translationsLang.createAccBtnBack }}
          </button>
        </div>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted, onBeforeMount } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import useTranslations from "@/composables/useTranslations";
import useVuelidate from "@vuelidate/core";
import { required, email, minLength, maxLength, sameAs } from "@vuelidate/validators";
import PasswordField from "@/components/FormElements/PasswordField.vue";
import PasswordRepeatField from "@/components/FormElements/PasswordRepeatField.vue";
import EmailField from "@/components/FormElements/EmailField.vue";
import NameField from "@/components/FormElements/NameField.vue";
import CodeField from "@/components/FormElements/CodeField.vue";
import ConfirmField from "@/components/FormElements/ConfirmField.vue";

export default {
  name: "AuthRegistration",
  components: {
    PasswordField,
    EmailField,
    NameField,
    CodeField,
    ConfirmField,
    PasswordRepeatField,
  },

  setup() {
    const store = useStore();
    const router = useRouter();
    const step = ref(1);
    const totalSteps = ref(2);
    const emailValue = ref("");
    const password1 = ref("");
    const password2 = ref("");
    const firstName = ref("");
    const lastName = ref("");
    const imgCode = ref("");
    const captchaCode = ref("");
    const captchaSecret = ref("");
    const confirm = ref(false);
    const password1ServerError = ref("");
    const password2ServerError = ref("");
    const isCode = ref(true);
    const { translationsLang } = useTranslations();

    const rules = {
  confirm: { sameAs: sameAs(() => true) },
  emailValue: { required, email },
  password1: { required, minLength: minLength(8), maxLength: maxLength(20) },
  password2: {
    required,
    minLength: minLength(8),
    maxLength: maxLength(20),
    sameAsPassword: sameAs(() => password1.value), 
  },
  firstName: { required, minLength: minLength(3) },
  lastName: { required, minLength: minLength(3) },
  captchaCode: {
    required,
    isCode() {
      return isCode.value;
    },
  },
};

    const v$ = useVuelidate(rules, {
      confirm,
      emailValue,
      password1,
      password2,
      firstName,
      lastName,
      captchaCode,
    });

    const getCaptcha = computed(() => store.getters["auth/captcha/getCaptcha"]);
    const isStep1Disabled = computed(
      () => !emailValue.value || !password1.value || !password2.value
    );
    const currentStep = computed(() => `${step.value}/${totalSteps.value}`);

    const nextStep = () => {
      step.value += 1;
    };

    const prevStep = () => {
      step.value -= 1;
    };

    const updateCatcha = () => {
      store.dispatch("auth/captcha/fetchCaptcha").then(() => {
        imgCode.value = getCaptcha.value.imgCode;
        captchaSecret.value = getCaptcha.value.secret;
      });
    };

    const submitHandler = () => {
      if (v$.$invalid) {
        v$.$touch();
        return;
      }
      const data = {
        email: emailValue.value,
        password1: password1.value,
        password2: password2.value,
        firstName: firstName.value,
        lastName: lastName.value,
        captchaCode: captchaCode.value,
        captchaSecret: captchaSecret.value
      };

      password1ServerError.value = "";
      password2ServerError.value = "";

      store
        .dispatch("auth/api/register", data)
        .then(() => {
          router.push({ name: "RegisterSuccess" });
        })
        .catch((error) => {
          const errorMessage = error?.response?.data?.message || "";
          if (errorMessage.includes("password1:")) {
            password1ServerError.value = errorMessage;
          }
          if (errorMessage.includes("password2:")) {
            password2ServerError.value = errorMessage;
          }
          if (errorMessage.includes("captcha")) {
            isCode.value = false;
          }
        });
    };

    watch(captchaCode, (newVal) => {
      if (!isCode.value && newVal) {
        isCode.value = true;
      }
    });
    watch(password1, () => {
      if (password1ServerError.value) {
        password1ServerError.value = "";
      }
    });
    watch(password2, () => {
      if (password2ServerError.value) {
        password2ServerError.value = "";
      }
    });

    onMounted(() => {
      // code.value = getCode.value;
    });

    onBeforeMount(() => {
      updateCatcha();
    });

    return {
      step,
      emailValue,
      password1,
      password2,
      firstName,
      lastName,
      imgCode,
      captchaCode,
      captchaSecret,
      translationsLang,
      v$,
      password1ServerError,
      password2ServerError,
      isStep1Disabled,
      currentStep,
      submitHandler,
      nextStep,
      prevStep,
      updateCatcha,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.btn__back
  margin-left 15px
  background-color transparent
  color rgba(255, 255, 255, 0.5)
  font-size font-size-small
  transition all .2s ease-in-out
  &:hover
    color ui-cl-color-white-theme

.form__captcha-desc
  font-weight font-weight-light
  color ui-cl-color-white-theme
  font-size font-size-small-medium
  margin-bottom 15px

.form__block+.form__block
  margin-top 30px

.form__block-nextstep
  background-color ui-cl-color-eucalypt
  border-radius border-super-small
  color ui-cl-color-white-theme
  padding 13px
  transition all .2s ease-in-out
  margin-top 30px
  &:disabled
    background-color ui-cl-color-light-grey
    &:hover
      background-color ui-cl-color-light-grey
  @media (any-hover: hover)
    &:hover
      background-color ui-cl-color-full-black

.registration
  position relative
  min-height 100%
  display flex
  flex-direction column
  justify-content center

  &__steps
    color #afadde
    position absolute
    top 0
    right 0

  .form__block
    margin-bottom 0

  .form__group+.form__group
    margin-top 30px

  .form__group:after
    right 14px
    bottom 18px

.registration__action
  margin-top 40px

  @media (max-width breakpoint-xxl)
    margin-top 20px

.img_captcha
  display flex
  gap 20px
  align-items center
  margin-bottom 6px

.btn__update
  background-color ui-cl-color-eucalypt
  border-radius border-super-small
  font-size font-size-super-medium-small
  color ui-cl-color-white-theme
  padding 5px
  transition all .2s ease-in-out
  &:hover
    background-color ui-cl-color-full-black

.last-block_reg
  margin-top 15px !important
</style>
