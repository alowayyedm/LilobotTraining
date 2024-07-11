<!--
  This component is responsible for displaying the web chat. It also
  keeps a record of the current chat in chatRecord. Use the method
  addMessage to input a message, text should be the message String
  and fromUser should be a boolean indicating whether the message
  is meant to be displayed as being from the user (right side) or
  not (left side). The attribute displayOptionButtons should be set
  to false by default, unless trainer has auto sending off.
  It also contains an input element (class input-box) for the user
  to input text. Once enter is pressed, the method handleInput will
  register the input.
-->

<template>
  <div class="chat-container">
    <div class="chat-header">
      <dynamic-text
        :header-text="`${headerText}`"
        :target-font-size=24
        :min-font-size=12
        :unit="'px'"
        :step-size=1
        :overflow-wrap="'anywhere'"
        style="padding: 0.5em 1em;"
      >
      </dynamic-text>
      <button id="exit-session" v-if="isConversationActive && !isPastConversation" @click="leaveSession()">
        <i class="fas fa-arrow-right-from-bracket" title="Leave session" ></i>
      </button>
    </div>
    <template v-if="isConversationActive">
      <div class="chat light-scrollbar" ref="chat">
        <div v-for="(message, index) in chatRecord" :key="index"
            :class="{'msg from': message.fromUser, 'msg to': !message.fromUser, 'editing':index===this.editingMessage}">

        <!-- Display text in input box inside the chat bubble if it is currently being edited -->
        <span v-if="index === this.editingMessage">{{ this.inputText }}</span>
        <!-- Otherwise, display message text -->
        <span v-else class='message'>{{ message.text }}</span>

          <!-- Display trainer option buttons if boolean is true -->
          <div class="option-buttons-container" v-if="message.displayOptionButtons">
            <button type="button" class="option-button confirm" @click="this.confirmMessage(index)"><img src="../../images/checkmark-icon.svg" alt="Confirm" class="icon"></button>
            <button type="button" class="option-button edit" @click="this.editMessage(index)"><img src="../../images/edit-icon.svg" alt="Edit" class="icon"></button>
            <button type="button" class="option-button delete" @click="this.deleteMessage(index)"><img src="../../images/cross-icon.svg" alt="Delete" class="icon"></button>
          </div>
        </div>
        <div class="msg to" ref="loading" :hidden="!showLoading">
          <div id="wave">
            <span class='dot'></span>
            <span class='dot'></span>
            <span class='dot'></span>
          </div>
        </div>
      </div>
      <div class="chat-mode" v-if="isTrainer" :class="{'rounded-bottom': autoSending}">
        <div>Automatisch verzenden</div>

      <label class="switch">
        <input id="change-chat-mode" type="checkbox" v-model="autoSending" @change="this.changeChatMode">
        <span class="slider"></span>
      </label>

        <div class="switch-label" v-if="autoSending">AAN</div>
        <div class="switch-label" v-else>UIT</div>
      </div>
      <textarea class="input-box light-scrollbar rounded-bottom"
                ref="inputBox"
                title="Typ een bericht"
                v-model="inputText"
                @keydown.enter="handleInput($event)"
                placeholder="Type a message..."
                v-if="!autoSending || (!isTrainer && !isPastConversation)"
                minlength="1"
                id="text-box">
      </textarea>
    </template>
    <div v-else class="join-session">
      <button id="join-button" title="Join session as Trainer" @click="requestSession" name="request-session">Join</button>
      <div>or start your own chat</div>
      <button id="join-button" title="Join session as Trainer" @click="startPrivateSession">Start private session</button>
      <div style="text-align: center">Warning: you can not join the session of a trainer</div>
    </div>
  </div>
</template>

<script>
import DynamicText from "@/components/DynamicText.vue";

