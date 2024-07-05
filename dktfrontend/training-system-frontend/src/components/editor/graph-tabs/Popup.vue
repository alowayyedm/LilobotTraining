<template>
  <div v-if="showPopup" class="popup" @keyup.esc="closePopup" @keyup.enter="chooseSelection">
    <div class="popup-content">
      <span class="close" @click="closePopup">&times;</span>
      <h5 class="text">{{ headerText }}</h5>
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    showPopup: {
      type: Boolean,
      default: false
    },
    headerText: {
      type: String,
      default: 'Select Intent'
    }
  },

  name: "ScenarioList",
  components: {
  },
  data() {
    return {

    }
  },
  methods: {
    closePopup() {
      this.$emit('update:showPopup', false);
    }
  }
}
</script>

<style scoped>
.popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup-content {
  background-color: var(--main-background);
  border-radius: 8px;
  padding: 10px;
  width: 80%;
  height: 80%;
  text-align: center;
  position: relative;
  box-sizing: border-box;
  overflow: auto;
}

.popup-content .btn {
  float: none;
}

.popup-content .newScenarioName {
  margin-top: 20px;
  margin-bottom: 10px;
}

.popup-content .text-danger {
  font-size: 14px;
}

.close {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 18px;
  cursor: pointer;
}

.text{
  border: none;
  color: var(--chat-widget-button-text);
  font-size: large;
  font-family: 'Anton', sans-serif;
}

</style>