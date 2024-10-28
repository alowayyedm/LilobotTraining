<template>
  <div class="tab-container light-scrollbar">
    <div class="container">
      <label>Assign a trainer:</label>
      <input type="text" name="trainer-username" @keydown.enter="addTrainer"
             v-model="newTrainerText" placeholder="Enter your trainer's username" class="field">
      <button class="add-button" @click="addTrainer" title="assign trainer">
        <i class="fa-solid fa-user"></i>
        <i class="fa-solid fa-plus"></i>
      </button>
    </div>

    <div class="alert-container">
      <toast-queue ref="alert" class="alert"></toast-queue>
    </div>

    <h3>Assigned Trainers</h3>

    <div v-for="trainer in trainerList" :key="trainer" class="trainer-list-container">
      <div class="trainer-entry">
        <span class="trainer-label">{{ trainer.username }}</span>
        <button class="remove-button" :title="`unassign ` + trainer.username + `as your trainer`" @click="this.$emit('remove-trainer-request', trainer.username)">
          <i class="fa-solid fa-user-slash"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import ToastQueue from "../../components/ToastQueue";

export default {
  name: "TrainersTab",
  components: {ToastQueue},
  emits: ['remove-trainer-request'],
  data() {
    return {
      username: "",
      newTrainerText: "",
      trainerList: []
    }
  },
  mounted() {
    this.username = this.$store.state.auth.username;
  },
  
  computed: {
    token() {
      return this.$store.state.auth.token;
    }
  },

  methods: {
    fetchTrainers() {
      let url = this.$config.agentServer + '/user/trainers';

      axios.get(url, {
        headers: {
          'Authorization': 'Bearer ' + this.token
        }
      })
          .then((response) => {
            this.trainerList.length = 0;
            const trainers = response.data;

            for (let i = 0; i < trainers.length; i++) {
              this.trainerList.push({
                id: trainers[i].first,
                username: trainers[i].second
              });
            }

          })
          .catch((error) => console.log(error));
    },

    async addTrainer() {
      let requestBody = this.newTrainerText;
      const result = await this.addTrainerRequest(requestBody)
      
      if (this.newTrainerText === this.username) {
          this.displayAlert({text: "Cannot assign yourself as trainer", type: "error", duration: 5000});
          return;
      }

      if (result === requestBody) {
        this.notifyAssign(result)
      } else {
        this.emitter.emit("notification-message", result);
      }
    },

    async removeTrainer(trainerUsername) {
      const result = await this.removeTrainerRequest(trainerUsername)

      if (result === trainerUsername) {
        this.notifyRemove(result)
      } else {
        this.emitter.emit("notification-message", result);
      }
    },

    addTrainerRequest(requestBody) {
      let url = this.$config.agentServer + '/user/assign_trainer';
      this.newTrainerText = "";

      return axios.patch(url, requestBody, {
        headers: {
          'Content-Type': 'text/plain',
          'Authorization': 'Bearer ' + this.token
        }
      })
          .then((response) => {
            this.$emit('display-alert', {text: "Assigned new trainer", type: "success", duration: 5000});
            this.displayAlert({text: "Assigned new trainer", type: "success", duration: 5000});
            this.fetchTrainers();
            return response.data;
          })
          .catch((error) => {
            console.log(error);
            const requestedUsername = error.config.data;
            const responseList = error.response.data;

            for (let i = 0; i < responseList.length; i++) {
              if (responseList[i].username === requestedUsername) {
                if (!responseList[i].userIsTrainer) {
                  this.displayAlert({text: "Cannot assign learner account as a trainer", type: "error", duration: 5000});
                  return;
                }
                break;
              }
            }

            this.displayAlert({text: "Failed to assign new trainer", type: "error", duration: 5000});
            this.fetchTrainers();
            return error.response.data;
          });
    },

    removeTrainerRequest(trainerUsername) {
      let url = this.$config.agentServer + '/user/remove_trainer';

      return axios.patch(url, trainerUsername, {
        headers: {
          'Content-Type': 'text/plain',
          'Authorization': 'Bearer ' + this.token
        }
      })
          .then((response) => {
            this.$emit('display-alert', {text: "Removed trainer successfully", type: "success", duration: 5000});
            this.displayAlert({text: "Removed trainer successfully", type: "success", duration: 5000});
            this.fetchTrainers();
            return response.data;
          })
          .catch((error) => {
            this.displayAlert({text: "Failed to remove trainer", type: "error", duration: 5000});
            this.$emit('display-alert', {text: "Failed to remove trainer", type: "error", duration: 5000});
            return error.response.data;
          });
    },
    displayAlert({text, type, duration}) {
      this.$refs.alert.toasts = [];
      this.$refs.alert.addToast(text, type, duration);
    },

    notifyAssign(trainerUsername) {
      this.emitter.emit('trainer-assignment', trainerUsername)
    },

    notifyRemove(trainerUsername) {
      this.emitter.emit('trainer-remove', trainerUsername)
    }
  }
}
</script>

<style scoped>
@import "../../../styles/main.css";


.tab-container {
  overflow-y: scroll;
  overflow-x: visible;
  height: 32rem;
}
.container {
  display: inline-block;
  margin: 1rem;
}

.alert-container {
  height: 1rem;
}

.alert {
  display: flex;
  justify-content: center;
}

.field {
  height: 2.5rem;
  top: 0.25rem;
}

.add-button {
  border: none;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 0.6rem;
  width: 3rem;
  height: 2.5rem;
  margin-left: 1rem;
}

.add-button:hover,
.add-button:focus {
  background-color: var(--user-settings-add-hover);
}

h3 {
  color: var(--user-settings-header-text);
  margin-left: 2rem;
}

.trainer-list-container {
}

.trainer-entry {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  border-radius: 1.5rem;
}

.trainer-entry:hover,
.trainer-entry:focus {
  background-color: var(--user-settings-list-entry-hover);
  outline: var(--button-outline-width) solid var(--button-outline-color);
  outline-offset: 4px;
}

.trainer-label {
  margin-left: 0.5rem;
  font-weight: bold;
}

.remove-button {
  background-color: transparent;
  border: none;
  padding: 0.6rem;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 0.6rem;
  width: 2.5rem;
  height: 2.5rem;
  margin: 0.25rem;
  color: var(--user-settings-text);
}

.remove-button:hover,
.remove-button:focus {
  background-color: var(--user-settings-remove-hover);
  color: var(--user-settings-remove-hover-text);
}
</style>