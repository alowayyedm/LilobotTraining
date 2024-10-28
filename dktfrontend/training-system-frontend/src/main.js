import { createApp } from 'vue';
import App from './App.vue';
import router from './Routes.js';
import config from './config.js';
import store from '../src/authentication/store'
import mitt from 'mitt'

const emitter = mitt();
const app = createApp(App)
    .use(store).use(router);

app.config.globalProperties.emitter = emitter;
app.config.globalProperties.$config = config;
app.mount('#app');