<template>
    <Popup class="popup"
      :showPopup="showPopup" 
      @update:showPopup="updateBeliefs"
      :headerText="`Edit belief '${chosenBelief}'`">
      <div class="mapping">
        <h5 class="text">Default starting value</h5>
        <input class="default-value" min="0" max="1" step="0.05" type="number" v-model="chosenBeliefObject.value">
      </div>
        <button 
        style="right: 50px;"
        class="click-button" @click="deleteBelief(false)">Delete Belief</button>
        <button class="click-button" @click="updateBeliefs(false)">Done</button>
    </Popup>
  </template>

<script>
import { reactive, ref } from "vue";
import Popup from "./Popup.vue";
import editorApi from "@/mixins/editorApi";

export default {
    props: {
    scenario: {
        type: String,
        default: 'no scenario was chosen'
    },
    showPopup: {
      type: Boolean,
      default: false
    },
    chosenBelief: {
      type: String,
      default: 'nothing; no belief was chosen'
    },
    singleBeliefObject: {
        type: Object,
        default: () => ({
            id: "0",
            name: "",
            value: "0"
        })
    },
    allBeliefs: {
      type: Array,
      default: () => (["I believe something", "I trust the councilor"])
    },
    beliefObjects: {
        type: Array,
        default: () => ([{name: "I believe something", value: "0.5"}])
    }
  },
  name: "EditBelief",
  components: {
    Popup,
  },
  data() {
    return {
        chosenBeliefObject: reactive(this.singleBeliefObject),
        chosenBeliefId: ref(this.singleBeliefObject.id),
        chosenBeliefValue: ref(this.singleBeliefObject.value),
        localAllBeliefs: this.allBeliefs,
        localBeliefObjects: [...this.beliefObjects],
    }
  },
  watch: {
    chosenBeliefObject: {
        deep: true,
        handler(newValue) {
            this.$emit('update:singleBeliefObject', newValue);
        }
    }
  },
  methods: {
    updateBeliefs(newValue) {
        const sendBeliefs = this.localBeliefObjects.map(belief => {
            return {
                id: belief.id,
                name: belief.name,
                value: belief.value
            }
        });
        editorApi.updateBeliefs(this.scenario, sendBeliefs);
        this.$emit('update:showPopup', newValue);
    },
    deleteBelief(newValue) {
        console.log(this.chosenBeliefObject);
        editorApi.deleteBelief(this.scenario, this.chosenBelief).then(() => {
            const index = this.localAllBeliefs.indexOf(this.chosenBelief);
            if (index > -1) {
                this.localAllBeliefs.splice(index, 1);
            }
            console.log(this.localAllBeliefs);
        })

        this.$emit('update:showPopup', newValue);
    }
  }
}
</script>

<style scoped>

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

.text{
  border: none;
  color: var(--chat-widget-button-text);
  font-size: large;
  font-family: 'Anton', sans-serif;
}

.default-value {
    border-radius: 16px;
    border: 0px solid white;
    margin-right: 2.5%;
    margin-left: 2.5%;
    margin-bottom: 5px;
    margin-top: 5px;
}

.click-button {
  margin-top: 5px;
  margin-left: 2px;
  margin-right: 2px;
  border-radius: 16px;
  background-color: var(--chat-widget-button);
  position: absolute;
  float: right;
  bottom: 5px;
  right: 5px;
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
</style>