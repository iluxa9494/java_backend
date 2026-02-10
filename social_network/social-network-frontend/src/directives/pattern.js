/* eslint-disable no-param-reassign */
const pattern = {
  update(el, binding) {
    const sourceValue = el.value;
    const maxLength = parseInt(binding.value, 10);
    el.maxLength = maxLength;

    const newValue = sourceValue
      .replace(/[^a-zA-Zа-яА-ЯёЁ_]/g, '')
      .substring(0, maxLength);

    if (sourceValue !== newValue) {
      el.value = newValue;
      el.dispatchEvent(new Event('input', { bubbles: true }));
    }
  },
};

export default pattern;
