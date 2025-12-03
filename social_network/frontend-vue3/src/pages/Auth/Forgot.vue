<template>
  <div class="forgot">
    <h2 class="forgot__title form__title">
      {{ translationsLang.recoveryPassTitle }}
    </h2>
    <span class="forgot__title-descr">{{
      translationsLang.recoveryPassDescription
    }}</span>
    <form class="forgot__form" @submit.prevent="submitHandler()">
      <email-field id="forgot-email" v-model="emailValue" :v="v$.emailValue" />

      <div class="forgot__action">
        <button
          :disabled="!emailValue"
          type="submit"
          class="form-layout__btn btn__fargot"
        >
          {{ translationsLang.recoveryPassBtn }}
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import { ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import useVuelidate from "@vuelidate/core";
import { required, email } from "@vuelidate/validators";
import useTranslations from "@/composables/useTranslations";
import EmailField from "@/components/FormElements/EmailField.vue";

export default {
  name: "AuthForgot",
  components: {
    EmailField,
  },

  setup() {
    const store = useStore();
    const router = useRouter();
    const emailValue = ref("");
    const { translationsLang } = useTranslations();

    const rules = {
      emailValue: { required, email },
    };

    const v$ = useVuelidate(rules, { emailValue });

    const submitHandler = () => {
      if (v$.$invalid) {
        v$.$touch();
        store.dispatch("global/alert/setAlert", {
          status: "status",
          text: "Такого email-адреса не существует",
        });
        return;
      }

      store
        .dispatch("profile/account/passwordRecovery", {
          email: emailValue.value,
        })
        .then(() => {
          const emailDomain = `https://${emailValue.value.split("@").pop()}`;
          store.dispatch("auth/info/setBtn", {
            pageId: "forgot-success",
            btnId: "href",
            btnIdValue: emailDomain,
          });

          router.push({ name: "ForgotSuccess" });
          store.dispatch("global/alert/setAlert", {
          status: "success",
          text: "Ваш пароль выслан на почту!",
        });
        });
    };

    return {
      emailValue,
      translationsLang,
      v$,
      submitHandler,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';
.forgot
  display flex
  justify-content center
  flex-direction column
  height 100%

.btn__fargot
  &:disabled
    background-color ui-cl-color-light-grey
    &:hover
      background-color ui-cl-color-light-grey !important
  @media (any-hover: hover)
    &:hover
      background-color ui-cl-color-full-black !important

.forgot__title
  color ui-cl-color-white-theme
  margin-bottom 5px

.forgot__title-descr
  color #afadde
  margin-bottom 30px

.forgot__action
  margin-top 30px
</style>
