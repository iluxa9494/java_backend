<template>
  <div class="admin__pages">
    <h1 class="admin__title">{{ activeComponent.text }}</h1>
    <div class="admin__pages-container">
      <component :is="activeComponent.component" />
      <admin-side-bar v-model="activeComponent.component" @change-component="onChange" />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import AdminSideBar from './SidebarStatistics.vue';

export default {
  name: 'AdminPanel',
  components: {
    AdminSideBar,
    AdminStatistics: () => import('./Statistics.vue'),
    AdminPosts: () => import('./Posts.vue'),
    AdminLikes: () => import('./Likes.vue'),
    AdminComments: () => import('./Comments.vue'),
    AdminUsers: () => import('./Users.vue'),
  },
  setup() {
    const activeComponent = ref('adminStatistics');
    const activeText = ref('Общая');
    const info = ref(null);

    const onChange = (item) => {
      activeComponent.value = item.component;
      activeText.value = item.text;
    };

    return {
      activeComponent,
      activeText,
      info,
      onChange,
    };
  },
};
</script>

<style lang="stylus">
@import '../../assets/stylus/base/vars.styl'

.loading__info
  font-size 20px
  padding 40px

.statistics-error
  display flex
  flex-direction column
  justify-content center
  align-items center
  padding 40px
  background #fff
  border-radius border-small
  box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.08)
  &__title
    margin 0
    font-size 40px
    font-weight 900
  &__image
    background-size cover
    width 688px
    height 308px
    background-image url('https://mveselovv.github.io/moire-app/img/not-found.744da957.gif')
    background-repeat no-repeat
    background-position top -60px center
    margin-bottom 35px
  &__button
    background transparent
    border 1px solid ui-cl-color-eucalypt
    padding 10px
    transition all .2s ease-in-out
    border-radius border-small
    @media (any-hover: hover)
      &:hover
        background ui-cl-color-eucalypt
        border-color ui-cl-color-eucalypt
        color ui-cl-color-white-theme

.panel
  box-shadow 0px 2px 8px rgba(0, 0, 0, 0.08)
  border-radius border-small
  overflow hidden
  background ui-cl-color-white-theme
.panel__title
  font-family 'Exo 2'
  font-style normal
  font-weight 200
  font-size 30px
  line-height 24px
  margin-bottom 30px

.panel-dynamics__title
  display block
  font-family 'Exo', Arial, sans-serif
  font-weight 700
  font-size 20px
  line-height 30px
  color #575757

.panel-dynamics__title.circle__title
  margin-bottom 20px

.panel-publications__top
  display flex
  align-items center
  max-height 130px
  min-height 130px
  justify-content space-between
  padding 30px
  background ui-cl-color-gun-powder

.panel__graphics
  padding 30px
  &-circle
    display grid
    padding 30px
    grid-template-columns repeat(2, 1fr)
    gap 40px
    &-left
      height 315px
      width 100%

.first__circle
  border-right 2px dashed #ebebeb

.first__graph
  border-bottom 2px dashed #ebebeb

.panel-dynamics__graph
  height 300px

.panel-publications__info
  color ui-cl-color-white-theme
  font-weight font-weight-bold
  span:nth-child(1)
    font-size 20px
    line-height 30px
    margin-right 10px
  span:nth-child(2)
    font-size 39px
    line-height 58px
.main-layout__header.admin
  .main-layout__user-name
    color ui-cl-color-grey-color
  .main-layout__user-post
    color green
.admin
  &__pages
    &-container
      display grid
      grid-template-columns 1000px 1fr
      align-items flex-start
      gap 40px
    &-list
      display grid
      grid-template-columns repeat(2, 1fr)
      gap 17px
    &-item
      display flex
      align-items center
      background #fff
      min-height 145px
      max-height 145px
      border-radius border-small
      overflow hidden
      box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.08)
    &-img
      display flex
      flex-direction column
      justify-content center
      align-items center
      background ui-cl-color-gun-powder
      height 100%
      width 148px
    &-info
      display flex
      flex-direction column
      padding 23px
      font-family 'Open Sans'
    &-title
      font-style normal
      font-weight font-weight-light
      font-size font-size-updefault
      line-height 30px
    &-statistic
      font-weight font-weight-bold
      font-size 40px
      line-height 48px
</style>
