<!------------------------ App ------------------------
		This is the main page, it has a navbar and tabs that
		connect to routes to display the wanted content. To
		add new tabs, follow the instructions in the rest of
		this file.

		The Rasa Webchat is part of the entire web application,
		but only shown on certain pages. This is controlled by
		a boolean method in `computed`.
				
-->
<template>
	<div>
		<nav>
      <img alt="Kindertelefoon logo" src="../images/kt_logo.png" width="70" height="60">
      <div class="navbar-title">
        <dynamic-text
            :header-text="`${$route.meta.title}`"
            :target-font-size=2.4
            :min-font-size=0.8
            :unit="'rem'"
            :step-size=0.1
            :overflow-wrap="'auto'"
        >
        </dynamic-text>
      </div>
			<ul>
				<!-- 
					The for loop below automatically adds the tabs to 
					the navbar. Nothing has to be changed here to add
					a new route. 
				-->
				<li v-for="tab in tabs" :key="tab.name" >
					<router-link v-if="hideTab(tab.name)" :to="tab.route" active-class="active">
						<div class="tab" >
							<div :class="'tab-circle ' + tab.circleClass">
								<div :class="tab.iconClass"></div>
							</div>
							<div class="tab-title">
                <dynamic-text
                    :header-text="tab.title"
                    :target-font-size=1
                    :min-font-size=0.4
                    :unit="'rem'"
                    :step-size=0.1
                    :overflow-wrap="'auto'"
                >
                </dynamic-text>
              </div>
						</div>
					</router-link>
        </li>

        <li class="profile" v-if="hideTab(profile.name)">
          <div role="button" :tabindex="0" aria-label="profile" id="account-tab" class="tab" @click="toggleSubmenu" @keyup.enter="toggleSubmenu">
            <div :class="'tab-circle ' + profile.circleClass" >
              <div :class="profile.iconClass"></div>
            </div>

            <nav class="submenu" v-if="showSubmenu">
              <ul class="submenu-list">
                <li v-for="tab in subMenuTabs" :key="tab.name">
                  <router-link :to="tab.route" active-class="active">
                    <div v-on:click="logout(tab.title)" class="submenu-tab">
                      <div class="submenu-icon" :class="tab.iconClass"></div>
                      <div class="submenu-title">{{ tab.title }}</div>
                    </div>
                  </router-link>
                </li>
              </ul>
            </nav>

            <div class="user-menu">
              <div class="tab-title">
                <dynamic-text
                    :header-text="username.toString()"
                    :target-font-size=1
                    :min-font-size=0.4
                    :unit="'rem'"
                    :step-size=0.1
                    :overflow-wrap="'auto'"
                >
                </dynamic-text>
              </div>
              <i v-if="showSubmenu" class="fa-solid fa-angle-up" title="Hide user menu"></i>
              <i v-else class="fa-solid fa-angle-down"  title="Show user menu"></i>
            </div>

          </div>

          <!-- HERE -->

        </li>

        <li class="notifications" v-if="hideTab(notifications.name)" >
          <div class="tab" role="button" :tabindex="0" title="notifications" aria-label="notifications" id="notification-tab" 
            @click="showNotificationsBox" @keyup.enter="showNotificationsBox">
            <span class="notification-count" title="new notifications" v-if="showNotificationCount" >{{ this.notificationCount }}</span>
            <div :class="'tab-circle ' + notifications.circleClass" >
              <div :class="notifications.iconClass" ></div>
            </div>

          </div>
        </li>

			</ul>
		</nav>

    <notification-component
        ref="notification">
    </notification-component>

	<router-view/>
	</div>
</template>

<script>
import DynamicText from "@/components/DynamicText.vue";

import router from './Routes.js'
import NotificationComponent from "@/components/NotificationComponent";

