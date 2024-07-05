import axios from 'axios';
import config from '@/config';

const agentServer = config.agentServer;

/**
 * Request for all scenarios.
 * @returns {Promise<Array>} A promise that resolves to an array of scenarios.
 * @throws {Error} If there is an error fetching the scenarios.
 */
export const getScenarios = async () => {
    try {
        const response = await axios.get(`${agentServer}/scenarios`, {
            headers: {
                Authorization: "Bearer " + JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching scenarios:', error);
        throw error;
    }
};

/**
 * Create a scenario.
 * @throws {Error} If there is an error creating the scenario.
 */
export const createScenario = async (scenario) => {
    try {
        const response = await axios.post(`${agentServer}/scenarios/${scenario}`,{}, {
            headers: {
                Authorization: "Bearer " + JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error creating scenario:', error);
        throw error;
    }
};

/**
 * Request for all intents for a scenario.
 * @param {string} scenarioId - The ID of the scenario.
 * @returns {Promise<Array>} A promise that resolves to an array of intents.
 * @throws {Error} If there is an error fetching the intents.
 */
export const getIntents = async (scenarioId) => {
    try {
        const response = await axios.get(`${agentServer}/scenarios/${scenarioId}/knowledge`, {
            headers: {
                Authorization: "Bearer " + JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching intents for scenario:', error);
        throw error;
    }
};

/**
 * Request for all examples for an intent.
 * @param {string} scenario - The ID of the scenario.
 * @param {string} intentId - The ID of the intent.
 * @returns {Promise<Array>} A promise that resolves to an array of examples.
 * @throws {Error} If there is an error fetching the examples.
 */
export const getExamples = async (scenarioId, intentId) => {
    try {
        const response = await axios.get(`${agentServer}/scenarios/${scenarioId}/knowledge/${intentId}`, {
            headers: {
                Authorization: "Bearer " + JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching examples for intent:', error);
        throw error;
    }
};

/**
 * Request to update examples for an intent.
 * @param {string} scenario - The ID of the scenario.
 * @param {string} intentId - The ID of the intent.
 * @param {Array} examples - An array of example responses.
 * @returns {Promise<boolean>} A promise that resolves to a boolean indicating the success of the update.
 * @throws {Error} If there is an error updating the examples.
 */
export const updateExamples = async (scenarioId, values) => {
    try {
        const response = await axios.put(`${agentServer}/scenarios/${scenarioId}/knowledge`, values, {
            headers: {
                Authorization: "Bearer " + JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.status === 200;
    } catch (error) {
        console.error('Error updating examples for intent:', error);
        throw error;
    }
};

export const removeIntentResponses = async (scenario, intent) => {
    try {
        const response = await axios.delete(`${agentServer}/scenarios/${scenario}/knowledge/${intent}`, {
            headers: {
                Authorization: "Bearer " + JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.status == 200;
    } catch (error) {
        console.error(`Error removing intent ${intent}:`, error);
        throw error;
    }
}

export const getAllBeliefs = async (scenario) => {
    try {
        const response = await axios.get(`${agentServer}/scenarios/${scenario}/beliefs`, {
            headers: {
                Authorization: "Bearer "+ JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.data;
    } catch (error) {
        console.error(`Error getting beliefs for scenario:`, error);
        throw error;
    }
}

export const getAllActions = async (scenario) => {
    try {
        const response = await axios.get(`${agentServer}/scenarios/${scenario}/actions`);
        return response.data;
    } catch (error) {
        console.error(`Error getting beliefs for scenario:`, error);
        throw error;
    }
}

export const updateBeliefs = async (scenario, values) => {
    try {
        const response = await axios.put(`${agentServer}/scenarios/${scenario}/beliefs`, values);
        return response.status == 200;
    } catch (error) {
        console.error(`Error updating beliefs for scenario:`, error);
        throw error;
    }
}

export const createBelief = async (scenario, value) => {
    try {
        const response = await axios.post(`${agentServer}/scenarios/${scenario}/beliefs`, value);
        return response.status == 200;
    } catch (error) {
        console.error(`Error creating belief for scenario:`, error);
        throw error;
    }
}

export const createActions = async (scenario, value) => {
    try {
        const response = await axios.put(`${agentServer}/scenarios/${scenario}/actions`, value);
        return response.status == 200;
    } catch (error) {
        console.error(`Error creating belief for scenario:`, error);
        throw error;
    }
}

export const deleteBelief = async (scenario, beliefId) => {
    try {
        const response = await axios.delete(`${agentServer}/scenarios/${scenario}/beliefs/${beliefId}`);
        return response.status == 200;
    } catch (error) {
        console.error(`Error deleting belief for scenario:`, error);
        throw error;
    }
}

export const getDesires = async (scenario) => {
    try {
        const response = await axios.get(`${agentServer}/scenarios/${scenario}/desires`, {
            headers: {
                Authorization: "Bearer "+ JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.data;
    } catch (error) {
        console.error(`Error getting desires for scenario:`, error);
        throw error;
    }
}

export const updateMappings = async (scenario, intent, value) => {
    try {
        const response = await axios.put(`${agentServer}/scenarios/${scenario}/intents/${intent}/mapping`, value, {
            headers: {
                Authorization: "Bearer "+ JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.status == 200;
    } catch (error) {
        console.error(`Error updating mapping:`, error);
        throw error;
    }
}

export const updateMappingConditions = async (scenario, intent, value) => {
    try {
        const response = await axios.put(`${agentServer}/scenarios/${scenario}/intents/${intent}/mapping/conditions`, value, {
            headers: {
                Authorization: "Bearer "+ JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.status == 200;
    } catch (error) {
        console.error(`Error updating mapping conditions:`, error);
        throw error;
    }
}

export const getMappings = async (scenario, intent) => {
    try {
        const response = await axios.get(`${agentServer}/scenarios/${scenario}/intents/${intent}/mapping/`, {
            headers: {
                Authorization: "Bearer "+ JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.data;
    } catch (error) {
        console.error(`Error getting mapping:`, error);
        throw error;
    }
}

export const commitScenario = async (scenario) => {
    try {
        const response = await axios.post(`${agentServer}/scenarios/${scenario}/commit/`);
        return response.status == 200;
    } catch (error) {
        console.error(`Error getting mapping:`, error);
        throw error;
    }
}

export const getMappingConditions = async (scenario, intent) => {
    try {
        const response = await axios.get(`${agentServer}/scenarios/${scenario}/intents/${intent}/mapping/conditions`, {
            headers: {
                Authorization: "Bearer "+ JSON.parse(sessionStorage.getItem("vuex")).auth.token
            }
        });
        return response.data;
    } catch (error) {
        console.error(`Error getting mapping conditons:`, error);
        throw error;
    }
}

export const deleteIntentMapping = async (scenario, intent) => {
    try {
        const response = await axios.delete(`${agentServer}/scenarios/${scenario}/intents/${intent}/mapping`);
        return response.status == 200;
    } catch (error) {
        console.error(`Error deleting intent mapping:`, error);
        throw error;
    }
}

export const updateDesires = async (scenario, desires) => {
    try {
        const response = await axios.put(`${agentServer}/scenarios/${scenario}/desires`, desires);
        return response.status == 200
    } catch (error) {
        console.error(`Error updating desires:`, error);
        throw error;
    }
}

export const createDesire = async (scenario, value) => {
    try {
        const response = await axios.post(`${agentServer}/scenarios/${scenario}/desires`, value);
        return response.status == 200;
    } catch (error) {
        console.error(`Error creating desire for scenario:`, error);
        throw error;
    }
}

export const deleteDesire = async (scenario, desire) => {
    try {
        const response = await axios.delete(`${agentServer}/scenarios/${scenario}/desires/${desire}`);
        return response.status == 200;
    } catch (error) {
        console.error(`Error deleting desire for scenario:`, error);
        throw error;
    }
}

export const setUserScenario = async (user, scenario) => {
    try {
        const response = await axios.put(`${agentServer}/agent/scenario/${user}/${scenario}`);
        return response.status == 200;
    } catch (error) {
        console.error(`Error updating scenario:`, error);
        throw error;
    }
}

export const getUserScenario = async (user) => {
    try {
        const response = await axios.get(`${agentServer}/agent/scenario/${user}`);
        if (response.status == 404) return null;
        return response.data;
    } catch (error) {
        console.error(`Error getting scenario:`, error);
        throw error;
    }
}


export const getActionConditions = async (scenario, intent) => {
    try {
        const response = await axios.get(`${agentServer}/scenarios/${scenario}/intents/${intent}/mapping/actions`);
        return response.data;
    } catch (error) {
        console.error(`Error getting action conditons:`, error);
        throw error;
    }
}

export const updateActionConditions = async (scenario, intent, value) => {
    try {
        const response = await axios.put(`${agentServer}/scenarios/${scenario}/intents/${intent}/mapping/actions`, value);
        return response.status == 200;
    } catch (error) {
        console.error(`Error updating action conditions:`, error);
        throw error;
    }
}

export default {
    getScenarios,
    createScenario,
    getIntents,
    getExamples,
    updateExamples,
    removeIntentResponses,
    getAllBeliefs,
    getAllActions,
    updateBeliefs,
    createBelief,
    createActions,
    deleteBelief,
    getDesires,
    updateMappings,
    updateMappingConditions,
    getMappings,
    commitScenario,
    getMappingConditions,
    updateDesires,
    createDesire,
    deleteDesire,
    setUserScenario,
    getUserScenario,
    getActionConditions,
    updateActionConditions,
    deleteIntentMapping
};