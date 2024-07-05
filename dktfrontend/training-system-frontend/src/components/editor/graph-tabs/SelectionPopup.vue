<template>
  <!-- <div v-if="showPopup" class="popup" @keyup.esc="closePopup" @keyup.enter="chooseSelection">
    <div class="popup-content">
      <span class="close" @click="closePopup">&times;</span>
      <h5>{{ headerText }}</h5> -->
  <Popup    
    :showPopup="showPopup" 
    @update:showPopup="updateShowPopup"
    :headerText="`Select ${headerText}`">
    <div class="form-group">
      <ul class="selectionList">
        <li v-for="selection in localSelectionItems" :key="selection" @click="doSomething(selection)">
           <div class="listTextPart"> {{ selection }} </div> 
           <button v-if="canAddToList" @click="removeSelection(selection)" class="btn-close"></button>
        </li>
      </ul>
    </div>
    <div class="wrapperButton">
      <button v-if="canAddToList" class="click-button" @click="updateShowPopup(false)">Done</button>
      <button v-if="canAddToList" class="click-button" @click="openCreatePopup">{{ `Create ` + headerText }}</button>
    </div>
    <div v-if="showAdditionPopup" class="popup" @keyup.esc="closePopup" @keyup.enter="submitScenario">
      <SmallPopup    
      :showPopup="showAdditionPopup" 
      @update:showPopup="updateCreatePopup"
      :headerText="headerText"
      :newName="newName"
      @update:newName="addNewSelection">
      </SmallPopup>
      <!-- <span class="close" @click="closePopup">&times;</span>
      <h5>CREATE SCENARIO</h5>
      <div class="form-group">
        <input v-model="newScenario" placeholder="Scenario Name" class="form-control newScenarioName" type="text">
      </div>
      <button class="btn btn-primary click-button mt-2 node" @click="submitScenario">Create Scenario</button>
      <div v-if="error" class="text-danger mt-2">{{ error }}</div>
      <div class="footer-text mt-4 node">Press ENTER to submit, ESC to exit.</div> -->
    </div>
    <!-- <div v-if="error" class="text-danger mt-2">{{ error }}</div> -->
    <!-- <div class="footer-text mt-4 node">Press ENTER to submit, ESC to exit.</div> -->
  </Popup>
    <!-- </div>
  </div> -->
</template>


<script>
import Popup from "./Popup.vue";
import SmallPopup from "./SmallPopup.vue";

export default {
  props: {
    showPopup: {
      type: Boolean,
      default: false
    },
    selectionItems: {
      type: Array,
      default: () => []
    },
    headerText: {
      type: String,
      default: 'Select Intent'
    },
    selectedItem: {
      type: String,
      default: ''
    },
    placeholderText: {
      type: String,
      default: 'Scenario Name'
    },
    buttonText: {
      type: String,
      default: 'Select Intent'
    },
    footerText: {
      type: String,
      default: 'Press ENTER to submit, ESC to exit.'
    },
    error: {
      type: String,
      default: ''
    },
    canAddToList: {
      type: Boolean,
      default: false
    }
  },

  name: "ScenarioList",
  components: {
    Popup,
    SmallPopup
  },
  data() {
    return {
      showAdditionPopup: false,
      newName: '',
      localSelectionItems: [...this.selectionItems]
    }   
  },
  methods: {
    updateShowPopup(newValue) {
      this.$emit('update:showPopup', newValue);
    },  
    closePopup() {
      this.$emit('update:showPopup', false);
    },
    doSomething(selection) {
      this.$emit('update:selectedItem', selection);
    },
    openCreatePopup() {
      this.showAdditionPopup = true;
    },
    updateCreatePopup(newValue) {
      this.showAdditionPopup = newValue;
    },
    addNewSelection(newValue) {
      this.localSelectionItems.push(newValue);
      this.$emit('update:selectionItems', this.localSelectionItems);
    },
    removeSelection(value) {
      this.localSelectionItems = this.localSelectionItems.filter(x => x != value);
      this.$emit('update:selectionItems', this.localSelectionItems);
    }
  }
}
</script>

<style scoped>
.wrapperButton {
  position: relative;
  margin-top: 60px;
  right: 5px;
  bottom: 5px;
}
.click-button {
  margin-top: 5px;
  margin-left: 2px;
  margin-right: 2px;
  border-radius: 16px;
  background-color: var(--chat-widget-button);
  /* position: absolute;
  right: 5px;
  bottom: 5px; */
  float: right;
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

.btn-close {
  margin: auto;
}

.newScenarioName {
  margin-top: 8px;
  width: 100%;
}

.selectionList li {
  display: flex;
  justify-content: spce-beatween;
}

.selectionList {
  box-sizing: border-box;
  width: 100%;
  color: #888888;
  border-width: 5px;
  list-style-type: none;
  padding: 0px;
}

.listTextPart {
  color: #FFFFFF;
  border: 1px solid #888888;
  padding: 10px;
  margin: 5px 0;
  background-color:#00DBA3;
  border-radius: 16px;
  font-family: 'Anton', sans-serif;
  cursor: pointer;
  transition: 0.4s all ease-out;
  width: 90%;
  float: left;
}

.listTextPart:hover {
    background-color: #00ca98;
}

.listTextPart.selected {
    background-color: #00ba8c;
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

.fullWidth {
  width: 100%;
}
</style>