export default {
  name: "WebChatComponent",
  emits: ['handle-input', 'request-session', 'leave-session', 'set-trainer-messaging', 'start-private-session'],
  props: {
    headerText: String
  },
  components: {
    DynamicText
  },
  data() {
    return {
      chatRecord: [], // Messages visible in the chat widget (in text bubbles)
      inputText: "", // Text in the "Typ een bericht..." field 
      joinId: "", // Text in the "Enter someone elses conversationId" field (id of the session to join)
      isTrainer: false, // Whether the user has trainer rights
      autoSending: true, // Whether messages get sent vs can first be accepted/edited/rejected
      editingMessage: -1, // This variable is -1 when no message is being edited (and the index of the message otherwise)
      showLoading: false, // Whether the most recent message is still loading
      isConversationActive: true, // Whether the chat is active (true if (learner) or (trainer and is joined/in own session))
      isPastConversation: false,  // If true, don't allow chatting. Used for chat history
      timeoutId: null // The id of the timeout for adding message with loading icons (needed to cancel when conversation is cleared)
    }
  },
  methods: {
    /**
     * This method appends the message provided to the chat by adding it to chatRecord.
     * It does some checks to ensure that the messages are placed in the right order.
     *
     * @param {String} text actual content of the message
     * @param {boolean} fromUser boolean indicating whether the message is meant to be displayed
     * as being from the user (right side) or not (left side).
     * @param {boolean} displayOptionButtons boolean to set if the message is shown with option buttons confirm/edit/delete
     */
    addMessage: function (text, fromUser, displayOptionButtons) {
      if (text.trim().length > 0) {
        // If the message to add is from the user and this is currently the trainer,
        // then skip all messages that are being edited or have not been
        // accepted/edited/deleted by the trainer. Otherwise, append the message to
        // the end of the chat as normal
        if (this.isTrainer && fromUser) {
          this.chatRecord.splice(this.getIndexToInsert(), 0, {
            text: text,
            fromUser: fromUser,
            displayOptionButtons: displayOptionButtons
          });
          // If a message is received from the user and the trainer is editing a message,
          // the index of the message being edited is increased
          if (this.editingMessage !== -1) {
            this.editingMessage++;
          }
        }             
        else if (!fromUser) {
          this.showLoading = true;
          const delay = Math.max(Math.min(text.length * 50, 3000), 1000);
          this.timeoutId = setTimeout(() => {
            this.showLoading = false;
            // if autosending was turned back on but the message was pending, autosend it
            // only for trainer
            if (this.isTrainer && this.autoSending && displayOptionButtons) {
              this.$emit('handle-input', [text]);
            // if the message has options, push it to the end.
            } else if (displayOptionButtons){
              this.chatRecord.push({
                text: text,
                fromUser: fromUser,
                displayOptionButtons: displayOptionButtons
              });
            // if the message is from the server with no options, place before messages with options
            } else {
              this.chatRecord.splice(this.getIndexToInsert(), 0, {
                text: text,
                fromUser: fromUser,
                displayOptionButtons: displayOptionButtons
              });
            }
          }, delay);
        } else {
          this.chatRecord.push({
            text: text,
            fromUser: fromUser,
            displayOptionButtons: false
          });
        }
      }
      // Scrolls the chat to the bottom after a message is sent
      this.$nextTick(() => {
        const chat = this.$refs.chat;
        chat.scrollTop = chat.scrollHeight;
      });
    },
    /**
     * Handles the user input of pressing enter after typing something in the input box.
     *
     * @param event the enter key down event that has called this method
     */
    handleInput: function (event) {
      if (this.inputText.trim().length > 0) {
        event.preventDefault(); // Prevent the input of the enter key from being handled twice
        // If a message was being edited it should first be removed
        if (this.editingMessage !== -1) {
          this.chatRecord.splice(this.editingMessage, 1);
          this.editingMessage = -1;
        }
        // Handle the input according to parent component
        this.$emit('handle-input', this.isTrainer ? [this.inputText] : this.inputText);
        // Clear input text
        this.inputText = "";
      }
    },
    setConversation: function (conversation) {
      // Resets chatbox and adds a list of messages.
      this.chatRecord = [];
      for (let i = 0; i < conversation.length; i++) {
        this.chatRecord.push({
          text: conversation[i].message,
          fromUser: conversation[i].fromUser,
          displayOptionButtons: false
        });
      }
    },
    setConversationActive() {
      this.isConversationActive = true;
    },
    setConversationInactive() {
      this.isConversationActive = false;
    },
    setAutoSendingTrue() {
      this.autoSending = true;
    },
    clearConversation: function() {
      this.showLoading = false;
      clearTimeout(this.timeoutId)
      this.setConversation([]);
    },
    requestSession() {
      this.$emit('request-session', this.joinId);
    },
    startPrivateSession() {
      this.$emit('start-private-session');
    },
    leaveSession() {
      this.clearConversation();
      this.$emit('leave-session');
    },
    setTrainer: function (bool) {
      if (typeof bool == "boolean") {
        this.isTrainer = bool;
      }
    },
    setIsPastConversation: function (bool) {
      if (typeof bool == "boolean") {
        this.isPastConversation = bool;
      }
    },
    /**
     * Confirms the message whose index is provided by forwarding it to the handleInput
     * method of the parent Vue component (TrainingPortal).
     * Deletes the message from chatRecord and then resends it from TrainingPortal.
     *
     * @param {int} msgIndex index of message in chatRecord to be confirmed
     */
    confirmMessage: function (msgIndex) {
      // If another message is being edited, add its options back and cancel editing
      if (this.editingMessage !== -1) {
        this.chatRecord[this.editingMessage].displayOptionButtons = true;
        this.editingMessage = -1;
        this.inputText = "";
      }
      const text = this.chatRecord[msgIndex].text;
      this.chatRecord.splice(msgIndex, 1);
      this.$emit('handle-input', [text]);
    },
    /**
     * Sets the message with the provided id as being edited
     * which allows the trainer to edit the text content of
     * the message by writing n the input box.
     *
     * @param {int} msgIndex index of message in chatRecord to be edited
     */
    editMessage: function (msgIndex) {
      // If another message is being edited, add its options back and stop editing
      if (this.editingMessage !== -1) {
        this.chatRecord[this.editingMessage].displayOptionButtons = true;
      }

      this.chatRecord[msgIndex].displayOptionButtons = false;
      this.editingMessage = msgIndex;
      this.inputText = this.chatRecord[msgIndex].text;

      // Focus on the input box
      this.$nextTick(() => {
        const inputBox = this.$refs.inputBox;
        if (inputBox) {
          inputBox.focus();
        }
      });
    },
    /**
     * Deletes the message with the provided index from the chat
     *
     * @param {int} msgIndex index of message in chatRecord to be deleted
     */
    deleteMessage: function (msgIndex) {
      // If another message is being edited, add its options back and stop editing
      if (this.editingMessage !== -1) {
        this.chatRecord[this.editingMessage].displayOptionButtons = true;
        this.editingMessage = -1;
        this.inputText = "";
      }
      // Delete the message
      this.chatRecord.splice(msgIndex, 1);
    },
    changeChatMode: function () {
      this.$emit('set-trainer-messaging', !this.autoSending);
      // Confirm and remove all unhandled messages when switching modes
      if (this.autoSending) {
        const messages = [];
        for (let i = this.chatRecord.length - 1; i >= 0; i--) {
          if (this.chatRecord[i].displayOptionButtons) {
            messages.unshift(this.chatRecord[i].text)
            this.chatRecord.splice(i, 1);
          }
        }
        this.$emit('handle-input', messages);
      }
    },
    /**
     * Gets the index to insert a message such that all messages above have
     * already been sent and messages still to be confirmed/edited/deleted
     * are below (visually in the chat box).
     * @returns {int} Index of where to place the new message
     */
    getIndexToInsert: function () {
      // Gets the first message that has options / is being edited.
      let indexToInsert = this.chatRecord.length;
      for (let i = 0; i < this.chatRecord.length; i++) {
        if (this.chatRecord[i].displayOptionButtons || this.editingMessage === i) {
          indexToInsert = i;
          break;
        }
      }
      return indexToInsert;
    }
  }
}
</script>

