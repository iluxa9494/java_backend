<template>
  <div class="admin-edit inner-page admin">
    <div class="admin__title">Добавить модератора/администратора</div>

    <form class="admin-edit__form" action="#" @submit.prevent="">
      <user-info-form-block label="Имя:" v-model="name" />
      <user-info-form-block label="E-Mail:" v-model="email" />
      <user-info-form-block label="Пароль:" v-model="password" />
      <user-info-form-block label="Повторите пароль:" v-model="passwordTwo" />

      <div class="user-info-form__block">
        <span class="user-info-form__label_stylus">Тип</span>
        <div class="user-info-form__wrp"></div>
      </div>
    </form>
  </div>
</template>

<script>
import { ref } from 'vue';
import UserInfoFormBlock from '@/components/Settings/UserInfoForm/Block.vue';

export default {
  name: 'AdminEdit',
  components: { UserInfoFormBlock },

  setup() {
    const name = ref('');
    const email = ref('');
    const password = ref('');
    const passwordTwo = ref('');

    return {
      name,
      email,
      password,
      passwordTwo,
    };
  },
};
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl'

.admin-edit__form
  max-width 610px
  width 100%
  background #fff
  box-shadow box-shadow-main
  padding 60px 85px 55px 40px

  .user-info-form__block + .user-info-form__block
    margin-top 15px

  .user-info-form__label_stylus
    width 120px
    white-space normal
</style>
