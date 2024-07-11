<!--
  This component is the message node, and does not contain any functionality, only styling.
-->
<template>
  <div class="message-node" :tabindex="0" aria-label="message graph node">
    <div class="message-phase-text">Phase</div>
    <div class="message-phase-number">{{ phase }}</div>
  </div>
</template>

<script>
export default {
  name: "MessageNode",
  props: ["phase"]
}
</script>

<style scoped>
.message-node {
  cursor: pointer;
  min-height: 5rem;
  width: 5rem;
  background-color: var(--inactive-node);
  border-radius: 50%;
  text-align: center;
}

.message-node:hover,
.message-node:focus {
  transform: scale(1.05);
  box-shadow: 0 4px 8px 0 rgba(0, 0.5, 0, 0.2), 0 4px 20px 0 rgba(0, 0, 0, 0.2);
}

.message-node:active {
  transform: scale(1.0);
}

.message-phase-text {
  padding-top: 0.75rem;
  font-size: 1rem;
}

.message-phase-number {
  font-size: 1.3rem;
  font-weight: bolder;
}
</style>