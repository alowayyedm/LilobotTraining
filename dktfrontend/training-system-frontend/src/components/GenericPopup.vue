<template>
  <div class="overlay" v-if="showPopup"></div>
  <div class="generic-popup-container" v-if="showPopup">
    <div id="popup-box">
      <div id="topbar">
        <button id="close" title="close popup" @click="decline"><i class="fa-solid fa-xmark fa-2xl"></i></button>
      </div>
      <div class="text">{{ popupText }}</div>
      <div id="buttons" v-if="showOptions">
        <button class ="button" :class="{'green' : !invertedColors, 'red': invertedColors }"
                id="accept" @click="accept">{{acceptText}}</button>
        <button class ="button" :class="{'green' : invertedColors, 'red': !invertedColors }"
                id="decline" @click="decline">{{declineText}}</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "GenericPopup",
  emits: ['accept', 'decline'],
  data() {
    return {
      popupText: "", // Message to be displayed in the popup
      acceptText: "", // Text in the left button
      declineText: "", // Text in the right button
      data: "", // Data to be emitted when accepted/declined
      showPopup: false, // If the popup is displayed
      showOptions: false, // If the popup contains option buttons (accept and decline)
      invertedColors: false // If the option buttons have inverted colors (if green and red are switched around)
    };
  },
  methods: {
    accept() {
      this.showPopup = false;
      this.$emit('accept', this.data);
    },
    decline() {
      this.showPopup = false;
      this.$emit('decline', this.data);
    },
    // Use this for a popup with buttons (all other text variables should be set beforehand)
    showOptionsPopup() {
      this.showOptions = true;
      this.showPopup = true;
    },
    // Use this for popup with no buttons (just a message)
    showAlertPopup(alertText) {
      this.showOptions = false;
      this.showPopup = true;
      this.popupText = alertText;
    },
    // Use this for a popup with buttons that also includes the provided data when emitting
    // (all other text variables should be set beforehand)
    showOptionsPopupWithData(data) {
      this.data = data;
      this.showOptions = true;
      this.showPopup = true;
    }
  }
}
</script>

<style scoped>
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

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 9998;
}


#popup-box{
  position: relative;
  width: 28rem;
  box-sizing: border-box;
  background-color: var(--popup-body);
  border-radius: 1.5rem;
  box-shadow: 0 0.25rem 0.5rem 0 rgba(0, 0.5, 0, 0.3), 0 0.25rem 1.25rem 0 rgba(0, 0, 0, 0.2);
  min-height: 6rem;
}

#popup-box #topbar{
  position: absolute;
  border-top-left-radius: inherit;
  border-top-right-radius: inherit;
  height: 3rem;
  width: inherit;
  background-color: var(--basic-popup-header);
  display: flex;
  justify-content: right;
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

#popup-box #buttons{
  position: relative;
  width: 28rem;
  border-bottom-right-radius: 1.5rem;
  border-bottom-left-radius: 1.5rem;
  font-family: 'Anton', sans-serif;
  font-size: small;
  text-align: center;
  padding: 1.125rem 0.625rem 0.625rem 0.625rem;
  color: var(--popup-footer-text);
}

.button{
  display: inline-block;
  margin-right: 2%;
  margin-left: 2%;
  border-radius: 0.9375rem;
  padding: 0.5rem;
  color: var(--popup-button-text);
  font-size: x-large;
  font-family: 'Anton', sans-serif;
  box-shadow: 0 0.25rem 0.5rem 0 rgba(0, 0.5, 0, 0.3), 0 0.25rem 1.25rem 0 rgba(0, 0, 0, 0.2);

  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);
}

.button:focus,
.button:hover {
  /* outlines only used in accessibility mode */
  outline: var(--button-outline-width) solid var(--button-outline-dark);
  outline-offset: 4px;
}

.button:active {
  outline-offset: 6px;
}



.green{
  background-color: var(--popup-button-green);
}

.green:hover,
.green:focus {
  background-color: var(--popup-button-green-focus);
  transform: scale(1.05);

  /* outlines only used in accessibility mode */
  outline: var(--button-outline-width) solid var(--button-outline-dark);
  outline-offset: 4px;
}

.red{
  background-color: var(--popup-button-red);
}

.red:hover,
.red:focus {
  background-color: var(--popup-button-red-focus);
  transform: scale(1.05);

  /* outlines only used in accessibility mode */
  outline: var(--button-outline-width) solid var(--button-outline-dark);
  outline-offset: 4px;
}

.red:active,
.green:active {
  outline-offset: 3px;
}

#close{
  padding:0.25rem 1rem;
  height: inherit;
  border-top-right-radius: inherit;
  background-color: var(--basic-popup-header);
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