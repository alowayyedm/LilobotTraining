<template>
    <Popup class="popup"
      :showPopup="showPopup" 
      @update:showPopup="updateDesires"
      :headerText="`Edit desire '${chosenDesire}'`">
      <div class="mapping">
        <h5 class="text">Phase</h5>
        <select class="styled-select mapItemChild" v-model="chosenDesireObject.phase" id="selectedOption">
            <option v-for="option in options" :key="option" :value="option"> {{ option }}</option>
        </select>
      </div>
        <BeliefUpdate
        :updateName="`Constraints`"
        :options="boundaryTypes"
        :buttonValue="`Add belief constraints`"
        :mapItems="constraints"
        @update:mapItems="updateConstraints"
        :allBeliefs="allBeliefs"
        @update:allBeliefs="updateAllDesires">
        </BeliefUpdate>
        <button class="click-button" @click="updateDesires(false)">Done</button>
        <button 
        style="right: 50px;"
        class="click-button" @click="deleteDesire(false)">Delete Desire</button>
    </Popup>
  </template>

<script>
import { reactive, ref } from "vue";
import Popup from "./Popup.vue";
import editorApi from "@/mixins/editorApi";
import BeliefUpdate from "./BeliefUpdate.vue";

export default {
    props: {
    scenario: {
        type: String,
        default: 'no scenario was chosen'
    },
    options: {
        type: Array,
        default: () => ["PHASE1", "PHASE2", "PHASE3", "PHASE4", "PHASE5"]
    },
    showPopup: {
      type: Boolean,
      default: false
    },
    chosenDesire: {
      type: String,
      default: 'nothing; no desire was chosen'
    },
    singleDesireObject: {
        type: Object,
        default: () => ({
            name: "",
            phase: "",
            constraints: []
        })
    },
    allDesires: {
      type: Array,
      default: () => ([])
    },
    desireObjects: {
        type: Array,
        default: () => ([])
    },
    allBeliefs: {
        type: Array,
        default: () => (["I believe something", "I trust the councilor"])
    }
  },
  name: "EditDesire",
  components: {
    Popup,
    BeliefUpdate
  },
  data() {

    var constraints = this.singleDesireObject.constraints.map(c => {
        return {
            name: c.belief,
            modifier: c.boundary,
            value: c.value
        }
    });

    return {
        chosenDesireObject: reactive(this.singleDesireObject),
        chosenDesirePhase: ref(this.singleDesireObject.phase),
        chosenDesireConstraints: ref(this.singleDesireObject.constraints),
        constraints,
        localAllDesires: this.allDesires,
        localDesireObjects: [...this.desireObjects],
        boundaryTypes: [
            {
                type: "LT",
                title: "Less than"
            },
            {
                type: "GT",
                title: "Greater than"
            },
            {
                type: "EQ",
                title: "Equal"
            },
            {
                type: "NEQ",
                title: "Not equal"
            },
            {
                type: "GEQ",
                title: "Greater than or equal"
            },
            {
                type: "LEQ",
                title: "Less than or equal"
            }
        ],
        // boundaryTypes: ["LT", "GT", "EQ", "NEQ", "GTE", "LTE"],
    }
  },
  watch: {
    chosenDesireObject: {
        deep: true,
        handler(newValue) {
            this.$emit('update:singleDesireObject', newValue);
        }
    },
    constraints: {
        deep: true,
        handler(newValue) {
            this.constraints = newValue;
            this.chosenDesireObject.constraints = this.constraints.map(c => {
                return {
                    belief: c.name,
                    boundary: c.modifier,
                    value: c.value
                }
            })
            this.$emit('update:singleDesireObject', this.chosenDesireObject);
            console.log(this.chosenDesireObject);
        }
    },
    chosenDesirePhase: {
        deep: true, 
        handler(newValue) {
            this.chosenDesireObject.phase = newValue;
            this.$emit('update:singleDesireObject', this.chosenDesireObject);
        }
    }
  },
  methods: {
    updateDesires(newValue) {
        const sendDesires = this.localDesireObjects.map(desire => {
            if (desire.name === this.chosenDesire) {
                return {
                    name: desire.name,
                    phase: this.chosenDesireObject.phase,
                    constraints: [this.chosenDesireObject.constraints]
                }
            } else {
                return {
                    name: desire.name,
                    phase: desire.phase,
                    constraints: [desire.constraints]
                }
            }
        });
        console.log(sendDesires);
        editorApi.updateDesires(this.scenario, sendDesires);
        this.$emit('update:showPopup', newValue);
    },
    deleteDesire(newValue) {
        //console.log(this.chosenDesireObject);
        editorApi.deleteDesire(this.scenario, this.chosenDesire).then(() => {
            const index = this.localAllDesires.indexOf(this.chosenDesire);
            if (index > -1) {
                this.localAllDesires.splice(index, 1);
            }
            //console.log(this.localAllBeliefs);
        })

        this.$emit('update:showPopup', newValue);
    },
    updateAllDesires(newValue) {
        this.localAllDesires = newValue;
        this.$emit('update:allBeliefs', this.localAllDesires);
    },
    updateConstraints(newMappings) {
        console.log(newMappings);
        this.constraints = newMappings;
        this.chosenDesireObject.constraints = this.constraints.map(c => {
            return {
                belief: c.name,
                boundary: c.modifier,
                value: c.value
            }
        })
    },
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
  position: static;
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