const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: process.env.VUE_APP_PUBLIC_PATH || '/projects/social-network/',

  devServer: {
    // proxy: 'http://localhost:8088',
    // proxy: 'http://89.111.155.206:8080'
    // proxy: 'http://89.111.155.206:8189'
    //  proxy: 'http://212.22.70.159:1337/',
    proxy: 'http://localhost:8765/', // api-gateway для локальной разработки
  },
})
