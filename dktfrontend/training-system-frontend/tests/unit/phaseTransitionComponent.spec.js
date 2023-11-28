import {mount} from '@vue/test-utils';
import PhaseInfo from '@/components/PhaseInfo.vue';

describe('phase info', () => {
    it('does not display if null', async () => {
        const wrapper = mount(PhaseInfo, {
            props: {
                phase: null,
                lastTransition: null
            }
        });

        const buttons = wrapper.findAll(".expand-button");
        expect(buttons.length).toBe(5);

        // No phase should be displayed, and none is hovered
        expect(wrapper.find(".activePhase").exists()).toBe(false);
        expect(wrapper.find(".expanded").exists()).toBe(false);

        // Info for user
        const phaseInfoText = wrapper.findAll(".phase-info-text");
        expect(phaseInfoText.length).toBe(1);
        expect(phaseInfoText[0].text()).toBe("No phase active.");
    });

    it('updates goodLastTransition', async () => {
        const wrapper = mount(PhaseInfo, {
            props: {
                phase: null,
                lastTransition: null
            }
        });
        expect(wrapper.vm.$data.goodLastTransition).toBe(null);

        await wrapper.setProps({
            phase: null,
            lastTransition: { from: 1, to: 4 }
        });
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.goodLastTransition).toBe(false);

        await wrapper.setProps({
            phase: null,
            lastTransition: { from: 1, to: 2 }
        });
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.goodLastTransition).toBe(true);

        await wrapper.setProps({
            phase: null,
            lastTransition: null
        });
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.goodLastTransition).toBe(null);
    });

    it('expands on hover', async () => {
        const wrapper = mount(PhaseInfo, {
            props: {
                phase: null,
                lastTransition: null
            }
        });

        const buttons = wrapper.findAll(".expand-button");
        expect(buttons.length).toBe(5);

        const widthBefore = buttons[0].element.clientWidth;
        await buttons[0].trigger('mouseenter');

        setTimeout(async () => {
            await wrapper.vm.$nextTick();
            const widthAfter = buttons[0].element.clientWidth;
            expect(widthAfter).toBe(widthBefore * 2);

            await buttons[0].trigger('mouseleave');
            setTimeout(async () => {
                await wrapper.vm.$nextTick();
                expect(buttons[0].element.clientWidth).toBe(widthBefore);
            }, 300);
        }, 300);
    });

    it('selects and displays phase info, good transition', async () => {
        const wrapper = mount(PhaseInfo, {
            props: {
                phase: null,
                lastTransition: null
            }
        });

        // Good transition
        await wrapper.setProps({
            phase: 2,
            lastTransition: { from: 1, to: 2 }
        });
        await wrapper.vm.$nextTick();

        const buttons = wrapper.findAll(".expand-button");
        expect(buttons.length).toBe(5);

        // Current phase: 2
        const activeNodes = wrapper.findAll(".activePhase");
        expect(activeNodes.length).toBe(1);
        expect(activeNodes[0].text()).toBe("Jump 2");

        // Info for user: good transition, not last phase
        const phaseInfoTexts = wrapper.findAll(".phase-info-text");
        expect(phaseInfoTexts.length).toBe(2);
        expect(phaseInfoTexts[0].text()).toBe("Last transition: Phase 1 → 2");
        expect(phaseInfoTexts[1].text()).toBe("Next → Phase 3: " + wrapper.vm.$data.buttons[1].nextConditions);
        expect(wrapper.find(".fa-face-smile").exists()).toBe(true);
        expect(wrapper.find(".fa-face-frown").exists()).toBe(false);
    });

    it('selects and displays phase info, bad transition', async () => {
        const wrapper = mount(PhaseInfo, {
            props: {
                phase: null,
                lastTransition: null
            }
        });

        // Bad transition
        await wrapper.setProps({
            phase: 2,
            lastTransition: { from: 3, to: 2 }
        });
        await wrapper.vm.$nextTick();

        // Current phase: 2
        const activeNodes = wrapper.findAll(".activePhase");
        expect(activeNodes.length).toBe(1);
        expect(activeNodes[0].text()).toBe("Jump 2");

        // Info for user: bad transition, not last phase
        const phaseInfoTexts = wrapper.findAll(".phase-info-text");
        expect(phaseInfoTexts.length).toBe(2);
        expect(phaseInfoTexts[0].text()).toBe("Last transition: Phase 3 → 2");
        expect(phaseInfoTexts[1].text()).toBe("Next → Phase 3: " + wrapper.vm.$data.buttons[1].nextConditions);
        expect(wrapper.find(".fa-face-smile").exists()).toBe(false);
        expect(wrapper.find(".fa-face-frown").exists()).toBe(true);
    });

    it('selects and displays phase info, last phase', async () => {
        const wrapper = mount(PhaseInfo, {
            props: {
                phase: null,
                lastTransition: null
            }
        });

        // Good transition, last phase
        await wrapper.setProps({
            phase: 5,
            lastTransition: { from: 4, to: 5 }
        });
        await wrapper.vm.$nextTick();

        // Current phase: 5
        const activeNodes = wrapper.findAll(".activePhase");
        expect(activeNodes.length).toBe(1);
        expect(activeNodes[0].text()).toBe("Jump 5");

        // Info for user: good transition, last phase
        const phaseInfoTexts = wrapper.findAll(".phase-info-text");
        expect(phaseInfoTexts.length).toBe(2);
        expect(phaseInfoTexts[0].text()).toBe("Last transition: Phase 4 → 5");
        expect(phaseInfoTexts[1].text()).toBe("Arrived in last phase.");
        expect(wrapper.find(".fa-face-smile").exists()).toBe(true);
        expect(wrapper.find(".fa-face-frown").exists()).toBe(false);
    });
});