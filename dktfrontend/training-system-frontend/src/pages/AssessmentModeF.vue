<!------------------------ Chat with Exploration page

-->
<template>
  <div class="instruction-grid-container">
    <div class="left" style="">
      <div class="instruction">
        <br> <br>
        <h3 class="subtitle">Welcome Back!</h3>
        <p>
          Now, you will start the assessment mode. Here, you will try to apply the five-phase model in the chat without using the guiding buttons. Therefore, you need to apply the five-phase model as best as you can.</p>
        <br>
      </div>
      <router-link  to="/assessmentportalf" id="chat-history-link">
        <button class="button-with-icon-and-text">Please click here to start the assessment mode</button>
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

