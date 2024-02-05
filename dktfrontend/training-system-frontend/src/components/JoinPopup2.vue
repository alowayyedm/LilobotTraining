<template>
  <div class="overlay" v-if="showJoinPopup"></div>
  <div class="join-popup-container" v-if="showJoinPopup">
    <div id="popup-box">
      <div id="topbar">
        <div id="headertop">
          {{ headerText }}
        </div>
        <button id="close" @click="closePopup"><i class="fa-solid fa-xmark fa-2xl"></i></button>
      </div>
      <form class="join-form">
        <div id="alert" v-if="alert">{{ alert }}</div>


          <div class="form-item" id="popupedit">
            <!-- <label for="username">GEBRUIKERSNAAM</label>
             <input type="text" id="username" class="field" placeholder="Gebruikersnaam" v-model="username">
             -->

            <div v-if="seenPhase" class="phases" id="phases">

              <strong>Phase 1: Building rapport</strong> <br>
              <u>•	Objective:</u> Create a welcoming atmosphere and build trust.<br>
              <u>•	Method:</u> Empathy, respect, sincere interest, active listening.<br>
              <br>
              <strong>Phase 2: Clarify the child’s story</strong> <br>
              <u>•	Objective:</u> Get a clear view of the child’s story, perspective, personality, network and competencies. <br>
              <u>•	Method:</u> Ask questions about the child’s story. <br>
              <br>
              <strong>Phase 3: Setting a goal for the session</strong>  <br>
              <u>•	Objective:</u> Both parties are aware of what the child may use the conversation for.<br>
              <u>•	Method:</u> Clarification on the child’s wishes, story, and desired goal.<br>
              <br>

              <strong>Phase 4: Work toward the session goal</strong> <br>
              <u>•	Objective:</u> To ensure that the child may benefit from the conversation.<br>
              <u>•	Method:</u> Stimulating the child’s own problem solving skills.<br>
              <br>
              <strong>Phase 5: Rounding off the conversation</strong> <br>
              <u>•	Objective:</u> That the child is left with as few questions as possible.<br>
              <u>•	Method:</u> Summing up and wrapping up the conversation.<br>
            </div>

            <div v-if="seenUndoPhase" class="phases" id="undoP">
              You are now back to phase {{currentphase}}. <br>
            </div>

            <div v-if="seenIncPhase" class="phases" id="undoInc">
              It's not possible to go back, you are in phase 1.<br>
            </div>


            <div v-if="seenIncPhase" class="phases" id="undoInc">
              It's not possible to go back, you are in phase 1.<br>
            </div>

            <div v-if="showMSG" class="phases" id="MsgGeneral">
              {{ this.MSG }}<br>
            </div>


           </div>

         <div id="join">
           <button id="join-button" @click="closePopup" name="join-session">CLOSE</button>
         </div>
       </form>
     </div>
   </div>
 </template>

 <script>
 import phaseInfo from "./PhaseInfo.vue";
 import beliefInput from "./BeliefInput.vue";

 export default {
   name: "JoinPopup2",
   computed: {
     beliefInput() {
       return beliefInput
     },
     phaseInfo() {
       return phaseInfo
     }
   },
   emits: ['send-join-request'],
   data() {
     return {
       //for now username is sessionId as users are not implemented yet
       username: "",
       dataPop:"",
       alert: "",
       showJoinPopup: false,
       showMSG: false,
       seenPhase: false,
       currentphase: 0,
       seenUndoPhase: false,
       seenIncPhase: false,
       headerText:"",
       MSG:""
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
       this.seenPhase=false;
       this.seenUndoPhase=false;
        this.seenIncPhase=false;
        this.showMSG=false;
       this.username = "";
     },
     openPopup() {
       this.showJoinPopup = true;
     },
     openPopupwMsg(msg) {
        this.MSG = msg;
       this.showMSG = true;
       this.showJoinPopup = true;
     },
     editDiv() {
       this.dataPop = "";
     },
     phasesDiv() {
       this.seenPhase=true;
     },
     undoPhaseDiv(currentP) {
       this.currentphase = currentP;
       this.seenUndoPhase=true;
     },
     undoIncorrectPhase() {
       this.seenIncPhase=true;
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
   width: 200%;
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
   width: 50rem;
   height: flex;
   box-sizing: border-box;
   background-color: var(--popup-body);
   border-radius: 1.5rem;
   box-shadow: 0 0.25rem 0.5rem 0 rgba(0, 0.5, 0, 0.3), 0 0.25rem 1.25rem 0 rgba(0, 0, 0, 0.2);
 }

 #headertop{
   border-radius: 16px;
   background-color: var(--chat-widget-button);
   border: none;
   color: var(--chat-widget-button-text);
   font-size: x-large;
   padding: 0.8rem;
   font-family: 'Anton', sans-serif;
   width: inherit;
   cursor: pointer;
 }

 #popup-box #topbar{
   position: absolute;
   border-top-left-radius: inherit;
   border-top-right-radius: inherit;
   height: 4rem;
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

 .phases{
   font-family: 'arial', sans-serif;
   text-align: left;
 }

 #popup-box #join{
   position: relative;
   width: 100%;

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
   cursor: pointer;
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
