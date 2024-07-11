import { shallowMount } from '@vue/test-utils';
import GraphComponent from '@/components/GraphComponent.vue';
import {createStore} from "vuex";

jest.mock('axios', () => ({
  get: jest.fn().mockReturnValue({
    then: jest.fn().mockReturnThis(),
    catch: jest.fn().mockReturnThis()
  }),
}));

/**
 * Checks if the loading icon is shown, and then if the error message is displayed (timeout of 200).
 *
 * @param wrapper           The wrapper of the component.
 * @returns {Promise<void>} Promise for execution.
 */
const checkLoadingAndThenError = async function (wrapper) {
  await wrapper.find('button').trigger('click');
  await wrapper.vm.$nextTick();

  expect(wrapper.find('#path-loading-icon').exists()).toBe(true);
  expect(wrapper.find('#error-message-path').exists()).toBe(false);
  expect(wrapper.vm.loading).toBe(true);
  expect(wrapper.vm.error).toBe(false);

  setTimeout(async () => {
    await wrapper.vm.$nextTick();

    expect(wrapper.find('#error-message-path').exists()).toBe(true);
    expect(wrapper.find('#path-loading-icon').exists()).toBe(false);
    expect(wrapper.vm.loading).toBe(false);
    expect(wrapper.vm.error).toBe(true);
  }, 200);
}

describe('path reload button', () => {
  it('triggers loading and then null causes error', async () => {
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
    expect(wrapper.find('#path-loading-icon').exists()).toBe(false);
    expect(wrapper.find('#error-message-path').exists()).toBe(false);

    jest.mock('axios', () => ({
      get: jest.fn().mockImplementation(() => {
        return new Promise((resolve) => {
          setTimeout(() => {
            resolve({
              data: null,
              status: 200
            });
          }, 200);
        });
      }),
    }));

    await checkLoadingAndThenError(wrapper);
  });

  it('triggers loading and then error code causes error', async () => {
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
    expect(wrapper.find('#path-loading-icon').exists()).toBe(false);
    expect(wrapper.find('#error-message-path').exists()).toBe(false);

    jest.mock('axios', () => ({
      get: jest.fn().mockImplementation(() => {
        return new Promise((resolve) => {
          setTimeout(() => {
            resolve({ status: 500 });
          }, 200);
        });
      }),
    }));

    await checkLoadingAndThenError(wrapper);
  });

  it('triggers loading and then shows path', async () => {
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
    expect(wrapper.find('#path-loading-icon').exists()).toBe(false);
    expect(wrapper.find('#error-message-path').exists()).toBe(false);

    jest.mock('axios', () => ({
      get: jest.fn().mockImplementation(() => {
        return new Promise((resolve) => {
          setTimeout(() => {
            resolve({
              data: {
                nodes : [
                    { beliefs: [ { value: 0.1 } ], desires: [ { isActive: false } ],
                      phase: "PHASE4", edge: { intentionName: 'x', exampleMsg: 'y' } },
                    { beliefs: [], desires: [], phase: "PHASE5", edge: null}
                ]
              }, status: 200})
          }, 200);
        });
      }),
    }));

    await wrapper.find('button').trigger('click');
    await wrapper.vm.$nextTick();

    expect(wrapper.find('#path-loading-icon').exists()).toBe(true);
    expect(wrapper.find('#error-message-path').exists()).toBe(false);
    expect(wrapper.find('#optimal-path').exists()).toBe(false);
    expect(wrapper.vm.dataAvailable).toBe(false);
    expect(wrapper.vm.loading).toBe(true);
    expect(wrapper.vm.error).toBe(false);

    setTimeout(async () => {
      await wrapper.vm.$nextTick();

      expect(wrapper.find('#error-message-path').exists()).toBe(false);
      expect(wrapper.find('#path-loading-icon').exists()).toBe(false);
      expect(wrapper.find('#optimal-path').exists()).toBe(true);

      // Assert on inner state
      expect(wrapper.vm.dataAvailable).toBe(true);
      expect(wrapper.vm.loading).toBe(false);
      expect(wrapper.vm.error).toBe(false);
      expect(wrapper.vm.activeMessageNode).toBe(0);
      expect(wrapper.vm.activePhase).toBe(4);

      // Assert on appearance
      expect(wrapper.findAll('.active').length).toBe(1);
      expect(wrapper.findAll('.edge-line').length).toBe(1);
      const edge = wrapper.find('.edge-line');
      const messageNodes = wrapper.findAll('.message-node');

      expect(messageNodes[0].text()).toBe("Phase 4");
      expect(messageNodes[1].text()).toBe("Phase 5");
      expect(edge.find(".intent").text()).toBe("y");
      expect(edge.find(".intent").attributes("title")).toBe("x");

      expect(wrapper.find('#active-message-phase').text()).toContain("Phase 4");
      const beliefTable = wrapper.find('#active-message-beliefs');
      expect(beliefTable.findAll("td")[0].text()).toContain("B1");
      expect(beliefTable.findAll("td")[1].text()).toContain("0.1");
      expect(beliefTable.findAll("td").length).toBe(2);
      const desireTable = wrapper.find('#active-message-desires');
      expect(desireTable.findAll("td")[0].text()).toContain(wrapper.vm.$data.desireDescriptions);
      expect(desireTable.findAll("td")[1].text()).toContain("F");
      expect(desireTable.findAll("td").length).toBe(2);
    }, 200);
  });
});