<!------------------------ Chat with Exploration page

-->
<template>
  <div class="instruction-grid-container">
    <div class="left" style="">
      <div class="instruction">
        <br> <br>
        <h3 class="subtitle">Welcome!</h3>
        <p>
          In the exploration mode, you will interact with two different virtual children. You are free to use exploration buttons to guide you. Please be aware that the virtual children cannot answer questions on unrelated topics. Ensure your responses align with the current topic of conversation.        </p>
<br>
      </div>
      <router-link  to="/train" id="chat-history-link">
        <button class="button-with-icon-and-text">Please click here to start the exploration mode</button>
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

