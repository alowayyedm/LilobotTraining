<!------------------------ Chat History ------------------------
    This component is the chat history.
-->
<template>
    <div class="wrapper">
        <div class="subtitle">CHATS</div>
        <div class="subtitle">FEEDBACK</div>
        <!-- TODO a dynamicHeader can be used for this -->
        <div class="first dark-scrollbar">
            <chat-history-list 
                :chats="chats" 
                :selected-chat="selectedChat" 
                @select-chat="selectChat"     
                @delete-chat="showConfirmDelete"
                @edit-chat="editChat">
            </chat-history-list>
        </div>
        <div class="second">
            <chat-history-feedback
                :selectedChat="selectedChat">
            </chat-history-feedback>
        </div>
        <div class="third">      
            <web-chat-component
                :header-text="'Chat met Lilobot'"
                ref="webChat">
            </web-chat-component>
        </div>
    </div>

  <generic-popup ref="popup"
                 @accept="deleteChat">
  </generic-popup>
  </template>
    
<script>
import ChatHistoryList from '../components/ChatHistoryList.vue';
import ChatHistoryFeedback from '../components/ChatHistoryFeedback.vue';
import WebChatComponent from "../components/WebChatComponent";
import axios from "axios";
import GenericPopup from "../components/GenericPopup";

export default {
    name: 'ChatHistory',
    components: {
        ChatHistoryList,
        ChatHistoryFeedback,
        GenericPopup,
        WebChatComponent
    },
    data () {
        return {
            chats: [],
            selectedChat: null
        };
    },

    computed: {
      token() {
        return this.$store.state.auth.token;
      }
    },

    mounted() {
        this.$refs.webChat.setIsPastConversation(true);
        
        axios.get(this.$config.agentServer + '/history/all', {
          headers: {
            'Authorization': 'Bearer ' + this.token
          }
        })
        .then(response => {
            this.chats = response.data.map(chat => {
                return {
                    id: chat.conversationId,
                    title: chat.title,
                    date: new Date(chat.timestamp)
                };
            });
        })
        .catch(error => {
            console.error(error); 
        });
    },
    methods: {
        selectChat(chat) {
            this.selectedChat = chat;
            
            axios.get(this.$config.agentServer + '/history/' + chat.id + '/chat', {
              headers: {
                'Authorization': 'Bearer ' + this.token
              }
            })
            .then(response => {
                this.$refs.webChat.setConversation(response.data);
            })
            .catch(error => {
                console.error(error); 
            });
            /* TODO add transition history for when a trainer joins a session late
            axios.get(this.$config.agentServer + '/history/' + chat.id + '/transitions', {
              headers: {
                'Authorization': 'Bearer ' + this.token
              }
            })
            .then((response) => {
                const pastBeliefUpdates = response.data;
                pastBeliefUpdates.forEach((beliefUpdate) => {
                this.processReceivedUpdate(beliefUpdate)
                });
            })
            .catch((error) => console.log(error));
            */

        },
        showConfirmDelete(chat) {
          this.$refs.popup.popupText = "Are you sure you want to delete "+chat.title+"? This action cannot be undone."
          this.$refs.popup.acceptText = "Delete";
          this.$refs.popup.declineText = "Cancel";
          this.$refs.popup.invertedColors = true;
          this.$refs.popup.showOptionsPopupWithData(chat.id);
        },
        deleteChat(chatId) {
            axios.delete(this.$config.agentServer + '/history/' + chatId + '/delete', {
              headers: {
                'Authorization': 'Bearer ' + this.token
              }
            })
            .then(response => {
                console.log(response.data);
            })
            .catch(error => {
                console.error(error); 
            });
            if (this.selectedChat != null && chatId === this.selectedChat.id) {
                this.selectedChat = null;
            }
            this.chats = this.chats.filter(chat => chat.id != chatId);
        },
        editChat(chatId, rename) {
            axios.put(this.$config.agentServer + '/history/' + chatId + '/rename', null, {
              headers: {
                'Authorization': 'Bearer ' + this.token
              },
              params: {
                  newName: rename
              }
            })
            .then(response => {
                console.log(response.data);
            })
            .catch(error => {
                console.error(error); 
            });
        },
    }
};
</script>
    
<style scoped>
@import "../../styles/main.css";

.wrapper {
    display: grid;
    height: calc(90vh - 0.5rem);
    grid-template-columns: 350px auto max-content;
    grid-template-rows: 0.1fr 1.9fr;
    grid-template-areas:
        "subtitle subtitle third"
        "first second third"
}
.subtitle {
    color: var(--main-subtitle);
    text-align: center;
}

.first,
.second,
.third {
    margin: 0.5rem;  
    overflow-y: auto;
}

.first {
    grid-area: first;
    border-radius: 16px;
    margin: 1.2rem;
    background-color: var(--hoverable-list-background);
    overflow-wrap: break-word;
}

.second {
    grid-area: second;
}
.third {
    grid-area: third;
    overflow: hidden;
    width: 30vw;
}

</style>

<style scoped src="../../styles/main.css"/>
  