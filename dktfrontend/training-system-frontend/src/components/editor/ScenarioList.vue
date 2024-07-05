<template>
  <div class="container" style="display: flex;">
    <div class="dynamic-h2" title="Scenarios">
      <dynamic-text :header-text="'SCENARIOS'" :target-font-size="1.3" :min-font-size="0.5" :unit="'rem'"
        :step-size="0.1" :overflow-wrap="'anywhere'">
      </dynamic-text>
    </div>
    <div style="margin-left: auto;">
      <img src="../../../images/question.png" height="25px" width="25px"
        title="Pick the scenario you want to edit">
    </div>
  </div>
  <div class="container scenarios">
    <ul class="scenarios__wrapper">
      <li v-for="scenario in scenarios" :class="{ selected: selectedScenario == scenario }" :key="scenario"
        @click="selectScenario(scenario), $emit('changeScenario', scenario)"> {{ scenario }} </li>
    </ul>
  </div>
  <div>
    <button class="btn btn-primary click-button" @click="showPopup = true">Create Scenario</button>
  </div>

  <div v-if="showPopup" class="popup" @keyup.esc="closePopup(false)" @keyup.enter="submitScenario">
    <SmallPopup :showPopup="showPopup" @update:showPopup="closePopup" :headerText="`scenario`" :newName="newScenario"
      @update:newName="addScenario" :validationFunction="testValidation" />
    <!-- <div class="popup-content">
      <span class="close" @click="closePopup">&times;</span>
      <h5>CREATE SCENARIO</h5>
      <div class="form-group">
        <input v-model="newScenario" placeholder="Scenario Name" class="form-control newScenarioName" type="text">
      </div>
      <button class="btn btn-primary click-button mt-2 node" @click="submitScenario">Create Scenario</button>
      <div v-if="error" class="text-danger mt-2">{{ error }}</div>
      <div class="footer-text mt-4 node">Press ENTER to submit, ESC to exit.</div>
    </div> -->
  </div>
</template>

<script>
import DynamicText from "@/components/DynamicText.vue";
import * as editorApi from '@/mixins/editorApi';
import SmallPopup from "./graph-tabs/SmallPopup.vue";
import { ref } from 'vue';

export default {
  name: "ScenarioList",
  components: {
    DynamicText,
    SmallPopup
  },
  props: {
    selectedScenario: String
  },
  emits: ['scenarioSelected', 'changeScenario'],
  watch: {
    newScenario(newVal) {
      this.addScenario(newVal);
    }
  },
  data() {
    return {
      scenarios: [],
      showPopup: ref(false),
      newScenario: '',
      error: ''
    }
  },
  created() {
    editorApi.getScenarios().then(s => {
      this.scenarios = s.scenarioNames;
    });
  },
  methods: {
    selectScenario(scenario) {
      this.$emit('changeScenario', scenario);
      this.$emit('scenarioSelected', scenario);
    },
    testValidation(scenarioName) {
      if (this.scenarios.includes(scenarioName)) {
        this.error = 'Scenario already exists';
        return [false, this.error]
      }
      return [true, ''];
    },
    submitScenario() {
      const scenarioName = this.newScenario.trim();
      if (!scenarioName) {
        this.error = 'Scenario name cannot be empty';
        return;
      }
      if (this.scenarios.includes(scenarioName)) {
        this.error = 'Scenario already exists';
        return;
      }
      this.addScenario(this.newScenario.trim());
      this.closePopup(false);
    },
    async addScenario(scenarioName) {
      this.newScenario = '';
      this.error = '';
      editorApi.createScenario(scenarioName).then(() => {
        this.scenarios.push(scenarioName)
        this.$emit('changeScenario', scenarioName);
        this.$emit('scenarioSelected', scenarioName);
      });
    },
    closePopup(newVal) {
      this.showPopup = newVal;
      this.newScenario = '';
      this.error = '';
    }
  }
}
</script>

<style scoped>
.newScenarioName {
  margin-top: 8px;
  width: 100%;
}

.float-block {
  display: block;
}

.float-block:after {
  clear: both;
  content: " ";
  display: block;
}

.dynamic-h2 {
  margin-left: 10px;
}

.scenarios {
  display: flex;
  margin-top: 1vh;
  margin-left: 5px;
  margin-right: 5px;
  border-radius: 16px;
  background-color: var(--list-background);
  padding: 1px;
  overflow-y: scroll;
  height: 100%;
  width: 100%;
}

.click-button {
  margin-top: 5px;
  margin-left: 2px;
  margin-right: 2px;
  float: right;
  border-radius: 16px;
  background-color: var(--chat-widget-button);
  border: none;
  color: var(--chat-widget-button-text);
  font-size: small;
  padding: 0.5rem;
  font-family: 'Anton', sans-serif;
  width: max-content;
  cursor: pointer;
  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);
}

.click-button:hover,
.click-button:focus {
  background-color: var(--chat-widget-button-focus);
  outline: var(--button-outline-width) solid var(--button-outline-dark);
  outline-offset: 4px;
}

ul {
  list-style-type: none;
  /* margin-left: -40px; */
}

.scenarios__wrapper {
  padding: 0;
  width: 100%;
}

.scenarios__wrapper li {
  cursor: pointer;
  transition: 0.4s all ease-out;
  text-indent: 20px;
}

.scenarios__wrapper li:hover {
  background-color: #8652b7;
}

.scenarios__wrapper li.selected {
  background-color: #8652b7;
}

/* Popup styles */
.popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 300px;
  text-align: center;
  position: relative;
}

.popup-content .btn {
  float: none;
}

.popup-content .newScenarioName {
  margin-top: 20px;
  margin-bottom: 10px;
}

.popup-content .text-danger {
  font-size: 14px;
}

.close {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 18px;
  cursor: pointer;
}

.footer-text {
  position: relative;
  font-size: 0.75rem;
  color: #888;
}
</style>
