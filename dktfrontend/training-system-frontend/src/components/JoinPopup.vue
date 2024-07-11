<template>
  <div class="overlay" v-if="showJoinPopup"></div>
  <div class="join-popup-container" v-if="showJoinPopup">
    <div id="popup-box">
      <div id="topbar">
        <button id="close" @click="closePopup"><i class="fa-solid fa-xmark fa-2xl"></i></button>
      </div>
      <form class="join-form">
        <div id="alert" v-if="alert">{{ alert }}</div>
        <fieldset class="field-elements">
          <div class="form-item">
            <label for="username">GEBRUIKERSNAAM</label>
            <input type="text" id="username" class="field" placeholder="Gebruikersnaam" v-model="username">
          </div>
        </fieldset>
        <div id="join">
          <button id="join-button" @click="sendRequest" name="join-session">JOIN</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: "JoinPopup",
  emits: ['send-join-request'],
  data() {
    return {
      //for now username is sessionId as users are not implemented yet
      username: "",
      alert: "",
      showJoinPopup: false
    };
  },
  methods: {
    sendRequest() {
      this.alert = "";
      this.$emit('send-join-request', this.username);
      this.username = "";
      this.showJoinPopup = false;
    },
    closePopup() {
      this.showJoinPopup = false;
      this.username = "";
    },
    openPopup() {
      this.showJoinPopup = true;
    }
  }
}
</script>

<style scoped>
.join-popup-container {
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
  background-color: rgba(0, 0, 0, 0.5); /* Adjust the background color and opacity as needed */
  z-index: 9998; /* Set the z-index lower than the popup container to place it below */
}

#alert {
  color: var(--green); /* TODO this color is currently not considered in the high contrast mode */
  font-size: medium;
  margin-top: 0.9375rem;
  text-align: center;
  margin-right: 0.625rem;
}

#popup-box{
  position: relative;
  width: 28rem;
  height: 14rem;
  box-sizing: border-box;
  background-color: var(--popup-body);
  border-radius: 1.5rem;
  box-shadow: 0 0.25rem 0.5rem 0 rgba(0, 0.5, 0, 0.3), 0 0.25rem 1.25rem 0 rgba(0, 0, 0, 0.2);
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

  /* border for accessibility */
  border-bottom-style: solid;
  border-bottom-width: var(--chat-widget-header-border-width);
}

.join-form{
  margin: 3.3125rem 0.9375rem 0.625rem;
  padding: 0.625rem 0.625rem 0.625rem 0.625rem;
  font-family: 'Anton', sans-serif;
  font-size: x-large;
}

fieldset{
  display: flex;
  padding-inline: initial;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: flex-end;
  border: none;
}

.form-item{
  margin-top: 0.9375rem;
}

.field{
  position: relative;
  margin-left: 1.25rem;
  width: 12.5rem;
  height: 3.125rem;
  border-bottom-right-radius: 0.625rem;
  border-bottom-left-radius: 0.625rem;
  border-top-right-radius: 0.625rem;
  background-color: var(--input-field-background);
  text-align: left;
  text-indent: 0.625rem;
  padding-top: 0.625rem;
  font-family: 'Inter', sans-serif;
  font-size: small;

  /* borders for accessibility */
  border: solid;
  border-color: var(--basic-border-dark);
  border-width: var(--basic-border-width);
}

.field:focus,
.field:hover {
  outline: var(--button-outline-width) solid var(--field-outline-dark);
  outline-offset: 2px;
}

#popup-box #join{
  position: relative;
  width: 28rem;
  margin-left: -1.5625rem;
  margin-top: 0.625rem;
  bottom: 0.3%;
  border-bottom-right-radius: 1.5625rem;
  border-bottom-left-radius: 1.5625rem;
  background-color: var(--popup-footer);
  font-family: 'Anton', sans-serif;
  font-size: small;
  text-align: center;
  padding: 1.125rem 0.625rem 0.625rem 0.625rem;
  color: var(--popup-footer-text);
}

#join-button{
  border-radius: 0.9375rem;
  height: 2.8125rem;
  width: 15.625rem;
  background-color: var(--popup-button-green);
  color: var(--popup-button-text);
  font-size: x-large;
  font-family: 'Anton', sans-serif;
  box-shadow: 0 0.25rem 0.5rem 0 rgba(0, 0.5, 0, 0.3), 0 0.25rem 1.25rem 0 rgba(0, 0, 0, 0.2);

  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);
}

#join-button:hover,
#join-button:focus {
  background-color: var(--popup-button-green-focus);
  transform: scale(1.05);

  /* outlines only used in accessibility mode */
  outline: var(--button-outline-width) solid var(--button-outline-dark);
  outline-offset: 4px;
}

#join-button:active {
  outline-offset: 3px;
}

.icon{
  width: 2rem;
  height: 2rem;
  margin-left: auto;
  margin-right: auto;
  display: block;
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