export default {
	name: 'App',
  components: {
      NotificationComponent,
      DynamicText
    },

  data() {
		return {
			tabs: [
          /**						 ------- ADDING NEW ROUTES -------
          *	To add a new route to the navbar, add the {name, title, iconClass, circleClass, route}
          *	(where route is the path you want the new page to correspond to)
          *	to the list below. Also add the info (especially the {route}) to
          *	Routes.js (in /src/). If successful, the {name} should appear on the
          *	right side of the navbar with a black circle left to it. The title +
          *	circle together work like a button.
          */
          { name: 'ChatWithLilobot', title: 'Chat with Lilobot', iconClass: 'fa-solid fa-message',
            circleClass: '', route: '/' },
          { name: 'TrainingPortal', title: 'Training Portal', iconClass: 'fa-solid fa-chalkboard-user',
            circleClass: '', route: '/train' },
          { name: 'SignUp', title: 'Sign Up', iconClass: 'fa-solid fa-circle-user user-icon',
            circleClass: 'user-icon-circle', route: '/signup' },
          { name: 'LogIn', title: 'Log In', iconClass: 'fa-solid fa-circle-user',
            circleClass: 'user-icon-circle', route: '/login' }
			],

      subMenuTabs: [
        { name: 'ChatHistory', title: 'Chat History', iconClass: 'fa-solid fa-clock',
          circleClass: '', route: '/history' },
        { name: 'UserSettings', title: 'Settings', iconClass: 'fa-solid fa-gear',
          circleClass: '', route: '/settings' },
        { name: 'ChatHistory', title: 'Log Out', iconClass: 'fa-solid fa-door-open',
          circleClass: '', route: '/' },
      ],

      profile: { name: 'Profile', title: 'Profile', iconClass: 'fa-solid fa-circle-user',
        circleClass: 'user-icon-circle'},

      notifications: { name: 'Notifications', title: 'Notifications', iconClass: 'fa-solid fa-bell',
        circleClass: 'bell-icon-circle'},

      notificationCount: 0,
      showNotificationCount: false,
      showSubmenu: false
    };
	},
  computed: {
    hideTab() {
      return(name) => {
        if (name === 'SignUp' || name === 'LogIn') {
          // only shown when user is not logged in
          return !this.$store.state.auth.status.loggedIn;
        } else if (name === 'Profile' || name === 'Notifications') {
          return this.$store.state.auth.status.loggedIn;
        }
        else if (name === 'TrainingPortal') {
          // Hide TrainingPortal from learners
          return (this.$store.state.auth.role !== "LEARNER") && sessionStorage.getItem('vuex');
        }

        // else it is always shown
        return true;
      }
    },
    username() {
      return this.$store.state.auth.username;
    }
  },
  mounted() {
    this.$refs.notification.clearMessages();

    window.onpopstate = () => {
      if (
          window.sessionStorage.getItem("vuex") !== null &&
          (router.currentRoute.value.name === 'SignUp' || router.currentRoute.value.name === 'LogIn')
      ) {
        router.push("/");
      }
    };

    window.addEventListener('click', this.handleWindowClick);

    this.emitter.on("notification-message", message => {
      this.showNotificationCount = true;
      this.notificationCount += 1;
      this.$refs.notification.addMessage(message);
    });

    const highContrastMode = localStorage.getItem('high_contrast_mode');
    const isHighContrast = JSON.parse(highContrastMode);
    const rootElement = document.documentElement;

    if (isHighContrast) {
      rootElement.setAttribute('data-contrast', 'high');
    } else {
      rootElement.removeAttribute('data-contrast');
    }
  },
  created() {
    sessionStorage.removeItem('rasa_session_id')
  },

  methods: {
    handleWindowClick(e) {
      if (this.showSubmenu === true && !e.target.classList.contains("fa-angle-down")
          && e.target.closest('#account-tab') === null) {
        this.disableSubmenu();
      } else if (this.$refs.notification.showNotification === true
          && !e.target.classList.contains("bell-icon-circle")
          && e.target.closest('#notification-tab') === null) {
        this.$refs.notification.showNotification = false;
      }
    },

    enableSubmenu() {
      this.showSubmenu = true;
    },
    disableSubmenu() {
      this.showSubmenu = false;
    },
    showNotificationsBox() {
      this.$refs.notification.showBox();
      this.resetNotificationCount();
    },

    resetNotificationCount() {
      this.notificationCount = 0;
      this.showNotificationCount = false;
    },

    toggleSubmenu() {
      this.showSubmenu = !this.showSubmenu;
    },

    hideSubmenu() {
      this.showSubmenu = false;
    },

    logout(tab) {
      if (tab === 'Log Out') {
        this.$store.dispatch('auth/logout');
        sessionStorage.clear();
        this.$refs.notification.clearMessages();
        this.resetNotificationCount();
        router.push('/');
      }
    }
  }
  
};

</script>

<style scoped>
.tab {
  position: relative;
}

nav .submenu {
  display: contents;
  height: inherit;
  width: inherit;
}

nav .submenu a {
  justify-content: left;
}

.submenu-list {
  display: flex;
  justify-content: flex-start;
  align-items: normal;
  flex-direction: column;
  padding: 0%;
  top: 10vh;
  position: absolute;
  background-color: var(--background-dropdown);
  box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
  z-index: 1;
}
.submenu-tab {
  position: relative;
  display: flex;
  flex-direction: row;
  text-align: left;
  align-items: center;
  padding: 0.5rem;
  gap: 0.7rem;
}
.profile {
  cursor: pointer;
}

.profile i {
  color: var(--menu-tab-text);
}

.submenu-icon {
  color: var(--submenu-text);
}

.submenu-list li {
  margin: 0%;
  float: none;
  color: var(--submenu-text);
  text-decoration: none;
  display: block;
  text-align: left;
  flex-grow: 1;
  min-width: max-content;
}

.submenu-list li:hover,
.submenu-list li:focus {
  border-radius: 0%;
  background-color: var(--submenu-hover);
}

.submenu-list li:hover *,
.submenu-list li:focus *{
  color: var(--submenu-hover-text);
}

</style>

<style scoped src="../styles/navbar.css"/>
<style src="../styles/main.css"/>
<style src="../styles/signup.css"/>
<style src="../node_modules/@fortawesome/fontawesome-free/css/all.css"/>