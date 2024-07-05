<template>
    <div class="mappingDiv">
      <h5 class="text">{{ updateName }}</h5>
      <ul class="listContainer">
        <li v-for="mapItem in currentMappings" :key="mapItem">
          <div class="mapping">
            <h5 class="nameOfEditedValue mapItemChild text">{{ mapItem.name }}</h5>
            <button @click="removeFromIntent(mapItem.name)" class="btn-close"></button>
          </div>
        </li>
      </ul>
      <button class="click-button" @click="openChooseMenu">{{ buttonValue }}</button>
    </div>
    <div v-if="showSelectionPopup">
      <SelectionPopup
      :showPopup="showSelectionPopup"
      @update:showPopup="closeSelectionPopup"
      v-model:selectedItem="newestItemToEdit"
      @update:selectedItem="chooseIntentForMapping"
      :selectionItems="localAllActions"
      @update:selectionItems="updateAllBeliefs"
      :headerText="'Action'"
      :canAddToList="true">
      </SelectionPopup>
    </div>
  </template>
  
  <script>
  import SelectionPopup from "./SelectionPopup.vue";
  import {watch, ref} from 'vue';
  
  export default {
    props: {
      showPopup: {
        type: Boolean,
        default: false
      },
      updateName: {
          type: String,
          default: "Select actions"
      },
      allActions: {
        type: Array,
        default: () => ([])
      },
      mapItems: {
          type: Array,
          default: () => []
      },
      buttonValue: {
        type: String,
        default: "add action"
      }
    },
    name: "BeliefUpdate",
    components: {
      SelectionPopup
    },
    setup(props, { emit }) {
      const currentMappings = ref(props.mapItems);

      watch(currentMappings, (newValue) => {
        emit('update:mapItems', newValue)
      })

      return {
        currentMappings
      }
    },
    data() {
      return {
        showSelectionPopup: false,
        newestItemToEdit: "",
        localAllActions: [...this.allActions],
      }   
    },
    methods: {
      updateShowPopup(newValue) {
        this.$emit('update:showPopup', newValue);
      },
      removeFromIntent(item) {
        this.currentMappings = this.currentMappings.filter(x => x.name != item);
      },
      openChooseMenu() {
        this.showSelectionPopup = true;
      },
      closeSelectionPopup() {
        this.showSelectionPopup = false;
      },
      chooseIntentForMapping() {
        this.showSelectionPopup = false;
        this.currentMappings.push({
            name: this.newestItemToEdit
          });
        console.log(this.currentMappings);
      },
      updateAllBeliefs(newValue) {
        this.localAllActions = newValue;
        this.$emit('update:allActions', this.localAllActions);
      }
    }
  }   
  </script>
  
  <style scoped>
  * {
      box-sizing: border-box
  }
  
  .mappingDiv {
      height: 90%;
      background-color: var(--lightest-purple);
      border-radius: 16px;
      margin: 10px;
      padding: 10px;
      position: relative;
  }
  
  .listContainer {
      padding: 0px;
      list-style-type: none;
  }
  
  .btn-close {
    width: 10%;
    margin: auto;
  }
  
  .mapping {
    width: 100%;
    display: flex;
    justify-content: space-between;
    color: #888888;
    list-style-type: none;
    padding: 0px;
    margin-top: 0px;
    margin-bottom: 0px;
  }
  
  .mapItemChild {
      border-radius: 16px;
      border: 0px solid white;
      margin-right: 2.5%;
      margin-left: 2.5%;
      margin-bottom: 5px;
      margin-top: 5px;
  }
  
  .valueBox {
      width: 20%;
  }
  
  .nameOfEditedValue {
      width: 50%;
  }
  
  .styled-select {
      width: 20%;
      font-size: 16px;
      text-align: center;
      font-family: 'Anton', sans-serif;
      cursor: pointer;
  }
  
  .click-button {
    margin-top: 5px;
    margin-left: 2px;
    margin-right: 2px;
    border-radius: 16px;
    background-color: var(--chat-widget-button);
    position: absolute;
    right: 5px;
    bottom: 5px;
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
  
  .text{
    border: none;
    color: var(--chat-widget-button-text);
    font-size: large;
    font-family: 'Anton', sans-serif;
  }
  
  </style>