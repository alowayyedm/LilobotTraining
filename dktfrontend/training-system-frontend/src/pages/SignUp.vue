<template>
  <div class="sign-up-log-in-container">
    <div id="log-in-box">
      <div id="profile">
        <div class="circle">
          <i class="fa-solid fa-circle-user"></i>
        </div>
        <div class="page-info-small">
          SIGN UP for the first session
        </div>
      </div>


      <form class="sign-up-form" @submit.prevent="register">
        <fieldset>
          <div class="form-item">
            <label for="username">You Username</label>
            <input type="text" id="username" class="field" v-model="username" placeholder="Username" :class="{ 'invalid': nameInvalid }">
          </div>

          <div class="form-item">
            <label  for="email">E-MAILADRES</label>
            <input  type="email" id="email" class="field" v-model="email" placeholder="E-mailadres" :class="{ 'invalid': emailInvalid } ">
          </div>

          <div class="form-item">
            <label  for="password">WACHTWOORD</label>
            <input type="password" id="password" class="field" v-model="password" placeholder="Wachtwoord" :class="{ 'invalid': passwordInvalid }">
          </div>

          <div class="form-item">
            <label  for="code">CODE</label>
            <input  type="password" id="code" class="field" v-model="code" placeholder="Code" :class="{ 'invalid': codeInvalid }">
          </div>

        </fieldset>

        <div class="trainer-role">
          <button  type="button" id="trainer" @click="changeRole" :class="{ 'checked': trainerSelected }"></button>
          <span  id="trainer-text">Sign up as a trainer</span>
        </div>

        <div id="aanmelden">
          <button type="submit" id="login">Start the first session</button>
<!--          <p>This is not your first session? <router-link to="/login" class="blue-link">Click here</router-link></p>-->
        </div>

      </form>

    </div>

    <div class="error-list" v-if="errors.length">
      <li class="error-msg" v-for="(error, index) of this.errors" :key="index">{{ error }}</li>
    </div>

  </div>

</template>

<script>
import UserCredentials from '../models/user-credentials';
import router from '../Routes.js';
import axios from "axios";

