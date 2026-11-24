package com.example.ctbs.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CTBSCapacityRulesTest {
    @Test
    void normalizesSpeciesNamesConsistently() {
        assertEquals("eevee", CTBSCapacityRules.normalizeSpecies(" EeVeE "));
        assertEquals("mr_mime", CTBSCapacityRules.normalizeSpecies("Mr Mime"));
        assertEquals("mr_mime", CTBSCapacityRules.normalizeSpecies("Mr   Mime"));
        assertEquals("mr_mime", CTBSCapacityRules.normalizeSpecies("Mr\tMime"));
    }

    @Test
    void rejectsNullAndBlankSpeciesNames() {
        assertThrows(IllegalArgumentException.class, () -> CTBSCapacityRules.normalizeSpecies(null));
        assertThrows(IllegalArgumentException.class, () -> CTBSCapacityRules.normalizeSpecies("  "));
    }

    @Test
    void returnsExpectedBaseCapacities() {
        assertEquals(2, CTBSCapacityRules.baseCapacity("eevee"));
        assertEquals(7, CTBSCapacityRules.baseCapacity("gengar"));
        assertEquals(5, CTBSCapacityRules.baseCapacity("zoroark"));
        assertEquals(4, CTBSCapacityRules.baseCapacity("greninja"));
        assertEquals(15, CTBSCapacityRules.baseCapacity("snorlax"));
        assertEquals(3, CTBSCapacityRules.baseCapacity("pikachu"));
    }

    @Test
    void validatesCapacityBoundaries() {
        assertTrue(CTBSCapacityRules.isValidOverride(0));
        assertTrue(CTBSCapacityRules.isValidOverride(100000));
        assertFalse(CTBSCapacityRules.isValidOverride(-1));
        assertFalse(CTBSCapacityRules.isValidOverride(100001));
        assertDoesNotThrow(() -> CTBSCapacityRules.validateOverride(0));
        assertDoesNotThrow(() -> CTBSCapacityRules.validateOverride(100000));
        assertThrows(IllegalArgumentException.class, () -> CTBSCapacityRules.validateOverride(-1));
        assertThrows(IllegalArgumentException.class, () -> CTBSCapacityRules.validateOverride(100001));
    }
}
