<template>
  <div>
    <header-block :is-deleted="false" />
    <div class="delete-account">
      <h3 class="delete-account__title">Ваша учетная запись была удалена.</h3>
      <div class="delete-account__content">
        <p class="delete-account__description">
          Если вы удалили аккаунт самостоятельно в настройках профиля, то
          нажмите "Восстановить аккаунт". Однако, если аккаунт был удален не
          вами, а злоумышленником, рекомендуем сменить пароль на странице
          настроек для обеспечения безопасности вашей учетной записи. Также,
          возможно, ваш аккаунт был удален за нарушение правил платформы. Если у
          вас возникнут какие-либо вопросы или трудности, наши специалисты
          готовы помочь вам. Свяжитесь с нами любым удобным для вас способом, и
          мы сделаем все возможное, чтобы помочь вам вернуться к использованию
          вашего аккаунта. (нет)
        </p>
        <p class="delete-account__description-timer">
          Вы можете восстановить свою страницу до
          <strong>{{ formattedTenDaysFromDeletion }}</strong>
          после чего ваш аккаунт будет удалён окончательно.
        </p>
        <button class="delete-account__btn" @click.prevent="submitHandler">
          Восстановить аккаунт
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import dayjs from "dayjs";
import HeaderBlock from "@/components/MainLayout/Header.vue";

export default {
  name: "DeleteAccount",
  components: {
    HeaderBlock,
  },

  props: {
    info: Object,
  },

  setup(props) {
    const store = useStore();
    const router = useRouter();

    const tenDaysFromDeletion = computed(() =>
      dayjs(props.info.deletionTimestamp).add(1, "days")
    );
    const formattedDeletionTimestamp = computed(() =>
      dayjs(props.info.deletionTimestamp).format("DD.MM.YYYY HH:mm")
    );
    const formattedTenDaysFromDeletion = computed(() =>
      tenDaysFromDeletion.value.format("DD.MM.YYYY HH:mm")
    );

    const submitHandler = async () => {
      await store
        .dispatch("profile/info/apiChangeInfo", {
          isDeleted: false,
        })
        .then(() => router.push("/profile"));
    };

    return {
      tenDaysFromDeletion,
      formattedDeletionTimestamp,
      formattedTenDaysFromDeletion,
      submitHandler,
    };
  },
};
</script>

<style lang="stylus">
.delete-account
  max-width 1250px
  margin 0 auto
  &__title
    text-align center
    font-size 25px
    margin-bottom 20px
  &__btn
    display block
    background-color #d1d1d1
    padding 10px
    border-radius 10px
    margin 0 auto
    transition all .2s ease-in-out
    @media (any-hover: hover)
      &:hover
        background-color #939191
        color #fff
  &__description
    margin-bottom 20px
    &-timer
      text-align center
      margin-bottom 20px
</style>
