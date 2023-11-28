import { createStore } from 'vuex'
import { auth } from './auth-module';
import VuexPersist from 'vuex-persist';

const vuexSessionStorage = new VuexPersist({
    key: 'vuex',
    storage: sessionStorage
})

const store = createStore({
    modules: {
        auth
    },
    plugins: [vuexSessionStorage.plugin]
});

export default store;
