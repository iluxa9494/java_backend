<template>
  <div class="shift-email">
    <form class="shift-email__form" @submit.prevent="submitHandler">
      <div class="form__block">
        <h4 class="form__subtitle">Смена почтового ящика</h4>

        <email-field
          id="shift-email"
          v-model="emailValue"
          :v="v$.emailValue"
          :class="{ checked: v$.emailValue.required && v$.emailValue.email }"
          placeholder="Новый почтовый ящик"
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

      <div class="shift-email__action">
        <button-hover tag="button" type="submit" variant="white"
          >Сменить</button-hover
        >
      </div>
    </form>
  </div>
</template>

<script>
import { ref, computed, onBeforeMount, onMounted } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import useVuelidate from "@vuelidate/core";
import { required, email } from "@vuelidate/validators";
import EmailField from "@/components/FormElements/EmailField.vue";
import CodeField from "@/components/FormElements/CodeField.vue";

export default {
  name: "ShiftEmail",
  components: {
    EmailField,
    CodeField,
  },

  setup() {
    const store = useStore();
    const router = useRouter();
    const route = useRoute();
    const emailValue = ref("");
    const imgCode = ref("");
    const captchaCode = ref("");
    const isCode = ref(true);
    const temp = ref("");

    const rules = {
      emailValue: { required, email },
      captchaCode: {
        required,
        isCode() {
          return isCode.value;
        },
      },
    };

    const v$ = useVuelidate(rules, { emailValue, captchaCode });

    const getCaptcha = computed(() => store.getters["auth/captcha/getCaptcha"]);

    const updateCaptcha = () => {
      store.dispatch("auth/captcha/fetchCaptcha").then(() => {
        imgCode.value = getCaptcha.value.imgCode;
        captchaCode.value = getCaptcha.value.secret;
      });
    };

    const submitHandler = () => {
      if (v$.$invalid) {
        v$.$touch();
        return;
      }

      store.dispatch(
        "profile/account/changeEmail",
        {
          email: emailValue.value,
          captchaCode: captchaCode.value,
          temp: temp.value,
        }.then(() => {
          const emailDomain = `https://${emailValue.value.split("@").pop()}`;
          store.dispatch("auth/info/setBtn", {
            pageId: "shift-email-success",
            btnId: "href",
            btnIdValue: emailDomain,
          });
          router.push({ name: "ShiftEmailSuccess" });
        })
      );
    };

    onBeforeMount(() => {
      updateCaptcha();
    });

    onMounted(() => {
      temp.value = route.query.temp || "";
    });

    return {
      emailValue,
      imgCode,
      captchaCode,
      isCode,
      v$,
      updateCaptcha,
      submitHandler,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';
.shift-email
  display flex
  justify-content center
  flex-direction column
  height 100%

.shift-email__title
  margin-bottom 20px

.shift-email__action
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
