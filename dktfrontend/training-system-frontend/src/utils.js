/*
    File for utility methods, shared between components.
 */

export const phaseEnumToNumber = function (phase) {
    return Number(phase.substring(phase.length - 1));
};