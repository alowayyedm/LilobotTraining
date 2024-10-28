<!--
  This component is responsible for displaying the optimal path. It contains the
  BeliefsDesiresDisplay, IntentionEdges, and MessageNodes.
-->
<template>
  <div class="optimal-path-container">
    <div class="header-container">
      <div class="dynamic-h2">
        <dynamic-text
            :header-text="'OPTIMAL CHAT CONTINUATION'"
            :target-font-size=1.3
            :min-font-size=0.5
            :unit="'rem'"
            :step-size=0.1
            :overflow-wrap="'anywhere'"
        >
        </dynamic-text>
      </div>
      <div class="button-container">
        <button class="general-icon" @click="reloadGraphData" title="(Re-)generate optimal path">
          <i class="fa-solid fa-rotate-right"></i>
        </button>
        <button class="general-icon" @click="showInfo" title="Show Info">
          <i class="fa-solid fa-info"></i>
        </button>
      </div>
    </div>

    <div class="path-value-display-container dark-scrollbar" >
      <div v-if="displayInfo" class="info-box">
        <p>
          Hier kan je de berichten zien die naar LiloBot gestuurd moeten worden om door het vijf fasen gespreksmodel te gaan.
        </p>

        <div>
          <p class="inline">
            Klik op
            <button class="inactive-icon" title="(Re-)generate optimal path" :tabindex="-1">
              <i class="fa-solid fa-rotate-right"></i>
            </button>
            om deze berichten te genereren.
          </p>
        </div>


        <p>
          Elke cirkel toont de interne staat van LiloBot. Daar kun je de huidige overtuigingen en verlangens van LiloBot zien.  Je kunt ook zien in welke fase van het gespreksmodel LiloBot zich bevindt.
        </p>
       </div>
      <div v-else-if="dataAvailable" id="optimal-path" class="optimal-path">
        <!-- For however many messages there are, create a message node and intention edge (if the edge is non-null).
        The last edge is null, since it is the ending message node. -->
        <template v-for="(message, index) in messagePropsList" :key="index">
          <!-- Add the active class iff the message node is active (i.e., selected). -->
          <message-node
              :class="{active: activeMessageNode === index}"
              :phase="this.phaseEnumToNumber(message.phase)"
              @click.prevent="messageNodeClicked(message, index)"
              @keyup.enter.prevent="messageNodeClicked(message, index)"></message-node>
          <intention-edge v-if="message.edge !== null"
                         :intention-name="message.edge.intentionName"
                         :example-msg="message.edge.exampleMsg"></intention-edge>
        </template>
      </div>

      <!-- Display the beliefs and desires of the active message node by passing them as the component params. -->
      <node-info-display
          v-if="dataAvailable && !displayInfo"
          :beliefs="activeBeliefs"
          :desires="activeDesires"
          :phase="activePhase"></node-info-display>

      <div v-if="loading && !displayInfo" id="path-loading-icon" class="loading-container">
        <i class="fas fa-spinner fa-pulse loading-icon"></i>
        <div class="loading-text">
          loading...<br><br><br>
          <i>If you are generating from phase 1 or early phase 2, this might take more than 1 or 2 minutes.</i>
        </div>
      </div>

      <div v-if="error && !displayInfo" id="error-message-path" class="error-message-path">
        <b>A path could not be generated. This can have different reasons:</b><br>
        ⦁ You are <b>not in a session</b>.<br>
        ⦁ The chatbot is already in <b>phase 5</b>.<br>
        ⦁ The chatbot is in a state which <b>does not allow</b> for going through the next phases. If you suspect this to be
        the case, switch to a specific phase using the buttons in the middle of the page.<br>
        ⦁ Something <b>went wrong</b> generating the path.<br>
      </div>
    </div>
  </div>
</template>

