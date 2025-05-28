<!------------------------ Training Portal ------------------------
    This component is the training portal page and is loaded into the
    browser by the router (see Routes.js). The separate components are
    imported and displayed on the right places through a css grid.

-->
<template>
  <!--  <div class="wrapper2">-->
  <div class="wrapper2">

    <!--<div v-if="this.phase=== null"><br><br><br><br><br><br><br><br><br></div>-->
    <div   name="col11" class="buttons1" >
      <pre>            <button v-if="showFeedback" class="feedbackSub" title="View Feedback" @click="this.openFeedback()" name = "request-session" >View feedback</button>      <button v-if="showRedo && FirstSession && showFeedback" class="join-button2" title="Continue to the second guidance session" @click="this.redoScenario()" name = "request-session" >To continue, click here to restart the conversation</button> <br> <br>   </pre>


      <div class="button-group">
        <button class="join-button2" id="ExplainSit" @click="ExplainSit"
                title="Explains what the child is thinking or feeling."
                :disabled="phase === 5 || phase === null">
          Current Child's Situation
        </button>
        <!--        <button class="join-button2" id="5Phases" @click="FivePhasesButton" title="Shows an overview of the 5-phase model" :disabled="phase === 5 || phase === null">5-Phase Model Summary</button>-->
        <!--        <div class="info-container">-->
        <!--          <div class="info-box">-->
        <!--            <i> Information about the child's thinking:</i>-->
        <!--            <p class="info-text">{{ infoText }}</p>-->
        <!--          </div>-->
        <!--        </div>-->
      </div>

      <!-- Information Display Area -->

      <br>



      <div class="info-container2">
        <div class="info-box2">
          <i><b>Five-phase Model:</b></i><br>

          <strong>Phase 1: Building rapport</strong><br>
          • <b>Objective:</b> A welcoming atmosphere and build trust.<br>
          • <b>Method:</b> Empathy, respect, sincere interest, active listening.<br><br>

          <strong>Phase 2: Clarify the child’s story</strong><br>
          • <b>Objective:</b> Get a clear view of the child’s story.<br>
          • <b>Method:</b> Ask specific questions about the child’s story (e.g., when/where something happened). This aligns with the <b>engaging</b> process in motivational interviewing. <br><br>

          <strong>Phase 3: Setting a goal for the session</strong><br>
          • <b>Objective:</b> Collaborate with the child to set the session goal.<br>
          • <b>Method:</b> Use the <b>motivational interviewing</b> process in this phase (as shown in the right).<br>

          <br>

          <strong>Phase 4: Work toward the session goal</strong><br>
          • <b>Objective:</b> Help the child benefit from the conversation.<br>
          • <b>Method:</b> Stimulate their own problem-solving skills. This aligns with the <b>planning</b> process in motivational interviewing.<br><br>

          <strong>Phase 5: Rounding off the conversation</strong><br>
          • <b>Objective:</b> Leave the child with as few  questions as possible.<br>
          • <b>Method:</b> Summarize and wrap up.
        </div>




        <div class="info-box2">
          <i><b>Motivational Interviewing:</b></i><br>
          &nbsp;&nbsp;1. <strong>Engaging</strong> – Build trust. <i>Example "What do you want to achieve in this conversation?"</i>
          &nbsp;&nbsp;2. <strong>Focusing</strong> – Identify concerns and values. <i>Examples: "So, you think that...?", "It sounds like you want..."</i>
          &nbsp;&nbsp;3. <strong>Evoking</strong> – Encourage the child to explore motivations. <i>Examples: "Why do you think...?", "What would happen if you...."</i>
          &nbsp;&nbsp;4. <strong>Planning</strong> – Support finding solutions. <i>Examples: "Other ways to ... ?", "What small steps could you take?"</i><br>
        </div>


      </div>



      <!--      <p class="section-title">-&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45; Change the conversation -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;</p>-->
      <!--      <div class="button-group">-->
      <!--        <button class="join-button3" id="UndoStep" @click="UndoStep" title="Deletes your last message in the conversation" :disabled="phase === 5 || phase === null">Undo My Last Message</button>-->
      <!--        <button class="join-button3" id="UndoPhase" @click="UndoPhase" title="Goes back to the start of the current phase in the 5-phase model" :disabled="phase === 5 || phase === null">Restart the Phase</button>-->
      <!--      </div>-->

      <!--      <p class="section-title">-&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45; Information about conversation -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;</p>-->
      <!--      <div class="button-group">-->
      <!--        <button class="join-button2" id="WhatNext" @click="WhenNext" title="Shows your progress in the current phase and how to proceed to the next phase." :disabled="phase === 5 || phase === null">Current Phase Progress</button>-->
      <!--        <button class="join-button2" id="advice" @click="GiveAdvice" title="Gives you a hint about what you should say next in the conversation." :disabled="phase === 5 || phase === null">Advice on Next Message</button>-->
      <!--      </div>-->

      <!--      <div class="button-group">-->
      <!--        <button class="join-button2" id="ExplainSit" @click="ExplainSit" title="Explains what the child is thinking or feeling." :disabled="phase === 5 || phase === null">Current Child's Situation</button>-->
      <!--        <button class="join-button2" id="ChildGoals" @click="childGoal" title="Shows what the child wants to achieve in the conversation." :disabled="phase === 5 || phase === null">Current Child's Goal</button>-->
      <!--      </div>-->

      <!--      <div class="button-group">-->
      <!--        <button class="join-button2" id="IncUtterances" @click="IncUtterances" title="Displays messages that might be better suited for a different phase than the one you wrote them in." :disabled="phase === 5 || phase === null">View Messages Evaluation</button>-->
      <!--        <button class="join-button2" id="5Phases" @click="FivePhasesButton" title="Shows an overview of the 5-phase model" :disabled="phase === 5 || phase === null">5-Phase Model Summary</button>-->
      <!--      </div>-->

    </div>


    <!--  <div v-if="visibleDiv === 1"  name="col12" class="buttons1">-->
    <!--    <pre>            <button v-if="showFeedback" class="feedbackSub" title="View Feedback" @click="this.openFeedback()" name = "request-session" >View feedback</button>      <button v-if="showRedo && FirstSession && showFeedback" class="join-button2" title="Continue to the second guidance session" @click="this.redoScenario()" name = "request-session" >To continue, click here to restart the conversation</button>   </pre>-->

    <!--    <p class="section-title">-&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45; Information about conversation -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;</p>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button2" id="ChildGoals" @click="childGoal" title="Shows what the child wants to achieve in the conversation." :disabled="phase === 5 || phase === null">Current Child's Goal</button>-->
    <!--      <button class="join-button2" id="ExplainSit" @click="ExplainSit" title="Explains what the child is thinking or feeling." :disabled="phase === 5 || phase === null">Current Child's Situation</button>-->
    <!--    </div>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button2" id="advice" @click="GiveAdvice" title="Gives you a hint about what you should say next in the conversation." :disabled="phase === 5 || phase === null">Advice on Next Message</button>-->
    <!--      <button class="join-button2" id="WhatNext" @click="WhenNext" title="Shows your progress in the current phase and how to proceed to the next phase." :disabled="phase === 5 || phase === null">Current Phase Progress</button>-->
    <!--    </div>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button2" id="5Phases" @click="FivePhasesButton" title="Shows an overview of the 5-phase model" :disabled="phase === 5 || phase === null">5-Phase Model Summary</button>-->
    <!--      <button class="join-button2" id="IncUtterances" @click="IncUtterances" title="Displays messages that might be better suited for a different phase than the one you wrote them in." :disabled="phase === 5 || phase === null">View Messages Evaluation</button>-->
    <!--    </div>-->

    <!--    <p class="section-title">-&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45; Change the conversation -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;</p>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button3" id="UndoPhase" @click="UndoPhase" title="Goes back to the start of the current phase in the 5-phase model" :disabled="phase === 5 || phase === null">Restart the Phase</button>-->
    <!--      <button class="join-button3" id="UndoStep" @click="UndoStep" title="Deletes your last message in the conversation" :disabled="phase === 5 || phase === null">Undo My Last Message</button>-->
    <!--    </div>-->

    <!--  </div>-->


    <!--  <div  v-if="visibleDiv === 2"  name="col13" class="buttons1">-->
    <!--    <pre>            <button v-if="showFeedback" class="feedbackSub" title="View Feedback" @click="this.openFeedback()" name = "request-session" >View feedback</button>      <button v-if="showRedo && FirstSession && showFeedback" class="join-button2" title="Continue to the second guidance session" @click="this.redoScenario()" name = "request-session" >To continue, click here to restart the conversation</button>   </pre>-->

    <!--    <p class="section-title">-&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45; Change the conversation -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;</p>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button3" id="UndoPhase" @click="UndoPhase" title="Goes back to the start of the current phase in the 5-phase model" :disabled="phase === 5 || phase === null">Restart the Phase</button>-->
    <!--      <button class="join-button3" id="UndoStep" @click="UndoStep" title="Deletes your last message in the conversation" :disabled="phase === 5 || phase === null">Undo My Last Message</button>-->
    <!--    </div>-->

    <!--    <p class="section-title">-&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45; Information about conversation -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;</p>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button2" id="5Phases" @click="FivePhasesButton" title="Shows an overview of the 5-phase model" :disabled="phase === 5 || phase === null">5-Phase Model Summary</button>-->
    <!--      <button class="join-button2" id="IncUtterances" @click="IncUtterances" title="Displays messages that might be better suited for a different phase than the one you wrote them in." :disabled="phase === 5 || phase === null">View Messages Evaluation</button>-->
    <!--    </div>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button2" id="ChildGoals" @click="childGoal" title="Shows what the child wants to achieve in the conversation." :disabled="phase === 5 || phase === null">Current Child's Goal</button>-->
    <!--      <button class="join-button2" id="ExplainSit" @click="ExplainSit" title="Explains what the child is thinking or feeling." :disabled="phase === 5 || phase === null">Current Child's Situation</button>-->
    <!--    </div>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button2" id="advice" @click="GiveAdvice" title="Gives you a hint about what you should say next in the conversation." :disabled="phase === 5 || phase === null">Advice on Next Message</button>-->
    <!--      <button class="join-button2" id="WhatNext" @click="WhenNext" title="Shows your progress in the current phase and how to proceed to the next phase." :disabled="phase === 5 || phase === null">Current Phase Progress</button>-->
    <!--    </div>-->


    <!--  </div>-->

    <!--  <div  v-if="visibleDiv === 3"  name="col13" class="buttons1">-->
    <!--    <pre>            <button v-if="showFeedback" class="feedbackSub" title="View Feedback" @click="this.openFeedback()" name = "request-session" >View feedback</button>      <button v-if="showRedo && FirstSession && showFeedback" class="join-button2" title="Continue to the second guidance session" @click="this.redoScenario()" name = "request-session" >To continue, click here to restart the conversation</button>   </pre>-->

    <!--    <p class="section-title">-&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45; Information about conversation -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;</p>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button2" id="ExplainSit" @click="ExplainSit" title="Explains what the child is thinking or feeling." :disabled="phase === 5 || phase === null">Current Child's Situation</button>-->
    <!--      <button class="join-button2" id="ChildGoals" @click="childGoal" title="Shows what the child wants to achieve in the conversation." :disabled="phase === 5 || phase === null">Current Child's Goal</button>-->
    <!--    </div>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button2" id="WhatNext" @click="WhenNext" title="Shows your progress in the current phase and how to proceed to the next phase." :disabled="phase === 5 || phase === null">Current Phase Progress</button>-->
    <!--      <button class="join-button2" id="advice" @click="GiveAdvice" title="Gives you a hint about what you should say next in the conversation." :disabled="phase === 5 || phase === null">Advice on Next Message</button>-->
    <!--    </div>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button2" id="IncUtterances" @click="IncUtterances" title="Displays messages that might be better suited for a different phase than the one you wrote them in." :disabled="phase === 5 || phase === null">View Messages Evaluation</button>-->
    <!--      <button class="join-button2" id="5Phases" @click="FivePhasesButton" title="Shows an overview of the 5-phase model" :disabled="phase === 5 || phase === null">5-Phase Model Summary</button>-->
    <!--    </div>-->


    <!--    <p class="section-title">-&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45; Change the conversation -&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;</p>-->
    <!--    <div class="button-group">-->
    <!--      <button class="join-button3" id="UndoStep" @click="UndoStep" title="Deletes your last message in the conversation" :disabled="phase === 5 || phase === null">Undo My Last Message</button>-->
    <!--      <button class="join-button3" id="UndoPhase" @click="UndoPhase" title="Goes back to the start of the current phase in the 5-phase model" :disabled="phase === 5 || phase === null">Restart the Phase</button>-->
    <!--    </div>-->

    <!--  </div>-->


    <div hidden class="first" >
      <graph-component ref="graphComponent"></graph-component></div>

    <div hidden  class="first">
      {{this.userIntents}}
      <!--      {{this.wrongUtterances}}-->
      <div  v-if="showFeedback"  name="col13" class="buttons2">
        <pre>            <button v-if="showFeedback" class="feedbackSub" title="View Feedback" @click="this.openFeedback()" name = "request-session" >View feedback</button>      <button v-if="showRedo && FirstSession && showFeedback" class="join-button2" title="Continue to the second guidance session" @click="this.redoScenario()" name = "request-session" >To continue, click here to restart the conversation</button>   </pre>

      </div>
      <pre><button @mouseover="showInfo" @mouseleave="hideInfo" class="join-button" id="UndoStep" @click="UndoStep" >Undo Last Message</button> </pre> <br>
      <pre><button class="join-button" id="advice" @click="GiveAdvice" >Advice on Next Message</button> </pre><br>
      <pre><button class="join-button" id="ExplainSit" @click="ExplainSit" >Current Child's Situation</button> </pre><br>
      <pre><button class="join-button" id="UndoPhase" @click="UndoPhase" >Restart the Phase</button> </pre><br>
      <pre><button class="join-button" id="WhatNext" @click="WhenNext" >Current Phase Progress</button> </pre><br>
      <pre><button class="join-button" id="ChildGoals"  @click="childGoal">Current Child's Goal</button> </pre> <br>
      <pre><button class="join-button" id="IncUtterances"  @click="IncUtterances">View Messages Evaluation</button> </pre> <br>

      <pre><button class="join-button" id="5Phases" @click="FivePhasesButton" title="Shows a summary of the 5-phase model" >5-Phase Model Summary</button> </pre>

      <!--      <div v-if="isHovered" class="info-box">-->
      <!--        More Information-->
      <!--      </div>-->

    </div>
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

    <div class="fourth">
      <div  class="timer"> <strong>Session Timer:</strong> {{ formattedTime }}</div>
      <web-chat-component
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
    },
    formattedTime() {
      const minutes = Math.floor(this.timer / 60).toString().padStart(2, '0');
      const seconds = (this.timer % 60).toString().padStart(2, '0');
      return `${minutes}:${seconds}`;
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
      infoText: "Click the button on the left to update this text.",
      phase: null,
      lastTransition: null,
      previousValues: {},
      sessionActive: null,
      subscriptions: [],
      stompClient: null,
      privateSession: false,
      test: null,
      timer: 0, // in seconds
      intervalId: null,
      intentMessage: null,
      userIntents: [],
      isHovered: false,
      adviceMsg: null,
      progressMsg: null,
      childGoalMsg: null,
      childSitMsg: null,
      phase2Intents: [
        "request_chitchat_greeting",
        "request_chitchat_faring",
        "trigger_unknown_what",
        "request_bullying_who",
        "request_unknown_who",
        "request_bullying_details",
        "request_unknown_details",
        "request_bullying_count",
        "request_bullying_location",
        "request_unknown_location",
        "request_bullying_duration",
        "request_unknown_duration",
        "request_bullying_frequency",
        "request_unknown_frequency",
        "request_unknown_when",
        "request_bullying_why",
        "request_unknown_why",
        "request_bullying_response",
        "request_unknown_response",
        "request_unknown_feeling",
        "request_bullying_confidant",
        "request_unknown_confidant",
        "request_bullying_parent",
        "confirm_bullying_summary"
      ],
      phase3Intents: [
        "request_goal_what",
        "request_goal_dream",
        "request_goal_feeling",
        "confirm_goal_summary",
        "confirm_goal_collaborate",
        "request_goal_howchild",
        "request_unknown_how",
        "request_confidant_who"
      ],
      phase4Intents: [
        "confirm_confidant_teacher",
        "confirm_confidant_parent",
        "request_confidant_when",
        "request_confidant_feeling",
        "request_confidant_why",
        "request_confidant_how",
        "inform_confidant_help",
        "inform_confidant_say",
        "request_confidant_say",
        "confirm_confidant_summary",
        "request_chitchat_end",
        "confirm_chitchat_satisfaction",
        "request_chitchat_goodbye"
      ],
      wrongUtterances: [],
      visibleDiv: 0,
      showFeedback: false,
      showRedo: false,
      FirstSession: true,
      Feedbackmsg: null
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
    // Set a timer for 5 minutes (300,000 milliseconds)
    setTimeout(() => {
      this.redirectToNextPage();
    }, 780000); // 900000 = 15 minutes
    this.startTimer(); // Start when component loads
  },
  beforeUnmount() {
    console.log("\u001B[31mTrainingPortal just unmounted\u001B[0m")
    this.unsubscribeFromTopics();
    this.disconnectWebsocket();
    this.stopTimer(); // Clean up when leaving page
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
        agentId: this.getSessionID(),
        firstSession: this.FirstSession,
        condition: 3
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


    recordReflection(){

      this.$router.push('/reflection');

    },

    handlePhase5() {
      this.$refs.webChat.disableTextarea();
      setTimeout(() => {
        this.$refs.webChat.reachedPhase5 = true;
        this.showFeedback=true;
        this.$refs.webChat.Fdbkmsg= true;

      }, 2000);


      //   change the popup information to the feedback information.
      //     so, the logic for the feedback should be here in this function
      // let the popup close. it only opens when they click the button with the information
      // also, check if this ths first time or the second time.
      //maybe use this counter also to show it's the first or the second time?
      // if the first time, open the popup that has the reset button, if second time, open the popup that goes to the reflection page
      // Also, send the information of the chat to the backend to save it in the database
    },

    openFeedback(){

      this.$refs.joinPopup2.headerText= "View feedback";
      this.$refs.webChat.Fdbkmsg= false;

      this.Feedbackmsg="";
      const prevPhase = this.$refs.beliefInput.updateLastphase();
      this.traverseAdvSitu(prevPhase);
      // maybe check the value of feedback here, then if its the same, add another function that checks on beliefs values?

      // if (this.$refs.beliefInput.beliefs[8].value > 0.7 && this.$refs.beliefInput.beliefs[9].value < 0.1 && this.$refs.beliefInput.beliefs[5].value >= 0.3) { // too many triggers
      //   this.Feedbackmsg= this.Feedbackmsg + "The child left because they felt they were doing most of the explaining. Clarify their situation by asking more specific questions (e.g., when, where, how often) instead of open-ended questions that can overwhelm them." ;
      // }

      if(prevPhase ===2) {
        if (this.$refs.beliefInput.beliefs[11].value > 0.9) {
          this.Feedbackmsg= this.Feedbackmsg + " At the beginning, the child didn't feel connected enough to open up and discuss solutions. They felt dismissed, making them hesitant to continue the discussion. They value talking to someone who genuinely cares about others." ;
        }
      }

      if(prevPhase ===3) {
        if (this.userIntents.some(item => item.UserIntent === "inform_goalhitstop_positive")) {
          this.Feedbackmsg = this.Feedbackmsg + "Also, the helpline doesn't give direct advice on what to do (e.g., whether to tell or not to tell someone). Instead, a counselor should encourage the child to find a goal they are fine with.";
        }
      }
      if(this.userIntents.some(item => item.UserIntent === "inform_goal_negative" || item.UserIntent === "inform_unknown_negative" || item.UserIntent === "inform_goalhitstop_negative")){
        this.Feedbackmsg= this.Feedbackmsg + " And you should avoid using negative expressions such as 'you shouldn't stay quite' or 'you shouldn't do this', as it can discourage the child. The child interpreted such statements as limiting their kindness to others." ;
      }

      this.showRedo=true;

      this.$refs.joinPopup2.openPopupwMsg(this.Feedbackmsg);
      this.infoText= "Click the button to see information about the child's thinking.";
    },

    redoScenario(){
      //if first is true, then empty all lists (beliefs list, intent lists and the conversation lists, and go back to the first (second?) phase) and unable the textarea and remove the msg that the child left
      // change first session to false (inside the if)
      // if false, then redirect to the reflection page (maybe have another button that shows if this is false or true)



//navigate to phase 2
      this.$refs.beliefInput.switchToPhase(1);
      this.$refs.webChat.clearConversation();
      this.$refs.webChat.reachedPhase5 = false;
      this.$refs.webChat.enableTextarea();
      this.$refs.beliefInput.AllBeliefList=[[0.5,0.3,0.4,0.6,0,0,0.7,1,0,0,0,0,0.5,1,0,0,0]];
      this.$refs.beliefInput.lastPhase=2;
      this.$refs.beliefInput.currBeliefs=[];
      this.userIntents=[];
      this.showFeedback=false;
      this.$refs.webChat.clearConversation();

      this.showRedo=false;
      // this.FirstSession=false;
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

    checkIfPhaseIsInList(){
      const lastElement = this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1];
      // const BeforelastElement = this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 2];

      // const ThirdlastElement = this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 3];
      if (lastElement === "phase2" || lastElement === "phase3" || lastElement === "phase4" || lastElement === "phase5") {
        this.$refs.beliefInput.AllBeliefList.splice(this.$refs.beliefInput.AllBeliefList.length-1, 1);
        //this.$refs.beliefInput.AllBeliefList.splice(this.$refs.beliefInput.AllBeliefList.length-1, 1);
        // if (BeforelastElement === "phase2" || BeforelastElement === "phase3" || BeforelastElement === "phase4") {
        //   const secondToLastIndex = this.$refs.beliefInput.AllBeliefList.length - 2;
        //   this.$refs.beliefInput.AllBeliefList.splice(secondToLastIndex, 1);
        // }
        // if (ThirdlastElement === "phase2" || ThirdlastElement === "phase3" || ThirdlastElement === "phase4") {
        //   const ThirdToLastIndex = this.$refs.beliefInput.AllBeliefList.length - 2;
        //   this.$refs.beliefInput.AllBeliefList.splice(ThirdToLastIndex, 1);
        // }
        // if (BeforelastElement === "phase2" || BeforelastElement === "phase3" || BeforelastElement === "phase4") {
        //   const secondToLastIndex = this.$refs.beliefInput.AllBeliefList.length - 1;
        //   this.$refs.beliefInput.AllBeliefList.splice(secondToLastIndex, 1);
        // }
      }
      // else if (BeforelastElement === "phase2" || BeforelastElement === "phase3" || BeforelastElement === "phase4") {
      //   const secondToLastIndex = this.$refs.beliefInput.AllBeliefList.length - 2;
      //   this.$refs.beliefInput.AllBeliefList.splice(secondToLastIndex, 1);
      // }


    },
    updateAllBeliefs() { // update the values of the beliefs to the one before
      this.checkIfPhaseIsInList(); // if the last element is phase2,3,4 then remove it and go to the one before it
      const BeforelastElement = this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 2]; // check of (..., number, phase 2, numbers) then take the numbers before phase 2
      if (BeforelastElement === "phase2" || BeforelastElement === "phase3" || BeforelastElement === "phase4" || BeforelastElement === "phase5") {
        let i;
        for (i=1;i<17;i++) {
          if ((this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length-3][i-1] !== this.$refs.beliefInput.currBeliefs[i-1]) && i!==3)
          {
            //try to assign the value of i-2 and then delete i-1! so change the order from the above (other) function
            this.updateBelief("B"+i, this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length-3][i-1]);
            //this.updateBelief("B4", this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1][3]);
            //this.updateBelief("B1", this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1][0]);
          }
        }
      }
      else{
        let i;
        for (i=1;i<17;i++) {
          if ((this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length-2][i-1] !== this.$refs.beliefInput.currBeliefs[i-1]) && i!==3)
          {
            //try to assign the value of i-2 and then delete i-1! so change the order from the above (other) function
            this.updateBelief("B"+i, this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length-2][i-1]);
            //this.updateBelief("B4", this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1][3]);
            //this.updateBelief("B1", this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1][0]);
          }
        }
      }
    },
    updateAllBeliefsBforPhase() { // update the values of the beliefs to the one before

      // const BeforelastElement = this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 2]; // check of (..., number, phase 2, numbers) then take the numbers before phase 2
      // if (BeforelastElement === "phase2" || BeforelastElement === "phase3" || BeforelastElement === "phase4" || BeforelastElement === "phase5") {
      //   let i;
      //   for (i=1;i<17;i++) {
      //     if ((this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length-3][i-1] !== this.$refs.beliefInput.currBeliefs[i-1]) && i!==3)
      //     {
      //       //try to assign the value of i-2 and then delete i-1! so change the order from the above (other) function
      //       this.updateBelief("B"+i, this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length-3][i-1]);
      //       //this.updateBelief("B4", this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1][3]);
      //       //this.updateBelief("B1", this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1][0]);
      //     }
      //   }
      // }
      // else{
      let i;
      for (i=1;i<17;i++) {
        if ((this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length-2][i-1] !== this.$refs.beliefInput.currBeliefs[i-1]) && i!==3)
        {
          //try to assign the value of i-2 and then delete i-1! so change the order from the above (other) function
          this.updateBelief("B"+i, this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length-2][i-1]);
          //this.updateBelief("B4", this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1][3]);
          //this.updateBelief("B1", this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1][0]);
        }
      }
      // }

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

    FivePhasesButton() {
      this.$refs.joinPopup2.headerText= "5-Phase Model Summary";
      this.handleButtonClick(8,this.$refs.beliefInput.phase,this.$refs.beliefInput.currBeliefs);

      this.$refs.joinPopup2.phasesDiv();
      this.$refs.joinPopup2.openPopup();
    },
    UndoStep() { // test it that everything works well!!
      this.handleButtonClick(1,this.$refs.beliefInput.phase,this.$refs.beliefInput.currBeliefs);
      this.$refs.joinPopup2.headerText= "Undo Last Message";
      if(this.$refs.beliefInput.AllBeliefList.length<2){
        this.$refs.joinPopup2.openPopupwMsg("You can't go back to the previous step.");}
      else {
        this.updateAllBeliefs();
        this.$refs.beliefInput.popBelList();
        //assign values
        //this.updateBelief("B1",1);
        this.$refs.joinPopup2.openPopupwMsg("You are back to the previous step.");

        //let deletedMSGs=this.$refs.webChat.deleteMessageUndo(); // returns list of deleted msgs
        // this.userIntents=this.$refs.webChat.deleteMessageUndo(); // returns list of deleted msgs
        this.removeDeletedMessages();
      }

    },
    removeDeletedMessages() {

      let deletedMSGs=this.$refs.webChat.deleteMessageUndo(this.$refs.beliefInput.phase); // returns list of deleted msgs

      deletedMSGs.forEach(deletedMsg => {
        // Check if the last element's UserText matches the deletedMsg
        let lastIndex = this.userIntents.length - 1;
        if (this.userIntents[lastIndex] && this.userIntents[lastIndex].UserText === deletedMsg) {
          // Remove from userIntents and deletedMSGs
          this.userIntents.splice(lastIndex, 1);
        } else {
          // Check the last three elements
          let found = false;
          for (let i = lastIndex; i >= Math.max(0, lastIndex - 2); i--) {
            if (this.userIntents[i] && this.userIntents[i].UserText === deletedMsg) {
              this.userIntents.splice(i, 1);
              found = true;
              break;
            }
          }
          if (found) {
            deletedMSGs = deletedMSGs.filter(msg => msg !== deletedMsg);
          }
        }
      });
      // Clean up deletedMSGs to remove processed messages
      deletedMSGs = deletedMSGs.filter(deletedMsg => {
        return this.userIntents.some(intent => intent.UserText === deletedMsg);
      });
    },

    GiveAdvice() {
      this.$refs.joinPopup2.headerText = "Advice on Next Message";

      this.traverseAdvSitu(this.$refs.beliefInput.phase);
      this.handleButtonClick(2,this.$refs.beliefInput.phase,this.$refs.beliefInput.currBeliefs);

      this.$refs.joinPopup2.openPopupwMsg(this.adviceMsg);


      //this.$refs.joinPopup2.openPopupwMsg(this.intentMessage);
      // const sessionId = this.getSessionID(); // Assuming this method gets the current session ID
      // // Make an HTTP request to the backend to send advice
      // axios.get(this.$config.agentServer + '/topic/sendAdvice/'  + sessionId)
      //     .then(response => {
      //       console.log('Advice message received:', response.data);
      //       this.$refs.joinPopup2.openPopupwMsg(response.data);
      //     })
      //     .catch(error => {
      //       console.error('Error receiving hello world message:', error);
      //     });

    },
    ExplainSit() {
      this.$refs.joinPopup2.headerText= "Current Child's Situation";
      this.traverseAdvSitu(this.$refs.beliefInput.phase);
      //do the if statement for D2 to see id they are close to hang up
      if(this.$refs.beliefInput.beliefs[0].value === 0.3){
        this.childSitMsg= this.childSitMsg + " Also, the child is starting to feel that they are not under control of the conversation.";
      }

      // already in the child goals
      // else if(this.$refs.beliefInput.beliefs[2].value < 0.6 && this.$refs.beliefInput.beliefs[11].value < 0.6){
      //   this.childSitMsg= this.childSitMsg + " On the other hand, the child is starting to feel that they are not under control of the conversation. They are also starting to feel that they are not under control of the conversation.";
      // }

      this.handleButtonClick(3,this.$refs.beliefInput.phase,this.$refs.beliefInput.currBeliefs);
      this.infoText= this.childSitMsg;

      this.$refs.joinPopup2.openPopupwMsg(this.childSitMsg);
    },

    traverseAdvSitu(PhaseNum){
      //check phase first
      if (PhaseNum ===2){
        if (this.$refs.beliefInput.beliefs[3].value < 0.7){
          this.childSitMsg="The child does not trust you yet and does not know if you are interested in their problem. They see that opening up might harm others or the bullies.";
          // this.progressMsg="You completed 0% of phase 2 (clarifying the child’s story). \n" +
          //     "This phase requires building rapport and trust with the child by showing empathy and understanding of their problem. This will make the child more open to discussing goals for their problem in phase 3 (Setting the session’s goal).";
          this.Feedbackmsg="You need to show more empathy and welcoming at the beginning in phases 1 and 2 to build trust and acknowledge the child's concerns, as the child feels opening up quickly lead to you calling their school and harming their peers.";
        }
        else if (this.$refs.beliefInput.beliefs[5].value < 0.2 && this.$refs.beliefInput.beliefs[4].value < 0.1){
          if (Math.random() < 0.5) {
            this.adviceMsg = "Ask specific questions to clarify details of the child's story. This can show your interest and willingness to help.";
          } else {
            this.adviceMsg = "Show empathy towards the child's story. This can build trust and acknowledge their concerns.";
          }
          this.childSitMsg="The child has started to trust you more. However, they are still not sure if you are interested in or understand their problem. They hesitate to open up because they feel that they need you to understand that they are also worried about others' well-being.";
          this.progressMsg="You completed 20% of phase 2 (clarifying the child’s story). \n" +
              "This phase requires building rapport and trust with the child by showing empathy and understanding of their problem. This will make the child more open to discussing goals for their problem in phase 3 (Setting the session’s goal).";
          this.Feedbackmsg="You need to show more empathy and warmth in phases 1 and 2 to build trust and acknowledge the child's concerns. The child was not sure if you are interested in their problem, and needs you to understand their worries about well-being of others.";
        }
        else if (this.$refs.beliefInput.beliefs[5].value < 0.2){
          this.adviceMsg = "Ask specific questions about the child's story to clarify details of the story. This can show your interest and willingness to help.";
          this.childSitMsg="The child trusts you now. However, they are still not sure if you are interested in their problem. They feel uncertain if you understand the situation, and  needs you to understand their worries about well-being of others.";
          this.progressMsg="You completed 40% of phase 2 (clarifying the child’s story). \n" +
              "This phase requires building rapport and trust with the child by showing empathy and understanding of their problem. This will make the child more open to discussing goals for their problem in phase 3 (Setting the session’s goal).";
          this.Feedbackmsg="In phase 2, ask more specific questions about the child's story (e.g., when, where, how often) instead of open-ended questions that could overwhelm them. The virtual child felt uncertain about your understanding and interest in their problem, and  they want to see if you recognize their emotions and care about their well-being.";

        }
        else if(this.$refs.beliefInput.beliefs[4].value < 0.1){
          this.adviceMsg = "Show empathy towards the child's story. This can build trust and acknowledge their concerns.";
          this.childSitMsg="The child trusts you now. However, they are still not sure if you are interested in their problem. They feel uncertain if you don’t recognize their efforts to handle the situation, so they need to feel that their concerns are validated by others, just as they care for others.";
          this.progressMsg="You completed 60% of phase 2 (clarifying the child’s story). \n" +
              "This phase requires building rapport and trust with the child by showing empathy and understanding of their problem. This will make the child more open to discussing goals for their problem in phase 3 (Setting the session’s goal).";
          this.Feedbackmsg="Show more empathy in phase 2 to build trust and acknowledge the child's concerns. The virtual child was unsure if you understood their problem, and whether you recognize their efforts to handle the situation.";

        }
        else if(this.$refs.beliefInput.beliefs[9].value < 1){
          this.adviceMsg = "Ask the child about their goals and what they wish to achieve in this conversation to show your interest in their goal.";
          this.childSitMsg="The child trusts you now and thinks that you understand them. However, they don't know what to do next in this conversation. They feel stuck because they are unsure how to ensure that the situation ends fairly for everyone, where no one is hurt, including the bullies.";
          this.progressMsg="You completed 80% of phase 2 (clarifying the child’s story). \n" +
              "This phase requires building rapport and trust with the child by showing empathy and understanding of their problem. This will make the child more open to discussing goals for their problem in phase 3 (Setting the session’s goal).";
          this.Feedbackmsg="In phase 2, ask the child about their goals and what they wish to achieve to show your interest and guide the conversation to the next phase. The child wants to feel empowered while ensuring their actions do not harm others.";
        }
        else{ // add random to give a random msg? or nothing delete?
          this.adviceMsg = "Try to clarify the child's story and ask about their desires!";
          this.childSitMsg="The child needs time to build trust, as they want to feel empowered while ensuring their actions do not harm others, including the bullies.";

        }
      }

      else if (PhaseNum ===3){
        // if (this.$refs.beliefInput.beliefs[3].value < 0.7 || this.$refs.beliefInput.beliefs[4].value < 0.1){
        //   this.adviceMsg = "Don’t forget to show empathy and warmth to help the child feel more comfortable and open up. Also, ask the child about their story.";
        //   this.childSitMsg="The child has started to trust you more. However, they are still not sure if you are interested in and understand their problem.";
        //   this.progressMsg="You completed 0% of phase 3 (Setting the session’s goal). \n " +
        //       "This phase requires collaboratively exploring possible solutions to the child's problem and identifying desired outcomes and goals. Then, the child can plan and think about how to achieve the goal in phase 4 (Working towards the session goal).";
        //   this.Feedbackmsg="Show more empathy and ask more specific questions in phase 2 to build trust and acknowledge the child's concerns. The virtual child was unsure if you understood their problem.";
        //
        // }
        // // else if (!this.userIntents.some(item => item.UserIntent === "confirm_goal_summary")) {
        //   this.adviceMsg = "Summarize the child's goal and confirm it, to show you understand and are interested in their goals.";
        //   this.childSitMsg = "The child trusts you now and thinks that you understand them. But they don't know what to do next.";
        //   this.progressMsg="You completed 25% of phase 3 (Setting the session’s goal). \n " +
        //       "This phase requires collaboratively exploring possible solutions to the child's problem and identifying desired outcomes and goals. Then, the child can plan and think about how to achieve the goal in phase 4 (Working towards the session goal).";
        //   this.Feedbackmsg="In phase 3, summarize and confirm the child's goal to show you understand and are interested. The virtual child was unsure what to do next.";
        //
        // }

        if(this.$refs.beliefInput.beliefs[21].value > 0.75){ // have misconception, needs to show more focusing and engagement
          this.adviceMsg = "Suggest working together with the child to find a goal for the conversation. This can strengthen your connection and show how you can help.";
          this.childSitMsg = "The child holds a misconception—that not telling anyone will show kindness to others and insure that others are safe."; // change this with the hit back scenarios
          this.progressMsg="You completed 50% of phase 3 (Setting the session’s goal). \n " +
              "This phase requires collaboratively exploring possible solutions to the child's problem and identifying desired outcomes and goals. Then, the child can plan and think about how to achieve the goal in phase 4 (Working towards the session goal).";
          this.Feedbackmsg="In phase 3, try to apply the motivational interviewing processing such as focusing and evoking. This will help you to understand the child's concerns and motivations. The virtual child misunderstood how to solve their problem and thought that not telling is the only solution that they feel comfortable with and aligns with their values.";
        }


            //   if B[17] < B[20] they haven't considered the alternative more yet
        // if (Math.random() < 0.5) {
        else if(this.$refs.beliefInput.beliefs[22].value >=  this.$refs.beliefInput.beliefs[23].value){
          this.adviceMsg = "Ask the child if there is someone they trust and can share the problem with. This can stimulate their problem-solving skills";
          this.childSitMsg = "The child thinks that not telling someone is the only solution that aligns with their desires and values. They have not considered the consequences of such an action.";
          this.progressMsg = "You completed 75% of phase 3 (Setting the session’s goal). \n " +
              "This phase requires collaboratively exploring possible solutions to the child's problem and identifying desired outcomes and goals. Then, the child can plan and think about how to achieve the goal in phase 4 (Working towards the session goal).";
          this.Feedbackmsg = "In phase 3, try to apply the motivational interviewing processing such as focusing and evoking. They have not considered the consequences of such an action and their motivations behind it. Try to explore the core values and concerns of the child.";

        }

        else if(this.$refs.beliefInput.beliefs[21].value <  this.$refs.beliefInput.beliefs[24].value){
          this.childSitMsg = "The child thought about the consequences of not telling that the bullying might not stop even for others, and they want to avoid that! Try to prime them to think about a different solution.";
          this.Feedbackmsg = "In phase 3, try to apply the motivational interviewing processing such as focusing and evoking. The child has not considered the consequences of such an action and other ways to tackle it. Try to explore the core values and concerns of the child and encourage them to think about a different solution.";

        }


        else{ // add random to give a random msg? or nothing delete?
          // the child started to convince that the old goal might blah blah, but needs more motivation and exploration of alternative goal
          this.childSitMsg = "The child thought about the consequences of not telling that it could harm themselves and others, and they want to avoid that! Try to prime them to think about a different solution.";
          this.Feedbackmsg = "In phase 3, try to apply the motivational interviewing processing such as focusing and evoking. The child has not considered the consequences and motivations of such an action and other ways to tackle it. Try to explore the core values and concerns of the child and encourage them to think about a different solution.";
        }
      }


      else if (PhaseNum ===4){
        // if B[1] < 0.5 try to motivate them and plan
        //if B[1] < 0.7 try to motivate them and plan ready to wrap up
        if (this.$refs.beliefInput.beliefs[1].value < 0.7){ ////// add another one for answering their concerns?
          this.childSitMsg = "The child has now started to consider telling someone as a solution, as it demonstrates kindness while avoiding hurting others. However, they still need support and encouragement in planning how to achieve this goal";

          this.Feedbackmsg="In phase 4, try to apply the planning process in motivational interviewing. The virtual child needs support and encouragement in planning and thinking about how to carry out the new goal of telling someone.";

          if (this.$refs.beliefInput.beliefs[1].value < 0.3)
            this.progressMsg="You completed 0% of phase 4 (Working towards the session goal). \n "+
                "This phase requires ensuring the child has the next steps to apply the agreed goal of talking to someone. If the child is confident enough and willing to apply this goal, then you can wrap up the conversation in phase 5";
          else if (this.$refs.beliefInput.beliefs[1].value < 0.5)
            this.progressMsg="You completed 40% of phase 4 (Working towards the session goal). \n "+
                "This phase requires ensuring the child has the next steps to apply the agreed goal of talking to someone. If the child is confident enough and willing to apply this goal, then you can wrap up the conversation in phase 5";
          else{
            this.progressMsg="You completed 80% of phase 4 (Working towards the session goal). \n "+
                "This phase requires ensuring the child has the next steps to apply the agreed goal of talking to someone. If the child is confident enough and willing to apply this goal, then you can wrap up the conversation in phase 5";
          }
        }

        else if (this.$refs.beliefInput.beliefs[1].value < 0.8){
          this.adviceMsg = "Wrap up the conversation and ask if it was helpful for them. This would show your continued interest in helping and understanding them, and to increase the child's commitment to the goal.";
          this.childSitMsg = "The child is now confident they can tell someone and everyone will be safe, including the bullies. They are ready to wrap up the conversation.";
          this.progressMsg="You completed 100% of phase 4 (Working towards the session goal). \n "+
              "This phase requires ensuring the child has the next steps to apply the agreed goal of talking to someone. If the child is confident enough and willing to apply this goal, then you can wrap up the conversation in phase 5";

          this.Feedbackmsg="In phase 5, you should wrap up the conversation and ask if it was helpful for them. This would show your continued interest in helping and understanding them, and to increase the child's commitment to the goal and feeling of caring for others.";
        }
        else if (this.$refs.beliefInput.beliefs[14].value < 1 && this.$refs.beliefInput.beliefs[1].value > 0.7){
          this.adviceMsg = "Remember to say goodbye before ending the conversation.";
          this.childSitMsg = "The child is happy about the conversation's outcome and is ready to apply the agreed goal (talking to someone), which aligns with their values.";
          this.progressMsg="You completed 100% of phase 4 (Working towards the session goal). \n "+
              "This phase requires ensuring the child has the next steps to apply the agreed goal of talking to someone. If the child is confident enough and willing to apply this goal, then you can wrap up the conversation in phase 5";
          this.Feedbackmsg="In phase 5, wrap up the conversation and ask if it was helpful for them. This shows your continued interest in helping and understanding, and increases the child's commitment to the goal.";
        }
        else{ // add random to give a random msg? or nothing delete?
          this.Feedbackmsg="Great job! You successfully completed the scenario. Lilo is happy with the outcome and ready to apply the alternative goal that they feel comfortable with and that aligns with their values!";
        }


      }

      else{
        this.adviceMsg = "No available advice for this phase.";
      }
    },

    showInfo() {
      this.isHovered = true;
    },
    hideInfo() {
      this.isHovered = false;
    },


    UndoPhase() {
      this.$refs.joinPopup2.headerText= "Restart the Phase";
      // if((this.$refs.beliefInput.phase) > 1){
      //   this.$refs.beliefInput.switchToPhase(this.$refs.beliefInput.phase-2);
      //   this.$refs.joinPopup2.undoPhaseDiv(this.$refs.beliefInput.phase-1);
      // }
      // else{
      //   this.$refs.joinPopup2.undoIncorrectPhase();
      // }

      if (this.$refs.beliefInput.phase ===2){
        this.restartPhaseTwo();
        this.$refs.joinPopup2.openPopupwMsg("You are back to the start of phases 1 and 2: Building rapport and clarifying the child's story.")
      }

      else if (this.$refs.beliefInput.phase ===3) {
        this.restartPhaseThree();

      }


      else if (this.$refs.beliefInput.phase ===4){
        this.restartPhaseFour();
      }

      else{
        this.$refs.joinPopup2.openPopupwMsg("You can't restart the phase.")
      }
      //if the element before the end of the user intent has phase 3
      //undo 1 step
      //then do the phase 3 function
      // otherwise
      // delete from user intent from the end, until you reach the element that has phase 3, but the one before it is phase 2
      // delete from the chat list until you reach the element in the chat list that has the same text as the last element in the user intent
      // delete from the belief list until you reached element of the phase name, and assign the values of the one after it
      //////////////// first know how to add the phase name to the beliefs list
      // }

      this.handleButtonClick(4,this.$refs.beliefInput.phase,this.$refs.beliefInput.currBeliefs);

      this.$refs.joinPopup2.openPopup();

    },
    restartPhaseTwo(){
      this.$refs.webChat.clearConversation();
      this.userIntents = [];
      this.$refs.beliefInput.AllBeliefList=  [[0.5,0.3,0.4,0.6,0,0,0.7,1,0,0,0,0,0.5,1,0,0,0],[0.5,0.3,0.4,0.6,0,0,0.7,1,0,0,0,0,0.5,1,0,0,0]];
      this.updateAllBeliefs();
      this.$refs.beliefInput.popBelList();
    },
    restartPhaseThree(){

      // first clean the beliefs list
      if (typeof this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1] !== "string"){
        while (typeof this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1] !== "string") {
          this.$refs.beliefInput.AllBeliefList.pop();
        }

      }

      // Clean userIntents

      while (this.userIntents.length > 0) {
        const lastIntent = this.userIntents[this.userIntents.length - 1];
        if (lastIntent.inPhase === "PHASE3") {
          this.userIntents.pop();
        } else {
          break;
        }
      }

      // Get the text in the last element of userIntents
      if(this.userIntents.length > 0){
        const lastUserText = this.userIntents[this.userIntents.length - 1].UserText;

        // Clean chatRecord

        this.$refs.webChat.CleanRecordPhase(lastUserText);
      }

      this.updateAllBeliefsBforPhase();
      this.$refs.joinPopup2.openPopupwMsg("You are back to the start of the third phase: Setting a goal for the session.")

    },
    restartPhaseFour(){
      // first clean the beliefs list
      if (typeof this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1] !== "string"){
        while (typeof this.$refs.beliefInput.AllBeliefList[this.$refs.beliefInput.AllBeliefList.length - 1] !== "string") {
          this.$refs.beliefInput.AllBeliefList.pop();
        }

      }

      // Clean userIntents

      while (this.userIntents.length > 0) {
        const lastIntent = this.userIntents[this.userIntents.length - 1];
        if (lastIntent.inPhase === "PHASE4") {
          this.userIntents.pop();
        } else {
          break;
        }
      }

      if(this.userIntents.length > 0) {
        // Get the text in the last element of userIntents
        const lastUserText = this.userIntents[this.userIntents.length - 1].UserText;

        // Clean chatRecord

        this.$refs.webChat.CleanRecordPhase(lastUserText);
      }

      this.updateAllBeliefsBforPhase();
      this.$refs.joinPopup2.openPopupwMsg("You are back to the start of the fourth phase: Working towards the session goal.")

    },

    WhenNext() {
      this.$refs.joinPopup2.headerText= "Current Phase Progress";
      this.traverseAdvSitu(this.$refs.beliefInput.phase);

      this.handleButtonClick(5,this.$refs.beliefInput.phase,this.$refs.beliefInput.currBeliefs);

      this.$refs.joinPopup2.openPopupwMsg(this.progressMsg);
    },
    IncUtterances() {
      this.$refs.joinPopup2.headerText= "View Messages Evaluation";
      this.wrongUtterances = [];
      this.userIntents.forEach(intentObj => {
        let flag = false;
        const { UserIntent, inPhase, UserText } = intentObj;

        if (inPhase !== "PHASE2") { // add a new list for phase1 here, then check below whether this includes in phase 1 list or phase 2
          if (this.phase2Intents.includes(UserIntent)) {
            this.wrongUtterances.push({
              usertext: UserText,
              usedInPhase: inPhase,
              correctPhase: "PHASE2"
            });
            flag = true;
          }
        }

        if (inPhase !== "PHASE3" && !flag) {
          if (this.phase3Intents.includes(UserIntent)) {
            this.wrongUtterances.push({
              usertext: UserText,
              usedInPhase: inPhase,
              correctPhase: "PHASE3"
            });
            flag = true;
          }
        }

        if (inPhase !== "PHASE4" && !flag) {// add a new list for phase5 here, then check below whether this includes in phase 4 list or phase 5
          if (this.phase4Intents.includes(UserIntent)) {
            this.wrongUtterances.push({
              usertext: UserText,
              usedInPhase: inPhase,
              correctPhase: "PHASE4"
            });
            flag = true;
          }
        }
      });


      this.handleButtonClick(7,this.$refs.beliefInput.phase,this.$refs.beliefInput.currBeliefs);

      this.$refs.joinPopup2.IncMSG(this.wrongUtterances);
    },

    childGoal() {
      this.$refs.joinPopup2.headerText= "Current Child's Goal";

      if (this.$refs.beliefInput.phase ===2){
        this.childGoalMsg = "The child wants to tell their story and get help with their problem.";
      }
      else if(this.$refs.beliefInput.phase ===3){
        if(this.$refs.beliefInput.beliefs[15].value < 0.75){
          this.childGoalMsg = "The child wants you to call someone and inform them about the problem.";
        }
        else
          this.childGoalMsg = "The child is willing to come up with a solution together with you.";
      }
      else if(this.$refs.beliefInput.phase ===4){
        if(this.$refs.beliefInput.beliefs[1].value < 0.7) {
          this.childGoalMsg = "The child is willing to talk to someone about the problem, but they don't know how to do it.";
        }
        if(this.$refs.beliefInput.beliefs[1].value > 0.7) {
          this.childGoalMsg = "The child is planning to tell someone about the bullying.";
        }
      }
      else if(this.$refs.beliefInput.phase ===5){
        this.childGoalMsg = "The child wants to end the conversation.";
      }

      this.handleButtonClick(6,this.$refs.beliefInput.phase,this.$refs.beliefInput.currBeliefs);


      this.$refs.joinPopup2.openPopupwMsg(this.childGoalMsg);
    },


    startTimer() {
      if (!this.intervalId) {
        this.intervalId = setInterval(() => {
          this.timer++;
        }, 1000);
      }
    },
    stopTimer() {
      clearInterval(this.intervalId);
      this.intervalId = null;
    },
    resetTimer() {
      this.timer = 0;
      this.stopTimer();
    },

    redirectToNextPage() {
      this.$router.push("/assessment"); // Change "/next-page" to the actual route
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
  width: 46%;
  white-space: normal; /* Allow text to wrap */
  cursor: pointer;
  //max-height: 90vh;
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
  white-space: normal; /* Allow text to wrap */

  width: 46%;
  cursor: pointer;
  //max-height: 90vh;
  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);

}

