import router from '@/router';
import store from '@/store';

router.beforeEach((to, from, next) => {
  if (to.meta.title) document.title = to.meta.title;
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    // этот путь требует авторизации, проверяем залогинен ли
    // пользователь, и если нет, перенаправляем на страницу логина
    if (!store.getters['auth/api/isAuthenticated']) {
      next({
        name: 'Login',
        query: {
          redirect: to.name,
        },
      });
      return false;
    } else {
      next();
      return false;
    }
  }

  if (
    to.matched.some((record) => record.meta.notWithoutAuth) &&
    store.getters['auth/api/isAuthenticated']
  ) {
    // этот путь только без авторизовации,
    // если пользователь авторизован, то перенаправляем на страницу новостей
    next({
      name: 'News',
    });
    return false;
  }
  next();
});
