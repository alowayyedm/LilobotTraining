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
            title: "Exploration Mode"
        }
    },
    {
        path: '/trainingtrainerinterface',
        name: 'TrainingPortalTrainer',
        component: TrainingPortalTrainer,
        meta: {
            title: "Trainer Mode"
        }
    },
    {
        path: '/trains',
        name: 'TrainingPortalS',
        component: TrainingPortalS,
        meta: {
            title: "Exploration Mode"
        }
    },
    {
        path: '/trainf',
        name: 'TrainingPortalF',
        component: TrainingPortalF,
        meta: {
            title: "Exploration Mode"
        }
    },
    {
        path: '/Exploration',
        name: 'ExplorationMode',
        component: ExplorationMode,
        meta: {
            title: "Exploration Mode"
        }
    },
    {
        path: '/ExplorationS',
        name: 'ExplorationModeS',
        component: ExplorationModeS,
        meta: {
            title: "Exploration Mode"
        }
    },
    {
        path: '/ExplorationF',
        name: 'ExplorationModeF',
        component: ExplorationModeF,
        meta: {
            title: "Exploration Mode"
        }
    },
    {
        path: '/assessment',
        name: 'AssessmentMode',
        component: AssessmentMode,
        meta: {
            title: "Assessment Mode"
        }
    },
    {
        path: '/assessments',
        name: 'AssessmentModeS',
        component: AssessmentModeS,
        meta: {
            title: "Assessment Mode"
        }
    },
    {
        path: '/assessmentf',
        name: 'AssessmentModeF',
        component: AssessmentModeF,
        meta: {
            title: "Assessment Mode"
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
            title: "Assessment Mode"
        }
    },
    {
        path: '/assessmentportalS',
        name: 'AssessmentPortalS',
        component: AssessmentPortalS,
        meta: {
            title: "Assessment Mode"
        }
    },
    {
        path: '/assessmentportalf',
        name: 'AssessmentPortalF',
        component: AssessmentPortalF,
        meta: {
            title: "Assessment Mode"
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