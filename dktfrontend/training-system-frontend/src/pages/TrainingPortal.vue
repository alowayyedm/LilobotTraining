<!------------------------ Training Portal ------------------------
    This component is the training portal page and is loaded into the
    browser by the router (see Routes.js). The separate components are
    imported and displayed on the right places through a css grid.

-->
<template>
  <div class="wrapper">
    <div class="first"><graph-component ref="graphComponent"></graph-component></div>
    <div class="second"><belief-input
      ref="beliefInput"
      :beliefs="beliefs" 
      :previousValues="previousValues"
      :sessionActive="sessionActive"
      :phase="phase"
      :lastTransition="lastTransition"
      @update-belief="updateBelief"
      :privateSession="privateSession"
      @switch-to-phase="switchToPhase">
    </belief-input></div>
    <div class="third">
      <belief-transitions-component 
        ref="beliefTransitions">
      </belief-transitions-component>
    </div>
    <div class="fourth"><web-chat-component
      ref="webChat"
      :header-text="`${sessionActive ? `${ sessionActive }'s Chat` : 'You are not watching anyone'}`"
      @handle-input="handleInput"
      @set-trainer-messaging="setTrainerMessaging"
      @request-session="requestSession"
      @leave-session="leaveSession"
      @start-private-session="startPrivateSession">
    </web-chat-component>
    </div>
  </div>
  <join-popup
      ref="joinPopup"
      @send-join-request="sendJoinRequest">
  </join-popup>
  <generic-popup
      ref="genericPopup" accept-text="ACCEPT" decline-text="DECLINE">
  </generic-popup>
</template>
  
