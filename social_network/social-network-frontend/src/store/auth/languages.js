export default {
  namespaced: true,

  state: {
    language: JSON.parse(localStorage.getItem('selectedLanguage')) || { id: 1, name: 'Русский', desc: 'RU' },
  },

  mutations: {
    setLanguage(state, lang) {
      state.language = lang
      localStorage.setItem('selectedLanguage', JSON.stringify(lang))
    }
  }
};
