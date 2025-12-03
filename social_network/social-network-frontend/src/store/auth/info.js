export default {
  namespaced: true,
  state: {
    pages: {
      login: {
        title: 'Привет!',
        text:
          'Мы создали это место, чтобы вы смогли развиваться, чувствуя себя максимально комфортно' +
          ' в кругу приятных людей. Это сообщество профессионалов, объединённых одной идеей — с' +
          ' помощью кода, магия станет реальностью!',
        btn: {
          link: 'Registration',
          text: 'Регистрация',
        },
        eng: {
          title: 'Hello!',
          text:
          'We created this place so that you could develop, feeling as comfortable as possible' +
          ' in a circle of nice people. This is a community of professionals united by one idea — with the help of '+
          ' code, magic will become a reality!',
          btn: {
            link: 'Registration',
            text: 'Registration',
          },
        }
      },
      registration: {
        title: 'Быстрая регистрация',
        text: 'Чтобы воспользоваться всеми возможностями сервиса: вам необходимо зарегистрироваться. Это не займёт больше 1 минуты!',
        descr: 'Если вы зарегистрированы, просто войдите в аккаунт',
        btn: {
          link: 'Login',
          text: 'У меня уже есть аккаунт',
          variant: 'dark',
        },
        eng: {
          title: 'Quick registration',
          text: 'To take advantage of all the features of the service: you need to register. It wont take more than 1 minute!',
          descr: 'If you are registered, just log in to your account',
          btn: {
            link: 'Login',
            text: 'I already have an account',
            variant: 'dark',
          },
        }
      },
      'registration-success': {
        title: 'Вы зарегистрированы!',
        text:
          'Ваш аккаунт успешно создан. Чтобы активировать его, вам необходимо нажать на кнопку,' +
          ' вход в систему произойдет автоматически, для этого будут использоваться данные указанные при регистрации.',
        btn: {
          link: 'Login',
          text: 'Войти',
        },
        eng: {
          title: 'You are registered!',
          text:
          'Your account has been successfully created. To activate it, you need to click on the ' +
          ' button to log in automatically, the data specified during registration will be used for this.',
          btn: {
            link: 'Login',
            text: 'Login',
          },
        }
      },
      forgot: {
        title: 'Восстановление пароля',
        text: 'Чтобы восстановить пароль, укажите e-mail, к которому привязана ваша страница. Мы отправим ссылку для восстановаления пароля туда.',
        btn: {
          link: 'Login',
          text: 'Вернуться к авторизации',
          variant: 'dark',
        },
        eng: {
          title: 'Password recovery',
          text: 'To restore the password, specify the e-mail to which your page is linked. We will send a password recovery link there.',
          btn: {
            link: 'Login',
            text: 'Back to authorization',
            variant: 'dark',
          },
        }
      },
      'forgot-success': {
        title: 'На ваш e-mail отправлена ссылка для восстановления пароля',
        text: 'В течение 5 минут, на указанную вами почту, придёт письмо со ссылкой. Перейдите по ней, чтобы восстановить пароль.',
        btn: {
          link: '',
          href: 'login',
          text: 'Перейти в почту',
        },
        eng: {
          title: 'A link for password recovery has been sent to your e-mail',
          text: 'Within 5 minutes, an email with a link will be sent to the email address you specified. Click on it to restore the password.',
          btn: {
            link: '',
            href: 'login',
            text: 'Go to mail',
          }
        }
      },
      'change-password': {
        title: 'Смена пароля',
        text: 'Ваш старый пароль был сброшен. Придумайте новый пароль для входа на сайт.',
        eng: {
          title: 'Password change',
          text: 'Your old password has been reset. Come up with a new password to log in to the site.',
        }
      },
      'change-password-success': {
        title: 'Пароль успешно изменён!',
        text: 'Пароль был успешно изменён, Используйте данный пароль для последующих процессов авторизации.',
        btn: {
          link: 'Login',
          text: 'Войти',
        },
        eng: {
          title: 'The password has been successfully changed!',
          text: 'The password has been successfully changed, use this password for subsequent authorization processes.',
          btn: {
            link: 'Login',
            text: 'Login',
          }
        }
      },
      'shift-password': {
        title: 'Смена пароля',
        text: 'Придумайте новый пароль, впоследсвии вы сможете его сменить.',
        eng: {
          title: 'Password change',
          text: 'Come up with a new password, then you can change it.',
        }
      },
      'shift-email': {
        title: 'Смена почты',
        text: 'Введите новый почтовый ящик',
        eng: {
          title: 'Changing mail',
          text: 'Enter a new mailbox',
        }
      },
      'shift-email-success': {
        title: 'На ваш новый  e-mail  отправлено письмо для подтверждения',
        text: 'В течение 5 минут, на указанную вами почту, придёт письмо со ссылкой. Перейдите по ней, чтобы подтвердить e-mail.',
        btn: {
          link: '',
          text: 'Перейти в почту',
          href: 'login',
        },
        eng: {
          title: 'A confirmation email has been sent to your new e-mail',
          text: 'Within 5 minutes, an email with a link will be sent to the email address you specified. Click on it to confirm the e-mail.',
          btn: {
            link: '',
            text: 'Go to mail',
            href: 'login',
          }
        }
      },
      'shift-email-confirm': {
        title: 'Подтверждения новой почты',
        text: '',
        btn: {
          link: '',
          text: 'Перейти на страницу авторизации',
          href: 'login',
        },
        eng: {
          title: 'New mail confirmations',
          text: '',
          btn: {
            link: '',
            text: 'Go to the authorization page',
            href: 'login',
          },
        }
      },
    },
  },
  getters: {
    getInfoById: (state) => (id) => state.pages[id],
  },
  mutations: {
    setBtn: (state, { pageId, btnId, btnIdValue }) => {
      state.pages[pageId].btn[btnId] = btnIdValue;
    },
  },
  actions: {},
};
