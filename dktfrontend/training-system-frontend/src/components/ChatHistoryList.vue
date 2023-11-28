<!------------------------ Chat History List ------------------------
    This component is the chat history list.
-->
<template>
    <ul class="chat-history-list" role="list">
        <div class="time-label"></div>
        <router-link :to="'/'" class="chat-item">
            <div class="chat-label">
                <div class="chat-icon">
                    <i class="far fa-plus"></i>
                </div>
                <div class="chat-info">Nieuw Gesprek</div>
            </div>
        </router-link>
        <div v-for="category in chatLabels" :key="category">
            <div class="time-label">{{ category.label }}</div>
            <li v-for="chat in category.chats"
                role="listitem" :tabindex="0" :aria-label="chat.title"
                @keyup.enter="selectChat(chat)"
                :class="['chat-item', { 'selected': selectedChat === chat }]"
                :key="chat.id" 
                @mouseover="hoveredChat = chat" 
                @mouseleave="hoveredChat = null"
                @focus="hoveredChat = chat">
                <div
                    :title="chat.title"
                    @click="selectChat(chat)"
                    class="chat-label">
                    <div class="chat-icon">
                        <i class="far fa-message"></i>
                    </div>
                    <div v-if="!chat.editing" class="chat-info">{{ chat.title }}</div>
                    <template v-else>
                        <input v-model="chat.newTitle" @keyup.enter="renameChat(chat)" class="title-input" type="text">
                    </template>
                </div>
                <div class="icons" :class="{ 'show-icons': hoveredChat === chat }">
                    <i class="icon fas fa-edit" title="Rename conversation" role="button" tabindex="0"
                    @click="editChatTitle(chat)"
                    @keydown.enter="editChatTitle(chat)"
                    @keydown.space="editChatTitle(chat)">
                </i>
                    <i class="icon fas fa-trash-alt" title="Delete conversation" role="button" tabindex="0"
                    @click="deleteChat(chat)"
                    @keydown.enter="deleteChat(chat)"
                    @keydown.space="deleteChat(chat)">
                </i>
                </div>
            </li>
        </div>
    </ul>      
</template>

<script>

export default {
    name: 'ChatHistoryList',
    props: {
        chats: Array,
        selectedChat: Object
    },
    data () {
        return {
            errors: [],
            hoveredChat: null,
        }
    },
    emits: ['editChat', 'selectChat', 'delete-chat'],
    computed: {
        chatLabels() {
            const chats = [...this.chats] // copy chats since chats should not be mutated (prop)
                .sort((a, b) => new Date(a.date) - new Date(b.date))
                .map(chat => this.getChatTimeLabel(chat));
            
            const groupedChats = groupBy(chats, 'label');
            const reducedArray = Object.values(groupedChats).reverse().map((innerArray) => {
                const label = innerArray[0].label;
                const chats = innerArray.map((item) => item.chat);
                return { label, chats };
            });
            return reducedArray;
        }
    },
    methods: {
        selectChat(chat) {
            this.$emit('selectChat', chat);
        },
        deleteChat(chat) {
            this.$emit('delete-chat', chat);  
        },
        editChatTitle(chat) {
            chat.editing = true;
            chat.newTitle = chat.title;
        },
        renameChat(chat) {
            chat.newTitle = chat.newTitle.trim();
            if (chat.newTitle.length === 0) {
                chat.editing = false;
            } else {
                chat.title = chat.newTitle;
                chat.editing = false;
                this.$emit('editChat', chat.id, chat.newTitle);  
            }
        },
        getChatTimeLabel(chat) {
            const today = new Date();
            const weekAgo = new Date(today - 7 * 24 * 60 * 60 * 1000);

            const chatDate = new Date(chat.date);

            if (chatDate > today) {
                console.warn(`${chatDate} is in the future.`);
                return { label: "Future", chat };
            } else if (
                chatDate.getFullYear() === today.getFullYear() &&
                chatDate.getMonth() === today.getMonth() &&
                chatDate.getDate() === today.getDate()
            ) {
                return { label: "Vandaag", chat };
            } else if (chatDate > weekAgo) {
                return { label: "Afgelopen Week", chat };
            } else if (
                chatDate.getFullYear() === today.getFullYear() &&
                chatDate.getMonth() === today.getMonth()
            ) {
                return { label: "Deze Maand", chat };
            } else if (
                chatDate.getFullYear() === today.getFullYear()
            ) { 
                return { label: chatDate.toLocaleDateString('nl-NL', {month: 'long'}), chat };
            } else {
                return { label: chatDate.getFullYear(), chat };
            }   
        }
    }
}

