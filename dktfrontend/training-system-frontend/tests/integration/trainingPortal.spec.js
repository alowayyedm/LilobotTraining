import { mount } from '@vue/test-utils';
import TrainingPortal from '@/pages/TrainingPortal.vue';
import { createStore } from 'vuex'


jest.mock('@stomp/stompjs',  () => ({
    Stomp: {
        client: jest.fn().mockReturnValue({
            connect: jest.fn(),
        }),
    },
}));

jest.mock('axios', () => ({
    get: jest.fn().mockReturnValue({
        then: jest.fn().mockReturnThis(),
        catch: jest.fn().mockReturnThis()
    }),
}));



describe('Join popup', () => {
    it('input pops up when join button is clicked and logged in', async () => {
        const store = createStore({
            state: {
                auth: {
                    status: {
                        loggedIn: true
                    }
                }
            }
        })

        const wrapper = mount(TrainingPortal, {
            global: {
                plugins: [store],
                mocks: {
                    $config: {
                        agentServer: 'server'
                    }
                }
            },
            data() {
                sessionActive: "test"
            }});
        await wrapper.vm.$nextTick()
        await wrapper.find('#join-button').trigger('click');

        expect(wrapper.find('.join-popup-container').exists()).toBeTruthy();
        expect(wrapper.find('.overlay').exists()).toBeTruthy();

    });

    it('alert pops up when join button is clicked and logged in', async () => {
        const store = createStore({
            state: {
                auth: {
                    status: {
                        loggedIn: false
                    }
                }
            }
        })

        const wrapper = mount(TrainingPortal, {
            global: {
                plugins: [store],
                mocks: {
                    $config: {
                        agentServer: 'server'
                    }
                }
            },
            data() {
                sessionActive: "test"
            }});
        await wrapper.vm.$nextTick()
        await wrapper.find('#join-button').trigger('click');

        expect(wrapper.find('.generic-popup-container').exists()).toBeTruthy();
        expect(wrapper.find('.overlay').exists()).toBeTruthy();
    });
});
