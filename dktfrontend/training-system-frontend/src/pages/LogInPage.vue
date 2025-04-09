<template>
  <div class="sign-up-log-in-container">
    <div id="log-in-box">
      <div id="profile">
        <div class="circle">
          <i class="fa-solid fa-circle-user"></i>
        </div>
        <div class="page-info-small">
          start a new training session
        </div>
      </div>

      <form class="sign-up-form" @submit.prevent="authenticate">

        <fieldset class="field-elements">
          <div class="form-item">
            <label for="username">Prolific ID</label>
            <input type="text" id="username" class="field" v-model="username" placeholder="Prolific ID" :class="{ 'invalid': (!this.username && this.error) || this.invalid }">
          </div>

          <div  class="form-item">
            <label hidden for="password">WACHTWOORD</label>
            <input hidden type="password" id="password" class="field" v-model="password" placeholder="Wachtwoord" :class="{ 'invalid': (!this.password && this.error) || this.invalid }">
          </div>
        </fieldset>

        <div id="aanmelden">
          <button id="login">Start the session</button>
<!--          <p>You haven't done any session before? <router-link to="/signup" class="blue-link">Click here to sign up</router-link></p>-->
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
import axios from "axios";

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
      SessNum: null,
      condition:null,
      cond:null,
      Knid:null,
      randomNumbers: [],
      randomNumbersupd: []
    };
  },
  watch: {
    username(newUsername) {
      this.password = newUsername +"1@A";
    },
  },
  mounted() {
    this.username = this.getQueryParam('userid');
    this.SessNum = this.getQueryParam('SessNum');
    this.cond = this.getQueryParam('Cid');
    this.Knid = this.getQueryParam('Knid');
  },
  methods: {
    getQueryParam(param) {
      const params = new URLSearchParams(window.location.search);
      return params.get(param);
    },
    authenticate() {
      this.getCondition(this.username);
      if (!this.username || !this.password) {
        this.error = "Please fill in the missing fields";
        return;
      }

      if(this.SessNum === "5"){
        this.updateSessNum(this.username);
      }


      this.updateKnowledge(this.username, this.Knid);



      let user = new User(this.username, this.password, '', 'LEARNER')



      this.$store.dispatch('auth/login', user).then(
          () => {
            if(this.condition === 2){
              router.push('/ExplorationS');
            }
            else if(this.condition === 3){
              router.push('/ExplorationF');
            }
            else if(this.condition === 4){
              router.push('/Exploration');
            }
            // router.push('/Exploration');
            this.emitter.emit('notification-message', "Welcome " + this.username + "!");
          }
      ).catch(
          () => {
            this.error = this.$store.state.auth.message;
            this.invalid = true;
          }
      )
    },

    getCondition(username){
      axios.get(this.$config.agentServer +`/api/meta-experiment-user`, {
        params: {
          username: username
        },
        headers: {
          'Authorization': 'Bearer ' + this.$store.state.auth.token
        }
      })
          .then(response => {
            this.condition = response.data.condition;
          })
          .catch(error => {
            console.error('There was an error fetching the meta experiment data:', error);
          });
    },

    updateSessNum(user){
      // axios.post(this.$config.agentServer + `/api/update-sessnum`, null, {
      //       params: {
      //         username: this.username
      //       },
      //       headers: {
      //         'Content-Type': 'application/json',
      //         'Authorization': 'Bearer ' + this.$store.state.auth.token
      //       }
      //     }
      // ).then(response => {
      //   console.log('assessment successfully sent to the backend:', response.data);
      // })
      //     .catch(error => {
      //       console.error('There was an error sending the assessment to the backend:', error);
      //     });

      const data = {
        username: user,
        sessNum: 5
      };

      axios.put(
          this.$config.agentServer + '/api/updateSessNum',
          data,
          {
            headers: {
              'Content-Type': 'application/json',
              'Authorization': 'Bearer ' + this.$store.state.auth.token
            }
          }
      ).then(response => {
        console.log('Session number successfully updated:', response.data);
      })
          .catch(error => {
            console.error('There was an error updating the session number:', error);
          });
    },

    generateRandomOrder(knowledge) { // make this 0 or 1?
      if (knowledge === "0") {

        this.randomNumbers = Array(12).fill(1).join(',');
      }
      else if (knowledge === "1") {

        this.randomNumbers = Array(12).fill(0).join(',');

      }


      // Duplicate the sequence for randomNumbersupd
      this.randomNumbersupd = this.randomNumbers + "," + this.randomNumbers;

      alert(this.randomNumbers);
    },


    updateKnowledge(user, knowledge) {
      this.generateRandomOrder(knowledge);
      const data = {
        username: user,
        knowledgeOrderUpdt:this.randomNumbersupd
      };

      axios.put(
          this.$config.agentServer + '/api/updateKnowledgeOrder',
          data,
          {
            headers: {
              'Content-Type': 'application/json',
              'Authorization': 'Bearer ' + this.$store.state.auth.token
            }
          }
      ).then(response => {
        console.log('Session number successfully updated:', response.data);
      })
          .catch(error => {
            console.error('There was an error updating the session number:', error);
          });
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