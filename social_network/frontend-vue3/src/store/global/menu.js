export default {
  namespaced: true,
  state: {
    user: [
      {
        link: {
          name: 'Profile',
        },
        exact: true,
        icon: 'profile',
        text: 'Моя страница',
        textEng: 'My page'
      },
      {
        link: {
          name: 'Friends',
        },
        icon: 'friends',
        text: 'Друзья',
        textEng: 'Friends'
      },
      {
        link: {
          name: 'Im',
        },
        icon: 'im',
        text: 'Сообщения',
        textEng: 'Messages'
      },
      {
        link: {
          name: 'News',
        },
        exact: true,
        icon: 'news',
        text: 'Новости',
        textEng: 'News'
      },
    ],
    // admin: [
    //   // Навигация статистика
    //   {
    //     link: {
    //       name: 'AdminStatistics',
    //     },
    //     icon: 'statistics',
    //     text: 'Статистика',
    //   },
    //   {
    //     link: {
    //       name: 'AdminUsers',
    //     },
    //     icon: 'users',
    //     text: 'Люди',
    //   },
    //   {
    //     link: {
    //       name: 'AdminPosts',
    //     },
    //     icon: 'posts',
    //     text: 'Публикации',
    //   },
    //   {
    //     link: {
    //       name: 'AdminComments',
    //     },
    //     icon: 'comments',
    //     text: 'Комментарии',
    //   },
    // ],
    admin: [
      {
        link: {
          name: 'AdminStatistics',
        },
        icon: 'statistics',
        text: 'Статистика',
      },
      {
        link: {
          name: 'UsersPanel',
        },
        icon: 'users',
        text: 'Люди',
      },
      {
        link: {
          name: 'PublicationPanel',
        },
        icon: 'posts',
        text: 'Публикации',
      },
      {
        link: {
          name: 'CommentsPanel',
        },
        icon: 'comments',
        text: 'Комментарии',
      },
      {
        link: {
          name: 'AdministratorPanel',
        },
        icon: 'moderators',
        text: 'Администраторы и модераторы',
      },
    ],
  },
  getters: {
    getSidebarById: (state) => (id) => state[id],
  },
};
