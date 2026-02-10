<template>
  <div class="shift-password">
    <form class="shift-password__form" @submit.prevent="submitHandler">
      <div class="success-shift-emaill">
        <img src="@/assets/static/img/success-forgot.svg" alt="img.svg" />
      </div>

      <div class="form__block">
        <h4 class="form__subtitle">
          Для подтверждения новой почты нажимайте на кнопку.
        </h4>
      </div>

      <div class="shift-btn__action">
        <button-hover tag="button" type="submit" variant="white"
          >Подтвердить</button-hover
        >
      </div>
    </form>
  </div>
</template>

<script>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

export default {
  name: "ShiftEmailSuccess",

  setup() {
    const route = useRoute();
    const temp = ref("");

    onMounted(() => {
      temp.value = route.query.temp || "";
    });

    const submitHandler = () => {
      axios({
        url: "/auth/confirm-email ",
        data: { temp: temp.value },
        method: "PUT",
      })
        .then(() => {
          alert("Почта удачно подтвеждена!");
        })
        .catch((error) => {
          alert(
            "Произошла ошибка при подтверждении почты. Пожалуйста, попробуйте снова."
          );
          console.error("Error confirming email:", error);
        });
    };

    return {
      temp,
      submitHandler,
    };
  },
};
</script>

<style lang="stylus">
.shift-password
  display flex
  justify-content center
  flex-direction column
  height 70%

.shift-btn__action
  margin-top 40px
  display flex
  justify-content center
  align-items center

.success-shift-email
  height 100%
  display flex
  justify-content center
  align-items center

  .simple-svg-wrapper
    width 120px
</style>
