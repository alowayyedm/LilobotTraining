/**------------------------ Vue Routing ------------------------ 
 *  Each of the routes in this file are added to the navbar of 
 *  the web application. To add a new route, check the steps in
 *  the rest of this file and in App.vue.
 */

import { createRouter, createWebHistory } from 'vue-router';

import ChatWithLilobot from './pages/ChatWithLilobot';
import TrainingPortal from './pages/TrainingPortal';
import SignUp from './pages/SignUp';
import LogIn from "./pages/LogInPage";
import ChatHistory from "./pages/ChatHistory"
import UserSettings  from "./pages/UserSettings";

/**             ------- ADDING NEW ROUTES ------- 
 *  To add a new route to the navbar, add the {path, name, component}
 *  to the list below, where {component} should be imported above from
 *  where your .vue file for the new route is located. Also add the info 
 *  (especially the {path}) to App.vue.
 */
const routes = [
    {
        path: '/',
        name: 'ChatWithLilobot',
        component: ChatWithLilobot,
        meta: {
            title: "LILOBOT TRAINING PORTAL"
        }
    },
    {
        path: '/train',
        name: 'TrainingPortal',
        component: TrainingPortal,
        meta: {
            title: "LILOBOT TRAINER VIEW"
        }
    },
    {
        path: '/signup',
        name: 'SignUp',
        component: SignUp,
        meta: {
            title: "SIGN UP AS LEARNER"
        }
    },
    {
        path: '/login',
        name: 'LogIn',
        component: LogIn,
        meta: {
            title: "LOG INTO YOUR ACCOUNT"
        }
    },
    {
        path: '/history',
        name: 'ChatHistory',
        component: ChatHistory,
        meta: {
            title: "CHAT HISTORY"
        }
    },
    {
        path: '/settings',
        name: 'UserSettings',
        component: UserSettings,
        meta: {
            title: "Settings"
        }
    }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  // Reset rasa sessionID when going to ChatWithLilobot
  if (to.name === 'ChatWithLilobot') {
    sessionStorage.removeItem('rasa_session_id');
  }

    // cannot access User Settings, Chat History or Training Portal if not logged in
    if (!sessionStorage.getItem('vuex') &&
        (to.path.includes("settings") || to.path.includes("history") || to.path.includes("train"))) {
        next("/");
        return;
    }

    // learners cannot access trainer's portal
    if (sessionStorage.getItem('vuex') && sessionStorage.getItem('vuex').includes("LEARNER")
        && to.path.includes("train")) {
        next("/");
        return;
    }

  next();

});

export default router;