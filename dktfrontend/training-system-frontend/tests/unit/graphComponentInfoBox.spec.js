import { shallowMount } from '@vue/test-utils';
import GraphComponent from '@/components/GraphComponent.vue';
import {createStore} from "vuex";

jest.mock('axios', () => ({
    get: jest.fn().mockReturnValue({
        then: jest.fn().mockReturnThis(),
        catch: jest.fn().mockReturnThis()
    }),
}));

describe('Info Box', () => {
    it('is shown after info icon clicked', async () => {
        const store = createStore({
            state: {
                auth: {
                    token: 'token'
                }
            }
        })

        const wrapper = shallowMount(GraphComponent, {
            global: {
                plugins: [store],
                mocks: {
                    $config: {
                        agentServer: 'server'
                    }
                }
            }
        });
        expect(wrapper.find('.optimal-path')).toBeDefined();
        await wrapper.find('.general-icon').trigger('click');
        expect(wrapper.find('.info-box')).toBeDefined();
    });

    it('is shown after info icon clicked', async () => {
        const store = createStore({
            state: {
                auth: {
                    token: 'token'
                }
            }
        })

        const wrapper = shallowMount(GraphComponent, {
            global: {
                plugins: [store],
                mocks: {
                    $config: {
                        agentServer: 'server'
                    }
                }
            }
        });
        wrapper.vm.displayInfo = false;
        expect(wrapper.find('.optimal-path')).toBeDefined();
        await wrapper.find('.general-icon').trigger('click');
        expect(wrapper.find('.transitions')).toBeDefined();
    });
});