function groupBy(array, key) {
    return array.reduce((result, item) => {
        const groupKey = item[key];
        if (!result[groupKey]) {
            result[groupKey] = [];
        }
        result[groupKey].unshift(item);
        return result;
    }, {});
}

</script>

<style scoped>
@import "../../styles/main.css";

ul {
    margin: 0px;
    padding: 0px;
}

.time-label {
    font-size: medium;
    color: var(--hoverable-list-entry-icon);
    font-weight: bold;
    margin: 1rem 1rem 0rem 0.9rem;
}

.chat-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    word-break: break-word;
    text-decoration: none;
    padding-right: 1rem;
    gap: 0.6rem;
    transition: background-color 0.2s ease;
    border: solid;
    border-width: var(--hoverable-list-entry-border-width);
    border-color: var(--hoverable-list-entry-border-color);
}

.chat-label {
    display: grid;
    align-items: center;
    grid-template-columns: auto auto;
    column-gap: 1rem;
    grid-template-rows: auto auto;
    grid-template-areas:
        "icon title"
        "error error";
    padding: 1rem;
    cursor: pointer;
    transition: background-color 0.2s ease;
    justify-content: flex-start;
    flex-grow: 1;
}
  
.chat-item:hover,
.chat-item:focus {
    background-color: var(--hoverable-list-entry-focus);
    border-color: var(--hoverable-list-entry-focus-border-color);
}
  
.chat-item.selected {
    background-color: var(--hoverable-list-entry-active);
    color: var(--hoverable-list-entry-active-text) !important;
}

.chat-item.selected .avatar,
.chat-item.selected .chat-info,
.chat-item.selected .chat-label,
.chat-item.selected .icon
 {
    color: var(--hoverable-list-entry-active-text) !important;
}


.chat-label .avatar {
    grid-area: icon;
    width: 2rem;
    height: 2rem;
    border-radius: 50%;
    margin-right: 10px;
}
  
.chat-label .chat-info {
    grid-area: title;
    flex: 1;
}
  
.chat-label .chat-info {
    font-weight: bold;
}
  
.chat-label .chat-info {
    color: var(--hoverable-list-text);
}
  
.chat-label {
    color: var(--hoverable-list-text);
    font-size: 1rem;
}

.icons {
    display: flex;
    visibility: hidden;
}

.icons i {
    margin-right: 5px;
    cursor: pointer;
}

.show-icons {
    visibility: visible;
}

.icon {
    color: var(--hoverable-list-entry-icon);
    transition: opacity 0.2s ease;
}
.icon:focus,
.icon:hover,
.icon:focus {
    color: var(--hoverable-list-entry-icon-focus);
    transform: scale(var(--hoverable-list-entry-icon-focus-size));
}

.title-input {
  position: relative;
  height: 2.5rem;
  border-radius: 0.625rem;
  background-color: var(--input-field-background);
  text-align: left;
  text-indent: 0.625rem;
  padding-right: 0.625rem;
  padding-top: 0.625rem;
  font-family: 'Inter', sans-serif;
  font-size: small;

  /* borders for accessibility */
  border: solid;
  border-color: var(--basic-border-dark);
  border-width: var(--basic-border-width);
}

.title-input:focus,
.title-input:hover {
  outline: var(--button-outline-width) solid var(--field-outline-dark);
  outline-offset: 2px;
}

</style>

<style scoped src="../../styles/main.css"/>
