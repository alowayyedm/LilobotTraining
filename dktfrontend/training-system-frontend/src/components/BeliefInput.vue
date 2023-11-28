<!------------------------ Belief Input ------------------------
    This component displays a list of beliefs, each of which
    has an numeric input field that will connect to the backend
    to change the belief values of the chatbot.
    
    To use this in another component: 
      1. Import this component inside the script of the parent:    
        `import BeliefInput from './BeliefInput.vue'`

      2. Add it to components in the parent's 'export':
        `components: {
          ...
          BeliefInput
          ...
        },`

      3. Add the <belief-input> tag in the template:
        <div><belief-input></belief-input></div> 
-->
<template>
  <div class="belief-changes-component">
    <div class="container">
      <div class="dynamic-h2" title="Phase info">
        <dynamic-text
            :header-text="'CHANGE BELIEFS'"
            :target-font-size=1.3
            :min-font-size=0.5
            :unit="'rem'"
            :step-size=0.1
            :overflow-wrap="'anywhere'"
        >
        </dynamic-text>
      </div>
      <button class="general-icon" @click="showInfo" title="Show Info on manual belief changing">
        <i class="fa-solid fa-info"></i>
      </button>
    </div>
    <phase-info
        :phase="phase"
        :lastTransition="lastTransition"
        @switch-to-phase="switchToPhase">
    </phase-info>
    <div><toast-queue ref="toastQueue" class="toast-container" direction="up" :duration=1500></toast-queue></div>
    <div class="belief-value-changing-box light-scrollbar">
      <div v-if="displayInfo" class="info-box">
        <p>
          Hier kun je de huidige overtuigingen van LiloBot zien. Je kunt de waarden aanpassen om te veranderen hoe LiloBot reageert.
        </p>
        <p>
          Let op: Sommige veranderingen kunnen ertoe leiden dat LiloBot niet kan antwoorden.
        </p>
        <p>
          Gebruik de knoppen hierboven om LiloBot direct naar een van de fasen van het gespreksmodel te brengen.
        </p>
      </div>
      <div v-else class="belief" v-for="belief in beliefs" :key="belief" title="belief input list">
        <input 
          type="number"
          class="belief-input"
          :disabled="!belief.isModifiable" 
          v-model.number="belief.value" 
          :step="0.05"
          @blur="updateBelief(belief)"
          @keyup.enter="updateBelief(belief)"
          v-bind:title="belief.isModifiable ? (`input box for ` + belief.id) : `Can't modify ${belief.id}: ${belief.disableReason}`">
            {{ belief.id }}: {{ belief.fullName }} 
      </div>
    </div>
  </div>
</template>
  
<script>
import ToastQueue from './ToastQueue.vue';
import PhaseInfo from './PhaseInfo.vue';
import DynamicText from "@/components/DynamicText.vue";

export default {
  name: "BeliefInput",
  components: {
    DynamicText,
    ToastQueue,
    PhaseInfo
  },
  props: {
    beliefs: Array,
    previousValues: Object,
    sessionActive: String,
    phase: Number,
    lastTransition: Object,
    privateSession: Boolean
  },
  data() {
    return {
      displayInfo: false
    }
  },
  methods: {
    updateBelief(belief) {
      const oldValue = this.previousValues[belief.id];
      belief.value = Math.min(Math.max(belief.value, 0), 1);
      const newValue = belief.value;
  
      if (oldValue !== newValue) {
        this.showToast(`Updated ${belief.id}`);
        this.$emit('update-belief', belief.id, belief.value);      
      }
    },
    switchToPhase(phase) {
      this.$emit('switch-to-phase', phase);
    },
    showToast(message) {
      this.$refs.toastQueue.addToast(message, 'success');
    },
    showInfo() {
      this.displayInfo = !this.displayInfo;
    }
  }
};
</script>

<style scoped>
@import "../../styles/main.css";

.belief-changes-component {
  position: relative;
  height: 100%;
  margin: 2vh 1vw 1vh;
}

.container {
  display: flex;
  gap: 1rem;
  align-items: center;
  justify-content: space-between;
}

.toast-container {
  position: absolute;
  bottom: 2rem;
  right: 0.5rem;
}

.toast i {
  color: var(--toast-icon);
}

.belief {
  font-size: 0.78rem;
  display: block;
  margin: 0.25rem 0;
  color: var(--list-text);
}

.belief-value-changing-box {
  display: block;
  border-radius: 16px;
  background-color: var(--list-background);
  padding: 1rem;
  overflow-y: auto;
  height: calc(100% - 10.4rem);
}

.belief-input {
  width: 2.95rem;
  text-align: center;
  font-family: 'Poppins', sans-serif;
  font-size: 0.8rem;
  border-radius: 4px;
  border-style: solid;
  border-width: 1px;
  border-color: var(--list-input-border);
}

.belief-input:disabled {
  border-color: var(--list-input-disabled-border);
  background-color: var(--list-input-disabled);
  color: var(--list-input-disabled-text);
}

</style>