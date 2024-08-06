<template>
  <div class="instruction-grid-container">
    <div class="left">
      <div class="instruction">
        <br><br>
        <h3 class="subtitle">Reflection Form</h3>
        <p class="subsubtitle">Please write your thoughts to reflect on your learning from the last exploration session.</p>
        <br>

        <form @submit.prevent="handleSubmit">
          <div class="form-item">
            <label for="conversationReflection">How did the conversation with the child go?</label>
            <textarea
                id="conversationReflection"
                v-model="conversationReflection"
                required
                minlength="50"
                placeholder="Please write your reflection here"
                class="textarea-field"
            ></textarea>
          </div>
          <br>
          <div class="form-item">
            <label for="learningReflection">Please reflect on your learning experience. Consider aspects such as what you learned, how you learned it, why learning it matters, how to apply it, whether it changed your way of thinking, what the outcomes were, and how you felt while learning.</label>
            <textarea
                id="learningReflection"
                v-model="learningReflection"
                required
                minlength="50"
                placeholder="Please write your reflection here"
                class="textarea-field"
            ></textarea>
          </div>
          <br>
          <button type="submit" class="button-with-icon-and-text">Submit Reflection</button>

          <!--          <router-link  to="/assessment" ><button type="submit" class="button-with-icon-and-text">Submit Reflection</button></router-link>-->
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      conversationReflection: '',
      learningReflection: ''
    };
  },
  methods: {
    handleSubmit() {
      if (this.conversationReflection.length >= 50 && this.learningReflection.length >= 50) {
        // Handle form submission, e.g., send data to the server

        // Redirect to the "/assessment" page

        const data = {
          username: this.$store.state.auth.username,
          conversationReflection: this.conversationReflection,
          learningReflection: this.learningReflection
        };


        axios.post(
            this.$config.agentServer + '/api/reflection', // or '/api/button-click/' if there's a trailing slash in the endpoint
            data,
            {
              headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + this.$store.state.auth.token
              }
            }
        ).then(response => {
          console.log('reflection successfully sent to the backend:', response.data);
        })
            .catch(error => {
              console.error('There was an error sending the reflection to the backend:', error);
            });


        this.$router.push('/assessment');
      }
      else {
        alert('Please fill out both reflections with at least 50 characters.');
      }

    }
  }
};
</script>

<style scoped>
.instruction-grid-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.left {
  width: 60%;
  margin: auto;
}

.instruction {
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.subtitle {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #1d1f25;
}

.subsubtitle {
  font-size: 1rem;
  margin-bottom: 1rem;
  color: #1d1f25;
}

.form-item {
  margin-bottom: 1.5rem;
}

.textarea-field {
  width: 100%;
  height: 150px;
  padding: 0.5rem;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.button-with-icon-and-text {
  padding: 0.75rem 1.5rem;
  font-size: 1.5rem;
  color: #fff;
  background-color: var(--basic-button);
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.button-with-icon-and-text:hover {
  background-color: #0cb98b;
}
</style>
