<template>
  <div id="app">
    <component :is="layout" v-if="layout && !isDeleted">
      <router-view />
    </component>
    <div class="deleted-account" v-else-if="isDeleted">
      <delete-account :info="info" />
    </div>
  </div>
</template>

<script>
import { reactive, computed, defineAsyncComponent, watch } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import DeleteAccount from "./layouts/DeleteAccount.vue";
import { toast } from "vue3-toastify";
import "vue3-toastify/dist/index.css";

export default {
  name: "App",
  components: {
    FormLayout: defineAsyncComponent(() => import("@/layouts/FormLayout")),
    MainLayout: defineAsyncComponent(() => import("@/layouts/MainLayout")),
    EmptyLayout: defineAsyncComponent(() =>
      import("@/layouts/EmptyLayout.vue")
    ),
    DeleteAccount,
  },
  setup() {
    const store = useStore();
    const route = useRoute();
    const toastifyOptions = reactive({
      position: "bottom-right",
      theme: "light",
      autoClose: 3000,
    });

    const isNotificationQueueEmpty = computed(
      () => store.getters["global/alert/isNotificationQueueEmpty"]
    );
    const getTheme = computed(() => store.getters["global/alert/getTheme"]);
    const alertState = computed(() => store.state.global.alert);
    const info = computed(() => store.state.profile.info);
    const layout = computed(() => route.meta.layout + "-layout");
    const isDeleted = computed(() => info.value && info.value.isDeleted);

    watch(getTheme, (newVal) => {
      toastifyOptions.theme = newVal;
    });

    watch(isNotificationQueueEmpty, (newValue) => {
      if (!newValue) return;

      switch (alertState.value.status) {
        case "success":
          toast.success(alertState.value.text, toastifyOptions);
          break;
        case "response":
          toast.success(alertState.value.text, toastifyOptions);
          break;
        case "action":
          toast.success(alertState.value.text, toastifyOptions);
          break;
        case "validation":
          toast.success(alertState.value.text, toastifyOptions);
          break;
        default:
          toast.error(alertState.value.text, toastifyOptions);
      }
    });

    return {
      isNotificationQueueEmpty,
      alertState,
      info,
      layout,
      isDeleted,
    };
  },
};
</script>

<style lang="stylus">

@import '@/assets/stylus/main.styl';
@import '@/assets/stylus/base/vars.styl';

.vt-notification-container {
  display block !important
}

.v-snack__wrapper
  &.success
    background-color ui-cl-color-eucalypt

  &.error
    background-color ui-cl-color-wild-watermelon

.deleted-account
  display flex
  align-items center
  justify-content center
  width 100%
  height 100vh
</style>

<style lang="css">
@import "../public/css/style.min.css";

::-webkit-scrollbar {
  width: 5px;
}

::-webkit-scrollbar-track {
  background-color: darkgrey;
}

::-webkit-scrollbar-thumb {
  box-shadow: inset 0 0 6px #363636;
  border-radius: 8px;
}
</style>
