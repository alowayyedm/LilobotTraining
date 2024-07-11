<!--
  This component displays the current phase the chatbot is in, and allows the user to switch to another phase
  by clicking a button. It also displays the last transition, and the conditions to get to the next phase.
-->
<template class="overflow-container dark-scrollbar">
  <div class="phase-info-container dark-scrollbar">
    <div class="button-container">
      <div role="button" :tabindex="0"  class="expand-button" v-for="button in buttons"
           :class="{ 'activePhase': isActive(button) }"
           :key="button.id"
           :title="`Jump to phase `+ button.phase"
           @click="clickButton(button)"
           @keyup.enter="clickButton(button)">
        <div class="info" title="jump to phase">
          Jump
        </div>
        <button
            class="phase-circle"
            :tabindex="-1" 
            :title="`jump to phase ${button.label}`"
            :class="{ 'expanded': button.isExpanded }">
          {{ button.label }}
        </button>
      </div>
    </div>

    <div class="phase-info-text" v-if="this.phase !== null && goodLastTransition !== null && lastTransition !== null">
      <b>Last transition:</b> Phase {{ lastTransition.from }} → {{ lastTransition.to }}
      <i class="fa-regular fa-face-smile" title="Successful transition" v-if="goodLastTransition"></i>
      <i class="fa-regular fa-face-frown" title="Unsuccessful transition" v-else></i><br>
    </div>
    <div class="phase-info-text" v-if="this.phase !== null && buttons[this.phase - 1].nextPhase !== -1">
      <b>Next → Phase {{ buttons[this.phase - 1].nextPhase }}:</b>
      {{ buttons[this.phase - 1].nextConditions }}
    </div>
    <div class="phase-info-text" v-if="this.phase !== null && buttons[this.phase - 1].nextPhase === -1">
      <b>Arrived in last phase.</b>
    </div>
    <div class="phase-info-text" v-if="this.phase === null">
      <b>No phase active.</b>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PhaseInfo',
  props: {
    phase: Number,
    lastTransition: Object
  },
  data() {
    return {
      buttons: [
        {
          id: 0,
          phase: 1,
          label: '1',
          nextPhase: 2,
          nextConditions: "B3 > 0.3, B9 < 0.7, B10 = 0, B12 = 0"
        },
        {
          id: 1,
          phase: 2,
          label: '2',
          nextPhase: 3,
          nextConditions: "(B2 < 0.7, B8 > 0.7, B10 = 1) or (B4 >= 0.5, B10 = 1, B12 = 0)"
        },
        {
          id: 2,
          phase: 3,
          label: '3',
          nextPhase: 4,
          nextConditions: "B3 >= 0.5, B12 = 1, B13 >= 0.5"
        },
        {
          id: 3,
          phase: 4,
          label: '4',
          nextPhase: 5,
          nextConditions: "B1 < 0.3 or B17 = 1 or (B3 < 0.5, B12 = 1) or B2 = 1"
        },
        {
          id: 4,
          phase: 5,
          label: '5',
          nextPhase: -1,
          nextConditions: ""
        },
      ],
      goodLastTransition: null
    };
  },
  methods: {
    isActive (button) {
      return button.id === this.phase - 1;
    },
    clickButton (button) {
      this.$emit('switch-to-phase', `PHASE${button.phase}`);
    },
  },
  watch: {
    lastTransition: function(newValue) {
      if (newValue == null) {
        this.goodLastTransition = null;
      } else {
        this.goodLastTransition = newValue.to === newValue.from + 1;
      }
    }
  }
};
</script>

<style scoped>
.button-container {
  --gap: 1rem;
  --diameter: 2.5rem;
}

.dark-scrollbar::-webkit-scrollbar {
  width: 0.6rem;
  height: 0.6rem;
}

.phase-info-text {
  margin: 0.3rem 0rem 0rem 0rem;
  font-size: 0.9rem;
  text-align: center;
  color: var(--main-text-secondary);
}

.phase-info-text b {
  font-size: 1rem;
}

.phase-info-container {
  height: 6.5rem;
  max-height: 6.5rem;
  overflow: auto;
  display: flex;
  flex-direction: column;
  margin-bottom: 0.2rem;
  padding-top: 0.35rem;
  align-items: center;
}

.button-container {
  margin: 0rem;
  display: flex;
  flex-direction: row;
  gap: var(--gap);
  min-width: calc(var(--gap) * 5 + var(--diameter) * 6);
  justify-content: center;
}

.expand-button {
  user-select: none;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-left: 0.9rem;
  transition: width 0.3s ease;
  width: var(--diameter);
  height: var(--diameter);
  border-radius: calc(0.5 * var(--diameter));
  font-family: 'Poppins', sans-serif;
  color: var(--node-text);
  background-color: var(--inactive-node);
  cursor: pointer;
}

.activePhase,
.activePhase .phase-circle {
  background-color: var(--active-node);
  outline: var(--basic-border-width) solid var(--button-outline-color);
  outline-offset: 4px;
}

.activePhase:hover .phase-circle,
.activePhase:focus .phase-circle {
  outline: none;
}

.expand-button:hover .phase-circle,
.expand-button:focus .phase-circle {
  outline: none;
}

.expand-button:hover,
.expand-button:focus {
  outline: var(--basic-border-width) solid var(--button-outline-color);
  outline-offset: 4px;
}


.expand-button:not(:last-child)::after {
  content: '';
  position: absolute;
  right: calc(0px - var(--gap));
  display: block;
  width: var(--gap);
  height: 0.2rem;
  background-color: var(--edge-line);
}

.expand-button:not(.info):hover,
.expand-button:not(.info):focus {
  width: calc(var(--diameter) * 2);
}

.phase-circle {
  width: var(--diameter);
  height: var(--diameter);
  min-width: var(--diameter);
  border-radius: calc(0.5 * var(--diameter));
  color: var(--node-text);
  background-color: var(--inactive-node);
  border: none;
  font-family: 'Poppins', sans-serif;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  pointer-events: none;
}

.info {
  width: 0;
  font-size: xx-small;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: clip;
}

.expand-button:hover .info,
.expand-button:focus .info {
  width: fit-content;
}

.expand-button:not(:hover):not(:focus) .info {
  width: fit-content;
}
</style>
  