import News from '@/pages/User/News';

export default [
  {
    path: '/',
    name: 'News',
    component: News,
    meta: {
      layout: 'main',
      title: 'Новости',
      titleEng: 'News',
      requiresAuth: true,
    },
  },
  {
    path: '/im/',
    name: 'Im',
    meta: {
      layout: 'main',
      title: 'Сообщения',
      titleEng: 'Messages',
      requiresAuth: true,
    },
    component: () => import('@/pages/User/Im.vue'),
  },
  {
    path: '/im/:activeDialogId',
    name: 'ImChat',
    props: true,
    meta: {
      layout: 'main',
      title: 'Сообщения',
      titleEng: 'Messages',
      requiresAuth: true,
    },
    component: () => import('@/pages/User/Im.vue'),
  },
  {
    path: '/profile',
    name: 'Profile',
    meta: {
      layout: 'main',
      title: 'Моя Страница',
      titleEng: 'My page',
      requiresAuth: true,
    },
    component: () => import('@/pages/User/Profile.vue'),
  },
  {
    path: '/profile/:id',
    name: 'ProfileId',
    meta: {
      layout: 'main',
      title: 'Профиль пользователя',
      titleEng: 'User profile',
      requiresAuth: true,
    },
    component: () => import('@/pages/User/ProfileId.vue'),
  },
  {
    path: '/settings',
    name: 'Settings',
    meta: {
      layout: 'main',
      title: 'Настройки',
      titleEng: 'Settings',
      requiresAuth: true,
    },
    component: () => import('@/pages/User/Settings.vue'),
  },
  {
    path: '/friends',
    name: 'Friends',
    meta: {
      layout: 'main',
      title: 'Друзья',
    },
    component: () => import('@/pages/User/Friends.vue'),
  },
  {
    path: '/friends/find',
    name: 'FriendsFind',
    meta: {
      layout: 'main',
      title: 'Поиск друзей',
      titleEng: 'Search for friends',
    },
    component: () => import('@/pages/User/FriendsFind.vue'),
  },
  {
    path: '/search',
    name: 'Search',
    meta: {
      layout: 'main',
      title: 'Поиск',
      titleEng: 'Search',
    },
    component: () => import('@/pages/User/Search.vue'),
  },
  {
    path: '/push',
    name: 'Push',
    meta: {
      layout: 'main',
      title: 'Уведомления',
      titleEng: 'Notifications',
    },
    component: () => import('@/pages/User/Push.vue'),
  },
];
