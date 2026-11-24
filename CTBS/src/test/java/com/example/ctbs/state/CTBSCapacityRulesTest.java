package com.example.ctbs.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CTBSCapacityRulesTest {
    @Test
    void knownSpeciesKeepTheirConfiguredBaseCapacity() {
        assertEquals(2, CTBSCapacityRules.baseCapacity("eevee"));
        assertEquals(7, CTBSCapacityRules.baseCapacity("gengar"));
        assertEquals(5, CTBSCapacityRules.baseCapacity("zoroark"));
        assertEquals(4, CTBSCapacityRules.baseCapacity("greninja"));
        assertEquals(15, CTBSCapacityRules.baseCapacity("scolipede"));
        assertEquals(15, CTBSCapacityRules.baseCapacity("snorlax"));
    }

    @Test
    void unknownSpeciesUseConservativeDefault() {
        assertEquals(3, CTBSCapacityRules.baseCapacity("lucario"));
        assertEquals(3, CTBSCapacityRules.baseCapacity("made_up_species"));
    }

    @Test
    void speciesNamesAreNormalized() {
        assertEquals(2, CTBSCapacityRules.baseCapacity("  EeVeE "));
        assertEquals("mr_mime", CTBSCapacityRules.normalizeSpecies(" Mr Mime "));
    }

    @Test
    void overrideRangeIncludesBothEndpoints() {
        assertTrue(CTBSCapacityRules.isValidOverride(0));
        assertTrue(CTBSCapacityRules.isValidOverride(100000));
        assertFalse(CTBSCapacityRules.isValidOverride(-1));
        assertFalse(CTBSCapacityRules.isValidOverride(100001));
    }

    @Test
    void invalidOverridesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> CTBSCapacityRules.validateOverride(-1));
        assertThrows(IllegalArgumentException.class, () -> CTBSCapacityRules.validateOverride(100001));
        assertThrows(IllegalArgumentException.class, () -> CTBSCapacityRules.normalizeSpecies(" "));
        assertThrows(IllegalArgumentException.class, () -> CTBSCapacityRules.normalizeSpecies(null));
    }
}
