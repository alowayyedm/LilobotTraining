<template>
  <div class="instruction-grid-container">
    <div class="left">
      <div class="instruction">
        <br> <br>
        <h3 class="subtitle">Welcome Back!</h3>
        <p>
          You will now be redirected back to the survey to answer questions about your experience.
        </p>
        <br>
      </div>
      <!-- Make sure to call redirectToSurvey(), not redirect() -->
      <button @click="redirectToSurvey" class="button-with-icon-and-text">
        Please click here to go to the survey
      </button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: "ChatWithExploration",
  data() {
    return {
      condition: null,
      knowledge: null,
      Sessnum: null
    };
  },
  methods: {
    async redirectToSurvey() {
      try {
        // Fetch data using axios
        const response = await axios.get(this.$config.agentServer + `/api/meta-experiment-user`, {
          params: {
            username: this.$store.state.auth.username
          },
          headers: {
            'Authorization': 'Bearer ' + this.$store.state.auth.token
          }
        });

        // Assign API response data to component state
        this.condition = response.data.condition;
        this.knowledge = response.data.knowledgetest;
        this.Sessnum = response.data.sessNum;

        // Construct the redirect URL
        const redirectUrl = `https://tudelft.fra1.qualtrics.com/jfe/form/SV_0uiqxSeU0W0gOii?PROLIFIC_PID=${this.$store.state.auth.username}&Cid=${this.condition}&ScenarioID=${this.knowledge}&SessNum=${this.Sessnum}`;

        // Redirect the user
        window.location.href = redirectUrl;
      } catch (error) {
        console.error('Error fetching meta experiment data:', error);
      }
    }
  }
};
</script>

<style scoped>
.webchat-learner {
  height: 80vh;
  width: 35vw;
}

#chat-history-link {
  margin: 3.2rem;
}
</style>

<style scoped src="../../styles/main.css" />
