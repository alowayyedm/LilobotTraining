<!--
  This component is the intention edge. It renders the example message. When hovering over the
  intent, it displays the intention name.
-->
<template>
  <div class="edge-line"></div>
  <div class="intent" :title="intentionName">{{ exampleMsg }}</div>
  <div class="edge-line"></div>
</template>

<script>
export default {
  name: "IntentionEdge",
  props: ["intentionName", "exampleMsg"]
}
</script>

<style scoped>
.intent {
  min-height: min-content;
  min-width: 5rem;
  max-width: 10rem;
  text-align: center;
  padding: 0.5rem;
  background-color: var(--edge-label);
  border-radius: 1rem;
  color: var(--edge-label-text);
}
.edge-line {
  border-left: 0.2rem solid var(--edge-line);
  min-height: 3rem;
}
</style>