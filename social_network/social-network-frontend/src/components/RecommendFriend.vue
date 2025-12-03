<template>
  <div class="recommend-block">
    <h3 class="recommend-block__title">
      {{ translationsLang.recommendBlockTitle }}
    </h3>
    <ul v-if="recomendationUsers.length !== 0" class="recommend-block__list">
      <li
        class="recommend-block__item"
        v-for="user in sortedUsers"
        :key="user?.id"
      >
        <div class="recommend-block__item-info">
          <div class="recommend-block__rating">
            <star-icon />
            <span class="recommend-block__rating-number">{{
              getFriendRating(user.id)
            }}</span>
          </div>
          <div class="recommend-block__img-container">
            <img
              v-if="user.photo"
              class="recommend-block__img"
              :src="user?.photo"
              :alt="user?.firstName"
            />
            <span v-else>
              <unknow-user />
            </span>
          </div>

          <router-link
            class="recommend-block__name"
            :to="{ name: 'ProfileId', params: { id: user?.id } }"
          >
            {{ user?.firstName + " " + user?.lastName }}
          </router-link>
        </div>
        <a
          class="recommend-block__button"
          href="#"
          @click.prevent="
            apiAddFriends({ id: user?.id, statusCode: user?.statusCode })
          "
          >{{ translationsLang.recommendBlockAdd }}</a
        >
      </li>
    </ul>
    <div v-else class="recommend-block__not">
      <p>
        {{ translationsLang.recommendBlockDescription }}
      </p>
      <router-link
        class="recommend-block__search"
        href="#"
        :to="{ name: 'FriendsFind' }"
      >
        {{ translationsLang.recommendBlockBtn }}
      </router-link>
    </div>
  </div>
</template>

<script>
import { computed, onBeforeMount } from "vue";
import { useStore } from "vuex";
import UnknowUser from "@/Icons/UnknowUser.vue";
import StarIcon from "@/Icons/StarIcon.vue";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "FriendsPossible",

  components: {
    UnknowUser,
    StarIcon,
  },

  setup() {
    const { getters, dispatch } = useStore();
    const { translationsLang } = useTranslations();

    const possibleFriends = computed(() =>
      getters["profile/friends/getResultById"]("recommendations")
    );
    const recomendationUsers = computed(
      () => getters["global/search/getRecomendationUsers"]
    );
    const getUsersQueryParamsRecomend = computed(
      () => getters["global/search/getUsersQueryParamsRecomend"]
    );

    const sortedUsers = computed(() => {
      const usersCopy = [...recomendationUsers.value.content];
      return usersCopy.sort((a, b) => {
        const ratingA = getFriendRating(a.id);
        const ratingB = getFriendRating(b.id);
        return ratingB - ratingA;
      });
    });
    const getIdsPossibleFriends = computed(() => {
      if (possibleFriends.value) {
        return possibleFriends.value.map((friend) => friend.friendId);
      } else {
        return null;
      }
    });

    onBeforeMount(() => {
      setTimeout(async () => {
        await dispatch("profile/friends/apiRecommendations");
        onSearchUsers();
      }, 5000);
    });

    const getFriendRating = (userId) => {
      const friend = possibleFriends.value.find((f) => f.friendId === userId);
      return friend ? friend.rating : "";
    };

    const onSearchUsers = () => {
      const searchQuery = {
        ...getUsersQueryParamsRecomend.value,
        ids: getIdsPossibleFriends.value,
      };
      dispatch("global/search/searchRecomendations", { payload: searchQuery });
    };

    const apiAddFriends = ({ id, statusCode }) => {
      if (statusCode === null) {
        statusCode = "";
        return dispatch("profile/friends/apiAddFriends", { id, statusCode });
      }

      return dispatch("profile/friends/apiAddFriends", { id, statusCode });
    };

    return {
      translationsLang,
      recomendationUsers,
      sortedUsers,
      getFriendRating,
      apiAddFriends,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

  .recommend-block
    min-width 430px
    height max-content
    padding 30px
    background ui-cl-color-white-theme
    box-shadow 0px 2px 8px rgba(0, 0, 0, 0.08)
    border-radius border-small

    &__rating
      display flex
      align-items center
      flex-direction row
      gap 5px

    &__rating-number
      font-weight font-weight-bold
      font-size font-size-super-medium-small


    &__img-container
      display flex
      align-items center
      justify-content center
      width 50px
      height 50px
      border-radius 50%
      overflow hidden
      flex none
      background-color #8bc49e
      img
        display flex
        align-items center
        justify-content center
        width 100%
        height 100%
        -o-object-fit cover
        object-fit cover
        margin-right 0

    &__not
      display flex
      flex-direction column
      align-items center
      p
        margin-bottom 15px
        line-height 120%

    &__title
      font-size 24px
      font-weight font-weight-bold
      padding-bottom 25px
      border-bottom 1px solid ui-cl-color-e6e6e6
      margin-bottom 15px

    &__list
      display flex
      flex-direction column

    &__item
      display flex
      align-items center
      padding-bottom 15px
      justify-content space-between

      &-info
        position relative
        display flex
        align-items center
        gap 7px

      &:not(nth-child(1))
        padding-top 15px

      &:not(:last-child)
        border-bottom 1px solid ui-cl-color-e6e6e6
        margin-bottom 15px
      &:last-child
        padding-bottom 0

    &__img
      border-radius border-half
      width 50px
      height 50px
      margin-right 5px

    &__name
      font-weight font-weight-medium
      font-size font-size-downdefault
      line-height 20px

    &__button
      background-color ui-cl-color-eucalypt
      padding 3px 5px
      font-size font-size-small-medium
      border-radius border-super-small
      line-height 21px
      color ui-cl-color-white-theme
      transition all .2s ease-in-out
      @media (any-hover: hover)
        &:hover
          background-color ui-cl-color-gun-powder

    &__search
      display block
      text-align center
      color ui-cl-color-eucalypt
      width 100%
      padding 18px 5px
      border 2px solid ui-cl-color-eucalypt
      border-radius border-super-small
      transition all .2s ease-in-out
      background transparent
      @media (any-hover: hover)
        &:hover
          background ui-cl-color-eucalypt
          border-color ui-cl-color-eucalypt
          color ui-cl-color-white-theme
</style>
