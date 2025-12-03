<template>
  <div class="shift-password">
    <form class="shift-password__form" @submit.prevent="submitHandler">
      <div class="form__block">
        <h4 class="form__subtitle">Смена пароля</h4>

        <password-field
          id="shift-password"
          v-model="password"
          :v="v$.password"
          info
          registration
          :class="{
            checked: v$.password.required && v$.passwordTwo.sameAsPassword,
          }"
        />

        <password-repeat-field
          id="shift-repeat-password"
          v-model="passwordTwo"
          :v="v$.passwordTwo"
          :class="{
            checked: v$.password.required && v$.passwordTwo.sameAsPassword,
          }"
        />
      </div>

      <div class="form__block">
        <h4 class="form__subtitle">
          Введите символы, которые вы видите на экране
        </h4>

        <button class="btn__update bold" @click.prevent="updateCaptcha()">
          Обновить
        </button>

        <div class="img_captcha">
          <img :src="imgCode" :alt="'Captcha image'" />
        </div>

        <code-field
          id="register-code"
          v-model="captchaCode"
          :v="v$.captchaCode"
          :class="{ checked: v$.captchaCode.required && v$.captchaCode.isCode }"
        />
      </div>

      <div class="shift-password__action">
        <button-hover tag="button" type="submit" variant="white"
          >Сменить</button-hover
        >
      </div>
    </form>
  </div>
</template>

<script>
import { computed, onBeforeMount, onMounted, ref } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import useVuelidate from "@vuelidate/core";
import { required, sameAs, minLength } from "@vuelidate/validators";
import PasswordField from "@/components/FormElements/PasswordField.vue";
import PasswordRepeatField from "@/components/FormElements/PasswordRepeatField.vue";
import CodeField from "@/components/FormElements/CodeField.vue";

export default {
  name: "ShiftPassword",
  components: {
    PasswordField,
    PasswordRepeatField,
    CodeField,
  },

  setup() {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();
    const password = ref("");
    const passwordTwo = ref("");
    const captchaCode = ref("");
    const isCode = ref(true);
    const imgCode = ref("");
    const temp = ref("");

    const rules = {
      password: { required, minLength: minLength(8) },
      passwordTwo: {
        required,
        sameAsPassword: sameAs("password"),
        minLength: minLength(8),
      },

      captchaCode: {
        required,
        isCode() {
          return isCode.value;
        },
      },
    };

    const v$ = useVuelidate(rules, { password, passwordTwo, captchaCode });

    const getCaptcha = computed(() => store.getters["auth/captcha/getCaptcha"]);

    const updateCaptcha = () => {
      store.dispatch("auth/captcha/fetchCaptcha").then(() => {
        imgCode.value = getCaptcha.value.imgCode;
        captchaCode.value = getCaptcha.value.secret;
      });
    };

    onBeforeMount(() => {
      updateCaptcha();
    });

    onMounted(() => {
      temp.value = route.query.temp || "";
    });

    const submitHandler = () => {
      if (v$.$invalid) {
        v$.$touch();
        return;
      }
      store.dispatch(
        "profile/account/changePassword",
        {
          password: password.value,
          captchaCode: captchaCode.value,
          temp: temp.value,
        }
          .then(() => {
            router.push({ name: "change-password-success" });
          })
          .catch((error) => {
            console.error("Error changing password:", error);
            alert(
              "Произошла ошибка при смене пароля. Пожалуйста, попробуйте снова."
            );
          })
      );
    };

    return {
      password,
      passwordTwo,
      captchaCode,
      isCode,
      imgCode,
      v$,
      updateCaptcha,
      submitHandler,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.shift-password
  display flex
  justify-content center
  flex-direction column
  height 100%

.shift-password__title
  margin-bottom 20px

.shift-password__action
  margin-top 40px

.img_captcha
  margin-bottom 15px

.btn__update
  margin-bottom 15px
  width 90px
  height 30px
  padding 1px
  background-color white
  color ui-cl-color-eucalypt
</style>
