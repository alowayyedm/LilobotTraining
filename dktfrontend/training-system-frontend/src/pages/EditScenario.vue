<template>
  <div class="wrapper">
    <div class="scenarioListWrapper" v-show="scenarioListVisible">
      <div class="scenarioList" v-show="scenarioListVisible">
        <i class="fa-solid fa-chevron-left toggle-button" @click="toggleScenarioList" title="Click to shrink!"></i>
        <Transition>
          <div>
            <scenario-list @scenarioSelected="hideScenarioList" @changeScenario="setScenario" :selectedScenario="scenario"></scenario-list>
          </div>
        </Transition>
      </div>
    </div>
    <div class="scenarioListToggle" v-show="!scenarioListVisible">
      <i class="fa-solid fa-chevron-right toggle-button" @click="toggleScenarioList" title="Click to expand!"></i>
    </div>
    <div class="editorContent">
      <div class="first">
        <intents-list :scenario="this.scenario" @changeIntents="updateIntents"></intents-list>
      </div>
      <div class="second">
        <graph-mapper :scenario="this.scenario" :intents="this.intents"></graph-mapper>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "../../styles/main.css";

.wrapper {
  display: flex;
  position: relative;
  min-height: 75vh;
  max-height: 75vh;
}

.scenarioListWrapper {
  max-width: calc(100vw / 3);
  margin-left: 5px;
  margin-right: 5px;
}

.scenarioListToggle {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 5px;
  margin-right: 5px;
}

.toggle-button {
  align-self: center;
  flex-basis: 1;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  margin-left: 15px;
  transition: cubic-bezier(1, 0, 0, 1) 0.5s;
}

.toggle-button:hover {
  color: var(--chat-widget-button);
  transform: scale(1.5);
}

.scenarioList {
  display: flex;
  flex-direction: row;
  height: 100%;
  max-width: calc(100vw / 3);
  margin-left: 5px;
  margin-right: 5px;
}

.editorContent {
  display: flex;
  flex: 1;
  margin-left: 5px;
  margin-right: 5px;
}

.first,
.second {
  flex: 1;
  margin-left: 5px;
  margin-right: 5px;
  height: 100%;
}

.v-enter-active,
.v-leave-active {
  transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>

<script>
import IntentsList from "@/components/editor/IntentsList";
import ScenarioList from "@/components/editor/ScenarioList";
import GraphMapper from "@/components/editor/GraphMapper";

export default {
  name:"EditScenario",
  components: {
    IntentsList,
    ScenarioList,
    GraphMapper
  },
  data (){
    return {
      scenario: null,
      scenarioListVisible: true,
      intents: []
    }
  },
  methods: {
    setScenario (scenario) {
      this.scenario = scenario;
    },
    toggleScenarioList() {
      this.scenarioListVisible = !this.scenarioListVisible;
    },
    hideScenarioList() {
      this.scenarioListVisible = false;
    },
    updateIntents(newv) {
      this.intents = newv;
    }
  }
}
</script>
