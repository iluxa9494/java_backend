<template>
  <form class="admin-login" action="#" @submit.prevent="submitHandler">
    <h1 class="admin-login__title form__title">Вход в админ-панель</h1>

    <email-field id="admin-login-email" v-model="emailvalue" :v="$v.emailvalue" />
    <password-field id="admin-login-password" v-model="password" :v="$v.password" />

    <div class="admin-login__action">
      <button-hover tag="button" type="submit" variant="white">Войти</button-hover>
      <router-link class="admin-login__link" :to="{ name: 'Forgot' }"> Забыли пароль?</router-link>
    </div>
  </form>
</template>

<script>
import { ref } from 'vue';
import useVuelidate from '@vuelidate/core';
import { required, email, minLength } from '@vuelidate/validators';
import PasswordField from '@/components/FormElements/PasswordField.vue';
import EmailField from '@/components/FormElements/EmailField.vue';

export default {
  name: 'AdminLogin',
  components: {
    PasswordField,
    EmailField,
  },

  setup() {
    const emailvalue = ref('');
    const password = ref('');

    const rules = {
      emailvalue: { required, email },
      password: { required, minLength: minLength(8) },
    };

    const $v = useVuelidate(rules, { emailvalue, password });

    const submitHandler = () => {
      if ($v.$invalid) {
        $v.$touch();
      }
    };

    return {
      emailvalue,
      password,
      $v,
      submitHandler,
    };
  },
};
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl'

.admin-login
  width 100%;
  max-width 450px

.admin-login__title
  margin-bottom 50px

.admin-login__action
  margin-top 50px
  display flex
  align-items center

.admin-login__link
  display block
  margin-left 30px
  font-size font-size-small
  color rgba(255, 255, 255, 0.5)
  transition all 0.2s

  &:hover
    color ui-cl-color-white-theme
</style>
