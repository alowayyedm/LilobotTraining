const phaseToEnumNumber = require('@/utils').phaseEnumToNumber

test("Phase to enum number", () => {
    expect(phaseToEnumNumber("PHASE1")).toBe(1);
    expect(phaseToEnumNumber("PHASE2")).toBe(2);
    expect(phaseToEnumNumber("PHASE3")).toBe(3);
    expect(phaseToEnumNumber("PHASE4")).toBe(4);
    expect(phaseToEnumNumber("PHASE5")).toBe(5);
});