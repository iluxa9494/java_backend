<template>
  <div class="change-password">
    <h2 class="change-password__title form__title">
      {{ translationsLang.setNewPasswordTitle }}
    </h2>

    <form class="change-password__form" @submit.prevent="submitHandler()">
      <password-field
        id="change-password"
        v-model="password"
        :v="v$.password"
        info="info"
        registration="registration"
        :class="{
          checked: v$.password.required && v$.passwordTwo.sameAsPassword,
        }"
      />

      <password-repeat-field
        id="change-repeat-password"
        v-model="passwordTwo"
        :v="v$.passwordTwo"
        :class="{
          checked: v$.password.required && v$.passwordTwo.sameAsPassword,
        }"
      />

      <div class="change-password__action">
        <button type="submit" class="form-layout__btn btn__fargot">
          {{ translationsLang.recoveryPassBtn }}
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import useVuelidate from "@vuelidate/core";
import { required, sameAs, minLength } from "@vuelidate/validators";
import useTranslations from "@/composables/useTranslations";
import PasswordField from "@/components/FormElements/PasswordField.vue";
import PasswordRepeatField from "@/components/FormElements/PasswordRepeatField.vue";

export default {
  name: "ChangePassword",
  components: {
    PasswordField,
    PasswordRepeatField,
  },

  setup() {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();
    const password = ref("");
    const passwordTwo = ref("");
    const secret = ref("");
    const { translationsLang } = useTranslations();

    const rules = {
      password: { required, minLength: minLength(8) },
      passwordTwo: {
        required,
        sameAsPassword: sameAs("password"),
        minLength: minLength(8),
      },
    };

    const v$ = useVuelidate(rules, { password, passwordTwo });

    onMounted(() => {
      secret.value = route.params.secret || "";
    });

    const submitHandler = () => {
      if (v$.$invalid) {
        v$.$touch();
        return;
      }
      const data = {
        password: passwordTwo.value,
        secret: secret.value,
      };
      store.dispatch("profile/account/passwordSet", data).then(() => {
        router.push({ name: "ChangePasswordSuccess" });
      });
    };

    return {
      password,
      passwordTwo,
      secret,
      translationsLang,
      v$,
      submitHandler,
    };
  },
};
</script>

<style lang="stylus">
.change-password
  display flex
  justify-content center
  flex-direction column
  height 100%

.change-password .form__group+.form__group
  margin-top 20px

.change-password__title
  margin-bottom 50px
  color #fff

.change-password__action
  margin-top 30px
</style>
