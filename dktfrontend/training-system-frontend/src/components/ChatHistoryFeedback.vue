<template>
    <div v-if="this.selectedChat != null">
        <h2 id="chat-name"> {{ this.selectedChat.title }}</h2>
        <div id="meta-data">
            <div class="label">Datum:</div>
            <div class="value">
                {{ new Date(this.selectedChat.date).toLocaleDateString('nl-NL', { year: 'numeric', month: 'long', day: 'numeric' }) }}
            </div>
        </div>
        <div class="feedback-container">
            <!-- TODO feedback from trainer can be shown here in the future -->
        </div>
        <div id="buttons">
            <button title="Download basic report" @click="downloadBasicReport" class="button-with-icon-and-text">
                <i class="fa-solid fa-download"></i>
                &nbsp;&nbsp;
                Basic Report
        </button>
            <button title="Download advanced report" @click="getAdvancedReportForm" class="button-with-icon-and-text" id="advanced-report-button">
                Advanced Report
            </button>
        </div>
    </div>
    <div v-else>
        <h3 id="chat-name"> Selecteer een gesprek om je feedback te zien. </h3>
    </div>
    <advanced-report-popup
        ref="advancedReportPopup"
        @download-advanced-report="downloadAdvancedReport">
    </advanced-report-popup>
</template>
  
<script>
import AdvancedReportPopup from '@/components/AdvancedReportPopup.vue';
import axios from "axios";

  export default {
    name: 'ChatHistoryFeedback',
    components: {
        AdvancedReportPopup,
    },
    props: {
        selectedChat: Object
    },
    methods: {
        getAdvancedReportForm() {
            this.$refs.advancedReportPopup.showAdvancedReportPopup();
        },
        downloadBasicReport() {
            this.downloadReport(null);
        },
        downloadAdvancedReport(options) {
            const params = options.reduce((query, option) => {
                const paramName = `${option.id}`;
                query[paramName] = option.value;
                return query;
            }, {});
            console.log(params);
            this.downloadReport(params);
        },
        downloadReport(params) {
            axios({
                method: 'GET',
                url: this.$config.agentServer + '/history/' + this.selectedChat.id + '/report',
                responseType: 'arraybuffer',
                headers: {
                  'Authorization': 'Bearer ' + this.$store.state.auth.token
                },
                params
            })
            .then(response => {
                const blob = new Blob([response.data], { type: 'application/octet-stream' });
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                const filename = this.selectedChat.title.toLowerCase().replace(/\s+/g, '_') + '.docx';
                link.setAttribute('download', filename);
                document.body.appendChild(link);
                link.click();
            })
            .catch(error => {
                console.error('Error downloading file:', error);
            });
        },
    },
  };
</script>

<style scoped>
@import "../../styles/main.css";

#chat-name {
    font-weight: normal;
}

#meta-data {
  color: var(--main-text-secondary);
  display: grid;
  position: relative;
  gap: 1rem;
  width: max-content;
  grid-template-columns: auto auto;
  grid-template-rows: 1fr;
  grid-template-areas:
    "label value"
}

.label {
    grid-area: label;
    font-weight: bold;
}

.feedback-container {
  display: block;
  border-radius: 16px;
  background-color: var(--list-background);
  color: var(--main-text-secondary);
  margin: 0.5rem;
  padding: 1rem;
  overflow-y: hidden;
}

#advanced-report-button {
    background-color: var(--advanced-report-button);
}
#advanced-report-button:hover,
#advanced-report-button:focus {
    background-color: var(--advanced-report-button-focus);
}
#advanced-report-button:active {
    background-color: var(--advanced-report-button-active);
}

#buttons {
    display: flex;
    gap: 1rem;
    justify-content: space-evenly;
    margin: 2rem;
}



.button-with-icon-and-text {
    height: auto;
    padding: 0.6rem 2.5rem;
}

</style>