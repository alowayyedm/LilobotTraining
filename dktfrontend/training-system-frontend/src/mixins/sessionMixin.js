import { io } from 'socket.io-client';
import {Stomp} from "@stomp/stompjs";

// This mixin is used to start and handle a chat session
export default {
    data() {
        return {
            stompClient: null,
            socket: null,
            isManual: false,
            subscriptions: [],
            notificationSubs: []
        }
    },
    methods: {
        setupWebsocketConnection() {
            this.stompClient = Stomp.client(this.$config.agentWsServer);
            this.stompClient.connect({}, () => {
                console.log('STOMP connection established');
                this.setUpNotifications();
            });
        },
        setupRasaSession() {
            // Sets up the rasa session
            this.socket = io(this.$config.rasaServer);
            const sessionID = this.getSessionID();
            this.socket.on('connect', () => {
                console.log("Connected to Rasa server");
                this.socket.emit('session_request', {
                    'session_id': sessionID,
                });
                console.log(`Session ID: ${sessionID}`);
            });

            this.socket.on('connect_error', (error) => {
                console.error(error);
            });
        },
        sendUserMessage(text) {
            this.sendToSpring(text)
            this.sendToRasa(text)
        },
        async sendToSpring(body) {
            // Make sure spring websocket connection is active
            await this.waitForStompConnection(this.stompClient);
            // Send the message to Spring through stomp websocket to be used in live chat
            if (this.stompClient?.connected) {
                // Send the message to /app/session/{sessionID} which gets forwarded to /topic/session/{sessionID}
                const sessionID = this.getSessionID()
                const message = {"message": body, "fromUser": true}
                this.stompClient.send('/app/session/' + sessionID, {}, JSON.stringify(message));
            }
        },
        async waitForStompConnection(stompClient) {
            // Retries 5 times every 200 ms
            return new Promise((resolve, reject) => {
                const retries = 10;
                const delay = 100;
                let count = 0

                const interval = setInterval(() => {
                    if (stompClient?.connected) {
                        clearInterval(interval)
                        resolve()
                    } else if (count >= retries) {
                        clearInterval(interval)
                        reject()
                    }
                    count++
                }, delay)
            })
        },
        sendToRasa(body) {
            // Send the message to Rasa through socket.io websocket to be processed as usual by the application
            if (this.socket) {
                this.socket.emit('user_uttered', {
                    message: body,
                    session_id: sessionStorage.getItem('rasa_session_id')
                });
            }
        },
        getSessionID() {
            // Gets the current session ID or generates a new one if one does not exist
            const sessionID = sessionStorage.getItem('rasa_session_id');
            if (sessionID) {
                return sessionID;
            }
            const newSessionID = this.generateHexId()
            sessionStorage.setItem('rasa_session_id', newSessionID);
            return newSessionID;
        },
        generateHexId() {
            // Generates a unique 32 digit hexadecimal ID to be used as session ID
            const characters = '0123456789abcdef';
            let hexId = '';
            for (let i = 0; i < 32; i++) {
                hexId += characters[Math.floor(Math.random() * 16)];
            }
            return hexId;
        },
        addSubscription(destination, messageHandler) {
            const subscription = this.stompClient.subscribe(destination, (message) => {
                messageHandler(message);
            }, { id: destination } );
            this.subscriptions.push(subscription);
        },
        unsubscribeFromTopics() {
            // Unsubscribe from previous topics
            this.subscriptions.forEach((subscription) => {
                subscription.unsubscribe();
            });
            this.subscriptions.length = 0;
        },
        setUpNotifications() {
            const trainerAssign = this.stompClient.subscribe('/topic/session/trainer_assign/' + this.$store.state.auth.username,
                (message) => {
                    this.emitter.emit("notification-message", message.body);
                });
            this.notificationSubs.push(trainerAssign);
        },
        clearNotifications() {
            this.notificationSubs.forEach((subscription) => {
                subscription.unsubscribe();
            });
        }
    }
}