<style scoped>

.chat-container {
  border-radius: 1rem;
  background-color: var(--chat-widget-body);
  margin: 2vh 1vw;
  height: calc(100% - 4vh);
  display: flex;
  flex-direction: column;
}

.chat-header {
  background-color: var(--chat-widget-header);
  color: var(--chat-widget-header-text);
  font-family: 'Anton', sans-serif;
  height: 8vh;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;

  /* border for accessibility */
  border-bottom-style: solid;
  border-bottom-width: var(--chat-widget-header-border-width);
}

#exit-session {
  padding: calc(4vh - var(--padding-removal-exit-button));
  font-size: 1.5em;
  border-radius: 0px 16px 0px 0px;
  background-color: var(--chat-widget-header-button);
  color: var(--chat-widget-header-text);
  border: none;
  justify-self: flex-end;
}

#exit-session:hover,
#exit-session:focus {
  background-color: var(--chat-widget-header-button-focus);
  cursor: pointer;
}

#exit-session:active {
  background-color: var(--chat-widget-header-button-active);
}

.chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 1.3rem;
  overflow-y: scroll;
  margin-left: auto;
  width: 100%;
}

.msg {
  position: relative;
  padding: 0.44rem 0.94rem;
  margin-bottom: 0.2rem;
  border-radius: 0.6rem;
  font-family: 'Inter', sans-serif;
  font-size: 1rem;
  text-align: left;
  max-width: 60%;
  overflow-wrap: anywhere;
}

.from {
  background: var(--chat-widget-message-from);
  color: var(--chat-widget-message-from-text);
  /* moves it to the right */
  align-self: flex-end;
}

