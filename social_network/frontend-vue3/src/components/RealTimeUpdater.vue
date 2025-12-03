<script>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useStore } from 'vuex';

const INTERVAL_DIALOG_MS = 2000;
const INTERVAL_NOTIFICATIONS_MS = 5000;

export default {
  setup() {
    const store = useStore();
    const intervalForMessages = ref(null);
    const intervalForNotifications = ref(null);

    const activeDialog = computed(
      () => store.getters['profile/dialogs/activeDialog']
    );

    onMounted(() => {
      intervalForMessages.value = setInterval(() => {
        if (activeDialog.value) {
          store.dispatch(
            'profile/dialogs/loadFreshMessages',
            activeDialog.value.id
          );
        }
      }, INTERVAL_DIALOG_MS);

      intervalForNotifications.value = setInterval(() => {
        // store.dispatch('profile/notifications/apiNotifications');
      }, INTERVAL_NOTIFICATIONS_MS);
    });

    onBeforeUnmount(() => {
      window.clearInterval(intervalForMessages.value);
      window.clearInterval(intervalForNotifications.value);
    });

    return {
      activeDialog,
    };
  },
  
  render: () => null
};
</script>