<script>
  import GraphComponent from '../components/GraphComponent.vue';
  import BeliefInput from '../components/BeliefInput.vue';
  import BeliefTransitionsComponent from "../components/BeliefTransitionsComponent";
  import WebChatComponent from "../components/WebChatComponent";
  import JoinPopup from "@/components/JoinPopup.vue";
  import GenericPopup from "@/components/GenericPopup.vue";
  import axios from 'axios';
  import { phaseEnumToNumber } from "@/utils";
  import sessionMixin from "@/mixins/sessionMixin";

  export default {
    name: 'TrainingPortal',
    components: {
      GenericPopup,
      BeliefTransitionsComponent,
      GraphComponent,
      BeliefInput,
      WebChatComponent,
      JoinPopup
    },
    mixins: [sessionMixin],
    data() {
      return {
        beliefs: null,
        phase: null,
        lastTransition: null,
        previousValues: {},
        sessionActive: null,
        subscriptions: [],
        stompClient: null,
        privateSession: false
      }
    },
    created() {
      window.addEventListener("beforeunload", this.unsubscribeFromTopics);
    }, 
    beforeMount() {
      this.fetchInitBeliefs();
      this.setupWebsocketConnection();
    },
    mounted() {
      this.$refs.webChat.setTrainer(true);
      this.$refs.webChat.setConversationInactive();
    },
    beforeUnmount() {
      console.log("\u001B[31mTrainingPortal just unmounted\u001B[0m")
      this.unsubscribeFromTopics();
      this.disconnectWebsocket();
    },

    unmounted() {
      this.clearNotifications();
    },

    methods: {
      setupPrivateSessionWebsocket() {
        // Sets up the websocket to send and receive messages to/from Spring server
        this.stompClient.connect({}, () => {
          console.log('STOMP connection established');

          // This subscription is used for the message stream.
          this.stompClient.subscribe('/topic/session/' + this.getSessionID(), (message) => {
            const messageData = JSON.parse(message.body);

            for (const i in messageData) {
              this.$refs.webChat.addMessage(messageData[i].message, messageData[i].fromUser, false);
            }
          });

          // This subscription is used to process live changes in agent beliefs
          this.stompClient.subscribe('/topic/beliefs/' + this.getSessionID(), (message) => {
            this.processReceivedUpdate(JSON.parse(message.body));
          });

          // Subscribe to the phase updates topic
          this.addSubscription('/topic/phase/' + this.getSessionID(), (message) => {
            const messageData = JSON.parse(message.body);
            if (messageData.phaseFrom !== null) {
              if (messageData.phaseFrom !== messageData.phaseTo) {
                this.lastTransition = {
                  from: phaseEnumToNumber(messageData.phaseFrom),
                  to: phaseEnumToNumber(messageData.phaseTo)
                }
              }
            } else {
              this.lastTransition = null;
            }
            this.phase = phaseEnumToNumber(JSON.parse(message.body).phaseTo);
          });

          axios.post(this.$config.agentServer + '/create/' + this.getSessionID(), null, {
            params: { username: this.$store.state.auth.username }
          }).then(response => {
            console.log(response.data);
            // Get the current phase of the agent
            this.stompClient.send(`/app/phase/${this.getSessionID()}`);
          }).catch(error => {
            console.error(error);
          });
        });
      },
      joinSession(sessionIdInput, username) {
        // Join the session of a learner given the sessionID

        this.$refs.webChat.setTrainer(true);
        this.privateSession = false;
        // Set session ID so it can be used where needed (not used yet)
        sessionStorage.setItem('rasa_session_id', sessionIdInput);

        this.sessionActive = username;

        // Unsubcribe from previous topics
        this.unsubscribeFromTopics();

        this.addSubscription('/topic/session/' + sessionIdInput, (message) => {
          const messageData = JSON.parse(message.body);
          for (const i in messageData) {
            this.$refs.webChat.addMessage(messageData[i].message, messageData[i].fromUser, false);
          }
        });
        
        this.addSubscription('/topic/trainer/' + sessionIdInput, (message) => {
          const messageData = JSON.parse(message.body);
          for (const i in messageData) {
            this.$refs.webChat.addMessage(messageData[i].message, messageData[i].fromUser, true);
          }
        });
        
        this.addSubscription('/topic/beliefs/' + sessionIdInput, (message) => {
          this.processReceivedUpdate(JSON.parse(message.body));
        });
        // Subscribe to the phase updates topic
        this.addSubscription('/topic/phase/' + sessionIdInput, (message) => {
          const messageData = JSON.parse(message.body);
          if (messageData.phaseFrom !== null) {
            if (messageData.phaseFrom !== messageData.phaseTo) {
              this.lastTransition = {
                from: phaseEnumToNumber(messageData.phaseFrom),
                to: phaseEnumToNumber(messageData.phaseTo)
              }
            }
          } else {
            this.lastTransition = null;
          }
          this.phase = phaseEnumToNumber(JSON.parse(message.body).phaseTo);
        });

        // Load past messages of learner
        axios.get(this.$config.agentServer + '/conversation/' + sessionIdInput, {
          headers: {
            'Authorization': 'Bearer ' + this.$store.state.auth.token
          }
        })
          .then((response) => {
            this.$refs.webChat.setConversation(response.data);
          })
          .catch((error) => console.log(error));

        // Load past belief transitions of learner
        axios.get(this.$config.agentServer + '/transitions/' + sessionIdInput, {
          headers: {
            'Authorization': 'Bearer ' + this.$store.state.auth.token
          }
        })
          .then((response) => {
            const pastBeliefUpdates = response.data;
            pastBeliefUpdates.forEach((beliefUpdate) => {
              this.processReceivedUpdate(beliefUpdate)
            });
          })
          .catch((error) => console.log(error));

        axios.get(this.$config.agentServer + '/beliefs/all/' + sessionIdInput, {
          headers: {
            'Authorization': 'Bearer ' + this.$store.state.auth.token
          }
        })
          .then(response => {
            const beliefs = response.data;
            beliefs.forEach(receivedBelief => {
              this.previousValues[receivedBelief.belief] = receivedBelief.value;
            });
          })
          .catch(error => {
            console.error(`Failed to fetch beliefs for ${sessionIdInput}: `, error);
          });

        // Get the current phase of the agent
        this.stompClient.send(`/app/phase/${sessionIdInput}`);

        this.$refs.webChat.setConversationActive();
        this.$refs.webChat.setAutoSendingTrue();
      },
      disconnectWebsocket() {
        this.stompClient.disconnect(() => {
          console.log('STOMP connection disconnected');
        });
      },
      leaveSession() {
        // Leave session and reset state of page

        // Turn auto
        if (!this.privateSession) {
          const sessionID = sessionStorage.getItem('rasa_session_id');
          axios.post(this.$config.agentServer + '/agent/changeMode/' + sessionID, JSON.stringify(false),
            {
              headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + this.$store.state.auth.token
              }
            });
        }

        sessionStorage.removeItem('rasa_session_id')
        this.unsubscribeFromTopics();
        this.fetchInitBeliefs();
        this.previousValues = {};
        this.sessionActive = null;
        this.privateSession = false;
        this.$refs.webChat.setConversationInactive();
        this.$refs.beliefTransitions.reset();
        this.$refs.graphComponent.clearGraphComponent();
        this.phase = null;
        this.lastTransition = null;
      },
      fetchInitBeliefs() {
        axios.get(this.$config.agentServer + '/beliefs/all', {
          headers: {
            'Authorization': 'Bearer ' + this.$store.state.auth.token
          }
        })
          .then(response => {
            this.beliefs = response.data;
            this.beliefs.forEach(belief => {
              this.previousValues[belief.id] = belief.value;
            });
          })
          .catch(error => {
            console.error(`Failed to fetch initial beliefs: `, error);
          });
      },
      switchToPhase(phase) {
        const sessionID = sessionStorage.getItem('rasa_session_id');
        axios.put(this.$config.agentServer + '/beliefs/phase', {
          sessionId: sessionID,
          phase: phase
        })
            .then(response => {
              for(let i = 0; i < response.data.length; i++) {
                this.beliefs.find(item => item.id === response.data[i].belief).value = response.data[i].value;
              }

              this.beliefs.forEach(belief => {
                this.previousValues[belief.id] = belief.value;
              });
            }).catch(error => {
              console.error(`Failed to switch session ${this.getSessionID()} to phase ${phase}: `, error);
            });
      },
      processReceivedUpdate(message) {
        const beliefId = message.belief;
        const newValue = message.value;
        let msgText = message.msgText;
        const logIndex = message.logIndex; 
        let updateType = message.beliefUpdateType;
        const causeType = (message.isManualUpdate) ? "MANUAL" : (msgText === null) ? "TRIGGER" : "MESSAGE";

        if (msgText === null) {
          msgText = ">>> TRIGGER"
        }
      
        // Boolean representing whether the log entry received is not yet in the transition list
        const logAhead = !this.$refs.beliefTransitions.messageList.some(entry => entry.index === logIndex && entry.index >= 0);

        let belief = this.beliefs.find(item => item.id === beliefId);

        if (!belief) {
          console.log(`Belief '${beliefId}' not found`);
          return;
        }

        let oldValue = belief.value;
        belief.value = newValue;
        this.previousValues[belief.id] = newValue;

        if (updateType !== "INCREASE" && updateType !== "DECREASE") {
          if (oldValue === newValue) {
            updateType = "UNCHANGED"
          }

          // Boolean to indicate whether the belief value it decreased or increased
          let increasedValue = oldValue < newValue;

          if (increasedValue) {
            updateType = "INCREASE";
          } else {
            updateType = "DECREASE";
          }
        }

        // If belief value has stayed the same but still sent to frontend,
        // add entry to history if not yet added but don't display the belief as being changed
        if (updateType === "UNCHANGED") {
          if (logAhead) {
            this.$refs.beliefTransitions.addMsgTransition(logIndex, msgText, causeType, {positiveBeliefs: [],
              negativeBeliefs: []});
          }
          return;
        }

        // If the corresponding message that changed the belief is not yet added to messageList
        if (logAhead) {
          if (updateType === "INCREASE") {
            this.$refs.beliefTransitions.addMsgTransition(logIndex, msgText, causeType, {positiveBeliefs: [belief.id],
              negativeBeliefs: []});
          } else {
            this.$refs.beliefTransitions.addMsgTransition(logIndex, msgText, causeType, {positiveBeliefs: [],
              negativeBeliefs: [belief.id]});
          }
        }
        else {
          if (updateType === "INCREASE") {
            if (!this.$refs.beliefTransitions.messageList.find(t => t.index === logIndex).positiveBeliefs.includes(belief.id)) {
              this.$refs.beliefTransitions.messageList.find(t => t.index === logIndex).positiveBeliefs.push(belief.id);
            }
          } else {
            if (!this.$refs.beliefTransitions.messageList.find(t => t.index === logIndex).negativeBeliefs.includes(belief.id)) {
              this.$refs.beliefTransitions.messageList.find(t => t.index === logIndex).negativeBeliefs.push(belief.id);
            }
          }
        }
      },
      updateBelief(beliefId, newValue) { 
        //this.beliefs.find(item => item.id === belief.id).value = belief.value;
        this.sendMessage(beliefId, newValue);
      },
      sendMessage(beliefId, newValue) {
        const sessionID = sessionStorage.getItem('rasa_session_id');
        const message = { belief: beliefId, value: newValue };
        this.stompClient.send('/app/update/' + sessionID, {}, JSON.stringify(message));
      },
      handleInput(text) {
        if (this.privateSession) {
          this.sendUserMessage(text);
        } else {
          this.sendTrainerMessage(text);
        }
      },
      startPrivateSession() {
        if (this.stompClient && this.stompClient.connected) {
          this.stompClient.disconnect();
        }
        this.privateSession = true;
        this.$refs.webChat.setTrainer(false);
        this.$refs.webChat.setConversationActive();
        sessionStorage.removeItem('rasa_session_id');
        this.sessionActive = this.$store.state.auth.username;

        // Generates the session id
        this.getSessionID();

        this.setupPrivateSessionWebsocket();

        if (!this.socket) {
          this.setupRasaSession();
        }
      },
      async sendTrainerMessage(messages) {
        await this.waitForStompConnection(this.stompClient);

        const messagesToSend = [];
        for (const i in messages) {
          if (messages[i].length > 0) {
            messagesToSend.push(messages[i]);
          }
        }
        if (messagesToSend.length > 0 && this.stompClient) {
          // Send the messages to /app/trainer/{sessionID} to send messages to client
          const sessionID = sessionStorage.getItem('rasa_session_id')
          this.stompClient.send('/app/trainer/' + sessionID, {}, JSON.stringify(messagesToSend));
        }
      },
      setTrainerMessaging(isTrainerResponding) {
        const sessionID = sessionStorage.getItem('rasa_session_id')
        axios.post(this.$config.agentServer + '/agent/changeMode/' + sessionID, JSON.stringify(isTrainerResponding),
          {
            headers: {
              'Content-Type': 'application/json',
              'Authorization': 'Bearer ' + this.$store.state.auth.token
            }
          });
      },

      async sendJoinRequest(username) {
        // check if this user is assigned as the learner's trainer
        const isAssigned = await this.checkAssignedTrainer(username)
        if (!isAssigned) {
          return;
        }

        const myUsername = this.$store.state.auth.username;
        this.addSubscription('/topic/session/accept/' + myUsername, (message) => {
          const messageData = JSON.parse(message.body);
          if (messageData.accepted) {
            this.joinSession(messageData.sessionId, username);
            this.emitter.emit("notification-message", username + " has accepted your join request.");
          } else {
            this.$refs.genericPopup.showAlertPopup(username +  " declined your session request.")
          }
        });
        this.stompClient.send('/app/session/join/' + username, {}, myUsername);
      },

      requestSession() {
        if (this.$store.state.auth.status.loggedIn) {
          this.$refs.joinPopup.openPopup();
        } else {
          this.$refs.genericPopup.showAlertPopup("Please log in to join a session.");
        }
      },

      checkAssignedTrainer(learner) {
        let url = this.$config.agentServer + '/user/join_request/' + learner;
        console.warn(learner)

        return axios.get(url, {
          headers: {
            'Content-Type': 'text/plain',
            'Authorization': 'Bearer ' + this.$store.state.auth.token
          }
        })
            .then((response) => {
              console.log(response);
              return true;
            })
            .catch((error) => {
              console.warn(error.response.data)
              this.emitter.emit("notification-message", error.response.data)
              this.$refs.genericPopup.showAlertPopup(error.response.data)
              return false;
            })
      }
    }
  }
</script>
  
<style scoped>
@import "../../styles/main.css";

.wrapper {
  display: grid;
  position: relative;
  max-height: 90vh;
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: 1fr 2fr;
  grid-template-areas:
    "first second fourth"
    "first third fourth";
}

.first {
  grid-area: first;
  max-width: calc(100vw / 3);
}

.second {
  grid-area: second;
  height: 50vh;
  max-width: calc(100vw / 3);
}

.third {
  grid-area: third;
  height: 40vh;
  max-width: calc(100vw / 3);
}

.fourth {
  grid-area: fourth;
  max-width: calc(100vw / 3);
}

</style>

<style scoped src="../../styles/main.css"/>
