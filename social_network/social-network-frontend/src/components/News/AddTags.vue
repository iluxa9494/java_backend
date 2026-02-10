<template>
  <div class="add-tags" ref="searchBoxRef">
    <div class="add-tags__form">
      <input
        class="add-tags__input"
        type="text"
        :placeholder="translationsLang.newsTagPlaceholder"
        v-model="tagsList"
        @input="searchTags"
        @keydown.enter="addTag(tag)"
        v-touppercase="10"
        ref="searchInputRef"
      />

      <div v-if="tagsList.length > 0" class="add-tags__btn" @click="addTag(tag)">
        <img class="accept" src="@/assets/static/img/add.svg" />
      </div>
    </div>

    <div class="add-tags__block">
      <div
        class="add-tags__item"
        v-for="(tag, index) in updateTags"
        :key="index"
      >
        #{{ tag.name }}
        <span
          class="add-tags__delete"
          @keydown="deleteTag(index)"
          @click="deleteTag(index)"
        >
          &#10005;
        </span>
      </div>
    </div>
    <transition name="fade">
      <div
        class="add-tags__search-results fade-in"
        v-if="searchResults.length > 0 || itemTag.length === 0"
      >
        <div
          class="add-tags__search-item"
          v-for="(result, index) in searchResults"
          :key="index"
          @keydown="addTagFromSearch(result)"
          @click="addTagFromSearch(result)"
        >
          #{{ result.name }}
          <progress-tag v-if="index < 2" />
          <progress-tag v-else :stroke-color="`#D69A02`" />
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from "vue";
import { useStore } from "vuex";
import axios from "axios";
import { debounce } from "lodash";
import useTranslations from "@/composables/useTranslations";
import touppercase from "@/directives/touppercase";
import ProgressTag from "@/Icons/ProgressTag.vue";

export default {
  name: "AddTags",

  components: {
    ProgressTag,
  },

  directives: {
    touppercase,
  },

  props: {
    tags: Array,
    allowManualAddition: {
      type: Boolean,
      default: true,
    },
  },

  setup(props, { emit }) {
    const store = useStore();
    const tagsList = ref([]);
    const itemTag = ref("");
    const searchResults = ref([]);
    const searchBoxRef = ref(null);
    const searchInputRef = ref(null);

    const { translationsLang } = useTranslations();

    const updateTags = computed(() => props.tags);

    const deleteTag = (index) => {
      tagsList.value = props.tags.filter((tag) => tag !== props.tags[index]);
      emit("change-tags", tagsList.value);
    };

    const searchTagsApi = (tag) => {
      axios
        .get(`/tag?name=${tag}`)
        .then((response) => {
          searchResults.value = response.data.slice(0, 5);
        })
        .catch((error) => console.error(error));
    };

    const debouncedSearchTags = debounce(() => {
      if (itemTag.value.length < 2) {
        searchResults.value = [];
        return;
      }
      searchTagsApi(itemTag.value);
    }, 1000);

    const addTagFromSearch = (tag) => {
      if (tagsList.value.find((t) => t.name === tag.name)) {
        store.dispatch("global/alert/setAlert", {
          status: "response",
          text: "Такой тег уже есть",
        });
        return;
      }
      if (tagsList.value.length >= 10) {
        store.dispatch("global/alert/setAlert", {
          status: "response",
          text: "Можно добавить максимум 10 тэгов",
        });
        console.log("Я сработал в методе addTagFromSearch");
        return;
      }
      itemTag.value = ""; // очищаем поле ввода
      tagsList.value.push(tag);
      searchResults.value = [];
      emit("change-tags", tagsList.value);
      nextTick(() => {
        searchInputRef.value.focus();
      });
    };

    const addTag = () => {
      if (!props.allowManualAddition) return;
      if (itemTag.value.length <= 0) return;
      const existingTag = props.tags.find((tag) => tag.name === itemTag.value);
      if (existingTag) {
        addTagFromSearch(existingTag);
        return;
      }

      if (tagsList.value.some((tag) => tag.name === itemTag.value)) {
        store.dispatch("global/alert/setAlert", {
          status: "response",
          text: "Тэг уже добавлен",
        });
        return;
      }

      if (tagsList.value.length >= 10) {
        store.dispatch("global/alert/setAlert", {
          status: "response",
          text: "Можно добавить максимум 10 тэгов",
        });
        return;
      }

      const newTag = {
        name: itemTag.value,
      };
      console.log("Я сработал в методе addTag()");
      tagsList.value.push(newTag);
      itemTag.value = "";
      emit("change-tags", tagsList.value);
      nextTick(() => {
        searchInputRef.value.focus();
      });
    };

    const handleClickOutside = (event) => {
      const isChild = searchBoxRef.value.contains(event.target);
      if (!isChild) {
        itemTag.value = "";
        searchResults.value = [];
      }
    };

    onMounted(() => {
      setTimeout(() => {
        tagsList.value = props.tags;
      }, 200);
      document.addEventListener("click", handleClickOutside);
    });

    onBeforeUnmount(() => {
      document.removeEventListener("click", handleClickOutside);
    });

    return {
      tagsList,
      itemTag,
      searchResults,
      searchBoxRef,
      searchInputRef,
      translationsLang,
      updateTags,
      addTagFromSearch,
      deleteTag,
      debouncedSearchTags,
      addTag,
    };
  },
};
</script>

<style lang="stylus">
@import '@/assets/stylus/base/vars.styl';
.add-tags
  position relative

.add-tags__search-results
  position: absolute
  top 24px
  left 0
  background-color #fbfbfb
  border-radius: 0 0 border-super-small border-super-small
  width 86%
  max-height 250px
  overflow-y auto
  box-shadow 0px 4px 4px rgba(0, 0, 0, 0.15)
  transition all .2s ease-in-out
  z-index 100

.add-tags__search-item
  display flex
  justify-content space-between
  align-items center
  font-family "Open Sans"
  padding 10px
  cursor pointer
  font-size font-size-small
  transition background-color .2s ease-in-out
  @media (any-hover: hover)
    &:hover
      background-color ui-cl-color-white-bright-second

.add-tags__form
  display flex

.add-tags__input
  border-bottom 1px solid rgba(0, 0, 0, 0.12)
  padding-bottom 5px
  font-size font-size-small
  color ui-cl-color-steel-gray
  margin-bottom 15px
  margin-right 20px
  width 130px
  &::placeholder
    color #B0B0BC

.add-tags__btn path
  transition fill .3s
  
.add-tags__btn
  width 20px
  cursor pointer
  &:hover path
    fill #21a45d
  
.add-tags__block
  margin 0 -4px 10px
  min-height 32px

.add-tags__item
  display inline-block
  align-items center
  color ui-cl-color-eucalypt
  font-size font-size-small
  background-color #F5F7FB
  padding 5px
  margin 0 5px 10px
  border-radius border-super-small

.add-tags__delete
  margin-left 5px
  color #B0B0BC
  font-size font-size-super-upsmall
  font-weight font-weight-bold
  cursor pointer

.fade-enter-active, .fade-leave-active
  transition opacity .5s

.fade-enter, .fade-leave-to
  opacity: 0
</style>
