<!------------------------ Training Portal ------------------------
    This component is the training portal page and is loaded into the
    browser by the router (see Routes.js). The separate components are
    imported and displayed on the right places through a css grid.

-->
<template>
  <!--  <div class="wrapper2">-->
  <div class="wrapper">
    <br><br>
      <pre>  <button v-if="showFeedback" class="feedbackSub" title="continueQuestionnaires" @click="this.redirectQuestionnaires()" name = "request-session" >Continue to the questionnaires</button></pre>


    <!--<div v-if="this.phase=== null"><br><br><br><br><br><br><br><br><br></div>-->

    <div  class="sim" >
      <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>

    </div>

    <div hidden class="first" >
      <graph-component ref="graphComponent"></graph-component></div>


    <div  hidden class="second"><belief-input
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

    <div  hidden   class="third">
      <belief-transitions-component
          ref="beliefTransitions">
      </belief-transitions-component>
    </div>

    <div class="fourth"><web-chat-component
        ref="webChat"
        :header-text="`${sessionActive ? `${ sessionActive }'s Chat` : 'Chat with a virtual child'}`"
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
  <join-popup2
      ref="joinPopup2"
      @send-join-request="sendJoinRequest">
  </join-popup2>
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
import JoinPopup2 from "@/components/JoinPopup2.vue";
import GenericPopup from "@/components/GenericPopup.vue";
import axios from 'axios';
import { phaseEnumToNumber } from "@/utils";
import sessionMixin from "@/mixins/sessionMixin";
import webChatComponent from "../components/WebChatComponent.vue";

