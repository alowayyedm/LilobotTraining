<!------------------------ Chat with Lilobot ------------------------
    This page is where the learner can chat with Lilobot. The actual
    chat widget can be found in the RasaWebChat component.

-->
<template>
  <div class="instruction-grid-container">
    <div class="left" style="">
      <div class="instruction">
        <h3 class="subtitle">WELKOM</h3>
        <p>
          Met deze trainingsomgeving kun je het Vijf-Fasen Gespreksmodel oefenen. Lilobot vertelt over zijn situatie en jouw doel is een gesprek aan te gaan met hem volgens het gespreksmodel.
        </p>

        <p>
          <b>Let op:</b>
          Lilo kan geen vragen van andere onderwerpen beantwoorden. Zorg ervoor dat je antwoorden passen bij het onderwerp van het gesprek.
        </p>

        <h3 class="subtitle">INSTRUCTIES</h3>
        <ul>
          <li>In de rechterhoek zie je een chat icoontje. Klik hierop om een gesprek te beginnen met Lilobot. </li>
          <li>Als je puntjes ziet, betekent dat Lilobot aan het typen is. Wacht even tot dat hij klaar is.</li>
          <li>Typ "doei" om het gesprek te beëindigen. Dit stopt de chat.</li>
          <li>Aan het einde, krijg een transcriptie van dit gesprek voor reflectie.</li>
        </ul>
      </div>
      <router-link v-if="this.$store.state.auth.status.loggedIn" to="/history" id="chat-history-link">
        <button class="button-with-icon-and-text"><i class="fa-solid fa-clock-rotate-left"></i>&nbsp;&nbsp; Chat History</button>
      </router-link>
    </div>

    <div class="right">
<!--      <web-chat-component -->
<!--          class="webchat-learner"-->
<!--        header-text="Chat met Lilobot"-->
<!--        ref="webChat"-->
<!--        @handle-input="sendUserMessage"-->
<!--        @leave-session="restartSession">-->
<!--      </web-chat-component>-->
    </div>
  </div>
  <generic-popup
      ref="genericPopup"
      @accept="acceptSessionRequest"
      @decline="declineSessionRequest">
  </generic-popup>
</template>

<script>
  import sessionMixin from "@/mixins/sessionMixin";
  import {Stomp} from "@stomp/stompjs";
  import axios from "axios";
  import GenericPopup from "@/components/GenericPopup.vue";

  export default {
    name: 'ChatWithLilobot',
    components: {
      GenericPopup,
    },
    mixins: [sessionMixin],
    created() {
      window.addEventListener("beforeunload", this.unsubscribeFromTopics);
    }, 
    beforeMount() {
      this.restartSession();
    },
    beforeUnmount() {
      this.unsubscribeFromTopics();
    },
    unmounted() {
      this.clearNotifications();
    },

    methods: {
      setupWebsocket() {
        // Sets up the websocket to send and recieve messages to Spring server    
        this.stompClient = Stomp.client(this.$config.agentWsServer);
        this.stompClient.connect({}, () => {
          console.log('STOMP connection established');
          this.setUpNotifications();

          // This subscription is used for the message stream.
          this.addSubscription('/topic/session/' + this.getSessionID(), (message) => {
            const messageData = JSON.parse(message.body);

            for (const i in messageData) {
              this.$refs.webChat.addMessage(messageData[i].message, messageData[i].fromUser, false);
            }
          });
          this.addSubscription('/topic/session/join/' + this.getSessionID(), (message) => {
            this.$refs.genericPopup.popupText = message.body + " wants to join your session.";
            this.$refs.genericPopup.acceptText = "Accept";
            this.$refs.genericPopup.declineText = "Decline";
            this.$refs.genericPopup.showOptionsPopupWithData(message.body);
            this.emitter.emit("notification-message", message.body + " wants to join your session");
          });
          if (this.$store.state.auth.status.loggedIn) {
            this.addSubscription('/topic/session/join/' + this.$store.state.auth.username, (message) => {
              this.$refs.genericPopup.popupText = message.body + " wants to join your session.";
              this.$refs.genericPopup.acceptText = "Accept";
              this.$refs.genericPopup.declineText = "Decline";
              this.$refs.genericPopup.showOptionsPopupWithData(message.body);
              this.emitter.emit("notification-message", message.body + " wants to join your session");
            });
          }

        });
      },
      acceptSessionRequest(username) {
        const response = {'accepted': true, 'sessionId': this.getSessionID()}
        this.stompClient.send('/app/session/accept/' + username, {}, JSON.stringify(response));
        this.emitter.emit('notification-message', username + " has joined your session.");
      },
      
      declineSessionRequest(username) {
        const response = {'accepted': false}
        this.stompClient.send('/app/session/accept/' + username, {}, JSON.stringify(response));
        this.emitter.emit('notification-message', "You have declined " + username + "'s request to join your session.");
      },
      restartSession() {
        // Reset old session
        // Unsubscribe to notify server that session is ended
        this.unsubscribeFromTopics();
        sessionStorage.removeItem('rasa_session_id')
        if (this.stompClient?.connected) {
          this.stompClient.disconnect()
        }
        // Start new session
        this.setupWebsocket();
        this.setupRasaSession();
        axios.post(this.$config.agentServer + '/create/' + this.getSessionID(), null, {
            params: { username: this.$store.state.auth.username }
          }).then(response => {
            console.log(response.data);
          }).catch(error => {
            console.error(error);
          });
      }
    }
  }
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

