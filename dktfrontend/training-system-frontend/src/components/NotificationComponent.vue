<template>
  <div class="notifications-box" v-if="showNotification">
    <span class="triangle"></span>

    <div class="notifications-container light-scrollbar" ref="notificationContainer">
      <div class="messages" v-for="message in this.messages" :key="message">
        <span class="message">{{ message.text }}</span>
        <span class="time-msg">{{ message.time }}</span>
      </div>
    </div>

  </div>

</template>

<script>
import moment from 'moment';

export default {
  name: 'NotificationComponent',

  data() {
    return {
      messages: [],
      showNotification: false
    };
  },

  methods: {
    addMessage(message) {
      this.messages.push({
        text: message,
        time: this.timestamp()
      });
    },

    showBox() {
      this.showNotification = !this.showNotification;
    },

    clearMessages() {
      this.messages = [];
    },

    timestamp() {
      return moment().format('YYYY-MM-DD hh:mm:ss');
    }
  }

}
</script>

<style scoped>

.notifications-box {
  position: absolute;
  width: 16rem;
  height: 20rem;
  box-sizing: border-box;
  background-color: var(--notification-box);
  border-radius: 1.5rem;
  box-shadow: 0 0.25rem 0.5rem 0 rgba(0, 0.5, 0, 0.3), 0 0.25rem 1.25rem 0 rgba(0, 0, 0, 0.2);
  right: 1rem;

  z-index: 9999;
}

.notifications-container {
  overflow-y: scroll;
  height: 95%;
  width: 93%;
  margin-top: 1vh;
  margin-left: 0.5vw;
  padding: 1vh;
}

.light-scrollbar::-webkit-scrollbar {
  width: 0.8rem;
  height: 0.8rem;
}

.light-scrollbar::-webkit-scrollbar-corner {
  background-color: var(--notification-box);
}

.triangle {
  position: absolute;
  top: -0.7rem;
  left: 12.3rem;
  height: 2rem;
  width: 1.7rem;
  border-radius: 5px;
  transform: rotate(45deg);
  background: var(--notification-box);
}

.messages {
  position: relative;
  height: 20%;
  margin-top: 1vh;
  margin-bottom: 1vh;
  padding: 0.3rem;
  border-bottom: var(--notification-separator);
  border-bottom-style: solid;
  border-bottom-width: thin;
}

.message {
  position: absolute;
  font-size: small;
  color: var(--notification-text);
}

.time-msg {
  font-size: xx-small;
  bottom: 0.3rem;
  position: absolute;
  right: 0.5rem;
}

</style>