export default {
  name: 'TrainingPortal',
  computed: {
    webChatComponent() {
      return webChatComponent
    }
  },
  components: {
    GenericPopup,
    BeliefTransitionsComponent,
    GraphComponent,
    BeliefInput,
    WebChatComponent,
    JoinPopup,
    JoinPopup2
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
      privateSession: false,
      test: null,
      intentMessage: null,
      userIntents: [],
      isHovered: false,
      adviceMsg: null,
      progressMsg: null,
      childGoalMsg: null,
      childSitMsg: null,
      visibleDiv: 0,
      showFeedback: false,
      showRedo: false,
      FirstSession: true,
      Feedbackmsg: null,
      condition: null,
      knowledge: null,
      Sessnum: null
    }
  },
  created() {
    window.addEventListener("beforeunload", this.unsubscribeFromTopics);
    this.visibleDiv = this.calculateVisibleDiv();
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

  watch: {
    phase(newPhase) {
      if (newPhase === 5) {
        this.handlePhase5();
      }
    }
  },


  methods: {
    setupPrivateSessionWebsocket() {
      // Sets up the websocket to send and receive messages to/from Spring server
      this.stompClient.connect({}, () => {
        console.log('STOMP connection established');

        // This subscription is used for the message stream.
        this.stompClient.subscribe('/topic/session/' + this.getSessionID(), (message) => {
          const messageData = JSON.parse(message.body);
          this.test=message.body; //remooveee thisssssssssssssssssssssss
          for (const i in messageData) {
            this.$refs.webChat.addMessage(messageData[i].message, messageData[i].fromUser, false);

          }
        });

        // This subscription is used to process live changes in agent beliefs
        this.stompClient.subscribe('/topic/beliefs/' + this.getSessionID(), (message) => {
          this.processReceivedUpdate(JSON.parse(message.body));
          //////////// adddddddd heeeerreeee for updating the message. check!
        });

        // Subscribe to the phase updates topic
        this.addSubscription('/topic/phase/' + this.getSessionID(), (message) => {
          const messageData = JSON.parse(message.body);
          if (messageData.phaseFrom !== null) {
            if (messageData.phaseFrom !== messageData.phaseTo) {
              if(messageData.phaseTo > messageData.phaseFrom) // so that it doesnt add phase 2 again if I did undo from phase 3 fist msg to phase 2
                this.$refs.beliefInput.addPhaseToList();
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


        this.stompClient.subscribe('/topic/intent/' + this.getSessionID(), (message) => {
          this.intentMessage = message.body;
          let parts = this.intentMessage.split(';').map(part => part.trim());

// Assign the split parts to two variables
          let useIntent = parts[0];
          let usePhase = parts[1];
          //this adds it with trigger! don't add if trigger????? don't delete this with the chat messages list! as it adds one extract if the nlp doesn't recognise
          if (useIntent=== "trigger__") {
            // this.userIntents.push({
            //   UserIntent: useIntent,
            //   inPhase: usePhase,
            //   UserText: "trigger"
            // });
          }
          else {
            this.userIntents.push({
              UserIntent: useIntent,
              inPhase: usePhase,
              UserText: this.$refs.webChat.GetlastUsermsg()
            });
          }
          this.$refs.beliefInput.addToList();
        });


        // Subscribe to the advice topic


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
              this.processReceivedUpdate(beliefUpdate);
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
        msgText = ">>> TRIGGER";
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

      if(causeType!=="MANUAL"){
        this.$refs.beliefInput.updateBelList();
      }

    },
    updateBelief(beliefId, newValue) {
      //this.beliefs.find(item => item.id === belief.id).value = belief.value;
      this.sendMessage(beliefId, newValue);

    },

    handleButtonClick(button,phase,Currbeliefs) { // record each button click
      const currentBeliefsString = Currbeliefs.join(','); // Convert array to comma-separated string

      const data = {
        username: this.$store.state.auth.username,
        buttonClicked: button,
        phase: phase,
        currentBeliefs: currentBeliefsString,
        agentId: this.getSessionID()
      };


      axios.post(
          this.$config.agentServer + '/api/button-click', // or '/api/button-click/' if there's a trailing slash in the endpoint
          data,
          {
            headers: {
              'Content-Type': 'application/json',
              'Authorization': 'Bearer ' + this.$store.state.auth.token
            }
          }
      ).then(response => {
        console.log('Data successfully sent to the backend:', response.data);
      })
          .catch(error => {
            console.error('There was an error sending the data to the backend:', error);
          });
    },



    handlePhase5() {
      this.$refs.webChat.exploreMode = false;
      this.$refs.webChat.disableTextarea();
      setTimeout(() => {
        this.$refs.webChat.reachedPhase5 = true;
        this.showFeedback=true;

      }, 2000);


      //   change the popup information to the feedback information.
      //     so, the logic for the feedback should be here in this function
      // let the popup close. it only opens when they click the button with the information
      // also, check if this ths first time or the second time.
      //maybe use this counter also to show it's the first or the second time?
      // if the first time, open the popup that has the reset button, if second time, open the popup that goes to the reflection page
      // Also, send the information of the chat to the backend to save it in the database
    },

    getWhyleft(){
      if (this.$refs.beliefInput.beliefs[0].value < 0.3 ) { // too many triggers
        return 1;}
      else if (this.$refs.beliefInput.beliefs[16].value > 0.9 ) {
        return 2;
      }
      else if (this.$refs.beliefInput.beliefs[2].value < 0.5 && this.$refs.beliefInput.beliefs[11].value > 0.9 ){
        return 3;
      }
      else if (this.$refs.beliefInput.beliefs[8].value > 0.6 && this.$refs.beliefInput.beliefs[9].value < 0.1 ){
        return 4;
      }
      else if (this.$refs.beliefInput.beliefs[9].value > 0.9 && this.$refs.beliefInput.beliefs[7].value < 1 && this.$refs.beliefInput.beliefs[15].value < 0.7 ){
        return 5;
      }
      else if (this.$refs.beliefInput.beliefs[1].value > 0.9 ){
        return 6;
      }
      else if (this.$refs.beliefInput.beliefs[14].value > 0.9  ){
        return 7;
      }
      else {
        return 99;
      }
    },

    traverseAdvSitu(PhaseNum){
      //check phase first
      if (PhaseNum ===2){
        if (this.$refs.beliefInput.beliefs[3].value < 0.7){
          this.Feedbackmsg=1;
        }
        else if (this.$refs.beliefInput.beliefs[5].value < 0.3 && this.$refs.beliefInput.beliefs[4].value < 0.2){

          this.Feedbackmsg=2;
        }
        else if (this.$refs.beliefInput.beliefs[5].value < 0.3){
          this.Feedbackmsg=3;
        }
        else if(this.$refs.beliefInput.beliefs[4].value < 0.2){
          this.Feedbackmsg=4;
        }
        else if(this.$refs.beliefInput.beliefs[9].value < 1){
          this.Feedbackmsg=5;       }
        else{ // add random to give a random msg? or nothing delete?
          this.adviceMsg = "Try to clarify the child's story and ask about their desires!";
          this.Feedbackmsg=6;
        }
      }

      else if (PhaseNum ===3){
        if (this.$refs.beliefInput.beliefs[3].value < 0.7 || this.$refs.beliefInput.beliefs[4].value < 0.2){
          this.Feedbackmsg=7;
        }
        else if (!this.userIntents.some(item => item.UserIntent === "confirm_goal_summary")) {
          this.Feedbackmsg=8;
        }

        else if(this.$refs.beliefInput.beliefs[15].value < 1){
          this.Feedbackmsg=9;
        }
        else if(this.$refs.beliefInput.beliefs[11].value < 1){
          this.Feedbackmsg=10;
        }
        else{ // add random to give a random msg? or nothing delete?
          this.adviceMsg = "Try to clarify the child's wishes and goals for the conversation.";
          this.Feedbackmsg=11;
        }
      }


      else if (PhaseNum ===4){

        if (this.$refs.beliefInput.beliefs[1].value < 0.7){ ////// add another one for answering their concerns?

          this.Feedbackmsg=12;

        }

        else if (this.$refs.beliefInput.beliefs[1].value < 0.8){
          this.Feedbackmsg=13;
        }
        else if (this.$refs.beliefInput.beliefs[14].value < 1 && this.$refs.beliefInput.beliefs[1].value > 0.7){
          this.Feedbackmsg=14;
        }
        else{ // add random to give a random msg? or nothing delete?
          this.Feedbackmsg=99;
        }


      }

      else{
        this.Feedbackmsg=15;
      }
    },

    redirectQuestionnaires(){


      const prevPhase = this.$refs.beliefInput.updateLastphase();
      this.traverseAdvSitu(prevPhase);
      const whyLeft=this.getWhyleft();
      const conversationEndMSG = this.Feedbackmsg; //this is an int
      const allBeliefsString = this.$refs.beliefInput.AllBeliefList.join(';'); // Convert array to comma-separated string


      const data = {
        username: this.$store.state.auth.username,
        prevPhase: prevPhase, //int
        agentId: this.getSessionID(),
        whyleft: whyLeft,
        conversationEndMSG: conversationEndMSG,
        allBeliefs:  allBeliefsString, // this is a long string
        condition: 4

        //lastphase
        //currentbeliefvalues
        //condition
      };


      axios.post(
          this.$config.agentServer + '/api/assessment', // or '/api/button-click/' if there's a trailing slash in the endpoint
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


      //this.$router.push('/assessment');
      //redirect with username and condition


        axios.get(this.$config.agentServer +`/api/meta-experiment-user`, {
          params: {
            username: this.$store.state.auth.username
          },
        headers: {
          'Authorization': 'Bearer ' + this.$store.state.auth.token
        }
      })
          .then(response => {
            this.condition = response.data.condition;
            this.knowledge = response.data.knowledge;
            this.Sessnum= response.data.sessNum;
            const redirectUrl = `https://tudelft.fra1.qualtrics.com/jfe/form/SV_20uk6d6gWZtclIq?PROLIFIC_PID=${this.$store.state.auth.username}&Cid=${this.condition}&RandKnowledge=${this.knowledge}&SessNum=${this.Sessnum}`;
            window.location.href = redirectUrl;
          })
          .catch(error => {
            console.error('There was an error fetching the meta experiment data:', error);
          });






      //const redirectUrl = `https://tudelft.fra1.qualtrics.com/jfe/form/SV_20uk6d6gWZtclIq?PROLIFIC_PID=${this.$store.state.auth.username}&Cid=${this.condition}&RandKnowledge=${this.knowledge}$SessNum={this.Sessnum}`;
      //window.location.href = redirectUrl;
    },




    calculateVisibleDiv() {
      // Consider only the last 3 characters of the username

      const lastThreeChars = this.$store.state.auth.username.slice(-3);
      // Simple hash function to determine the div
      let hash = 0;
      for (let i = 0; i < lastThreeChars.length; i++) {
        const char = lastThreeChars.charCodeAt(i);
        hash = (hash << 5) - hash + char;
        hash |= 0; // Convert to 32bit integer
      }
      return Math.abs(hash) % 4; // Ensure the hash is a positive number and within 0-3 range

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

    testSession() {

      this.$refs.joinPopup2.openPopup();

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

.wrapper2 {
  display: grid;
  position: relative;
  max-height: 90vh;
  grid-template-columns: 2fr 1fr;
  grid-template-rows: 1fr 2fr;
  grid-template-areas:
    "first  fourth"
    "first  fourth";
}

.wrapper3 {
  display: grid;
  position: relative;
  max-height: 90vh;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 2fr;
  grid-template-areas:
    "first second fourth"
    "first third fourth";
}

.first {
  grid-area: first;
  max-width: calc(100vw / 3);
}

.sim {
  grid-area: first;
  max-width: calc(100vw / 3);
  height: 100%;
}

.join-button{
  border-radius: 16px;
  background-color: var(--chat-widget-button);
  border: none;
  color: var(--chat-widget-button-text);
  font-size: x-large;
  padding: 0.8rem;
  font-family: 'Anton', sans-serif;
  width: 80%;
  cursor: pointer;

  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);

}

.join-button2{
  border-radius: 16px;
  //background-color: var(--chat-widget-button);
  background-color: #08d047;
  border: none;
  color: var(--chat-widget-button-text);
  font-size: x-large;
  padding: 0.8rem;
  font-family: 'Anton', sans-serif;
  width: 40%;
  cursor: pointer;

  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);

}

.join-button3{
  border-radius: 16px;
  background-color: #08cad0;
  border: none;
  color: var(--chat-widget-button-text);
  font-size: x-large;
  padding: 0.8rem;
  font-family: 'Anton', sans-serif;
  width: 40%;
  cursor: pointer;

  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);

}

.feedbackSub{
  border-radius: 16px;
  background-color: #08cad0;
  border: none;
  color: var(--chat-widget-button-text);
  font-size: x-large;
  padding: 0.8rem;
  font-family: 'Anton', sans-serif;
  width: 60%;
  height: 30%;
  cursor: pointer;

  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);

}


.join-button3:disabled {
  background-color: #d3d3d3; /* Light grey background */
  cursor: not-allowed; /* Change cursor to not-allowed */
  opacity: 0.6; /* Reduce opacity */
}


.join-button2:disabled {
  background-color: #d3d3d3; /* Light grey background */
  cursor: not-allowed; /* Change cursor to not-allowed */
  opacity: 0.6; /* Reduce opacity */
}

.hello-message {
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  padding: 10px;
  margin: 10px 0;
  font-size: 50px;
}

.buttons1 {
  width: 90%;
  padding-top: 50px;
}
.buttons2 {
  width: 150%;
  padding-top: 30%;
  padding-left: 15%;
}

.join-button:hover {
  background-color: var(--chat-widget-header-button-focus);
  cursor: pointer;
}


.section-title {
  font-weight: bold;
  margin-bottom: 10px;
  padding-left: 50px;
  font-size: xx-large;
  color: floralwhite;
  text-align: center;
}

#UndoStep:hover {

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
  height: 100%;
}

</style>

<style scoped src="../../styles/main.css"/>
