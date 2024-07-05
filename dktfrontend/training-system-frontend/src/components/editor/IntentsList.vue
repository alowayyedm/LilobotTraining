<template>
  <div>
    <div class="container" style="display: flex;">
      <div class="dynamic-h2" title="Responses">
        <dynamic-text
            :header-text="'RESPONSES'"
            :target-font-size=1.3
            :min-font-size=0.5
            :unit="'rem'"
            :step-size=0.1
            :overflow-wrap="'anywhere'"
        >
        </dynamic-text>
        <i class="fa-solid fa-square-check intents_loaded" title="RASA Server Running" v-show="isStatusOK"
           style="float: left"></i>
        <i class="fa-solid fa-square-xmark intents_loaded" title="RASA Server is Not Running" v-show="!isStatusOK"
           style="float:left;"></i>
      </div>
      <div style="margin-left: auto;">
        <img src="../../../images/question.png" height="25px" width="25px"
             title="The list of user responses where you can edit, delete or create new ones. Each response must be created with the name in the format subject_attribute_type. Then you can add example responses for each user response.">
      </div>
    </div>
    <div class="container intents">
      <div v-for="intent in intents" v-bind:key="intent" @load="this.modified = false">
        <div class="float-block">
          <i class="fa-solid fa-caret-down toggle-caret" v-show="intent.show" @click="intent.show = false"></i>
          <i class="fa-solid fa-caret-right toggle-caret" v-show="!intent.show" @click="intent.show = true"></i>
          <select v-model="intent.name" :title="intent.name" @change="this.modified = true"> // todo: fix proper intents
            <option v-for="i in this.existingIntents" v-bind:key="i" class="text-input"
                    :selected="intent.name == (i.type + '_' + i.subject + '_' + i.attribute)">{{ i.type + "_" + i.subject + "_" +
            i.attribute}}</option>
          </select>
          <!--        <input class="text-input" type="text" v-model="intent.name" style="display: inline; float: left"-->
          <!--          @input="this.modified = true, verifyinput(intent)" :class="{ badinput: intent.invalid }"-->
          <!--          :title="(intent.invalid ? 'Make sure the intent name follows the format type_subject_attribute and does not have the same name as any other intent' : '')">-->

          <button class="general-icon float-right-button" @click="removeIntent(intent)" title="Remove Intent">
            <i class="fa-solid fa-close"></i>
          </button>
        </div>
        <div v-show="intent.show" v-for="(example, index) in intent.examples" v-bind:key="example" class="float-block">
          <input class="text-input" type="text" v-model="example.text" style="display: inline; float:left;"
                 @input="this.modified = true" :ref="intent.name + '-' + index">
          <i class="fa-solid fa-close" @click="removeExample(intent, example);"></i>
        </div>
        <div v-show="intent.show" @click="addExample(intent)" class="click-button">Add new example</div>
        <hr class="intent-divider">
      </div>
      <div @click="addIntent" v-show="this.scenario != ''" class="click-button"> Add new intent</div>
      <div v-show="this.scenario == ''"> Select a scenario to view the intents for </div>
    </div>
    <button class="general-icon float-right-button" :class="{ grayscale: !this.modified }" @click="saveIntent()"
            :disabled="!this.modified" style="margin-top: 5px">
      <i class="fa-solid fa-save"></i>
    </button>
  </div>

    <router-link to="/train/editIntents">
      <div style="float: right; margin-top: 5px" class="click-button float-right-button">Edit intent definitions</div>
    </router-link>
</template>

<script>
import DynamicText from "@/components/DynamicText.vue";
import rasaApi from '@/mixins/rasaApi';
import editorApi from '@/mixins/editorApi';

export default {
  name: "IntentsList",
  components: {
    DynamicText
  },
  props: {
    scenario: {
      type: String,
      required: true
    }
  },
  emits: ['changeIntents'],
  watch: {
    scenario: function (newv) {
      //console.log(newv)
      editorApi.getIntents(newv).then(s => {

        this.intents = s.map(i => {
          //console.log(i)
          return { name: i.intent, examples: i.values.map(v => { return { text: v } }) }
        });
      })
    }
  },
  data() {
    return {
      intents: [],
      isStatusOK: false,
      existingIntents: [],
      modified: false,
    }
  },
  created() {
    this.checkStatus(); // Check the status on component creation
  },
  methods: {
    async checkStatus() {
      rasaApi.getStatus().then(valid => {
        if (valid) {
          this.loadRasaIntents();
          this.isStatusOK = true;
        }
        else setTimeout(() => this.checkStatus(), 10000);
      });
    },
    async loadRasaIntents() {
      rasaApi.getIntents().then(intents => {
        // console.log(intents);
        this.existingIntents = intents;
      });
      rasaApi.getTrainingData().then(data => {
        if (data == true) {
          console.log(data);
        }
      });
    },
    addIntent() {
      let newintent = { name: "New Intent", examples: [{ text: "Example response" }] }
      this.intents.push(newintent)
      this.modified = true
    },
    removeIntent(intent) {
      editorApi.removeIntentResponses(this.scenario, intent.name).then(r => {
        if (r) {
          this.intents.splice(this.intents.indexOf(intent), 1)
        }
      })
    },
    loadExamples(intent) {
      editorApi.getExamples(this.scenario, intent.name).then((examples) => {
        intent.examples = examples;
      });
    },
    addExample(intent) {
      intent.examples.push({ text: "Example response" })
      this.modified = true
      this.$nextTick(() => {
        this.$refs[intent.name + '-' + (intent.examples.length - 1)][0].select()
      })
    },
    removeExample(intent, example) {
      intent.examples.splice(intent.examples.indexOf(example), 1)
      this.modified = true
    },
    saveIntent() {
      this.modified = false
      console.log(this.intents);
      this.$emit("changeIntents", this.intents.map(i => i.name));
      editorApi.updateExamples(this.scenario, {
        intents: this.intents.map(i => {
          return { intent: i.name, values: i.examples.map(e => { return e.text }) }
        })
      }
      )
    },
    verifyinput(e) {
      // console.log(this.intents.filter(i => i.name == e.name).length)
      e.invalid = !(/[A-z]+_[A-z]+_[A-z]+/.test(e.name)) || this.intents.filter(i => i.name == e.name).length >= 2
    },
  }
}
</script>

<style scoped>
.float-block {
  display: block;
}

.float-block:after {
  clear: both;
  content: " ";
  display: block;
}

.intents_loaded {
  position: absolute;
  left: 1%;
}

.intents {
  height: 100%;
  margin-top: 1vh;
  margin-right: 5px;
  border-radius: 16px;
  background-color: var(--list-background);
  padding: 16px;
  overflow-y: scroll;
}

.text-input {
  background: none;
  outline: none;
  border: none;
  padding: 5px;
  border-radius: 100px;
}

.text-input:focus {
  background: var(--main-background);
}

.grayscale,
.grayscale:hover {
  background: gray;
}

.grayscale:hover {
  transform: none;
  box-shadow: none;
}

.toggle-caret {
  float: left;
  margin-right: 1rem;
}

.float-right-button {
  display: inline;
  float: right;
  margin-left: 1rem;
  margin-bottom: 5px;
}

.intent-divider {
  border-radius: 10px;
  border-color: var(--main-background);
  border-width: 2px;
  border-style: solid;
  margin: 1rem 0;
}

.click-button {
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

.badinput {
  border: red 5px;
  border-style: solid;
}
</style>
