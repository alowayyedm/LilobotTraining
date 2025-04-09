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
import ExplorationMode from "@/pages/ExplorationMode.vue";
import ReflectionPage from "@/pages/ReflectionPage.vue";
import AssessmentMode from "@/pages/AssesmentMode.vue";
import AssessmentPortal from "@/pages/AssessmentPortal.vue";
import ExplorationModeS from "@/pages/ExplorationS.vue";
import TrainingPortalS from "@/pages/TrainingPortalS.vue";
import AssessmentModeS from "@/pages/AssessmentModeS.vue";
import AssessmentPortalS from "@/pages/AssessmentPortalS.vue";
import ExplorationModeF from "@/pages/ExplorationModeF.vue";
import TrainingPortalF from "@/pages/TrainingPortalF.vue";
import AssessmentModeF from "@/pages/AssessmentModeF.vue";
import AssessmentPortalF from "@/pages/AssessmentPortalF.vue";
import TrainingPortalTrainer from "@/pages/TraininingTrainer.vue";

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
            title: "Training Portal"
        }
    },
    {
        path: '/train',
        name: 'TrainingPortal',
        component: TrainingPortal,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/trainingtrainerinterface',
        name: 'TrainingPortalTrainer',
        component: TrainingPortalTrainer,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/trains',
        name: 'TrainingPortalS',
        component: TrainingPortalS,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/trainf',
        name: 'TrainingPortalF',
        component: TrainingPortalF,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/Exploration',
        name: 'ExplorationMode',
        component: ExplorationMode,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/ExplorationS',
        name: 'ExplorationModeS',
        component: ExplorationModeS,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/ExplorationF',
        name: 'ExplorationModeF',
        component: ExplorationModeF,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/assessment',
        name: 'AssessmentMode',
        component: AssessmentMode,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/assessments',
        name: 'AssessmentModeS',
        component: AssessmentModeS,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/assessmentf',
        name: 'AssessmentModeF',
        component: AssessmentModeF,
        meta: {
            title: "Training"
        }
    },

    {
        path: '/reflection',
        name: 'ReflectionPage',
        component: ReflectionPage,
        meta: {
            title: "Reflect on your learning experience"
        }
    },

    {
        path: '/signup',
        name: 'SignUp',
        component: SignUp,
        meta: {
            title: "SIGN UP FOR THE FIRST SESSION"
        }
    },
    {
        path: '/login',
        name: 'LogIn',
        component: LogIn,
        meta: {
            title: "LOG IN TO START YOUR 2ND, 3RD, 4TH, OR 5TH SESSION"
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
        path: '/assessmentportal',
        name: 'AssessmentPortal',
        component: AssessmentPortal,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/assessmentportalS',
        name: 'AssessmentPortalS',
        component: AssessmentPortalS,
        meta: {
            title: "Training"
        }
    },
    {
        path: '/assessmentportalf',
        name: 'AssessmentPortalF',
        component: AssessmentPortalF,
        meta: {
            title: "Training"
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