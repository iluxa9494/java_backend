export function getRouteByNotification(notification) {
  switch (notification.notificationType) {
    case 'MESSAGE':
      return { name: 'Im', params: { id: notification } };
    default:
      return {
        name: 'ProfileId',
        params: { id: notification },
      };
  }
}
