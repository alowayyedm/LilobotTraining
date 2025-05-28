<!------------------------ Chat with Exploration page

-->
<template>
  <div class="instruction-grid-container">
    <div class="left" style="">
      <div class="instruction">
        <br> <br>
        <h3 class="subtitle">Welcome!</h3>
        <p>
          Next, you will have 12 minutes to interact with a virtual children scenario. Please take your time in interactions, and keep restarting the conversation when it ends until the time runs out. You will be automatically redirected after 12 minutes.

          <br> Please be aware that the virtual child cannot answer questions on unrelated topics. Ensure your responses align with the current topic of conversation.        </p>
<br>
      </div>
      <router-link  to="/train" id="chat-history-link">
        <button class="button-with-icon-and-text">Please click here to start the interaction</button>
      </router-link>
    </div>


  </div>

</template>

<script>


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

<style scoped src="../../styles/main.css"/>

