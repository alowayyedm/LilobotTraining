<template>
    <div class="tabs">
        <ul class="tabs__header">
            <li v-for="title in tabTitles" 
            :key="title"
            :class="{ selected: title == selectedTitle }"
            @click="selectedTitle = title"
            >
                {{ title }}
            </li>
        </ul>
        <slot></slot>
    </div>
</template>

<script>

import { ref, provide } from 'vue'   

export default {
    setup (props, { slots }) {
        const tabTitles = ref(slots.default().map((tab) => tab.props.title))
        const selectedTitle = ref(tabTitles.value[0])

        provide("selectedTitle", selectedTitle)
        return {
            selectedTitle,
            tabTitles,
        }
    },
}
</script>

<style>

.tabs {
    max-width: 100%;
}

.tabs__header {
    margin-bottom: 10px;
    list-style: none;
    padding: 0;
    display: flex;
}

.tabs__header li {
    width: 100%;
    text-align: left;
    padding: 10px 10px;
    margin-right: 10px;
    background-color:#00DBA3;
    border-radius: 5px;
    font-family: 'Anton', sans-serif;
    font-size: small;
    cursor: pointer;
    transition: 0.4s all ease-out;
}

.tabs__header li:hover {
    background-color: #00ca98;
}

.tabs__header li.selected {
    background-color: #00ba8c;
}

</style>