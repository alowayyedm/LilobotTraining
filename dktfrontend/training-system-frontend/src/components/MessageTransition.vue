<!--
  This component is the message transition entry, and does not contain any functionality,
  only styling and vue conditionals
-->
<template>
  <div class="message-transition">
    <div>{{ makeTextEntry(text, causeType) }}</div>

    <table class="beliefs-container">
      <tr>
        <td>
          <div class="beliefs">
            <i class="fa-solid fa-arrow-up fa-xl"></i>

            <span v-if="posBeliefs.length === 0">
              -
            </span>
            <span v-for="belief in posBeliefs" :key="belief" :title="getBeliefDescription(belief)">
              {{`${belief}&nbsp;`}}
            </span>
          </div>
        </td>
        <td>
          <div class="beliefs">
            <i class="fa-solid fa-arrow-down fa-xl"></i>

            <span v-if="negBeliefs.length === 0">
              -
            </span>
            <span v-for="belief in negBeliefs" :key="belief" :title="getBeliefDescription(belief)">
              {{`${belief}&nbsp;`}}
            </span>
          </div>

        </td>
      </tr>
    </table>

  </div>
</template>

<script>
export default {
  name: "MessageTransition",
  props: ["text", "causeType", "posBeliefs", "negBeliefs"],
  data() {
    return {
      beliefDescriptions: [
        "B1: I feel in control in the conversation",
        "B2: I think I am competent to solve the problem",
        "B3: I feel connected to KT",
        "B4: I think KT can be trusted",
        "B5: I think KT understands me",
        "B6: I think KT is interested in my story",
        "B7: I think KT can help me",
        "B8: I think KT can solve my problem",
        "B9: I think I talked about my situation",
        "B10: I think KT is asking about my wish",
        "B11: I think KT is asking for a positive wish",
        "B12: I think KT is asking about a confidant",
        "B13: I think my teacher can help me",
        "B14: I feel safe in the conversation",
        "B15: I think KT wants to end the conversation",
        "B16: I think KT and I will find a solution together",
        "B17: I think KT will solve the problem for me"
      ]
    }
  },
  methods: {
    getBeliefDescription(belief) {
      return this.beliefDescriptions[parseInt(belief.substring(1)) - 1];
    },
    makeTextEntry(text, causeType) {
      if (causeType === 'MESSAGE') {
        return `\u201C ${text} \u201D`;
      } else if (causeType === 'TRIGGER') {
        return `Trigger`
      } else if (causeType === 'MANUAL') {
        return `${text}`
      } else {
        return `reden onbekend`
      }
    }
  }
}
</script>

<style scoped>
.message-transition {
  line-height: 2rem;
  font-weight: bold;
}

.beliefs {
  display: flex;
  align-items: center;
}

.beliefs-container {
  font-family: 'Poppins', sans-serif;
  font-weight: bold;
  width: 100%;
  text-align: left;
  table-layout: fixed;
}

.fa-arrow-up {
  color: var(--success-icon);
  width: 1.5rem;
  flex-shrink: 0;
}

.fa-arrow-down {
  color: var(--warning-icon);
  width: 1.5rem;
  flex-shrink: 0;
}

</style>