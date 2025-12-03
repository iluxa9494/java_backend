export default [
  {
    path: '/login',
    name: 'Login',
    meta: {
      layout: 'form',
      requiresAuth: false,
      notWithoutAuth: true,
      title: 'Авторизация',
    },
    component: () => import('@/pages/Auth/Login.vue'),
  },
  {
    path: '/registration',
    name: 'Registration',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Регистрация',
    },
    component: () => import('@/pages/Auth/Registration.vue'),
  },
  {
    path: '/registration-success',
    name: 'RegisterSuccess',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Успешная регистрация',
    },
    component: () => import('@/pages/Auth/RegisterSuccess.vue'),
  },
  {
    path: '/forgot',
    name: 'Forgot',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Востановление',
    },
    component: () => import('@/pages/Auth/Forgot.vue'),
  },
  {
    path: '/forgot-success',
    name: 'ForgotSuccess',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Успешное востановление',
    },
    component: () => import('@/pages/Auth/ForgotSuccess.vue'),
  },
  {
    path: '/api/v1/auth/password/recovery/:secret',
    redirect: '/change-password/:secret',
  },
  {
    path: '/change-password/:secret',
    name: 'ChangePassword',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Изменить пароль',
    },
    component: () => import('@/pages/Auth/ChangePassword.vue'),
  },
  {
    path: '/change-password-success',
    name: 'ChangePasswordSuccess',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Успешное изменение пароля',
    },
    component: () => import('@/pages/Auth/ChangePasswordSuccess.vue'),
  },
];
