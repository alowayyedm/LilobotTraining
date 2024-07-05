<!--  Graph Mapper component
This template displays how BDI values are mapped and allows them for easy creation, deletion and `upda`ting of these values.
!-->
<template>
  <div class="container" style="display: flex;">
    <div class="dynamic-h2" title="Graph Editor">
      <dynamic-text :header-text="'BDI MAPPINGS'" :target-font-size=1.3 :min-font-size=0.5 :unit="'rem'" :step-size=0.1
        :overflow-wrap="'anywhere'">
      </dynamic-text>
    </div>
    <div style="margin-left: auto;">
      <img src="../../../images/question.png" height="25px" width="25px"
        title="Decide how beliefs, desires and user responses are mapped to each other.">
    </div>
  </div>
  <div class="container graph">
    <tab-wrapper class="tabs">
      <tab-item title="Belief mapping">
        <ul class="mappingList" v-for="key in intentMapping.keys()" :key="key">
          <li @click="openEditorWithIntent(key)"
            title="Click to reconfigure mapping for this response">{{ key }}
          </li>
          <button class="general-icon float-right-button" @click="deleteIntentionMapping(scenario, key)" title="Remove Mapping">
              <i class="fa-solid fa-close"></i>
          </button>
        </ul>
        <button class="click-button" @click="setupBeliefMapping">Add mapping</button>
      </tab-item>
      <tab-item title="Phase 1">
        <ul class="mappingList" v-for="key in phaseMapping.get('phase1').keys()" :key="key">
          <li
            @click="openDesireEditorWithDesire('phase1', key)">{{ key }}
          </li>
          <button class="general-icon float-right-button" @click="deleteDesireMapping(scenario, key, 'phase1')" title="Remove Mapping">
              <i class="fa-solid fa-close"></i>
          </button>
        </ul>
        <button class="click-button" @click="setupDesireMapping('phase1')">Add desire</button>
      </tab-item>
      <tab-item title="Phase 2">
        <ul class="mappingList" v-for="key in phaseMapping.get('phase2').keys()" :key="key">
          <li
            @click="openDesireEditorWithDesire('phase2', key)">{{ key }}
          </li>
          <button class="general-icon float-right-button" @click="deleteDesireMapping(scenario, key, 'phase2')" title="Remove Mapping">
              <i class="fa-solid fa-close"></i>
          </button>
        </ul>
        <button class="click-button" @click="setupDesireMapping('phase2')">Add desire</button>
      </tab-item>
      <tab-item title="Phase 3">
        <ul class="mappingList" v-for="key in phaseMapping.get('phase3').keys()" :key="key">
          <li
            @click="openDesireEditorWithDesire('phase3', key)">{{ key }}
          </li>
          <button class="general-icon float-right-button" @click="deleteDesireMapping(scenario, key, 'phase3')" title="Remove Mapping">
              <i class="fa-solid fa-close"></i>
          </button>
        </ul>
        <button class="click-button" @click="setupDesireMapping('phase3')">Add desire</button>
      </tab-item>
      <tab-item title="Phase 4">
        <ul class="mappingList" v-for="key in phaseMapping.get('phase4').keys()" :key="key">
          <li
            @click="openDesireEditorWithDesire('phase4', key)">{{ key }}
          </li>
          <button class="general-icon float-right-button" @click="deleteDesireMapping(scenario, key, 'phase4')" title="Remove Mapping">
              <i class="fa-solid fa-close"></i>
          </button>
        </ul>
        <button class="click-button" @click="setupDesireMapping('phase4')">Add desire</button>
      </tab-item>
      <tab-item title="Phase 5">
        <ul class="mappingList" v-for="key in phaseMapping.get('phase5').keys()" :key="key">
          <li
            @click="openDesireEditorWithDesire('phase5', key)">{{ key }}
          </li>
          <button class="general-icon float-right-button" @click="deleteDesireMapping(scenario, key, 'phase5')" title="Remove Mapping">
              <i class="fa-solid fa-close"></i>
          </button>
        </ul>
        <button class="click-button" @click="setupDesireMapping('phase5')">Add desire</button>
      </tab-item>
    </tab-wrapper>
  </div>
  <div style="display: flex; float: right">
    <button class="click-button" @click="showBeliefs()">Edit Beliefs</button>
    <button class="click-button" @click="showDesires()">Edit Desires</button>
    <button class="click-button" @click="showActions()">Edit Actions</button>
    <button class="click-button" @click="saveMapping">Save Mapping</button>
  </div>
  <div>
    <div v-if="showPopup" class="popup" @keyup.esc="closePopup" @keyup.enter="chooseSelection">
      <SelectionPopup v-model:showPopup="showPopup" v-model:headerText="selectionTitle"
        v-model:selectedItem="selectedIntent" @update:selectedItem="chooseSelectionForMapping"
        :selectionItems="allSelection" @update:selectionItems="updateSelection" :canAddToList="canAddNewExample">
      </SelectionPopup>
    </div>
    <div v-if="showIntentMapping" class="popup" @keyup.esc="closePopup" @keyup.enter="chooseSelection">
      <IntentMapping v-model:showPopup="showIntentMapping" :chosenIntent="selectedIntent"
        v-model:singleIntentMapping="singleIntentMapping" :allBeliefs="allBeliefs"
        @update:allBeliefs="updateAllBeliefs">
      </IntentMapping>
    </div>
    <div v-if="showDesireMapping" class="popup" @keyup.esc="closePopup" @keyup.enter="chooseSelection">
      <DesireMapping v-model:showPopup="showDesireMapping" :chosenIntent="selectedIntent"
        v-model:singleDesireMapping="singleDesireMapping" :allBeliefs="allBeliefs" @update:allBeliefs="updateAllBeliefs"
        v-model:allActions="allActions">
      </DesireMapping>
    </div>
    <div v-if="showBeliefEditor" class="popup" @keyup.esc="closePopup" @keyup.enter="chooseSelection">
      <EditBelief :scenario="scenario" v-model:showPopup="showBeliefEditor" :chosenBelief="selectedIntent"
        v-model:singleBeliefObject="singleBeliefObject" :beliefObjects="beliefObjects" :allBeliefs="allBeliefs"
        @update:allBeliefs="updateAllBeliefs">
      </EditBelief>
    </div>
    <div v-if="showDesireEditor" class="popup" @keyup.esc="closePopup" @keyup.enter="chooseSelection">
      <EditDesire :scenario="scenario" v-model:showPopup="showDesireEditor" :chosenDesire="selectedIntent"
        v-model:singleDesireObject="singleDesireObject" :desireObjects="desireObjects" :allDesires="allDesires"
        @update:allDesires="updateAllDesires" :allBeliefs="allBeliefs">
      </EditDesire>
    </div>
  </div>
