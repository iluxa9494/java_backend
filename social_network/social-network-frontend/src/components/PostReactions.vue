<template>
  <div class="post-reactions">
    <div class="post-reactions__likes">
      <span
        v-for="(react, index) in localReactionsInfo"
        :key="index"
        :id="react.reactionType"
        v-on="
          localReaction === react.reactionType ? { click: deletedReaction } : {}
        "
        class="post-reactions__selected"
        :class="{
          selected: react.reactionType == isSelect,
          added: react.reactionType == isSelect && addedClass == true,
          deleted: react.reactionType == isSelect && deletedClass == true,
        }"
      >
        <img
          :src="`/static/img/post-reactions/${react.reactionType}.gif`"
          :alt="react.reactionType"
          ref="reactionImg"
        />
        <span class="post-reactions__quantity">
          {{ react.count }}
        </span>
      </span>
      <span
        v-if="myReactionStatus"
        @click="deletedReaction"
        class="post-reactions__selected selected"
        :class="{ added: addedClass === true, deleted: deletedClass === true }"
      >
        <img
          :src="`/static/img/post-reactions/${myCurrentReaction.reactionType}.gif`"
          :alt="myCurrentReaction.reactionType"
          ref="reactionImg"
        />
        <span class="post-reactions__quantity">
          {{ myCurrentReaction.count }}
        </span>
      </span>
    </div>
    <div class="post-reactions__heart">
      <div class="post-reactions__selected" @mouseover="showReactions = true">
        <like-icon /><b class="like-icon__add">+</b>
      </div>
      <div
        class="post-reactions__reactions"
        @mouseleave="showReactions = false"
      >
        <div
          v-for="(reaction, index) in reactions"
          :key="index"
          class="post-reactions__reaction"
          @click="addReaction(reaction.type)"
        >
          <img
            :title="reaction.label"
            :src="`/static/img/post-reactions/${reaction.type}.gif`"
            :alt="reaction.label"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted, reactive, ref, watch } from "vue";
import LikeIcon from "@/Icons/LikeIcon.vue";

