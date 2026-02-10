<template>
  <div class="settings-main-input">
    <span class="user-info-form__label_stylus">{{ label }}</span>

    <div class="user-info-form__wrap">
      <textarea
        class="user-info-form__input_stylus user-info-form__input_stylus--textarea"
        :value="modelValue"
        @input="updateValue"
        v-if="about"
        maxlength="200"
      ></textarea>

      <input
        class="user-info-form__input_stylus"
        type="text"
        v-pattern="20"
        :value="modelValue"
        :placeholder="placeholder"
        :required="required"
        @input="updateValue"
        v-if="text"
      />

      <input
        class="user-info-form__input_stylus"
        type="text"
        :value="modelValue"
        :placeholder="placeholder"
        @input="updateValue"
        :ref="phone ? 'phoneRef' : undefined"
        v-if="phone"
      />
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import Inputmask from 'inputmask';
import pattern from '@/directives/pattern';

export default {
  name: 'UserInfoFormBlock',

  directives: {
    pattern: pattern.directives,
  },

  props: {
    label: String,
    placeholder: String,
    modelValue: String,
    phone: Boolean,
    about: Boolean,
    text: String,
    required: Boolean,
  },

  setup(props, { emit }) {
    const phoneRef = ref(null);

    onMounted(() => {
    if (props.phone) {
      var im = new Inputmask('+7 (999) 999-99-99');
      im.mask(phoneRef.value);
    }
    });

    const updateValue = (event) => {
      emit('update:modelValue', event.target.value)
    };

    const validate = () => {
      if(!props.modelValue) {
        return false;
      }
      return true;
    }

    return {
      phoneRef,
      updateValue,
      validate,
    };
  },
};
</script>

<style lang="stylus">
.settings-main-input
  &:not(:last-child)
    margin-bottom 15px
</style>
