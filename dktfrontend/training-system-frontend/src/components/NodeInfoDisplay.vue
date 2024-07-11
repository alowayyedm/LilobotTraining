<!--
  This component is responsible for displaying the phase, beliefs and desires for the currently selected
  message node.
-->
<template>
  <div class="belief-value-container dark-scrollbar">
    <!-- Displaying the phase -->
    <h4 v-if="phase !== -1">PHASE</h4>
    <div v-if="phase !== -1" id="active-message-phase">
      Message in <b>Phase {{ phase }}</b>.
    </div>
    <!-- Displaying the beliefs -->
    <h4 v-if="beliefs !== null && beliefs.length > 0">BELIEFS</h4>
    <div
        v-if="beliefs !== null && beliefs.length > 0"
        id="active-message-beliefs"
        class="table-container dark-scrollbar">
      <!-- The structuring with two tables makes sure that half of the beliefs are in each column
      in order to save space -->
      <table>
        <tr
            v-for="(belief, index) in beliefs.slice(0, Math.ceil(beliefs.length / 2))"
            :key="index"
            :title="beliefDescriptions[index]">
          <td style="padding-right: 0.5rem;">{{ "B" + (index + 1) }}</td>
          <td>{{ Math.round(Number(belief.value) * 100) / 100 }}</td>
        </tr>
      </table>
      <table>
        <tr
            v-for="(belief, index) in beliefs.slice(Math.ceil(beliefs.length / 2), beliefs.length)"
            :key="index + Math.ceil(beliefs.length / 2)"
            :title="beliefDescriptions[index + Math.ceil(beliefs.length / 2)]">
          <td style="padding-right: 0.5rem;">{{ "B" + (index + 1 + Math.ceil(beliefs.length / 2)) }}</td>
          <td>{{ Math.round(Number(belief.value) * 100) / 100 }}</td>
        </tr>
      </table>
    </div>
    <!-- Displaying the desires -->
    <h4 v-if="desires !== null && desires.length > 0">DESIRES</h4>
    <div
        v-if="desires !== null && desires.length > 0"
        id="active-message-desires"
        class="table-container dark-scrollbar">
      <table>
        <tr v-for="(desire, index) in desires" :key="index" class="desire-row">
          <td>{{ desireDescriptions[index] }}</td>
          <td>{{ desire.isActive ? "T" : "F" }}</td>
        </tr>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: "BeliefsValuesDisplay",
  props: ["beliefs", "desires", "phase"],
  data() {
    return {
      beliefDescriptions: [
        "B1: Ik voel me in controle in het gesprek",
        "B2: Ik denk dat ik competent ben om het probleem op te lossen",
        "B3: Ik voel me verbonden met KT",
        "B4: Ik denk dat KT te vertrouwen is",
        "B5: Ik denk dat KT mij begrijpt",
        "B6: Ik denk dat KT geïnteresseerd is in mijn verhaal",
        "B7: Ik denk dat KT mij kan helpen",
        "B8: Ik denk dat KT het probleem kan oplossen",
        "B9: Ik denk dat ik mijn verhaal heb verteld",
        "B10: Ik denk dat KT vraagt naar een wens",
        "B11: Ik denk dat KT vraagt naar een positieve wens",
        "B12: Ik denk dat KT vraagt naar een vertrouwenspersoon",
        "B13: Ik denk dat Juf Ellie mij kan helpen",
        "B14: Ik voel me veilig in het gesprek",
        "B15: Ik denk dat KT wil het gesprek beëindigen",
        "B16: Ik denk dat KT en ik samen tot een oplossing zullen komen",
        "B17: Ik denk dat KT het probleem voor mij gaat oplossen"
      ],
      desireDescriptions: [
        "Lilobot wants to talk about their problem",
        "Lilobot wants to end the conversation",
        "Lilobot wants the Kindertelefoon to take the bullies out of school",
        "Lilobot wants to talk to his teacher about the problem",
        "Lilobot wants to find a solution together with the Kindertelefoon"
      ]
      // TODO use endpoints to retrieve the belief/desire descriptions, to make this adapt to backend changes
    }
  }
}
</script>

<style scoped>
/* Here, the dark scrollbar should have a smaller width, since these are sub-
components of the surrounding path component. */
.dark-scrollbar::-webkit-scrollbar {
  width: 0.6rem;
  height: 0.6rem;
}

.table-container {
  overflow: auto;
  max-height: calc(calc(90vh - 15rem) / 2);
  display: flex;
  flex-direction: row;
  margin-right: 0.2rem;
}

table {
  padding-right: 0.5rem;
}

.belief-value-container {
  width: 45%;
  position: sticky;
  top: 0;
  overflow: auto;
  color: var(--main-text-secondary);
}

h4 {
  margin-bottom: 0.1rem;
  margin-top: 0.5rem;
}

h4::before {
  content: '';
  display: inline-block;
  margin-right: 0.5rem;
  height: 1rem;
  width: 1rem;
  background-color: var(--active-node);
  border-radius: 50%;
}

.desire-row {
  font-size: 0.8rem;
  display: block;
  padding-bottom: 0.8rem;
  font-weight: bold;
}
</style>