</template>

<script>
import DynamicText from "@/components/DynamicText.vue";
import TabWrapper from "./graph-tabs/TabWrapper.vue";
import TabItem from "./graph-tabs/TabItem.vue";
import SelectionPopup from "@/components/editor/graph-tabs/SelectionPopup.vue";
import IntentMapping from "@/components/editor/graph-tabs/IntentMapping.vue";
import DesireMapping from "@/components/editor/graph-tabs/DesireMapping.vue";
import { ref, reactive } from 'vue';
import editorApi from "@/mixins/editorApi";
import EditBelief from "./graph-tabs/EditBelief.vue";
import EditDesire from "./graph-tabs/EditDesire.vue";

export default {
  name: "GraphMapper",
  components: {
    DynamicText,
    TabWrapper,
    TabItem,
    SelectionPopup,
    IntentMapping,
    DesireMapping,
    EditBelief,
    EditDesire
  },
  props: {
    scenario: {
      type: String,
      required: true
    },
    intents: {
      type: Array,
      required: true
    }
  },
  watch: {
    intents: {
      handler(newv) {
        this.allIntents = [...new Set([...newv, ...this.allIntents])];
      }
    },
    scenario: async function (newv) {
      this.resetState();
      const a = await editorApi.getAllActions(newv);
      this.allActions = a.map(a => a.name);

      const s = await editorApi.getIntents(newv);
      this.allIntents = s.map(i => i.intent);

      for (var i of this.allIntents) {
        const m = await editorApi.getMappings(this.scenario, i);
        const c = await editorApi.getMappingConditions(this.scenario, i);

        if (m.length == 0 && c.length == 0) continue;
        const mappings = m.map(item => ({
          name: item.name,
          modifier: item.type,
          value: item.value
        }));
        const constraints = c.map(item => ({
          name: item.name,
          modifier: item.type,
          value: item.value
        }));
        this.intentMapping.set(i, {
          constraints: constraints,
          mappings: mappings
        });
      }

      editorApi.getAllBeliefs(newv).then(s => {
        this.beliefObjects = s.map(b => {
          return {
            id: b.id,
            name: b.name,
            value: b.value,
          }
        })
        this.allBeliefs = s.map(b => {
          return b.name;
        });
      });
      editorApi.getDesires(newv).then(responseObjects => {
        this.desireObjects = responseObjects.map(d => {
          return {
            name: d.name,
            phase: d.phase,
            constraints: d.constraints[0],
            actions: d.actions,
          }
        })
        this.allDesires = responseObjects.map(d => d.name);
        for (const obj of responseObjects) {
          const { phase, name, constraints, actions } = obj;
          if (phase == null) {
            continue;
          }
          var phaseName = phase.toLowerCase();

          if (!this.phaseMapping.has(phaseName)) {
            this.phaseMapping.set(phaseName, new Map());
          }

          const nameMap = this.phaseMapping.get(phaseName);
          nameMap.set(name, {
            constraints: constraints[0].map(({ belief, boundary, value }) => ({
              name: belief,
              modifier: boundary,
              value: value
            })),
            actions: actions.map(a => ({ name: a.name }))
          });
        }
      });
    },
    singleIntentMapping: {
      deep: true,
      handler(newValue) {
        if (!this.performWatchFlag) {
          return;
        }
        this.intentMapping.set(this.selectedIntent, newValue);
      }
    },
    // singleDesireMapping:  {
    //   deep: true,
    //   handler(newValue) {
    //     this.desireMapping.set(this.selectedIntent, newValue);
    //     this.phaseMapping.set(this.currentPhase, this.desireMapping);
    //   }
    // },
    showIntentMapping: {
      deep: false,
      handler(newValue) {
        if (newValue) {
          return;
        }
        for (const [key, value] of this.intentMapping) {
          const intent = key;
          const mappings = value.mappings;
          const constraints = value.constraints;
          const genericBeliefsBU = [];
          const genericBeliefsBC = [];
          mappings.map(m => {
            genericBeliefsBU.push({
              name: m.name,
              type: m.modifier,
              value: m.value
            })
          });
          constraints.map(c => {
            genericBeliefsBC.push({
              name: c.name,
              type: c.modifier,
              value: c.value
            })
          });
          editorApi.updateMappings(this.scenario, intent, genericBeliefsBU);
          editorApi.updateMappingConditions(this.scenario, intent, genericBeliefsBC);
        }
      }
    },
    // allBeliefs: {
    //   deep: true,
    //   handler(newValue) {
    //     if (newValue == 'some complete random shit') {
    //       console.log("i gues you did get here");
    //     }
    //     console.log("could mayby update now");
    //   }
    // },
    allActions: {
      deep: true,
      handler(newValue) {
        this.saveActions(newValue);
      }
    },
    allBeliefs: {
      deep: true,
      handler(newv) {
        console.log(this.prevBeliefs, "prev");
        console.log(newv, "new");
        var test = newv.filter(element => !this.prevBeliefs.map(c => c).includes(element));
        this.prevBeliefs = newv.slice();
        const currentBeliefNames = this.beliefObjects.map(b => {
            return b.name;
          })
        for (const belief of test) {
          if (!currentBeliefNames.includes(belief)) {
            var newBelief = {
              name: belief,
              value: "0"
            }
            console.log(newBelief, "found");
            editorApi.createBelief(this.scenario, newBelief);
          }
        }
      }
    },
    allDesires: {
      deep: true,
      handler(newv) {
        this.allDesires = newv;
        const currentDesireNames = this.desireObjects.map(d => {
            return d.name;
          })
        for (const desire of this.allDesires) {
          if (!currentDesireNames.includes(desire)) {
            var newDesire = {
              name: desire,
              phase: null,
              constraints: []
            }
            editorApi.createDesire(this.scenario, newDesire);
          }
        }
      }
    },
    showDesireMapping: {
      deep: false,
      handler(newValue) {
        if (newValue) {
          return;
        }
        this.commitDesires();
      }
    },
    singleBeliefObject: {
      deep: true,
      handler(newValue) {
        this.beliefObjects.forEach(belief => {
          if (belief.name === this.selectedIntent) {
            belief.value = newValue.value;
          }
        })
      }
    }
  },
  data() {
    // return this.getInitialState();
    return {
        // Define reactive properties using ref and reactive
        //const scenarios = ref([]);
        showPopup: ref(false),
        prevBeliefs: [],
        selectionTitle: ref(''),
        showIntentMapping: ref(false),
        showDesireMapping: ref(false),
        showBeliefEditor: ref(false),
        showDesireEditor: ref(false),
        functionAfterSelction: () => {

        },
        canAddNewExample: ref(false),
        //used as temporary data containers that the editor component will edit
        // when calling the intentMapping they should be set to the thing edited at this moment
        selectedIntent: ref(''),
        singleIntentMapping: reactive({
          mappings: [],
          constraints: []
        }),
        singleDesireMapping: reactive({
          constraints: [],
          actions: []
        }),
        allSelection: ref([]),
        allIntents: ref([]),
        beliefObjects: ref([{name: "I believe something", value: "0.5"}]),
        allBeliefs: ref([]),
        singleBeliefObject:reactive({
          id: "0",
          name: "",
          value: "0"
        }),
        desireObjects: ref([]),
        allDesires: ref([]),
        allActions: ref([]),
        singleDesireObject:reactive({
          name: "",
          phase: "",
          constraints: []
        }),
        intentMapping: new Map(),
        phaseMapping: new Map([
          ['phase1', new Map()],
          ['phase2', new Map()],
          ['phase3', new Map()],
          ['phase4', new Map()],
          ['phase5', new Map()]
        ]),
        currentPhase: 'phase1',
        modified: false,
        currentlyEditing: '',
      }
  },
  methods: {
    // initial state
    getInitialState() {
      return {
        // Define reactive properties using ref and reactive
        //const scenarios = ref([]);
        showPopup: ref(false),
        selectionTitle: ref(''),
        showIntentMapping: ref(false),
        showDesireMapping: ref(false),
        showBeliefEditor: ref(false),
        showDesireEditor: ref(false),
        functionAfterSelction: () => {

        },
        canAddNewExample: ref(false),
        //used as temporary data containers that the editor component will edit
        // when calling the intentMapping they should be set to the thing edited at this moment
        selectedIntent: ref(''),
        singleIntentMapping: reactive({
          mappings: [],
          constraints: []
        }),
        singleDesireMapping: reactive({
          constraints: [],
          actions: []
        }),
        allSelection: ref([]),
        allIntents: ref([]),
        beliefObjects: ref([{name: "I believe something", value: "0.5"}]),
        allBeliefs: ref([]),
        singleBeliefObject:reactive({
          id: "0",
          name: "",
          value: "0"
        }),
        desireObjects: ref([]),
        allDesires: ref([]),
        allActions: ref([]),
        singleDesireObject:reactive({
          name: "",
          phase: "",
          constraints: []
        }),
        intentMapping: new Map(),
        phaseMapping: new Map([
          ['phase1', new Map()],
          ['phase2', new Map()],
          ['phase3', new Map()],
          ['phase4', new Map()],
          ['phase5', new Map()]
        ]),
        currentPhase: 'phase1',
        modified: false,
        currentlyEditing: '',
      }
    },
    resetState() {
      this.performWatchFlag = false;
      Object.assign(this.$data, this.getInitialState());
      this.$nextTick(() => {
        this.performWatchFlag = true; 
      });

    },

    // intent to belief mapping functions
    setupBeliefMapping() {
      this.showPopup = !this.showPopup;
      this.selectionTitle = 'Intent';
      this.modified = true;
      this.canAddNewExample = false;
      this.allSelection = this.allIntents;
      this.functionAfterSelction = this.intentMappingSelectionFunc;
    },
    toggleIntentMapping() {
      this.showIntentMapping = !this.showIntentMapping;
    },
    commitDesires() {
      if (this.singleDesireMapping.constraints.length > 0 || this.singleDesireMapping.actions.length > 0) {
          this.desireMapping.set(this.selectedIntent, this.singleDesireMapping);
          this.phaseMapping.set(this.currentPhase, this.desireMapping);
        }
        const desires = [];
        // var counter = 0;
        for (const [key, value] of this.phaseMapping) {
          const phase = key.toUpperCase();
          for (const [desireName, conditions] of value) {
            var constraints = [];
            if (conditions.constraints !== undefined) {
              constraints = conditions.constraints.map(c => {
                return {
                  belief: c.name,
                  boundary: c.modifier,
                  value: c.value
                }
              });
            }
            var actions = [];
            if (conditions.actions !== undefined) {
              actions = conditions.actions.map(a => {
                return {
                  name: a.name
                }
              })
            }
            const desire = {
              name: desireName,
              phase: phase,
              constraints: [constraints],
              actions: actions
            }
            // if (desires.some(d => d.name !== desireName)) {
            desires.push(desire);
            // }
          }
        }
        editorApi.updateDesires(this.scenario, desires);
    },
    intentMappingSelectionFunc() {
      this.singleIntentMapping = reactive({
        mappings: [],
        constraints: []
      });
      this.showIntentMapping = true;
    },
    openEditorWithIntent(key) {
      this.selectedIntent = key;
      this.singleIntentMapping = reactive(this.intentMapping.get(key));
      this.showIntentMapping = true;
    },
    // desire mapping functions

    setupDesireMapping(phase) {
      this.currentPhase = phase;
      this.showPopup = !this.showPopup;
      this.selectionTitle = 'Desire';
      this.modified = true;
      this.canAddNewExample = true;
      this.allSelection = this.allDesires;
      this.desireMapping = this.phaseMapping.get(phase);
      this.functionAfterSelction = this.desireMappingSelectionFunc;
    },
    desireMappingSelectionFunc() {
      this.singleDesireMapping = reactive({
        constraints: [],
        actions: []
      });
      this.desireMapping.set(this.selectedIntent, []);
      this.showDesireMapping = true;
    },
    deleteDesireMapping(scenario, desire, phase) {
      this.phaseMapping.get(phase).delete(desire);
      this.commitDesires();
      editorApi.deleteDesire(scenario, desire);
    },
    deleteIntentionMapping(scenario, intent) {
      editorApi.deleteIntentMapping(scenario, intent);
      this.intentMapping.delete(intent);
    },
    openDesireEditorWithDesire(phase, key) {
      this.selectedIntent = key;
      this.currentPhase = phase;
      this.desireMapping = this.phaseMapping.get(phase);
      var tempConstraints = {
        constraints: [],
        actions: []
      };
      if (this.desireMapping.has(key)) {
        tempConstraints = this.desireMapping.get(key);
      }
      this.singleDesireMapping = reactive(tempConstraints);
      this.showDesireMapping = true;
    },

    // general functions

    chooseSelectionForMapping() {
      this.showPopup = false;
      this.functionAfterSelction();
    },
    updateAllBeliefs(newValue) {
      this.allBeliefs = newValue;
    },
    updateSelection(newValue) {
      if (this.selectionTitle == 'Action') {
        this.allActions = newValue;
      }

      if (this.canAddNewExample) {
        if (this.selectionTitle == 'Desire') {
          this.allDesires = newValue;
        }
        if (this.selectionTitle == 'Belief') {
          this.allBeliefs = newValue;
        }
      }
    },

    //view beliefs and desires
    showActions() {
      this.showPopup = !this.showPopup;
      this.selectionTitle = 'Action';
      this.modified = true;
      this.canAddNewExample = true;
      this.allSelection = this.allActions;
      this.functionAfterSelction = () => { };
    },
    showBeliefs() {
      this.showPopup = !this.showPopup;
      this.selectionTitle = 'Belief';
      this.modified = true;
      this.canAddNewExample = true;
      this.allSelection = this.allBeliefs;
      this.functionAfterSelction = this.editBeliefs;
    },
    showDesires() {
      this.showPopup = !this.showPopup;
      this.selectionTitle = 'Desire';
      this.modified = true;
      this.canAddNewExample = true;
      this.allSelection = this.allDesires;
      this.functionAfterSelction = this.editDesires;
    },
    editBeliefs() {
      this.showPopup = true;
      this.beliefObjects.forEach(belief => {
        if (this.selectedIntent === belief.name) {
          this.singleBeliefObject = reactive(belief);
        }
      });
      this.showBeliefEditor = !this.showBeliefEditor;
    },
    editDesires() {
      this.showPopup = true;
      this.desireObjects.forEach(desire => {
        if (this.selectedIntent === desire.name) {
          this.singleDesireObject = reactive(desire);
        }
      });
      this.showDesireEditor = !this.showDesireEditor;
    },
    saveActions(actions) {
      const apiActions = actions.map(x => {
        return {
          name: x
        }
      })
      editorApi.createActions(this.scenario, apiActions);
    },
    saveMapping() {
      editorApi.commitScenario(this.scenario);
    }
  }
}
</script>

<style scoped>
.mappingList {
  width: 100%;
  color: #888888;
  border-width: 5px;
  list-style-type: none;
  padding: 0px;
  margin: 0;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.mappingList li {
  color: #FFFFFF;
  border: 1px solid #888888;
  padding: 10px;
  margin: 5px 0;
  background-color: #00DBA3;
  border-radius: 16px;
  font-family: 'Anton', sans-serif;
  cursor: pointer;
  transition: 0.4s all ease-out;
  width: 90%;
}

.mappingList li:hover {
  background-color: #00ca98;
}

.mappingList li.selected {
  background-color: #00ba8c;
}

.float-block {
  display: block;
}

.float-block:after {
  clear: both;
  content: " ";
  display: block;
}

.graph {
  height: 100%;
  margin-top: 1vh;
  margin-left: 5px;
  margin-right: 5px;
  border-radius: 16px;
  background-color: var(--list-background);
  padding: 16px;
  overflow-y: scroll;
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

.click-button:active {
  background-color: var(--chat-widget-button-active);
  outline-offset: 3px;
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