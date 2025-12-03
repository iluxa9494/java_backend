export default {
  install(app) {
    let socket;
    const socketApi = {
      connect() {
        // const serverUrl = 'ws://185.129.146.54:8765/api/v1/streaming/ws';
        // const serverUrl = 'ws://localhost:8765/api/v1/streaming/ws';

        const options = {
          server: '/ws'
        };

        const serverUrl = (window.location.protocol === 'https:' ? 'wss://' : 'ws://') 
        + window.location.host + options.server; // options.server === '/ws'

        console.log(serverUrl);
        socket = new WebSocket(serverUrl);
        console.log('function connect');

        socket.onopen = () => {
          console.log('Websocket connected...');
        };

        socket.onmessage = (event) => {
          let serverMessage = {
            type: 'none'
          };
          let msg = event.data;

          try {
            serverMessage = JSON.parse(msg);
            console.log(serverMessage);
          } catch (e) {
            console.log({ e });
          }
        };

        socket.onerror = (e) => {
          console.log('connect error!!', e);
          console.log('Error code:', e.code);
          console.log('Error message:', e.message);
        };

        socket.onclose = (e) => {
          console.log(`[close] Соединение закрыто чисто, код = ${e.code}, причина = ${e.reason}`, e);
          setTimeout(() => {
            this.connect(); // Попытка повторного подключения через 5 секунд
          }, 5000);
        };
      },

      sendMessage(payload) {
        const message = JSON.stringify(payload);
        console.log(message);
        if (socket && socket.readyState === WebSocket.OPEN) {
          socket.send(message);
        } else {
          console.log('Socket is not open.');
        }
      },

      subscribe(eventType, callback) {
        console.log(eventType);

        socket.onmessage = (event) => {
          let serverMessage = null;
          let msg = event.data;
          try {
            serverMessage = JSON.parse(msg);
            console.log('Сработал в файле websocket.js');
            console.log(serverMessage);
          } catch (e) {
            console.log(e);
          }
          callback(serverMessage);
        };
      }
    };

    app.config.globalProperties.$socket = socketApi;
  }
};
