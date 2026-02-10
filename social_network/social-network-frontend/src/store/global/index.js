import search from './search';
import menu from './menu';
import likes from './likes';
import alert from './alert';
import authors from './authors';
import storage from './storage';
import storagePostPhoto from './storagePostPhoto';
import status from './status';
import geo from './geo';

export default {
  namespaced: true,
  modules: {
    search,
    menu,
    likes,
    alert,
    authors,
    storage,
    storagePostPhoto,
    status,
    geo,
  },
};
