import { shallowMount } from '@vue/test-utils';
import WebChatComponent from '@/components/WebChatComponent.vue';

describe('message loading icon', () => {
    it('is shown after message received from server', async () => {
        const msg = 'Hello';
        const wrapper = shallowMount(WebChatComponent);
        wrapper.vm.addMessage(msg, false, false);
        const loadingIcon = wrapper.vm.$refs.loading;
        expect(loadingIcon).toBeDefined();
        await wrapper.vm.$nextTick();
        expect(loadingIcon.hidden).toBe(false);
        expect(wrapper.vm.showLoading).toBe(true);
    });

    it('is hidden after message sent from client', async () => {
        const msg = 'Hello';
        const wrapper = shallowMount(WebChatComponent);
        wrapper.vm.addMessage(msg, true, false);
        const loadingIcon = wrapper.vm.$refs.loading;
        expect(loadingIcon).toBeDefined();
        await wrapper.vm.$nextTick();
        expect(loadingIcon.hidden).toBe(true);
        expect(wrapper.vm.showLoading).toBe(false);
    });

    it('is shown after message sent from trainer without options', async () => {
        const msg = 'Hello';
        const wrapper = shallowMount(WebChatComponent);
        wrapper.vm.setTrainer(true);
        wrapper.vm.addMessage(msg, false, false);
        const loadingIcon = wrapper.vm.$refs.loading;
        expect(loadingIcon).toBeDefined();
        await wrapper.vm.$nextTick();
        expect(loadingIcon.hidden).toBe(false);
        expect(wrapper.vm.showLoading).toBe(true);
    });

    it('is shown after message sent from rasa with options', async () => {
        const msg = 'Hello';
        const wrapper = shallowMount(WebChatComponent);
        wrapper.vm.setTrainer(true);
        wrapper.vm.addMessage(msg, false, true);
        const loadingIcon = wrapper.vm.$refs.loading;
        expect(loadingIcon).toBeDefined();
        await wrapper.vm.$nextTick();
        expect(loadingIcon.hidden).toBe(false);
        expect(wrapper.vm.showLoading).toBe(true);
    });
});

describe('add message order', () => {
    jest.useFakeTimers()
    it('message is shown above message with options', async () => {
        const wrapper = shallowMount(WebChatComponent, {
            data() {
                return {
                    isTrainer: true
                }
            }
        });
        wrapper.vm.isTrainer = true;
        wrapper.vm.autoSending = false;
        wrapper.vm.addMessage("Hello", false, true);
        jest.runOnlyPendingTimers();
        await wrapper.vm.$nextTick();
        wrapper.vm.addMessage("Hi", false, false);
        jest.runOnlyPendingTimers();
        await wrapper.vm.$nextTick();

        const messages = wrapper.findAll('.message')
        expect(messages.length).toBe(2);
        expect(messages[0].text()).toBe("Hi");
        expect(messages[1].text()).toBe("Hello");
    });

    it('messages with options have correct order', async () => {
        const wrapper = shallowMount(WebChatComponent, {
            data() {
                return {
                    isTrainer: true
                }
            }
        });
        wrapper.vm.isTrainer = true;
        wrapper.vm.autoSending = false;
        wrapper.vm.addMessage("Hello", false, true);
        jest.runOnlyPendingTimers();
        await wrapper.vm.$nextTick();
        wrapper.vm.addMessage("Hi", false, true);
        jest.runOnlyPendingTimers();
        await wrapper.vm.$nextTick();

        const messages = wrapper.findAll('.message')
        expect(messages.length).toBe(2);
        expect(messages[0].text()).toBe("Hello");
        expect(messages[1].text()).toBe("Hi");
    });
});

describe('option buttons', () => {
    jest.useFakeTimers()
    it('option buttons are shown', async () => {
        const msg = 'Hello';
        const wrapper = shallowMount(WebChatComponent);
        wrapper.vm.addMessage(msg, false, true);
        jest.runOnlyPendingTimers();
        await wrapper.vm.$nextTick();
        expect(wrapper.find('.option-buttons-container').exists()).toBe(true);
    });
});

/**
 * Tests for visibility of elements in the chat widget concerning the joining and leaving
 * of sessions.
 */
describe('exit/join session', () => {

    /**
     * For trainers, chat widget content is hidden until a session is joined
     */
    it('trainer`s buttons visibility before joining', async () => {
        const wrapper = shallowMount(WebChatComponent, {
            data() {
                return {
                    isTrainer: true,
                    isConversationActive: false
                }
            }
        })

        // Initially, exit button, chat, chat mode and input box should be hidden
        const exitButton = wrapper.find('#exit-session');
        const chat = wrapper.find({ ref: 'chat' });
        const inputBox = wrapper.find({ ref: 'inputBox' });
        const chatMode = wrapper.find('.chat-mode');

        expect(exitButton.exists()).toBe(false);
        expect(chat.exists()).toBe(false);
        expect(inputBox.exists()).toBe(false);
        expect(chatMode.exists()).toBe(false);

        // Simulate clicking join button
        const joinButton = wrapper.find('#join-button');
        expect(joinButton.exists()).toBe(true);

        const joinButtons = wrapper.findAll('#join-button');
        expect(joinButtons.length).toBe(2);
    });

    /**
     * For trainers that have joined a session, chat widget content is shown
     * until they leave the session
     */
    it('trainer`s buttons visibility after joining', async () => {
        const wrapper = shallowMount(WebChatComponent, {
            data() {
                return {
                    isTrainer: true,
                    isConversationActive: true
                }
            }
        })

        // Initially, exit button, chat, chat mode and input box should be visible
        const exitButton = wrapper.find('#exit-session');
        const chat = wrapper.find({ ref: 'chat' });
        const chatMode = wrapper.find('.chat-mode');

        expect(exitButton.exists()).toBe(true);
        expect(chat.exists()).toBe(true);
        expect(chatMode.exists()).toBe(true);

        // Join button should be hidden
        const joinButton = wrapper.find('#join-button');
        expect(joinButton.exists()).toBe(false);
    });

    /**
    * For learners, the join buttons are hidden, and the conversation
    * is set to Active
    */
    it('learners`s join hidden and exit showing', async () => {
        const wrapper = shallowMount(WebChatComponent, {
            data() {
                return {
                    isTrainer: false,
                }
            }
        })
        expect(wrapper.vm.isConversationActive).toBe(true);

        const joinButton = wrapper.find('#join-button');
        expect(joinButton.exists()).toBe(false);

        const exitButton = wrapper.find('#exit-session');
        expect(exitButton.exists()).toBe(true);
    });
});
