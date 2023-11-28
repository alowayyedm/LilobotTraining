<!------------------------ Toast Queue ------------------------
    This component displays a small box where little popups 
    are shown for a short time after addToast(message: String, 
    type: String, duration: int) is called. The message is the 
    text that will be displayed, the type is 'info', 'success', 
    'warning' or 'error'. The type determines the color of the 
    toast message and the symbol in front of the message.
    
    When using this component in another Vue component (= parent), 
    the following 4 things must be done: 
      1. Import this component inside the script part of the 
      parent component:     
        `import ToastQueue from './ToastQueue.vue'`

      2. Add 'ToastQueue' to the components of the parent, inside 
      the 'export' part:
        `components: {
          ...
          ToastQueue
          ...
        },`

      3. Add the <toast-queue> tag in the template:
        `<div><toast-queue ref="toastQueue"></toast-queue></div>`

      4. To actually make toast messages, add this in the method 
      that should create the toasts:
        `this.$refs.toastQueue.addToast(message, 'success')`
-->
<template>
  <div :style="this.toastContainerStyle">
    <transition-group name="fade">
      <div v-for="toast in toasts" :key="toast.id" :class="['toast', toast.type]">
        <i :class=getIcon(toast.type)></i>&nbsp;{{ toast.message }}
      </div>
    </transition-group>
  </div>
</template>
  
<script>
import { ref } from 'vue';

export default {
  name: 'ToastQueue',
  props: {
    direction: String,
    duration: Number
  },
  computed: {
    toastContainerStyle: function() {
      const directionMap = {
        up: 'column-reverse',
        down: 'column',
        left: 'row-reverse',
        right: 'row'
      };
      return {
        display: 'flex',
        flexDirection: directionMap[this.direction] || 'row'
      };
    },
  },
  setup() {
    const toasts = ref([])
  
    function addToast(message, type = 'info', duration = this.duration) {
      const id = Date.now().toString()
      toasts.value.push({ id, message, type })
      setTimeout(() => {
        removeToast(id)
      }, duration)
    }
  
    function removeToast(id) {
      toasts.value = toasts.value.filter(toast => toast.id !== id)
    }

    return {
      toasts,
      addToast,
      removeToast
    }
  },
  methods: {
    getIcon(type) {
      switch (type) {
        case 'info':
          return 'fas fa-info-circle';
        case 'success':
          return 'fas fa-check-circle';
        case 'warning':
          return 'fas fa-exclamation-circle';
        case 'error':
          return 'fas fa-times-circle';
      }
    }
  }
}
</script>

<style scoped>
@import "../../styles/main.css";

.toast-container {
  display: flex;
}
.toast {
  color: var(--toast-text);
  padding: 0.3rem;
  margin: 0.1rem;
  border-radius: 2px;
  z-index: 9999;
  font-size: smaller;
  font-weight: 500;
}
.toast.fade-enter-active,
.toast.fade-leave-active {
  transition: scale 0.12s linear;
}

.toast.fade-enter-from {
  scale: 0;
}

.toast.info {
  background-color: var(--info-toast-background);
}

.toast.success {
  background-color: var(--success-toast-background);
}

.toast.warning {
  background-color: var(--warning-toast-background);
}

.toast.error {
  background-color: var(--error-toast-background);
}

</style>