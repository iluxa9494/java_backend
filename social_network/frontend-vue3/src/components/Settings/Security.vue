<template>
  <div class="settings-security">
    <div class="settings-security__block">
      <div class="settings-security__mail">
        <h3 class="settings-security__title">Сменить E-mail:</h3>

        <input
          class="settings-security__value"
          v-model="changeEmail"
          autocomplete="off"
        />

        <button
          class="settings-security__btn"
          @click.prevent="openModal('email')"
        >
          {{ translationsLang.settingBtnChange }}
        </button>
      </div>

      <h3 class="settings-security__title">Сменить пароль:</h3>
      <password-field-change-old
        id="old-password"
        v-model="passwordOld"
        :v="v$.passwordOld"
        info
        registration
        :class="{
          checked: v$.passwordOld.required,
        }"
      />
      <password-field-change
        id="change-password"
        v-model="password"
        :v="v$.password"
        info
        registration
        :class="{
          checked: v$.password.required && v$.passwordTwo.sameAsPassword,
        }"
      />
      <password-repeat-field-change
        id="change-repeat-password"
        v-model="passwordTwo"
        :v="v$.passwordTwo"
        :class="{
          checked: v$.password.required && v$.passwordTwo.sameAsPassword,
        }"
      />
      <div class="change-password__action">
        <button
          type="submit"
          class="settings-security__btn"
          @click.prevent="openModal('password')"
        >
          {{ translationsLang.settingBtnChange }}
        </button>
      </div>
    </div>

    <modal v-model="modalShow">
      <p v-if="modalText">{{ modalText }}</p>

      <template :v-slot="actions">
        <button-hover @click="closeModal">{{
          translationsLang.yes
        }}</button-hover>
      </template>
    </modal>
  </div>
</template>

<script>
import { computed, onMounted, ref } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import useTranslations from "@/composables/useTranslations";
import { useVuelidate } from "@vuelidate/core";
import { required, sameAs, minLength } from "@vuelidate/validators";
import PasswordFieldChange from "@/components/FormElements/PasswordFieldChange";
import PasswordRepeatFieldChange from "@/components/FormElements/PasswordRepeatFieldChange";
import PasswordFieldChangeOld from "@/components/FormElements/PasswordFieldChangeOld.vue";
import Modal from "@/components/Modal";
import auth from "@/requests/auth";

export default {
  name: "SettingsSecurity",
  components: {
    Modal,
    PasswordFieldChange,
    PasswordRepeatFieldChange,
    PasswordFieldChangeOld,
  },

  setup() {
    const { getters, dispatch } = useStore();
    const route = useRoute();
    const router = useRouter();
    const modalShow = ref(false);
    const modalText = ref("");
    const changeEmail = ref("");
    const passwordOld = ref("");
    const password = ref("");
    const passwordTwo = ref("");
    const secret = ref("");
    const { translationsLang } = useTranslations();

    const rules = {
      passwordOld: {
        required,
      },
      password: {
        required,
        minLength: minLength(8),
      },
      passwordTwo: {
        required,
        sameAsPassword: sameAs(() => password.value),
        minLength: minLength(8),
      },
    };

    const v$ = useVuelidate(rules, { passwordOld, password, passwordTwo });

    const getInfo = computed(() => getters["profile/info/getInfo"]);

    onMounted(() => {
      setTimeout(() => {
        changeEmail.value = getInfo.value?.email;
        password.value = "";
        passwordTwo.value = "";
      }, 300);

      secret.value = route.params.secret || "";
    });

    const closeModal = () => {
      modalShow.value = false;
    };

    /* const openModal = async (id) => {
       if (id === "email") {
         await auth
           .requestChangeEmailLink({ email: changeEmail.value })
           .then(() => {
             modalText.value = `${translationsLang.settingModalEmailChange} ${changeEmail.value}`;
             modalShow.value = true;
             setTimeout(() => {
               dispatch("auth/api/logout").finally(() => {
                 router.push("/login");
               });
             }, 3000);
           });
       }
    
      if (id === "password") {
         if (v$.$invalid) {
           v$.$touch();
           return;
         }
         if (password.value === passwordTwo.value) {
           await auth
             .requestChangePasswordLink({
               oldPassword: passwordOld.value,
               newPassword1: password.value,
               newPassword2: passwordTwo.value,
             })
             .then(() => {
               modalText.value = `${translationsLang.settingModalPasswordChange}`;
               modalShow.value = true;
             });
         }
       }
     };*/

    const openModal = async (id) => {
      if (id === "email") {
        await auth
            .requestChangeEmailLink({ email: changeEmail.value })
            .then(() => {
              modalText.value = `${translationsLang.value.settingModalEmailChange} ${changeEmail.value}`;
              modalShow.value = true;
              setTimeout(() => {
                dispatch("auth/api/logout").finally(() => {
                  router.push("/login");
                });
              }, 3000);
            });
      }

      if (id === "password") {
        // Проверка валидации
        if (v$.$invalid) {
          v$.$touch();
          return;
        }

        console.log({
          oldPassword: passwordOld.value,
          newPassword1: password.value,
          newPassword2: passwordTwo.value,
        });

        await auth
            .requestChangePasswordLink({
              oldPassword: passwordOld.value,
              newPassword1: password.value,
              newPassword2: passwordTwo.value,
            })
            .then(() => {
              modalText.value = `${translationsLang.value.settingModalPasswordChange}`;
              modalShow.value = true;
            });
      }
    };



    // const passwordSet = () => {
    //   dispatch('profile/account/passwordSet');
    // };

    return {
      modalShow,
      modalText,
      changeEmail,
      passwordOld,
      password,
      passwordTwo,
      translationsLang,
      v$,
      getInfo,
      closeModal,
      openModal,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';


.settings-security__block
  background ui-cl-color-white-theme
  box-shadow box-shadow-main
  display flex
  flex-direction column
  width 100%
  padding 30px
  font-size font-size-downdefault
  border-radius border-big-radius

  &+&
    margin-top 20px

.settings-security__mail
  margin-bottom 30px

.settings-security__btn
  display block
  min-width 180px
  max-width 180px
  color ui-cl-color-eucalypt
  border-radius border-small
  text-align center
  background ui-cl-color-white-theme
  border 1px solid ui-cl-color-eucalypt
  font-size font-size-small-medium
  padding 10px
  @media (any-hover: hover)
    &:hover
      background ui-cl-color-eucalypt
      color ui-cl-color-white-theme

.settings-security__title
  color ui-cl-color-full-black
  margin-bottom 15px
  font-family 'Exo', Arial, sans-serif
  font-size 24px
  font-weight font-weight-bold

.form__input_stylus
  color ui-cl-color-full-black

.settings-security__value
  display block
  width 100%
  color ui-cl-color-767676
  border-radius border-small
  background ui-cl-color-white-theme
  border 1px solid ui-cl-color-ababab
  font-size font-size-small-medium
  padding 10px 15px
  margin-bottom 15px

.change-password__action
  margin-top -15px
</style>
