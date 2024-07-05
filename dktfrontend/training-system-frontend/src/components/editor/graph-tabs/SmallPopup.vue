<template>
    <div v-if="showPopup" class="popup" @keyup.esc="closePopup" @keyup.enter="chooseSelection">
        <div class="popup-content">
            <span class="close" @click="closePopup">&times;</span>
            <h5 class="text">Create {{ headerText }}</h5>
            <div class="form-group">
                <input v-model="localName" :placeholder="computedPlaceholder" class="form-control newScenarioName" type="text">
            </div>
            <button class="btn btn-primary click-button mt-2 node" @click="submitScenario">Create {{ headerText }}</button>
            <div v-if="error" class="text-danger mt-2">{{ error }}</div>
            <div class="footer-text mt-4 node">Press ENTER to submit, ESC to exit.</div>
        </div>
    </div>
    <!-- <div v-if="showPopup" class="popup" @keyup.esc="closePopup" @keyup.enter="submitScenario">
    <div class="popup-content">
      <span class="close" @click="closePopup">&times;</span>
      <h5>CREATE SCENARIO</h5>
      <div class="form-group">
        <input v-model="newScenario" placeholder="Scenario Name" class="form-control newScenarioName" type="text">
      </div>
      <button class="btn btn-primary click-button mt-2 node" @click="submitScenario">Create Scenario</button>
      <div v-if="error" class="text-danger mt-2">{{ error }}</div>
      <div class="footer-text mt-4 node">Press ENTER to submit, ESC to exit.</div>
    </div> -->
  <!-- </div> -->
</template>

<script>
export default {
  props: {
    showPopup: {
      type: Boolean,
      default: false
    },
    headerText: {
      type: String,
      default: ''
    },
    newName: {
        type: String,
        required: true
    },
    validationFunction: {
        type: Function,
        default: (scenarioName) => {
            return [true, scenarioName]
        }
    }
  },

  name: "ScenarioList",
  components: {
  },
  data() {
    return {
        localName: "",
        error: ''
    }
  },
  computed: {
    computedPlaceholder() {
      return `${this.headerText} Name`;
    }
  },
  methods: {
    closePopup() {
      this.$emit('update:showPopup', false);
    },
    submitScenario() {
      const trimmedName = this.localName.trim();
      if (!trimmedName) {
        this.error = 'Scenario name cannot be empty';
        return;
      }
      const validationResult = this.validationFunction(trimmedName);
      if (!validationResult[0]) {
        this.error = validationResult[1];
        return;
      }
      this.$emit('update:newName', trimmedName);
      this.closePopup();
    }
  }
}
</script>

<style scoped>
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