export default {
  components: {
    LikeIcon,
  },
  props: {
    active: null,
    reactionsInfo: {
      type: Array,
      default: () => [],
    },
    quantity: {
      type: Number,
      required: true,
    },
    reaction: String,
  },

  setup(props, { emit }) {
    const reactions = reactive([
      { type: "heart", label: "Нравится" },
      { type: "funny", label: "Смешно" },
      { type: "wow", label: "Ого!" },
      { type: "delight", label: "Восторг" },
      { type: "sadness", label: "Печаль" },
      { type: "malice", label: "&$#%!" },
    ]);
    const showReactions = ref(true);
    const localQuantity = ref(1);
    const localActive = ref(null);
    const selectedReaction = ref(null);
    const localReaction = ref(props.reaction);
    const quantity = ref(props.quantity);
    const active = ref(props.active);

    const localReactionsInfo = ref(
      JSON.parse(JSON.stringify(props.reactionsInfo))
    );

    const myCurrentReaction = reactive({
      reactionType: props.reaction,
      count: 1,
    });
    const myNewReaction = ref(null);

    const prevSelected = ref(null);
    const currentSelected = ref(null);

    const myReactionStatus = ref(null);
    const addedClass = ref(null);
    const deletedClass = ref(null);

    const isSelect = computed(() => localReaction.value);

    watch(quantity, (newVal) => {
      localQuantity.value = newVal;
    });

    watch(active, (newVal) => {
      localActive.value = newVal;
    });

    onMounted(() => {
      localActive.value = active.value;
    });

    const addReaction = (reactionType) => {
      selectedReaction.value = reactionType;

      addedClass.value = true;

      setTimeout(() => {
        addedClass.value = false;
      }, 300);

      if (
        myCurrentReaction.reactionType !== null &&
        myCurrentReaction.reactionType !== ""
      ) {
        console.log("1)Уже стоит реакция");

        if (
          localReactionsInfo.value.filter(
            (type) => type.reactionType === selectedReaction.value
          ).length === 0
        ) {
          console.log("1.1)Замена");
          myReactionStatus.value = true;

          for (const reaction of localReactionsInfo.value) {
            if (prevSelected.value === reaction.reactionType) {
              reaction.count -= 1;
              prevSelected.value = null;
            }

            if (myCurrentReaction.reactionType === reaction.reactionType) {
              reaction.count -= 1;
              localReaction.value = null;

              if (reaction.count === 0) {
                localReactionsInfo.value.splice(
                  localReactionsInfo.value.indexOf(reaction),
                  1
                );
              }
            }
          }

          myNewReaction.value = {
            reactionType: selectedReaction.value,
            count: 1,
          };

          localReaction.value = null;
          myCurrentReaction.value = { ...myNewReaction.value };
          emit("reaction-added", reactionType);
        } else {
          console.log("1.2)Меняем кол-во");
          myReactionStatus.value = false;
          localQuantity.value = 1;

          for (const reaction of localReactionsInfo.value) {
            if (selectedReaction.value === localReaction.value) return;

            if (prevSelected.value === reaction.reactionType) {
              reaction.count -= 1;
            }

            if (myCurrentReaction.reactionType === reaction.reactionType) {
              reaction.count -= 1;
              if (reaction.count === 0) {
                localReactionsInfo.value.splice(
                  localReactionsInfo.value.indexOf(reaction),
                  1
                );
              }
            }

            setTimeout(() => {
              if (selectedReaction.value === reaction.reactionType) {
                reaction.count += 1;
                prevSelected.value = selectedReaction.value;
                localReaction.value = selectedReaction.value;
              }
            }, 0);
          }
          emit("reaction-added", reactionType);
        }
      } else {
        console.log("2)Нету реакции");
        if (
          localReactionsInfo.value.filter(
            (type) => type.reactionType === selectedReaction.value
          ).length >= 1
        ) {
          console.log("2.1)Меняем кол-во");
          for (const reaction of localReactionsInfo.value) {
            currentSelected.value = selectedReaction.value;
            if (prevSelected.value === reaction.reactionType) {
              reaction.count -= 1;
            }

            setTimeout(() => {
              if (currentSelected.value === reaction.reactionType) {
                reaction.count += 1;
                prevSelected.value = currentSelected;
                localReaction.value = selectedReaction.value;
              }
            }, 0);
          }
          emit("reaction-added", reactionType);
        } else {
          console.log("2.2)Добавляем новую");

          myReactionStatus.value = true;

          for (const reaction of localReactionsInfo.value) {
            if (prevSelected.value === reaction.reactionType) {
              reaction.count -= 1;
              prevSelected.value = selectedReaction.value;
            }
          }

          myNewReaction.value = {
            reactionType: selectedReaction.value,
            count: 1,
          };

          myCurrentReaction.value = { ...myNewReaction };
          console.log(reactionType);
          emit("reaction-added", reactionType);
        }
      }
    };

    const deletedReaction = (reactionType) => {
      deletedClass.value = true;
      console.log(deletedClass.value);
      setTimeout(() => {
        for (const reaction of localReactionsInfo.value) {
          if (
            myCurrentReaction.reactionType === reaction.reactionType &&
            localQuantity.value !== null
          ) {
            reaction.count -= 1;
            localQuantity.value = null;
            if (reaction.count === 0) {
              localReactionsInfo.value.splice(
                localReactionsInfo.value.indexOf(reaction),
                1
              );
            }
            myCurrentReaction.reactionType = null;
          }

          if (prevSelected.value === reaction.reactionType) {
            reaction.count -= 1;
            prevSelected.value = null;
          }
        }

        localReaction.value = null;

        if (myReactionStatus.value) {
          myReactionStatus.value = false;
        }

        emit("update:reaction-deleted", reactionType);
        deletedClass.value = false;
      }, 300);
    };

    const getReactionCount = (reactionType) => {
      const reaction = props.reactionsInfo.find(
        (r) => r.reactionType === reactionType
      );
      return reaction ? reaction.count : 0;
    };

    return {
      reactions,
      showReactions,
      localReaction,
      localReactionsInfo,
      myCurrentReaction,
      myReactionStatus,
      addedClass,
      deletedClass,
      isSelect,
      addReaction,
      deletedReaction,
      getReactionCount,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';

.post-reactions
  display flex
  align-items center
  cursor pointer
  height 32px
  padding 4px 4px
  transition all .2s ease-in-out
  gap 5px

.post-reactions__label
  font-size 10px

.post-reactions__heart
  position relative
  cursor pointer

.post-reactions__reactions
  position absolute
  bottom -1px
  left -10px
  background-color #fff
  padding 5px
  border-radius 16px
  box-shadow 0px 2px 8px rgba(0,0,0,0.3)
  margin-left 10px
  display flex
  flex-direction column-reverse
  gap 5px
  width 100%
  opacity 0
  visibility: hidden

.post-reactions__heart .post-reactions__reactions
  transition all .3s ease-in-out

.post-reactions__heart:hover .post-reactions__reactions
  visibility: visible
  opacity 1

.post-reactions__reaction
  display flex
  flex-direction column
  align-items center
  cursor pointer
  transition all .2s ease-in-out
  &:hover
    img
      transition all .2s ease-in-out
      transform scale(1.3)

.added
  -webkit-animation: grow .3s;
  animation: grow .3s;
.deleted
  -webkit-animation: shrink .3s;
  animation: shrink .3s;

@-webkit-keyframes grow
  0%
    -webkit-transform: scale(0.0, 0.0)
    transform: scale(0.0, 0.0)
  25%
    -webkit-transform: scale(0.5, 0.5)
    transform: scale(0.5, 0.5)
  50%
    -webkit-transform: scale(1.0, 1.0)
    transform: scale(1.0, 1.0)
  75%
    -webkit-transform: scale(1.3, 1.3)
    transform: scale(1.3, 1.3)
  100%
    -webkit-transform: scale(1.0, 1.0)
    transform: scale(1.0, 1.0)

@keyframes grow
  0%
    -webkit-transform: scale(0.0, 0.0)
    transform: scale(0.0, 0.0)
  25%
    -webkit-transform: scale(0.5, 0.5)
    transform: scale(0.5, 0.5)
  50%
    -webkit-transform: scale(1.0, 1.0)
    transform: scale(1.0, 1.0)
  75%
    -webkit-transform: scale(1.3, 1.3)
    transform: scale(1.3, 1.3)
  100%
    -webkit-transform: scale(1.0, 1.0)
    transform: scale(1.0, 1.0)

@-webkit-keyframes shrink
  0%
    -webkit-transform: scale(1.0, 1.0)
    transform: scale(1.0, 1.0)

  25%
    -webkit-transform: scale(1.3, 1.3)
    transform: scale(1.3, 1.3)

  50%
    -webkit-transform: scale(1.0, 1.0)
    transform: scale(1.0, 1.0)

  75%
    -webkit-transform: scale(0.5, 0.5)
    transform: scale(0.5, 0.5)

  100%
    -webkit-transform: scale(0.0, 0.0)
    transform: scale(0.0, 0.0)

@keyframes shrink
  0%
    -webkit-transform: scale(1.0, 1.0)
    transform: scale(1.0, 1.0)

  25%
    -webkit-transform: scale(1.3, 1.3)
    transform: scale(1.3, 1.3)

  50%
    -webkit-transform: scale(1.0, 1.0)
    transform: scale(1.0, 1.0)

  75%
    -webkit-transform: scale(0.5, 0.5)
    transform: scale(0.5, 0.5)

  100%
    -webkit-transform: scale(0.0, 0.0)
    transform: scale(0.0, 0.0)

.post-reactions__reaction img
  width 40px
  height 30px

.post-reactions__likes
  display flex
  align-items center
  margin-right 20px
  gap 5px

.post-reactions__quantity
  font-size 15px
  font-weight 500
  color #818c99

.post-reactions__selected
  padding 5px 10px
  background-color #F0F2F5
  border-radius 32px
  &:hover
    color ui-cl-color-wild-watermelon
    img
      transition all .2s ease-in-out
      transform scale(1.3)

.post-reactions__selected img
  width 24px
  height 24px

.selected
  background-color #8bc49e
  &:hover
    background-color #21a45d

.selected .post-reactions__quantity
  color white

.post-reactions
  &--wow,
  &--funny,
  &--delight
    background-color #FFF2D6
  &--sadness
    background-color #EDF3FA
  &--malice
    background-color #FDEBE8
  &--heart
    background-color #FFEDED

  .like-icon__add
    color #818c99
</style>