.to {
  background: var(--chat-widget-message-to);
  color: var(--chat-widget-message-to-text);
  border-radius: 0px 10px 10px 10px;
  border: solid;
  border-width: var(--chat-widget-message-to-border-width);
  /* moves it to the left */
  align-self: flex-start;
}

.chat-mode {
  background-color: var(--input-field-background);
  display: inline-flex;
  align-items: center;
  height: 7%;
  font-family: 'Inter', sans-serif;
  color: var(--input-field-text);
  padding-right: 3rem;
  justify-content: right;
}

/* The switch - the box around the slider */
/* Taken from https://www.w3schools.com/howto/howto_css_switch.asp */
.switch {
  position: relative;
  display: inline-block;
  width: 3.75rem;
  max-width: 3.75rem;
  min-width: 3.75rem;
  height: 2.125rem;
  transform: scale(0.7);
}

/* Hide default HTML checkbox */
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

/* The slider */
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--chat-widget-slider-inactive);
  border-radius: 2.125rem;

  /* border only in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);
}

.slider:hover {
  outline: var(--button-outline-width) solid var(--button-outline-dark);
  outline-offset: 4px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 1.625rem;
  width: 1.625rem;
  left: var(--chat-widget-slider-circle-offset);
  bottom: var(--chat-widget-slider-circle-offset);
  background-color: var(--chat-widget-slider-circle-inactive);
  -webkit-transition: .4s;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: var(--chat-widget-slider-active);
}

input:checked + .slider:before {
  background-color: var(--chat-widget-slider-circle-active);
}

input:focus + .slider {
  box-shadow: 0 0 0.063rem var(--chat-widget-slider-shadow);
}

input:checked + .slider:before {
  transform: translateX(1.625rem);
}

.switch-label {
  font-weight: bolder;
  width: 2%;
}

.input-box {
  background-color: var(--input-field-background);
  font-family: 'Inter', sans-serif;
  height: 5%;
  resize: none;
  border: none;
  outline: none;
  padding: 1rem;
  font-size: 1.5rem;
  box-sizing: content-box;
}

.join-session {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  top: 40%;
  right: 0;
  width: calc(100% / 3); 
  z-index: 9998;
}
.conversationId-input {
  text-align: center;
  width: 75%;
  font-family: 'Poppins', sans-serif;
  font-size: 1rem;
}

#join-button{
  border-radius: 16px;
  background-color: var(--chat-widget-button);
  border: none;
  color: var(--chat-widget-button-text);
  font-size: medium;
  padding: 0.8rem;
  font-family: 'Anton', sans-serif;
  width: inherit;
  cursor: pointer;

  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);

}

#join-button:hover,
#join-button:focus {
  background-color: var(--chat-widget-button-focus);
  outline: var(--button-outline-width) solid var(--button-outline-dark);
  outline-offset: 4px;
}

#join-button:active {
  background-color: var(--chat-widget-button-active);
  outline-offset: 3px;  
}

.rounded-bottom {
  border-bottom-left-radius: 1rem;
  border-bottom-right-radius: 1rem;
}

.editing {
  background-color: var(--chat-widget-message-edit-background);
  overflow-wrap: anywhere;
}

.option-button {
  border: none;
  padding: 0.6rem;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 0.6rem;
  width: 3rem;
  margin: 0.25rem;

  /* borders only used in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-dark);
}

.option-button:hover,
.option-button:focus {
  /* brightness only used in normal mode */
  filter: brightness(var(--user-settings-button-focus-brightness));
    
  /* outline only used in accessibility mode */
  outline: var(--button-outline-width) solid var(--button-outline-dark);
  outline-offset: 3px;
}

.option-button:active {
  outline-offset: 2px;
}

.icon {
  width: 1rem;
  height: 1rem;
  margin-left: auto;
  margin-right: auto;
  display: block;
}

.confirm {
  background-color: var(--chat-widget-message-confirm-button);
}

.edit {
  background-color: var(--chat-widget-message-edit-button);
}

.delete {
  background-color: var(--chat-widget-message-delete-button);
}
  /* Wave animation from original Rasa webchat widget https://github.com/botfront/rasa-webchat */
div#wave {
  position: relative;
  text-align: center;
  width: 25px;
  height: 13px;
  margin-left: auto;
  margin-right: auto
}

div#wave .dot {
  opacity: .4;
  display: inline-block;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  margin-right: 3px;
  background: #000;
  animation: wave 1.6s linear infinite
}

div#wave .dot:nth-child(2) {
  animation-delay: -1.4s
}

div#wave .dot:nth-child(3) {
  animation-delay: -1.2s
}

@keyframes wave {
  0%,60%,to {
    transform: none
  }

  30% {
    transform: translateY(-5px)
  }
}
</style>