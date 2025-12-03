/* eslint-disable no-param-reassign */
const touppercase = {
  update(el, binding) {
    const sourceValue = el.value;
    const maxLength = parseInt(binding.value, 10);
    el.maxLength = maxLength;

    const newValue = sourceValue
      .toLowerCase() // все вводимые знаки с маленькой буквы.
      .replace(/ /g, '_') // заменяем пробелы на нижние подчёркивания. Подмена автоматический.
      .replace(/[^a-zA-Zа-яА-ЯёЁ0-9_]/g, '') // убираем знаки препиния, кирилица/латиница/0-9 - разрешены.
      // ограничиваем колличество вводимых знаков, дублируя ограничение атрибутом.
      .substring(0, maxLength);

    if (sourceValue !== newValue) {
      el.value = newValue;
      el.dispatchEvent(new Event('input', { bubbles: true }));
    }
  },
};

export default touppercase;