<script>
import IntentionEdge from "./IntentionEdge";
import MessageNode from "./MessageNode";
import NodeInfoDisplay from "./NodeInfoDisplay";
import axios from "axios";
import DynamicText from "@/components/DynamicText.vue";
import { phaseEnumToNumber } from "@/utils";

export default {
  name: "GraphComponent",
  components: {DynamicText, MessageNode, IntentionEdge, NodeInfoDisplay},
  data() {
    return {
      messagePropsList: [],
      activeMessageNode: 0,
      activeBeliefs: [],
      activeDesires: [],
      activePhase: -1,
      dataAvailable: false,
      loading: false,
      error: false,
      displayInfo: false
    }
  },

  computed: {
    token() {
      return this.$store.state.auth.token;
    }
  },

  methods: {
    phaseEnumToNumber(phase) {
      return phaseEnumToNumber(phase);
    },
    // This function updates the beliefs and desires according to the selected message node
    messageNodeClicked: function (message, idx) {
      this.activeBeliefs = message.beliefs;
      this.activeDesires = message.desires;
      this.activePhase = this.phaseEnumToNumber(message.phase);
      this.activeMessageNode = idx;
    },
    // This function retrieves the data for the graph
    reloadGraphData() {
      const sessionId = sessionStorage.getItem("rasa_session_id");
      this.dataAvailable = false;
      this.error = false;
      this.loading = true;
      this.displayInfo = false;

      axios.get(this.$config.agentServer + `/optimal-path/${sessionId}`, {
        headers: {
          'Authorization': 'Bearer ' + this.token
        }
      }).then((response) => {
        this.loading = false;
        this.messagePropsList = response.data.nodes;
        if (this.messagePropsList == null || this.messagePropsList.length === 0) {
          this.activePhase = -1;
          this.activeMessageNode = 0;
          this.dataAvailable = false;
          this.error = true;
        } else {
          this.messageNodeClicked(this.messagePropsList[0], 0);
          this.dataAvailable = true;
        }
      }).catch(() => {
        this.activePhase = -1;
        this.activeMessageNode = 0;
        this.loading = false;
        this.dataAvailable = false;
        this.error = true;
      });
    },
    showInfo() {
      this.displayInfo = !this.displayInfo;
    },
    clearGraphComponent() {
      this.messagePropsList = [];
      this.activeMessageNode = 0;
      this.activeBeliefs = [];
      this.activeDesires = [];
      this.activePhase = -1;
    }
  }
}
</script>

<style scoped>
.optimal-path-container {
  margin: 1vh 1vw;
}

.path-value-display-container {
  overflow-y: auto;
  width: 30vw;
  margin-top: 1vh;
  max-height: calc(90vh - 3rem - 3vh);
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
}

.error-message-path {
  color: var(--main-text-secondary);
  font-size: 1rem;
  margin: 1.1rem 1.5vw 0;
  text-align: center;
  padding: 1rem 1vw;
  background: var(--list-background);
  border-radius: 1rem;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.loading-icon {
  margin-top: 3vh;
  font-size: 2.2rem;
  color: var(--graph-loading);
}

.loading-text {
  margin: 0.5rem 5vw 0;
  font-size: 0.8rem;
  color: var(--graph-loading);
  text-align: center;
  word-wrap: break-word;
}

.optimal-path {
  margin-top: 1.1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 30%;
}

.active {
  background-color: var(--active-node);
  outline: var(--button-outline-width) solid var(--button-outline-color);
  outline-offset: 4px;
}

.header-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  /* padding: 0 1vw; */
  width: 100%;
}

.button-container {
  display: flex;
  flex-direction: row;
  float: right;
  gap: 0.5rem;
}

.inactive-icon {
  min-height: 2.5rem;
  min-width: 2.5rem;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--basic-button);
  border: none;
  color: var(--basic-button-text);
  font-size: 1.2rem;
  pointer-events: none;
}

.inline {
  display: flex;
  align-items: center;
}

.inline button {
  margin-left: 0.6rem;
  margin-right: 0.6rem;
}


</style>