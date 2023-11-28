<template>
  <div class="sign-up-log-in-container">
    <div id="log-in-box">
      <div id="profile">
        <div class="circle">
          <i class="fa-solid fa-circle-user"></i>
        </div>
        <div class="page-info-small">
          LOG IN
        </div>
      </div>

      <form class="sign-up-form" @submit.prevent="authenticate">

        <fieldset class="field-elements">
          <div class="form-item">
            <label for="username">GEBRUIKERSNAAM</label>
            <input type="text" id="username" class="field" v-model="username" placeholder="Gebruikersnaam" :class="{ 'invalid': (!this.username && this.error) || this.invalid }">
          </div>

          <div class="form-item">
            <label for="password">WACHTWOORD</label>
            <input type="password" id="password" class="field" v-model="password" placeholder="Wachtwoord" :class="{ 'invalid': (!this.password && this.error) || this.invalid }">
          </div>
        </fieldset>

        <div id="aanmelden">
          <button id="login">INLOGGEN</button>
          <p>HEEFT U NOG GEEN ACCOUNT? <router-link to="/signup" class="blue-link">AANMELDEN</router-link></p>
        </div>

      </form>
      
    </div>

    <div class="error-list" v-if="error.length" >
      <p class="error-msg">{{ error }}</p>
    </div>

  </div>

</template>

<script>
import User from "@/models/user-credentials";
import router from '../Routes.js';

export default {
  name: "LogInTrainer",

  props: {
    userType: String
  },

  data() {
    return {
      username: "",
      password: "",
      error: "",
      invalid: false,
    };
  },
  methods: {
    authenticate() {
      if (!this.username || !this.password) {
        this.error = "Please fill in the missing fields";
        return;
      }

      let user = new User(this.username, this.password, '', 'LEARNER')

      this.$store.dispatch('auth/login', user).then(
          () => {
            router.push('/');
            this.emitter.emit('notification-message', "Welcome " + this.username + "!");
          }
      ).catch(
          () => {
            this.error = this.$store.state.auth.message;
            this.invalid = true;
          }
      )
    }
  }
}
</script>

<style scoped>
@import "../../styles/main.css";

#login:hover,
#login:focus {
  background-color: var(--popup-button-green-focus);
  transform: scale(1.05);
  box-shadow: 0 4px 8px 0 rgba(0, 0.5, 0, 0.3), 0 4px 20px 0 rgba(0, 0, 0, 0.2);
}

#login:active {
  background-color:  var(--popup-button-green-active);
  transform: scale(0.96);
}

.field {
  background-color: var(--field-bg-color);
  border-style: var(--field-border-style);
  border-width: var(--field-border-width);
  border-color: var(--field-border-color);
}

/* Styles for the 'invalid' state */
.field.invalid {
  background-color: var(--field-bg-color-error);
  border-style: var(--field-border-style-error);
  border-width: var(--field-border-width-error);
  border-color: var(--field-border-color-error);
}

.field:focus,
.field:hover {
  outline: var(--button-outline-width) solid var(--field-outline-dark);
  outline-offset: 2px;
}

.error-list {
  padding: 0;
}

fieldset {
  padding-right: 1.5rem;
}

#log-in-box {
  height: 20rem;
}

</style>