export default {
  name: "SignUp",

  data() {
    return {
      username: "",
      email: "",
      password: "",
      code: 'HyP$jdIHV$zK5#2X',
      role: "TRAINER",
      cond: null,
      Knid: null,
      Sessnum: null,
      errors: [],
      errorCodes: [],
      randomNumbers: [],
      randomNumbersupd: []
    };
  },
  mounted() {
    this.username = this.getQueryParam('userid');
    this.cond = this.getQueryParam('Cid');
    this.Knid = this.getQueryParam('Knid');
    this.Sessnum= this.getQueryParam('SessNum');
    //handle condition they are assigned to, and also the knowledge sequence and chack if its true that they are in session 5, then this is sent to qualtrics
  },
  computed: {
    nameInvalid() {
      return this.errorCodes.some(item => item.includes("USERNAME"))
    },

    emailInvalid() {
      return this.errorCodes.some(item => item.includes("EMAIL"))
    },

    // Returns false if there are no errors related to the password
    passwordInvalid() {
      return this.errorCodes.some(item => item.includes("PASSWORD"))
    },

    codeInvalid() {
      return this.errorCodes.some(item => item.includes("CODE"))
    },
    trainerSelected() {
      return this.role === "TRAINER";
    },
  },

  // watch: {
  //   username(newUsername) {
  //     this.email = `${newUsername}@mail.com`;
  //     this.password = newUsername +"1@A";
  //   },
  // },
  methods: {
    getQueryParam(param) {
      const params = new URLSearchParams(window.location.search);
      return params.get(param);
    },
    changeRole() {
      if (this.role === "LEARNER") {
        this.role = "TRAINER";
      } else {
        this.role = "LEARNER";
      }

      console.warn("Change role to " + this.role);
    },
    generateRandomOrder() {
      // Create an array of numbers from 0 to 11
      let numbers = Array.from({ length: 12 }, (_, i) => i);

      // Shuffle the array using the Fisher-Yates algorithm
      for (let i = numbers.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [numbers[i], numbers[j]] = [numbers[j], numbers[i]];
      }

      // Set the shuffled array to randomNumbers
      this.randomNumbers = numbers.join(',');

      // Repeat the randomNumbers sequence to create randomNumbersupd
      this.randomNumbersupd = this.randomNumbers + "," + this.randomNumbers;
    },

    register() {
      this.errors = []
      this.errorCodes = []

      // Validate username
      if (!this.usernameValidate()) {
        this.errors.push("Username should be between 1 and 40 characters long.")
        this.errorCodes.push("USERNAME")
        return;
      }

      // Validate email
      if (!this.emailValidate()) {
        this.errors.push("Invalid email.")
        this.errorCodes.push("EMAIL")
        return;
      }

      // Validate password
      this.passwordValidate(this.password)
      if (this.errors.length > 0) {
        this.errorCodes.push("PASSWORD")
        return;
      }

      // Check whether code is missing
      if (this.code.length === 0) {
        this.errors.push("Insert code.")
        this.errorCodes.push("CODE")
        return;
      }

      // add adding meta experiment data here
      this.generateRandomOrder();
      const data = {
        username: this.username,
        condition: this.cond, //int
        knowledgetest: this.Knid,
        sessNum: this.Sessnum,
        knowledgeOrder: this.randomNumbers,
        knowledgeOrderUpdt:this.randomNumbersupd
      };





      let user = new UserCredentials(this.username, this.password, this.email, this.role, this.code)
      this.$store.dispatch('auth/register', user).then(
          () => {
            axios.post(
                this.$config.agentServer + '/api/metaExperiment', // or '/api/button-click/' if there's a trailing slash in the endpoint
                data,
                {
                  headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + this.$store.state.auth.token
                  }
                }
            ).then(response => {
              console.log('assessment successfully sent to the backend:', response.data);
            })
                .catch(error => {
                  console.error('There was an error sending the assessment to the backend:', error);
                });

            if(this.cond === "2") { // redirect to Simulation only if the condition is 2
              router.push('/ExplorationS');
              this.emitter.emit('notification-message', "Welcome " + this.username + "!");
              this.emitter.emit('notification-message', "Chat history is saved, to view it go to chat history page");
            }
            else if(this.cond ==="3"){ // redirect to simulation and feedback if the condition is 3
              router.push('/ExplorationF');
              this.emitter.emit('notification-message', "Welcome " + this.username + "!");
              this.emitter.emit('notification-message', "Chat history is saved, to view it go to chat history page");

            }
            else if(this.cond ==="4"){ // redirect to simulation, feedback and reflection if the condition is 4
              router.push('/Exploration');

              this.emitter.emit('notification-message', "Welcome " + this.username + "!");
              this.emitter.emit('notification-message', "Chat history is saved, to view it go to chat history page");
            }
            else{
              router.push('/');

              this.emitter.emit('notification-message', "Welcome " + this.username + "!");
              this.emitter.emit('notification-message', "Chat history is saved, to view it go to chat history page");

            }
          }
        ).catch(
          () => {
            this.errors.push(this.$store.state.auth.message);
            if (this.$store.state.auth.message.includes("name")) {
              this.errorCodes.push("USERNAME");
            }
            if (this.$store.state.auth.message.includes("email")) {
              this.errorCodes.push("EMAIL");
            }
            if (this.$store.state.auth.message.includes("code")) {
              this.errorCodes.push("CODE");
            }
          }
        )
    },

    usernameValidate() {
      return this.username.length > 0 && this.username.length < 40;
    },

    emailValidate() {
      const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(this.email);
    },

    // If the password is not valid, it shows what it is missing
    passwordValidate(password) {
      if (password.search(/[a-z]/) < 0) {
        this.errors.push("Your password must contain at least one lowercase letter.")
      }
      if (password.search(/[A-Z]/) < 0) {
        this.errors.push("Your password must contain at least one uppercase letter.")
      }
      if (password.search(/[0-9]/) < 0) {
        this.errors.push("Your password must contain at least one digit.")
      }
      if(password.search(/[!@#$%^&*()_+.,;:-]/) < 0) {
        this.errors.push("Your password must contain at least one special character.")
      }
      if (password.length < 8) {
        this.errors.push("Your password must be at least 8 characters long.")
      }
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
  position: relative;
  margin-left: 20px;
  width: 200px;
  height: 50px;
  border-bottom-right-radius: 10px;
  border-bottom-left-radius: 10px;
  border-top-right-radius: 10px;
  background-color: var(--field-bg-color);
  text-align: left;
  text-indent: 10px;
  padding-top: 10px;
  border-style: var(--field-border-style);
  border-width: var(--field-border-width);
  border-color: var(--field-border-color);
  font-family: 'Inter', sans-serif;
  font-size: small;
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

li:not(:last-child) {
  margin-bottom: 1.5vh;
}

.trainer-role {
  position: relative;
  display: flex;
  top: -1vh;
  margin-right: 7vw;
  margin-left: 3.5rem;
  margin-bottom: 2.5vh;
  letter-spacing: 0.02rem;
}

#trainer {
  height: 2vh;
  width: 1.2vw;
  background-color: var(--role-checkbox-unchecked);
}

#trainer.checked {
  background-color: var(--role-checkbox-checked); 
}

#trainer-text {
  font-size: small;
  margin-bottom: 0vh;
  margin-top: 0vh;
  margin-left: 0.5rem;
}

</style>