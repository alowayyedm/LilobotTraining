<template>
  <Popup    
    :showPopup="showPopup" 
    @update:showPopup="updateShowPopup"
    :headerText="`Mapping for ${chosenIntent}`">
    <div  class="test">
      <BeliefUpdate
      :updateName="`belief mapping`"
      :options="updateTypes"
      :buttonValue="`add belief`"
      :mapItems="mappings"
      @update:mapItems="updateMappings"
      :allBeliefs="localAllBeliefs"
      @update:allBeliefs="updateAllBeliefs">      
      </BeliefUpdate>
    </div>
    <div class="test2">
      <BeliefUpdate
      :updateName="`constraint mapping`"
      :options="boundaryTypes"
      :buttonValue="`add belief`"
      :mapItems="constraints"
      @update:mapItems="updateConstraints"
      :allBeliefs="localAllBeliefs"
      @update:allBeliefs="updateAllBeliefs">

      </BeliefUpdate>
    </div>
    <button class="click-button" @click="updateShowPopup(false)">Done</button>
  </Popup>
</template>


<script>
import Popup from "./Popup.vue";
import BeliefUpdate from "./BeliefUpdate.vue";
import { ref} from 'vue';
import { reactive } from "vue";

export default {
  props: {
    showPopup: {
      type: Boolean,
      default: false
    },
    chosenIntent: {
      type: String,
      default: 'nothing; no intent was chosen'
    },
    singleIntentMapping: {
      type: Object,
      default: () => (
      {
        mappings: [],
        constraints: []
      })
    },
    allBeliefs: {
      type: Array,
      default: () => ([])
    }
  },
  name: "IntentMapping",
  components: {
    Popup,
    BeliefUpdate
  },
  // setup(props, { emit }) {
  //   const mappingObject = reactive(props.singleIntentMapping);
  //   const updateTypes = ref(["INCREASE", "SET_TO", "DECREASE"]);
  //   const boundaryTypes = ref(["LT", "GT", "EQ", "NEQ", "GTE", "LTE"]);
  //   const mappings = ref(props.singleIntentMapping.mappings);
  //   const constraints = ref(props.singleIntentMapping.constraints);
  //   mappingObject.value.mappings = mappings;
  //   mappingObject.value.constraints = constraints;
  //   watch(mappingObject, (newValue) => {
  //     emit('update:singleIntentMapping', newValue)
  //   })

  //   return {
  //     mappingObject,
  //     updateTypes,
  //     boundaryTypes,
  //   }
  // },
  data() {
    return {
      mappingObject: reactive(this.singleIntentMapping),
      updateTypes: [
        {
          type: "INCREASE",
          title: "Increase"
        },
        {
          type: "SET_TO",
          title: "Set to"
        },
        {
          type: "DECREASE",
          title: "Decrease"
        }
      ],
      // updateTypes: ["INCREASE", "SET_TO", "DECREASE"],
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
      // boundaryTypes: ["LT", "GT", "EQ", "NEQ", "GEQ", "LEQ"],
      mappings: ref(this.singleIntentMapping.mappings),
      constraints: ref(this.singleIntentMapping.constraints),
      localAllBeliefs: [...this.allBeliefs]
    };
  },
  watch: {
    mappingObject: {
      deep: true,
      handler(newValue) {
        this.$emit('update:singleIntentMapping', newValue);
      }
    }
  },
  methods: {
    checkMappings() {
      if (this.mappings.every(v => {
        return v >= 0 && v <= 1
      })) {
        return true;
      } else {
        return false;
      }
    },
    checkConstraints() {
      if (this.constraints.every(v => {
        return v >= 0 && v <= 1
      })) {
        return false;
      } else {
        return true;
      }
    },
    updateShowPopup(newValue) {
      this.$emit('update:showPopup', newValue);
    },
    handleMappingsUpdate(newMappings) {
      this.mappings = newMappings
    },
    updateConstraints(newMappings) {
      this.constraints = newMappings
    },
    updateAllBeliefs(newValue) {
      this.localAllBeliefs = newValue;
      this.$emit('update:allBeliefs', this.localAllBeliefs);
    },
  }
}
</script>

<style scoped>
.test {
  height: 45%;
  overflow: auto;
}
.test2 {
  height: 45%;
  overflow: auto;
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
</style>