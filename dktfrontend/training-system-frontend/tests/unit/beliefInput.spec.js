import { shallowMount } from '@vue/test-utils';
import BeliefInput from '@/components/BeliefInput.vue';

describe('Info Box', () => {
    it('is shown after info icon clicked', async () => {
        const wrapper = shallowMount(BeliefInput);
        expect(wrapper.find('.belief')).toBeDefined();
        await wrapper.find('.general-icon').trigger('click');
        expect(wrapper.find('.info-box')).toBeDefined();
    });

    it('is shown after info icon clicked', async () => {
        const wrapper = shallowMount(BeliefInput);
        wrapper.vm.displayInfo = false;
        expect(wrapper.find('.belief')).toBeDefined();
        await wrapper.find('.general-icon').trigger('click');
        expect(wrapper.find('.transitions')).toBeDefined();
    });
});