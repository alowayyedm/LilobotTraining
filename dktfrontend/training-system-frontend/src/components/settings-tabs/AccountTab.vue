<template>
  <h3>Account</h3>

  <div class="alert-container">
    <toast-queue ref="alert" class="alert"></toast-queue>
  </div>

  <table class="details">
    <tr>
      <td>Username:</td>
      <td>
        <input type="text" name="username" class="field" v-model="this.username" readonly>
      </td>
    </tr>
    <tr>
      <td>Email:</td>
      <td>
        <input type="text" name="email" class="field" v-model="this.email" readonly>
      </td>
    </tr>
    <tr>
      <td>Role:</td>
      <td>
        <input type="text" name="role" class="field" v-model="this.role" readonly>
      </td>
      <td>
        <button v-if="this.role==='LEARNER'" class="role-button" @click="changeRole('TRAINER')">Upgrade account</button>
        <button v-if="this.role==='TRAINER'" class="role-button" @click="changeRole('LEARNER')">Demote account</button>
      </td>
    </tr>
    <tr>
      <td>Password:</td>
      <td>
        <button v-if="!enteringOldPass && !enteringNewPass" class="password-button"
                @click="this.enteringOldPass = true">Change</button>
        <input :hidden="!enteringOldPass" type="password" name="oldPassword" class="field"
               @keydown.enter="checkOldPassword" v-model="this.passwordText" placeholder="Enter current password">
        <input :hidden="!enteringNewPass" type="password" name="newPassword" class="field"
               v-model="this.passwordText" placeholder="Enter new password">
      </td>
      <td>
        <input :hidden="!enteringNewPass" type="password" name="newRepeatPassword" class="field"
               @keydown.enter="changePassword" v-model="this.passwordRepeatText" placeholder="Repeat new password">
      </td>
    </tr>
    <tr class="password-buttons-row">
      <td></td>
      <td>
        <button :hidden="!(enteringOldPass || enteringNewPass)" class="confirm-button"
                v-on="{click: enteringOldPass ? checkOldPassword : changePassword}">
          {{ enteringOldPass ? 'Continue' : 'Confirm' }}
        </button>
        <button :hidden="!(enteringOldPass || enteringNewPass)" class="cancel-button" @click="resetPasswordOptions">Cancel</button>
      </td>
    </tr>
  </table>

  <div class="error-list-wrapper">
    <div class="error-list" v-if="warnings.length">
      <li class="error-msg" v-for="(error, index) of this.warnings" :key="index">{{ error }}</li>
    </div>
  </div>

  <div class="delete-button-container">
    <button class="delete-button" @click="this.$emit('delete-account-request')">Delete Account</button>
  </div>

</template>

<script>
import axios from "axios";
import ToastQueue from "../../components/ToastQueue";

