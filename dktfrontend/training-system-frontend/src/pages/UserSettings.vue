<!------------------------ User Settings ------------------------
    This component is the user's settings page and is loaded into the
    browser by the router (see Routes.js). The separate components are
    imported and displayed on the right places through a css grid.
-->
<template>
  <generic-popup ref="popup"
                 v-on="{accept: tab === 0 ? deleteAccount : this.$refs.trainerTab.removeTrainer}">
  </generic-popup>

  <div class="settings-container">
    <aside class="settings-tabs">
      <div class="title"><i class="fa-solid fa-gears"></i> Settings</div>
      <button class="tab-button" @click="switchTab(0)">
        <i class="fa-solid fa-user"></i>
        <label class="button-label">Account</label>
      </button>
      <button class="tab-button" id="trainers-button" @click="switchTab(1)">
        <i class="fa-solid fa-file-pen"></i>
        <label class="button-label" id="trainers">Trainers</label>
      </button>
      <button class="tab-button" @click="switchTab(2)">
        <i class="fa-solid fa-sliders"></i>
        <label class="button-label">Voorkeuren</label>
      </button>
    </aside>
    <div class="tab-container">
      <div :hidden="tab !== 0">
        <account-tab ref="accountTab"
                     @delete-account-request="displayDeleteAccountPopup">
        </account-tab>
      </div>
      <div :hidden="tab !== 1">
        <trainers-tab ref="trainerTab"
                      @remove-trainer-request="displayRemoveTrainerPopup">
        </trainers-tab>
      </div>
      <div :hidden="tab !== 2">
        <preferences-tab ref="preferencesTab"></preferences-tab>
      </div>
    </div>
  </div>
</template>

<script>
import AccountTab from "../components/settings-tabs/AccountTab";
import PreferencesTab from "../components/settings-tabs/PreferencesTab.vue";
import TrainersTab from "../components/settings-tabs/TrainersTab";
import {Stomp} from "@stomp/stompjs";
import GenericPopup from "../components/GenericPopup";
import axios from "axios";

const defaultState = {auth: {message: "", role: "", status: {loggedIn: false}, token: "", username: ""}};

export default {
  name: "UserSettings",
  components: {
    AccountTab,
    TrainersTab,
    PreferencesTab,
    GenericPopup
  },
  data() {
    return {
      tab: 0,
      stompClient: null,
      // Stores subscriptions to receive notifications.
      // Each subscription corresponds to a specific scenario for which the client receives
      // notifications; in this case, the client subscribes to receive notifications
      // when a trainer gets assigned/removed
      notificationSubs: [],
    }
  },

  mounted() {
    this.$refs.accountTab.fetchDetails();

    this.stompClient = Stomp.client(this.$config.agentWsServer);
    this.stompClient.connect({}, () => {
      console.log('STOMP connection established');

      const trainerAssign = this.stompClient.subscribe('/topic/session/trainer_assign/' + this.$store.state.auth.username,
          (message) => {
            this.emitter.emit("notification-message", message.body);
          });
      this.notificationSubs.push(trainerAssign)
      console.log("NOTIFICATION SUBSCRIPTION")
    });

    this.emitter.on("trainer-assignment", trainerName => {
      const myUsername = this.$store.state.auth.username;
      console.warn("TRAINER ASSIGNED")

      if (this.stompClient !== null) {
        this.stompClient.send('/app/session/trainer_assign/' + trainerName, {},
            myUsername + " has added you as a trainer.");
      }
    });

    this.emitter.on("trainer-remove", trainerName => {
      const myUsername = this.$store.state.auth.username;
      console.warn("TRAINER REMOVED");

      if (this.stompClient !== null) {
        this.stompClient.send('/app/session/trainer_assign/' + trainerName, {},
            "You are not a trainer for " + myUsername + " anymore.");
      }
    });
  },

  unmounted() {
    this.notificationSubs.forEach((subscription) => {
      subscription.unsubscribe();
    });
    this.stompClient = null;
  },

  methods: {
    switchTab(tab) {
      if (this.tab === tab) return;

      if (tab === 0) {
        this.$refs.accountTab.fetchDetails();
      } else if (tab === 1) {
        this.$refs.trainerTab.fetchTrainers();
      } 

      this.tab = tab;
    },
    displayRemoveTrainerPopup(trainerName) {
      this.$refs.popup.popupText = "Are you sure you want to remove " + trainerName + " as your trainer? " +
          "You can always add them back later";
      this.$refs.popup.acceptText = "Remove";
      this.$refs.popup.declineText = "Cancel";
      this.$refs.popup.invertedColors = true;
      this.$refs.popup.showOptionsPopupWithData(trainerName);
    },
    displayDeleteAccountPopup() {
      this.$refs.popup.popupText = "Are you sure you want to delete your account? " +
          "This action cannot be undone and you will no longer be able to login into this account";
      this.$refs.popup.acceptText = "Delete Account";
      this.$refs.popup.declineText = "Cancel";
      this.$refs.popup.invertedColors = true;
      this.$refs.popup.showOptionsPopup();
    },

    deleteAccount() {
      let url = this.$config.agentServer + '/user/delete';

      axios.delete(url, {
        headers: {
          'Authorization': 'Bearer ' + this.$store.state.auth.token
        }
      })
          .then((response) => {
            console.log(response);
            this.$store.replaceState(defaultState);
            this.$router.push('/');
          })
          .catch((error) => console.log(error));
    }
  }
}
</script>

<style scoped>
@import "../../styles/main.css";

.alert-container {
  display: flex;
  justify-content: center;
  position: fixed;
  width: 100%;
  height: 2rem;
  transform: scale(2);
}

.title {
  text-align: center;
  color: var(--user-settings-text);
  padding: 1rem;
}

.settings-container {
  width: 50rem;
  height: 38rem;
  border-radius: 1rem;
  background-color: var(--user-settings-container);
  display: flex;
  margin: 3rem auto auto;
  overflow: clip;

  /* border styling only used iff accessibility mode is on */
  border: solid;
  border-color: var(--basic-border-color);
  border-width: var(--basic-border-width);
}

.settings-tabs {
  display: flex;
  background-color: var(--user-settings-tab);
  flex-direction: column;
  width: 30%;
  align-items: center;

  /* border styling only used iff accessibility mode is on */
  border: solid;
  border-width: 0 var(--basic-border-width) 0 0;
  border-color: var(--basic-border-color);
}

.tab-container {
  width: 35rem;
  height: 32rem;
  color: var(--user-settings-text);
}

.tab-button {
  background: transparent;
  outline: none;
  border: none;
  border-radius: 0.75rem;
  height: 4rem;
  width: 95%;
  cursor: pointer;
  color: var(--user-settings-text);

}

.tab-button:hover, .tab-button:focus {
  background: var(--user-settings-tab-focus);
  border: solid;
  border-width: var(--hoverable-list-entry-border-width);
  border-color: var(--hoverable-list-entry-focus-border-color);
}

.button-label {
  font-family: 'Anton', sans-serif;
  font-size: 1rem;
  padding: 1rem;
  z-index: 99;
  cursor: pointer;
  color: var(--user-settings-text);
}

</style>
