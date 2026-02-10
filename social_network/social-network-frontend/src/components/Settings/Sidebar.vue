<template>
  <div class="aside-filter">
    <h2 class="aside-filter__title">{{ translationsLang.sidebarSetting }}</h2>

    <ul class="aside-filter__list">
      <li
        class="aside-filter__item"
        v-for="item in list"
        :key="item.component"
        :class="{ active: item.component === value }"
        @click="changeComponent(item)"
      >
        {{ currentTranslations === "Русский" ? item.text : item.textEng }}
      </li>
    </ul>
  </div>
</template>

<script>
import { reactive, computed } from "vue";
import { useStore } from "vuex";
import useTranslations from "@/composables/useTranslations";

export default {
  name: "SettingsSidebar",
  props: {
    value: String,
  },

  setup(props, { emit }) {
    const store = useStore();
    const list = reactive([
      { component: "settings-main", text: "Основные", textEng: "Main" },
      {
        component: "settings-push",
        text: "Настройка оповещений",
        textEng: "Setting up alerts",
      },
      {
        component: "settings-security",
        text: "Безопасность",
        textEng: "Security",
      },
      {
        component: "settings-delete",
        text: "Удалить профиль",
        textEng: "Delete profile",
      },
    ]);
    const { translationsLang } = useTranslations();

    const currentTranslations = computed(
      () => store.state.auth.languages.language.name
    );

    const changeComponent = (item) => {
      emit("change-component", item);
    };

    return {
      list,
      translationsLang,
      currentTranslations,
      changeComponent,
    };
  },
};
</script>
