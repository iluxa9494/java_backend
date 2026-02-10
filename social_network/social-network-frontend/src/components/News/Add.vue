<template>
  <div class="news-add" :class="{ open: isOpen }">
    <div class="news-add__mask" v-if="!isOpen" @click.prevent="openForm()">
      <template v-if="getInfo">
        <div class="news-add__pic imgDiv" v-if="user">
          <img
            v-if="getInfo.photo"
            class="avatar"
            :src="getInfo.photo"
            :alt="fullName"
          />

          <div class="avatar" v-else>
            <unknow-user />
          </div>
        </div>
      </template>

      <span class="news-add__placeholder">{{
        translationsLang.newsAddPlaceholder
      }}</span>

      <div class="news-add__block add" @click.prevent="openForm()">
        <add-icon />
      </div>
    </div>

    <add-form v-else @submit-complete="closeForm" @close-form="closeForm" />
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { useStore } from 'vuex';
import useTranslations from '@/composables/useTranslations';
import AddIcon from '@/Icons/AddIcon.vue';
import AddForm from '@/components/News/AddForm.vue';
import UnknowUser from '@/Icons/UnknowUser.vue';

export default {
  name: "NewsAdd",
  components: {
    AddForm,
    AddIcon,
    UnknowUser,
  },
  props: {
    user: Boolean,
  },

  setup() {
    const store = useStore();
    const isOpen = ref(false);
    const { translationsLang } = useTranslations();

    const getInfo = computed(() => store.getters["profile/info/getInfo"]);
    const fullName = computed(
      () => `${getInfo.value.firstName[0]} ${getInfo.value.lastName[0]}`
    );

    const openForm = () => {
      isOpen.value = true;
    };
    const closeForm = () => {
      isOpen.value = false;
    };

    return {
      isOpen,
      translationsLang,
      getInfo,
      fullName,
      openForm,
      closeForm,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

@media (min-width: 320px) and (max-width: 768px)
  .news-add
    &__mask
      padding 10px 20px
      height unset
    &__text-title
      font-size font-size-updefault
      padding-bottom 10px
</style>

<style lang="stylus" scoped>
.avatar
  display flex
  align-items center
  justify-content center
  width 100%
  height 100%
  object-fit cover

.imgDiv
  background-color #bcdfc7
</style>
