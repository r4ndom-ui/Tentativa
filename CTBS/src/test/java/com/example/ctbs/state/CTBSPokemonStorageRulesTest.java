package com.example.ctbs.state;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CTBSPokemonStorageRulesTest {
    @Test
    void acceptsUniquePayloadBelowCapacity() {
        assertTrue(CTBSPokemonStorageRules.canStore(new HashMap<>(), UUID.randomUUID(), "{\"species\":\"eevee\"}", 3));
    }

    @Test
    void rejectsDuplicatePokemonUuid() {
        UUID id = UUID.randomUUID();
        Map<String, String> stored = new HashMap<>();
        stored.put(id.toString(), "{\"species\":\"eevee\"}");
        assertFalse(CTBSPokemonStorageRules.canStore(stored, id, "{\"species\":\"eevee\"}", 3));
    }

    @Test
    void rejectsWhenCapacityIsReached() {
        Map<String, String> stored = new HashMap<>();
        stored.put(UUID.randomUUID().toString(), "{}");
        assertFalse(CTBSPokemonStorageRules.canStore(stored, UUID.randomUUID(), "{}", 1));
    }

    @Test
    void rejectsEmptyOrOversizedPayload() {
        assertFalse(CTBSPokemonStorageRules.canStore(new HashMap<>(), UUID.randomUUID(), " ", 3));
        assertFalse(CTBSPokemonStorageRules.canStore(new HashMap<>(), UUID.randomUUID(), "x".repeat(2_000_001), 3));
    }

    @Test
    void rejectsInvalidCapacity() {
        assertFalse(CTBSPokemonStorageRules.canStore(new HashMap<>(), UUID.randomUUID(), "{}", -1));
        assertFalse(CTBSPokemonStorageRules.canStore(new HashMap<>(), UUID.randomUUID(), "{}", 100001));
    }
}