.feedbackSub{
  border-radius: 16px;
  background-color: #089094;
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
  //height: calc(100% - 4vh);
  //display: flex;
  //flex-direction: column;
}
.buttons2 {
  width: 150%;
  padding-top: 30%;
  padding-left: 15%;
  //max-height: 90vh;

}

.join-button:hover {
  background-color: var(--chat-widget-header-button-focus);
  cursor: pointer;
}


.section-title {
  font-weight: bold;
  margin-bottom: 7px;
  padding-left: 50px;
  font-size: 24px;
  color: floralwhite;
  text-align: center;
}

#UndoStep:hover {

}

.button-group {
  display: flex;
  justify-content: space-between; /* Adjusts the space between the buttons */
  margin-bottom: 5%; /* Space below each group of buttons */
  padding-left: 7%;
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
  height: 98%;
}

/* Center the entire information box */
.info-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 10vh; /* Adjust this if needed */
  width: 90%;
}

/* Styled Information Box */
.info-box {
  width: 100%;
  max-width: 700px;
  min-height: 100%;
  display: block; /* Ensure it behaves normally */
  padding: 20px;
  margin-left: 10px;
  background-color: #eaeaea; /* Light gray background for visibility */
  border: 2px solid #666;
  border-radius: 10px;
  text-align: center;
  font-size: 18px;
  color: black !important; /* Force text to be black */
  font-weight: bold;
  color: #000; /* Black text for better readability */
  align-items: center;
  justify-content: center;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
}


