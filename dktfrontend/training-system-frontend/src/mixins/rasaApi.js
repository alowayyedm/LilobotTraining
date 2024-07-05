import axios from 'axios';
import config from '@/config';
import * as YAML from "yaml";

const endpoint = config.rasaServer;
const agentServer = config.agentServer;

let rasaApi = {
    /**
     * Check if the status endpoint returns 200.
     * @returns {Promise<boolean>} - Returns true if the response status is 200, otherwise false.
     */
    async getStatus() {
        try {
            const response = await axios.get(`${endpoint}/status?token=${await this.getAuthToken()}`);
            return response.status === 200;
        } catch (error) {
            return false;
        }
    },

    /**
     * Get the list of intents from the domain endpoint.
     * @returns {Promise<{type: string, subject: string, attribute: string}[]>} - Returns an array of objects with type, subject, and attribute.
     */
    async getIntents() {
        try {
            const response = await axios.get(`${endpoint}/domain?token=${await this.getAuthToken()}`, {
                headers: {
                    'Accept': 'application/json'
                }
            });
            const intents = response.data.intents || [];
            const validIntents = intents
                .filter(intent => /^[a-zA-Z0-9]+_[a-zA-Z0-9]+_[a-zA-Z0-9]+$/.test(intent))
                .map(intent => {
                    const [type, subject, attribute] = intent.split('_');
                    return { type, subject, attribute };
                });
            return validIntents;
        } catch (error) {
            console.error('Error fetching intents:', error);
            return [];
        }
    },

    /**
    * Fetch the list of intents and examples from the agent's training data endpoint.
    * @returns {Promise<Map<String, List<String>>>} A promise that resolves to the training data.
    */
    async getTrainingData() {
        try {
            const response = await axios.get(`${agentServer}/nlu/intents`, {
                headers: {
                    Authorization: "Bearer "+ JSON.parse(sessionStorage.getItem("vuex")).auth.token
                }
            });
            return response.data;
        } catch (error) {
            console.error('Error fetching training data:', error);
            return {};
        }
    },

    /**
    * Update the training data for a given intent.
    * @param {string} intent - The intent to update.
    * @param {string[]} examples - An array of examples to update the intent with.
    * @returns {Promise<boolean|{}>} A promise that resolves to true if the update was successful,
    * or an empty object if an error occurred.
    */
    async updateTrainingData(intent, examples) {
        try {
            const response = await axios.put(`${agentServer}/nlu/intents/${intent}`, examples,{
                headers: {
                    Authorization: "Bearer "+ JSON.parse(sessionStorage.getItem("vuex")).auth.token
                }
            });
            return response.status === 200;
        } catch (error) {
            console.error('Error updating training data:', error);
            return {};
        }
    },

    /**
     * Trains the Rasa model with the current rules, stories, and NLU data.
     * @param {*} nlu - The NLU model
     * @returns {Promise<boolean>} - Returns true if the response status is 200, otherwise false.
     */
    async train(nlu) {
        try {
            console.log(nlu)
            const response = await axios.post(`${endpoint}/model/train?save_to_default_model_directory=true,token=${await this.getAuthToken()}`, YAML.stringify({
                rules: this.getRules(),
                stories: this.getStories(),
                nlu: nlu.map(e => {return {intent:e.intent,examples:e.values.map(e => "- " + e).join("\n")+"\n"}}),
                actions: ["action_use_bdi", "action_set_reminder", "action_react_to_reminder","action_forget_reminders", "action_get_bdi"],
                intents: nlu.map(e => e.intent),
            }, {
                blockQuote: 'literal',
            })).catch(e => {throw e});
            return this.selectNewModel(response.headers.get("filename"));
        } catch (error) {
            console.error('Error training model:', error);
            throw error
        }
    },

    /**
     * Select the new model for rasa to use.
     * @param model The model to select
     * @returns {Promise<boolean>} The response from the RASA api
     */
    async selectNewModel(model) {
        try {
            const response = await axios.put(`${endpoint}/model?token=${await this.getAuthToken()}`, {
                model_file:"models/"+model,
            })
            return response.status == 200;
        } catch (err) {
            console.error('Error selecting new model', err)
        }
    },

    async getAuthToken() {
        try {
            const response = await axios.get(`${agentServer}/nlu/auth`, {
                headers: {
                    Authorization: "Bearer "+ JSON.parse(sessionStorage.getItem("vuex")).auth.token
                }
            });
            return response.data;
        } catch (err) {
            console.error('Error fetching rasa data', err);
        }
    },

    /**
    * Returns an array of rule objects.
    * 
    * @returns {Array<Object>} The array of rule objects.
    */
    getRules() {
        return [
            {
                rule: "react to reminder",
                steps: [
                    { intent: "EXTERNAL_reminder" },
                    { action: "action_react_to_reminder" }
                ]
            },
            {
                rule: "print bdi",
                steps: [
                    { intent: "request_chitchat_goodbye" },
                    { action: "action_use_bdi" },
                    { action: "action_get_bdi" },
                    { action: "action_forget_reminders" }
                ]
            }
        ];
    },

    /**
     * Returns an array of story objects.
     * 
     * @returns {Array<Object>} The array of story objects.
     */
    getStories() {
        return [
            {
                story: "bdi path",
                steps: [
                    { intent: "request_chitchat_greeting" },
                    { action: "action_use_bdi" },
                    { action: "action_set_reminder" }
                ]
            }
        ];
    }
};

export default rasaApi;
