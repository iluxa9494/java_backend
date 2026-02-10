import info from './info';
import account from './account';
import friends from './friends';
import feeds from './feeds';
import dialogs from './dialogs';
import comments from './comments';
import notifications from './notifications';

export default {
  namespaced: true,
  modules: {
    info,
    account,
    friends,
    feeds,
    dialogs,
    comments,
    notifications,
  },
};
