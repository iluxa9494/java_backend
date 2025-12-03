<template>
  <div class="login">
    <h2 class="login__title form__title">{{ translationsLang.loginAuth }}</h2>

    <form class="login__form" @submit.prevent="submitHandler()">
      <email-field id="login-email" v-model="emailValue" :v="v$.emailValue" />

      <password-field id="login-password" v-model="password" :v="v$.password" />

      <div class="login__action">
        <button class="form-layout__btn btn__login" :disabled="isButtonDisabled" type="submit">
        {{ translationsLang.loginAuthBtn }}
        </button>
        <router-link class="login__link" :to="{ name: 'Forgot' }">
          {{ translationsLang.loginAuthForgot }} 
        </router-link>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { useStore } from 'vuex';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';
import { required, email, minLength } from '@vuelidate/validators';
import PasswordField from '@/components/FormElements/PasswordField.vue';
import EmailField from '@/components/FormElements/EmailField.vue';
import useTranslations from '@/composables/useTranslations';

export default {
  name: 'AuthLogin',
  components: {
    PasswordField,
    EmailField,
  },

  setup() {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();
    const emailValue = ref('');
    const password = ref('');
    const { translationsLang } = useTranslations();

    const rules = {
      emailValue: { required, email },
      password: { required, minLength: minLength(8) },
    };

    const v$ = useVuelidate(rules, { emailValue, password });

    const redirectUrl = computed(() => route.query.redirect || 'News');
    const isButtonDisabled = computed(() => !emailValue.value || !password.value);

    const submitHandler = () => {
      if (v$.$invalid) {
        v$.$touch();
        return;
      }

      store.dispatch('auth/api/login', { email: emailValue.value, password: password.value })
        .then(() => {
          store.dispatch('profile/info/apiInfo');
        })
        .then(() => {
          router.push({ name: redirectUrl.value });
        })
        .catch(() => {
          store.dispatch('global/alert/setAlert', {
            status: 'error',
            text: 'Произошла ошибка при входе. Пожалуйста, попробуйте еще раз.',
          }, { root: true });
        });
    };

    return {
      emailValue,
      password,
      isButtonDisabled,
      submitHandler,
      v$,
      translationsLang,
    };
  },
};
</script>


<style lang="stylus" scoped>
@import '@/assets/stylus/base/vars.styl';
.login
  display flex
  flex-direction column
  justify-content center

.login__title
  color ui-cl-color-white-theme
  font-weight font-weight-medium
  margin-bottom 50px

.login__action
  display flex
  align-items center
  margin-top 30px
  .form-layout__btn
    min-width 150px

.login__link
  font-size font-size-small
  color rgba(255, 255, 255, 0.5)
  margin-left 30px
  white-space nowrap
  transition all 0.2s

  &:hover
    color ui-cl-color-white-theme
</style>
