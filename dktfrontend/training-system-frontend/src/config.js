const config = {
    agentServer: 'http://'+process.env.VUE_APP_DKT_SERVER_BACKEND,
    agentWsServer: 'ws://'+process.env.VUE_APP_DKT_SERVER_BACKEND+'/session',
    rasaServer: 'http://'+ process.env.VUE_APP_DKT_SERVER_RASA,
};

console.log(process.env)

export default config;