export default {
  name: "AccountTab",
  components: {ToastQueue},
  emits: ['delete-account-request'],
  data() {
    return {
      email: "",
      role: "",
      passwordText: "",
      passwordRepeatText: "",
      enteringOldPass: false,
      enteringNewPass: false,
      warnings: []
    }
  },

  methods: {
    fetchDetails() {
      let emailUrl = this.$config.agentServer + '/user/email';

      axios.get(emailUrl, {
        headers: {
          'Authorization': 'Bearer ' + this.token
        }
      })
          .then((response) => {
            this.email = response.data;
          })
          .catch((error) => console.log(error));

      let roleUrl = this.$config.agentServer + '/user/role';

      axios.get(roleUrl, {
        headers: {
          'Authorization': 'Bearer ' + this.token
        }
      })
          .then((response) => {
            this.role = response.data;
          })
          .catch((error) => console.log(error));
    },
    checkOldPassword() {
      if (this.passwordText.length > 0) {

        this.displayAlert({text: "Checking password...", type: "info", duration: 3000});

        let url = this.$config.agentServer + '/auth/authenticate/';

        axios.post(url, {username: this.username, password: this.passwordText}, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.token
          }
        })
            .then(() => {
              this.displayAlert({text: "Password Correct", type: "success", duration: 3000});
              this.$emit('display-alert', {text: "Password Correct", type: "success", duration: 3000});
              this.resetPasswordOptions();
              this.enteringNewPass = true;
            })
            .catch((error) => {
              this.displayAlert({text: "Password Incorrect", type: "error", duration: 5000});
              console.log(error);
              this.resetPasswordOptions();
            });

      }
    },
    changePassword() {
      this.emitter.emit("notification-message", "You have successfully changed your password.");
      this.warnings = [];

      if (this.passwordText.length > 0 && this.passwordRepeatText.length > 0) {
        if (this.passwordText !== this.passwordRepeatText) {
          this.warnings.push("Passwords entered are not the same.");
          return;
        }

        if (!this.validatePassword()) {
          return;
        }

        let url = this.$config.agentServer + '/user/update_password';

        axios.patch(url, this.passwordText, {
          headers: {
            'Content-Type': 'text/plain',
            'Authorization': 'Bearer ' + this.token
          }
        })
            .then(() => {
              this.displayAlert({text: "Password changed successfully", type: "success", duration: 8000});
              this.$emit('display-alert', {text: "Password changed successfully", type: "success", duration: 8000});
              this.resetPasswordOptions();
            })
            .catch((error) => {
              this.displayAlert({text: "Password failed to change", type: "error", duration: 8000});
              console.log(error)
            });
      } else {
        this.warnings.push("You must fill in both fields.");
      }
    },
    validatePassword() {
      const password = this.passwordText;

      if (password.search(/[a-z]/) < 0) {
        this.warnings.push("Your password must contain at least one lowercase letter.")
      }
      if (password.search(/[A-Z]/) < 0) {
        this.warnings.push("Your password must contain at least one uppercase letter.")
      }
      if (password.search(/[0-9]/) < 0) {
        this.warnings.push("Your password must contain at least one digit.")
      }
      if(password.search(/[!@#$%^&*()_+.,;:-]/) < 0) {
        this.warnings.push("Your password must contain at least one special character.")
      }
      if (password.length < 8) {
        this.warnings.push("Your password must be at least 8 characters long.")
      }

      return this.warnings.length === 0;
    },
    resetPasswordOptions() {
      this.enteringOldPass = false;
      this.enteringNewPass = false;
      this.passwordText = "";
      this.passwordRepeatText = "";
      this.warnings = [];
    },
    changeRole(role) {
      if (role !== "LEARNER" && role !== "TRAINER") return;

      let url = this.$config.agentServer + '/user/update_role';

      axios.patch(url, role, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + this.token
        }
      })
          .then(() => {
            this.displayAlert({text: "Role changed successfully", type: "success", duration: 5000});
            this.$emit('display-alert', {text: "Role changed successfully", type: "success", duration: 5000});
            this.fetchDetails();
            this.emitter.emit("notification-message", "You are now a " + role + ".");
            this.$store.dispatch('auth/changeRole', role)
          })
          .catch((error) => console.log(error));
    },
    displayAlert({text, type, duration}) {
      this.$refs.alert.toasts = [];
      this.$refs.alert.addToast(text, type, duration);
    }
  },

  computed: {
    username() {
      return this.$store.state.auth.username;
    },
    token() {
      return this.$store.state.auth.token;
    }
  }
}
</script>

<style scoped>
@import "../../../styles/main.css";
@import "../../../styles/signup.css";

h3 {
  color: var(--user-settings-header-text);
  margin-left: 1rem;
}

.alert-container {
  height: 1rem;
}

.alert {
  display: flex;
  justify-content: center;
}

.details {
  margin-left: 1rem;
  border-spacing: 0 2rem;
}

.field {
  height: 2.5rem;
  width: 12rem;
  margin-left: 1.25rem;
}

.role-button {
  font-family: 'Anton', sans-serif;
  background-color: var(--user-settings-role-button);
  margin-left: 1.25rem;
  cursor: pointer;
  border: none;
  padding: 0.6rem;
  font-size: 1rem;
  border-radius: 0.6rem;
  width: 10rem;
}

.password-button {
  font-family: 'Anton', sans-serif;
  margin-left: 1.25rem;
  cursor: pointer;
  border: none;
  padding: 0.6rem;
  font-size: 1rem;
  border-radius: 0.6rem;
  height: 2.5rem;
  width: 5rem;
}

.password-buttons-row {
  display: flex;
  flex-direction: row;
  position: absolute;
  margin-left: 6.5rem;
  margin-top: -2rem;
}

.confirm-button {
  font-family: 'Anton', sans-serif;
  border: none;
  border-radius: 0.5rem;
  height: 2rem;
  width: 6rem;
  cursor: pointer;
}

.cancel-button {
  font-family: 'Anton', sans-serif;
  border: none;
  border-radius: 0.5rem;
  height: 2rem;
  width: 6rem;
  cursor: pointer;
  margin-left: 0.2rem;
}

.error-list-wrapper {
  height: 7rem;
}

.error-list {
  width: 75%;
  margin: 0 auto;
}

.delete-button-container {
  display: flex;
  justify-content: center;
  left: calc(50% + 4rem);
}

.delete-button {
  background: var(--user-settings-delete-button);
  border: none;
  border-radius: 0.5rem;
  height: 2rem;
  width: 8rem;
  font-family: 'Anton', sans-serif;
  cursor: pointer;
}

.role-button:hover, 
.password-button:hover, 
.confirm-button:hover, 
.cancel-button:hover, 
.delete-button:hover,
.role-button:focus, 
.password-button:focus, 
.confirm-button:focus, 
.cancel-button:focus, 
.delete-button:focus {
  filter: brightness(var(--user-settings-button-focus-brightness));
}

</style>