/* Center the entire information box */
.info-container2 {
  display: flex;
  justify-content: left;
  align-items: flex-start; /* Align to the top initially */
  height: 10vh; /* Adjust as needed */
  width: 100%;
  position: relative;
}

/* Styled Information Box */
.info-box2 {
  width: 50%;
  max-width: 90%;
  margin-left: 5%;
  margin-top: -5%;
  padding: 20px;
  background-color: #f7f7f7; /* a bit softer */
  border: 1px solid #aaa;
  border-radius: 10px;
  font-size: 15.5px;
  font-weight: normal;
  color: black;
  display: block; /* Ensure it behaves normally */
  white-space: pre-line; /* Fixes line breaks */
  text-align: left;
  overflow-y: auto; /* Scroll if content exceeds max height */
  max-height: 550px; /* Prevents it from growing too large */
  line-height: 1.5;
  box-shadow: 1px 1px 5px rgba(0, 0, 0, 0.1);
}


.info-text {
  color: black !important;
  text-align: center;
}

.info-box i {
  font-size: 16px; /* Make it smaller */
  font-style: italic;
  text-align: left;
  display: block;
  width: 100%; /* Ensures alignment with the content */
  margin-bottom: 5px; /* Adds some spacing before the main text */
}

.timer{
  color: #dcd8d8;
}

.info-box2 i {
  font-size: 16px; /* Make it smaller */
  font-style: italic;
  text-align: left;
  display: block;
  width: 100%; /* Ensures alignment with the content */
  margin-bottom: 5px; /* Adds some spacing before the main text */
}


</style>

<style scoped src="../../styles/main.css"/>
