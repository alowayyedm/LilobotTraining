<template>
    <h3>Weergave-instellingen</h3>
  
    <div class="container">
        <div class="contrast-mode" title="high contrast mode">
          <div>Hoog contrast</div>

            <label class="switch">
                <input type="checkbox" 
                    title="contrast toggle"
                    v-model="isHighContrast" 
                    @change="this.toggleHighContrast">
                <span class="slider"></span>
            </label>

          <div class="switch-label" title="high contrast mode enabled" v-if="isHighContrast">AAN</div>
          <div class="switch-label" title="high contrast mode disabled" v-else>UIT</div>
        </div>
    </div>
</template>
  
<script>  
  export default {
    name: "PreferencesTab",
    data() {
      return {
        isHighContrast: this.getContrastModeFromLocalStorage()
      }
    },
    methods: {
        toggleHighContrast() {
            localStorage.setItem('high_contrast_mode', this.isHighContrast);

            const rootElement = document.documentElement;

            if(this.isHighContrast) {
                rootElement.setAttribute('data-contrast', 'high');
            } else {
                rootElement.removeAttribute('data-contrast');
            }
        },
        getContrastModeFromLocalStorage() {
            const highContrastMode = localStorage.getItem('high_contrast_mode');
            const isHighContrast = JSON.parse(highContrastMode);

            return isHighContrast;
        }
    }
  }
</script>

<style scoped>
@import "../../../styles/main.css";

h3 {
  color: var(--user-settings-header-text);
  margin-left: 1rem;
}

.container {
  display: inline-block;
  margin: 1rem;
  font-family: 'Poppins', sans-serif;
}

.contrast-mode {
  display: grid;
  gap: 1rem;
  grid-template-columns: 8rem 3rem 2rem;
  align-items: center;
  height: 7%;
}

/* The switch - the box around the slider */
/* Taken from https://www.w3schools.com/howto/howto_css_switch.asp */
.switch {
  position: relative;
  display: inline-block;
  width: 3.75rem;
  max-width: 3.75rem;
  min-width: 3.75rem;
  height: 2.125rem;
  transform: scale(0.7);
}

/* Hide default HTML checkbox */
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

/* The slider */
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--contrast-mode-slider-inactive);
  border-radius: 2.125rem;

  /* border only in accessibility mode */
  border: solid;
  border-width: var(--basic-border-width);
  border-color: var(--basic-border-color);
}

.slider:hover {
  outline: var(--button-outline-width) solid var(--button-outline-color);
  outline-offset: 4px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 1.625rem;
  width: 1.625rem;
  left: var(--chat-widget-slider-circle-offset);
  bottom: var(--chat-widget-slider-circle-offset);
  background-color: var(--contrast-mode-slider-circle-inactive);
  -webkit-transition: .4s;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: var(--contrast-mode-slider-active);
}

input:checked + .slider:before {
  background-color: var(--contrast-mode-slider-circle-active);
}

input:focus + .slider {
  box-shadow: 0 0 0.063rem var(--contrast-mode-slider-shadow);
}

input:checked + .slider:before {
  transform: translateX(1.625rem);
}

</style>