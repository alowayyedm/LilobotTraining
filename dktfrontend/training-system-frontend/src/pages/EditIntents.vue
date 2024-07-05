<template>
  <div class="wrapper">

    <div class="dynamic-h2" title="Intents" style="margin: 5px; height: 5vh">
      <dynamic-text :header-text="'INTENTS'" :target-font-size="1.3" :min-font-size="0.5" :unit="'rem'" :step-size="0.1"
                    :overflow-wrap="'anywhere'" style="float: left">
      </dynamic-text>
      <div class="click-button" @click="back">Go back</div>
      <input placeholder="new_intent_name" class="intent-input" :class="{badinput: this.badformat}" v-model="this.newintent" style="float: right" @input="this.badformat =  !/[A-z]+_[A-z]+_[A-z]+/.test(this.newintent)">
      <div class="click-button" @click="(!this.badformat && this.newintent != '') ? this.intents.push({intent:this.newintent, values:[], modified:true}) : ()=>{}">Add intent</div>
      <div style="margin-bottom: 5px; float: right" class="click-button" :style="this.training?'background: gray':null" :disable="this.training" @click="trainRasa"> {{ this.training ? 'Training...' : 'Train RASA'}}</div>
    </div>
    <div class="intents">
      <div v-for="intent in intents" v-bind:key="intent.intent" style="display: inline-block" class="intent" @load="intent.modified=false">
        <dynamic-text :header-text="intent.intent" style="color: white" :target-font-size="1.3" :min-font-size="0.5" :unit="'rem'" :step-size="0.1"
                      :overflow-wrap="'anywhere'">
        </dynamic-text>
          <div style="margin-bottom: 5px" class="click-button" @click="intent.values.unshift('New data');intent.modified=true">Add data</div>
          <button class="general-icon float-right-button" style="display: inline-block" :class="{ grayscale: !intent.modified }" @click="saveIntent(intent.intent, intent.values); intent.modified = false"
                  :disabled="!intent.modified">
            <i class="fa-solid fa-save"></i>
          </button>
          <button class="general-icon float-right-button" style="display: inline-block" @click="saveIntent(intent.intent, []);intents.splice(intents.indexOf(intent),1)">
            <i class="fa-solid fa-close"></i>
          </button>
        <div style="overflow-y: scroll; height: 100%; width: 100%">

          <div v-for="(example, index) in intent.values" v-bind:key="index" class="float-block" style="">
            <input class="text-input" type="text" v-model="intent.values[index]" style="display: inline; float:left;"
                   @input="intent.modified = true">
            <i class="fa-solid fa-close" style="color: var(--vibrant-red); padding: 5px" @click="intent.values.splice(index, 1);intent.modified=true"></i>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-if="trainerror" class="popup">
    <div class="popup-content">
      <h5>SOMETHING WENT WRONG DURING TRAINING</h5>
      <div class="click-button" @click="this.trainerror=false">Close</div>
    </div>
  </div>
</template>

<script>
import DynamicText from "@/components/DynamicText";
import rasaApi from "@/mixins/rasaApi";

export default {
  name: "EditIntents",
  components: {
    DynamicText,
  },
  data: function () {
    return {
      intents: [],
      training: false,
      trainerror: false,
      newintent: '',
      badformat: false,
      unsaved: false,
    }
  },
  created() {
    window.onbeforeunload = (e) => {
      if (this.intents.filter(i => i.modified).length > 0) {
        e.preventDefault()
      }
    }
    rasaApi.getTrainingData(this.$store).then(data => {
      Object.keys(data).forEach(k => this.intents.push({intent:k,values:data[k]}))
    });
  },
  methods: {
    saveIntent(intent,values) {
      rasaApi.updateTrainingData(intent,values);
    },
    async trainRasa() {
      try {
        this.training = true;
        await rasaApi.train(this.intents);
      } catch (e) {
        this.trainerror = true;
        console.error("AAAA",e);
      } finally {
        this.training = false;
      }
    },
    back() {
      if (confirm('Changes that you made may not be saved.')){
        window.history.back()
      }
    }
  }
}
</script>

<style scoped>
.wrapper {
  margin: 1vw;
  height: 87vh;
}

.float-block {
  display: block;
  position: relative;
  height: auto;
  padding: 5px;
}

.intents {
  overflow-x: scroll;
  background: var(--list-background);
  display: flex;
  border-radius: 16px;
  flex-direction: row;
  flex-wrap: nowrap;
  overflow-y: clip;
  max-height: 94%;
}

.intent {
  flex-shrink: 0;
  margin: 10px;
}

.text-input {
  height: 100%;
  background: none;
  outline: none;
  border: none;
  padding: 5px;
  border-radius: 100px;
}

.grayscale,
.grayscale:hover {
  background: gray;
}

.grayscale:hover {
  transform: none;
  box-shadow: none;
}

.text-input:focus {
  background: var(--main-background);
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

.intent-input {
  margin-top: 5px;
  margin-left: 2px;
  margin-right: 2px;
  float: right;
  border-radius: 16px;
  background-color: var(--chat-widget-button);
  border: none;
  color: white;
  font-size: small;
  padding: 0.5rem;
  font-family: 'Anton', sans-serif;
  width: max-content;
  cursor: pointer;

  /* borders only used in accessibility mode */
  border: none;

}

.badinput {
  outline: none;
  border: solid;
  border-color: var(--vibrant-red) !important;
  border-width: 2px;
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

</style>