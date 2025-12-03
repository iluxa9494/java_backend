export default [
  {
    path: '/admin/login',
    name: 'AdminLogin',
    meta: {
      layout: 'empty',
    },
    component: () => import('@/pages/Admin/Login.vue'),
  },
  {
    path: '/admin/panel/statistics',
    name: 'AdminStatistics',
    meta: {
      layout: 'main',
    },
    component: () => import('@/pages/Admin/Main.vue'),
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    meta: {
      layout: 'main',
    },
    component: () => import('@/pages/Admin/Users.vue'),
  },
  {
    path: '/admin/comments',
    name: 'AdminComments',
    meta: {
      layout: 'main',
    },
    component: () => import('@/pages/Admin/Comments.vue'),
  },
  {
    path: '/admin/posts',
    name: 'AdminPosts',
    meta: {
      layout: 'main',
    },
    component: () => import('@/pages/Admin/Posts.vue'),
  },
  {
    path: '/admin/likes',
    name: 'AdminLikes',
    meta: {
      layout: 'main',
    },
    component: () => import('@/pages/Admin/Likes.vue'),
  },
  {
    path: '/admin/panel/users',
    name: 'UsersPanel',
    meta: {
      layout: 'main',
    },
    component: () => import('@/pages/Admin/AdminPanel/Users.vue'),
  },
  {
    path: '/admin/panel/publications',
    name: 'PublicationPanel',
    meta: {
      layout: 'main',
    },
    component: () => import('@/pages/Admin/AdminPanel/Publications.vue'),
  },
  {
    path: '/admin/panel/comments',
    name: 'CommentsPanel',
    meta: {
      layout: 'main',
    },
    component: () => import('@/pages/Admin/AdminPanel/Comments.vue'),
  },
  {
    path: '/admin/panel/administrators',
    name: 'AdministratorPanel',
    meta: {
      layout: 'main',
    },
    component: () => import('@/pages/Admin/AdminPanel/Administrators.vue'),
  },
];
