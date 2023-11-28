<template>
    <div class="header-area" ref="headerArea">
      <div ref="headerText" class="header-text">{{ headerText }}</div>
    </div>
  </template>
  
<script>
export default {
  name: 'DynamicText',
  props: {
    headerText: String,
    targetFontSize: Number,
    minFontSize: Number,
    unit: String,
    stepSize: Number,
    overflowWrap: String
  },
  mounted() {
    this.resizeText();
    window.addEventListener('resize', this.resizeText);
  },
  updated() {
    this.$nextTick(() => {
      this.resizeText();
    });
  },
  unmounted() {
    window.removeEventListener('resize', this.resizeText);
  },
  methods: {
    resizeText() {
      const headerArea = this.$refs.headerArea;
      const headerText = this.$refs.headerText;

      headerText.style.fontSize = `${this.targetFontSize}${this.unit}`;
      headerText.style.lineHeight = `${this.targetFontSize + 0.3 * this.targetFontSize}${this.unit}`;
      headerText.style.whiteSpace = 'nowrap';
      headerText.style.overflow = 'hidden';
      headerText.style.textOverflow = 'ellipsis';

      this.$nextTick(() => {
        let currentFontSize = this.targetFontSize;

        while (
          (headerText.clientWidth > (headerArea.clientWidth) ||
              headerText.clientHeight > (headerArea.clientHeight -
                  parseFloat(getComputedStyle(headerArea).paddingTop))) &&
          currentFontSize > this.minFontSize) {
          currentFontSize -= this.stepSize;
          headerText.style.fontSize = `${currentFontSize}${this.unit}`;
          headerText.style.lineHeight = `${currentFontSize + 0.3 * currentFontSize}${this.unit}`;
        }
      });
      headerText.style.whiteSpace = 'normal';
      headerText.style.overflowWrap = this.overflowWrap;
    },
  },
};
</script>
  
<style scoped>
.header-area {
  height: inherit;
  display: flex;
  align-items: center;
}

.header-text {
  display: inline-block;
}
</style>
  