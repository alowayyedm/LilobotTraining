import { shallowMount } from '@vue/test-utils';
import BeliefTransitionsComponent from '@/components/BeliefTransitionsComponent.vue';

describe('Info Box', () => {
    it('is shown after info icon clicked', async () => {
        const wrapper = shallowMount(BeliefTransitionsComponent);
        expect(wrapper.find('.transitions')).toBeDefined();
        await wrapper.find('.general-icon').trigger('click');
        expect(wrapper.find('.info-box')).toBeDefined();
    });

    it('is shown after info icon clicked', async () => {
        const wrapper = shallowMount(BeliefTransitionsComponent);
        wrapper.vm.displayInfo = false;
        expect(wrapper.find('.info-box')).toBeDefined();
        await wrapper.find('.general-icon').trigger('click');
        expect(wrapper.find('.transitions')).toBeDefined();
    });
});