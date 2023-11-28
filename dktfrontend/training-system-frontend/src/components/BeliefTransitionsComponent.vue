<!------------------------ Belief Transitions ------------------------
    This component displays the change in belief values for every
    message sent by the user.
    Displays message history and the corresponding belief value
    transition.
-->
<template>
  <div class="belief-transitions-container">
    <div class="header-container">
      <div class="dynamic-h2">
        <dynamic-text
            :header-text="'BELIEF VALUE TRANSITIONS HISTORY'"
            :target-font-size=1.3
            :min-font-size=0.5
            :unit="'rem'"
            :step-size=0.1
            :overflow-wrap="'anywhere'"
        >
        </dynamic-text>
      </div>
      <button class="general-icon" @click="showInfo" title="Show Info">
        <i class="fa-solid fa-info"></i>
      </button>
  </div>
  <div class="belief-transitions-box light-scrollbar" ref="transitionContainer">
      <div v-if="displayInfo" class="info-box">
        <p>
          Hier kun je zien hoe de overtuigingen van LiloBot veranderen na elk bericht.
        </p>

        <p>
          Beweeg over de acroniemen van de overtuigingen om de volledige tekst te zien.
        </p>
      </div>
      <div v-else v-for="message in messageList" :key="message" class="transitions">
        <MessageTransition v-bind:text="message.text"
                           v-bind:causeType="message.causeType"
                           v-bind:pos-beliefs="message.positiveBeliefs"
                           v-bind:neg-beliefs="message.negativeBeliefs">
        </MessageTransition>
      </div>
    </div>
  </div>
</template>

<script>
import MessageTransition from "../components/MessageTransition";
import DynamicText from "@/components/DynamicText.vue";

export default {
  name: "BeliefTransitionsComponent",
  components: {DynamicText, MessageTransition},
  data () {
    return {
      messageList: [],
      displayInfo: false
    }
  },
  methods: {
    /**
     * Add a new entry to messageList which will display the message followed by the belief changes
     * in the belief value transitions history component in the Training Portal.
     *
     * @param {int} index the index of the message w.r.t. all user messages
     * @param {String} message the text message that caused the belief values to change
     * @param {String} causeType the type of cause: MANUAL, TRIGGER, or MESSAGE
     * @param beliefChange dictionary with two entries positiveBeliefs and negativeBeliefs which are
     * both lists that include String values of each belief that was affected,
     * example input: {positiveBeliefs: ["B3", "B9"], negativeBeliefs: ["B10"]}
     */
    addMsgTransition: function (index, message, causeType, beliefChange) {
      this.messageList.push({
        index: index,
        text: message,
        causeType: causeType,
        positiveBeliefs: beliefChange.positiveBeliefs,
        negativeBeliefs: beliefChange.negativeBeliefs
      });

      // Scrolls the transition history to the bottom after an entry is added
      this.$nextTick(() => {
        const container = this.$refs["transitionContainer"];
        container.scrollTop = container.scrollHeight;
      });
    },
    showInfo() {
      this.displayInfo = !this.displayInfo;
    },
    reset() {
      this.messageList = [];
    }
  }

}
</script>

<style scoped>
.belief-transitions-container {
  margin: 2vh 1vw 1vh;
}

.belief-transitions-box {
  display: block;
  border-radius: 16px;
  background-color: var(--list-background);
  color: var(--list-text);
  overflow-y: auto;
  padding: 1rem;
  margin-top: 1vh;
  height: calc(40vh - 4vh - 3rem);
}

.transitions {
  font-size: 1rem;
  margin-bottom: 1rem;
}

.header-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}
</style>