import axios from "axios";
import config from '../config.js';

const API_URL = config.agentServer + '/auth';

export const auth = {
    namespaced: true,
    state: {
        status: { loggedIn: !!sessionStorage.getItem('token') },
        message: sessionStorage.getItem('message') || "",
        token: sessionStorage.getItem('token') || '',
        username: sessionStorage.getItem('username') || '',
        role: sessionStorage.getItem('role') || '',
    },

    actions: {
        login({ commit }, user) {
            return axios
                .post(API_URL + '/authenticate', {
                    username: user.username,
                    password: user.password

                }).then(response => {
                    commit('setStatus', "Logged In")
                    commit('setName', response.data.username)
                    commit('setRole', response.data.role)
                    commit('setToken', response.data.token)
                    return Promise.resolve(response);

                }).catch(error => {
                    console.warn("Failed to log in: " + error.response.data)
                    commit('logInFailure', error.response.data);
                    return Promise.reject(error);
                });
        },

        register({ commit }, user) {
            return axios
                .post(API_URL + '/register', {
                    username: user.username,
                    password: user.password,
                    email: user.email,
                    role: user.role,
                    code: user.code

                }).then(response => {
                    commit('setStatus', "Registered")
                    commit('setName', response.data.username)
                    commit('setRole', response.data.role)
                    commit('setToken', response.data.token)
                    return Promise.resolve(response);

                }).catch(error => {
                    console.warn("Failed to sign up: " + error.response.data);
                    commit('registerFailure', error.response.data);
                    return Promise.reject(error);
                });
        },

        logout({ commit }) {
            commit('logoutSuccess');
        },

        changeRole({ commit }, newRole) {
            commit('setRole', newRole);
        }
    },

    mutations: {
        setStatus(state, text) {
            state.status.loggedIn = true;
            state.message = text;
        },
        setName(state, name) {
            state.username = name;
        },
        setRole(state, role) {
            state.role = role;
        },
        setToken(state, token) {
          state.token = token;
        },
        logoutSuccess(state) {
          state.status.loggedIn = false;
          state.username = '';
          state.role = '';
          state.token = '';
        },
        logInFailure(state, text) {
            state.status.loggedIn = false;
            state.message = text;
        },
        registerFailure(state, text) {
            state.status.loggedIn = false;
            state.message = text;
        }
    }
};

