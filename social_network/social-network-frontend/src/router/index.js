import { createRouter, createWebHashHistory } from 'vue-router';

import authRoutes from './modules/auth';
import shiftRoutes from './modules/shift';
import userRoutes from './modules/user';
// import adminRoutes from './modules/admin';

const routes = [
  {
    path: '/:catchAll(.*)',
    redirect: {
      name: 'News',
    },
  },
  ...authRoutes,
  ...shiftRoutes,
  ...userRoutes,
  // ...adminRoutes,
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
