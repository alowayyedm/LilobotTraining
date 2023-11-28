<template>
    <div class="overlay" v-if="showPopup"></div>
    <div class="generic-popup-container" v-if="showPopup">
      <div id="popup-box">
        <div id="topbar">ADVANCED REPORT
          <button id="close" title="close popup" @click="close" aria-label="Close">
            <i class="fa-solid fa-xmark fa-2xl"></i>
        </button>
        </div>
        <div>
            <ul>
                <li v-for="option in options" :key="option.id">
                    <div class="optionLabel">
                        <input type="checkbox" v-model="option.value" :title="option.label"/>
                        {{ option.label }}
                    </div>
                </li>
            </ul>
        </div>
        <div id="buttons">
            <button class="button-with-icon-and-text" id="advanced-report-button" @click="accept">
                <i class="fa-solid fa-download"></i>
                &nbsp;&nbsp;
                Advanced Report</button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "AdvancedReportPopup",
    emits: ['download-advanced-report'],
    data() {
      return {
        showPopup: false,
        acceptPopup: false,
        options: [
            { id: 'abbreviations', label: 'Show belief and desire abbreviations', value: false },
            { id: 'all_belief_updates', label: 'Show all belief updates', value: false },
            { id: 'desire_updates', label: 'Show desire updates', value: true },
            { id: 'belief_update_causes', label: 'Show belief update causes', value: true },
            { id: 'belief_values', label: 'Show numeric values of updated beliefs', value: false },
        ]
      };
    },
    methods: {
      accept() {
        this.showPopup = false;
        this.$emit('download-advanced-report', this.options);
      },
      close() {
        this.showPopup = false;
      },
      showAdvancedReportPopup() {
        this.showPopup = true;
      }
    }
  }
  </script>
  
  <style scoped>

  input[type="checkbox"] {
    transform: scale(1.2);
    translate: 0px 1.1px;
  }

  .generic-popup-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 9999;
  }

  .optionLabel {
    color: var(--popup-text);
  }
  
  .overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 9998;
  }
  
  
  #popup-box {
    position: relative;
    width: 28rem;
    box-sizing: border-box;
    background-color: var(--popup-body);
    border-radius: 1.5rem;
    box-shadow: 0 0.25rem 0.5rem 0 rgba(0, 0.5, 0, 0.3), 0 0.25rem 1.25rem 0 rgba(0, 0, 0, 0.2);
    min-height: 6rem;
  }
  
  #popup-box #topbar {
    padding: 0rem 0rem 0rem 1.5rem;
    border-top-left-radius: inherit;
    border-top-right-radius: inherit;
    height: 3rem;
    width: inherit;
    background-color: var(--advanced-report-popup-header);
    display: flex;
    justify-content: space-between;
    align-items: center;

    /* border for accessibility */
    border-bottom-style: solid;
    border-bottom-width: var(--chat-widget-header-border-width);
  }
  
  #popup-box .text{
    font-size: medium;
    font-weight: bold;
    margin-top: 13%;
    text-align: center;
    margin-right: 0.65rem;
  }
  
  #popup-box #buttons {
    position: relative;
    width: 28rem;
    border-bottom-right-radius: 1.5rem;
    border-bottom-left-radius: 1.5rem;
    font-family: 'Anton', sans-serif;
    font-size: small;
    text-align: center;
    padding: 0.25rem 0.625rem 1.25rem 0.625rem;
    color: var(--popup-footer-text);
  }

  #advanced-report-button {
    background-color: var(--advanced-report-button);

    /* borders only used in accessibility mode */
    border: solid;
    border-width: var(--basic-border-width);
    border-color: var(--basic-border-dark);
  }
  #advanced-report-button:hover,
  #advanced-report-button:focus {
    background-color: var(--advanced-report-button-focus);

    /* outlines only used in accessibility mode */
    outline: var(--button-outline-width) solid var(--button-outline-dark);
    outline-offset: 4px;
  }
  #advanced-report-button:active {
      background-color: var(--advanced-report-button-active);
  }
  
#close{
  padding:0.25rem 1rem;
  height: inherit;
  border-top-right-radius: inherit;
  background-color: var(--advanced-report-popup-header);
  background-repeat: no-repeat;
  border: none;
  cursor: pointer;
  overflow: hidden;
  outline: none;
}

#close:hover,
#close:focus {
  background-color: var(--popup-close-button-focus);

  /* outlines only used in accessibility mode */
  outline: var(--button-outline-width) solid var(--button-outline-color);
  outline-offset: 4px;
}
#close:active {
  background-color: var(--popup-close-button-active);
  outline-offset: 3px;
}
</style>
