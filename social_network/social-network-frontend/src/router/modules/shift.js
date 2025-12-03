export default [
  {
    path: '/shift-password',
    name: 'ShiftPassword',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Изменить пароль',
    },
    component: () => import('@/pages/Shift/ShiftPassword.vue'),
  },
  {
    path: '/shift-email',
    name: 'ShiftEmail',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Изменить Email',
    },
    component: () => import('@/pages/Shift/ShiftEmail.vue'),
  },
  {
    path: '/shift-email-success',
    name: 'ShiftEmailSuccess',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Успешное изменение Email',
    },
    component: () => import('@/pages/Shift/ShiftEmailSuccess.vue'),
  },
  {
    path: '/shift-email-confirm',
    name: 'ShiftEmailConfirm',
    meta: {
      layout: 'form',
      requiresAuth: false,
      title: 'Подтверждение новой Email',
    },
    component: () => import('@/pages/Shift/ShiftEmailConfirm.vue'),
  },
];
