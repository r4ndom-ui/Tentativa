package com.example.ctbs.state;

import java.util.Locale;

/** Pure capacity rules, kept separate so they can be verified without launching Minecraft. */
public final class CTBSCapacityRules {
    public static final int MIN_CAPACITY = 0;
    public static final int MAX_CAPACITY = 100000;

    private CTBSCapacityRules() {}

    public static String normalizeSpecies(String species) {
        if (species == null) throw new IllegalArgumentException("Species cannot be null");
        String normalized = species.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        if (normalized.isEmpty()) throw new IllegalArgumentException("Species cannot be blank");
        return normalized;
    }

    public static int baseCapacity(String species) {
        return switch (normalizeSpecies(species)) {
            case "eevee" -> 2;
            case "gengar" -> 7;
            case "zoroark" -> 5;
            case "greninja" -> 4;
            case "scolipede", "snorlax" -> 15;
            default -> 3;
        };
    }

    public static boolean isValidOverride(int capacity) {
        return capacity >= MIN_CAPACITY && capacity <= MAX_CAPACITY;
    }

    public static void validateOverride(int capacity) {
        if (!isValidOverride(capacity)) {
            throw new IllegalArgumentException("Capacity must be between " + MIN_CAPACITY + " and " + MAX_CAPACITY);
        }
    }
}
