package com.example.ctbs.state;

/** Pure gameplay tuning rules, isolated from Minecraft runtime state for regression tests. */
public final class CTBSGameplayRules {
    /** Vanilla food saturation is capped at 20 points. */
    public static final float MAX_DIGESTION_SATURATION = 20.0f;
    /** Baseline conversion: one saturation point per two points of maximum health. */
    public static final float SATURATION_PER_MAX_HEALTH = 0.5f;

    private CTBSGameplayRules() {}

    /**
     * Computes the saturation reward from a creature's total maximum health.
     * Invalid, non-finite, or non-positive health grants no reward.
     */
    public static float digestionSaturation(float totalMaxHealth) {
        if (!Float.isFinite(totalMaxHealth) || totalMaxHealth <= 0.0f) return 0.0f;
        return Math.min(MAX_DIGESTION_SATURATION, totalMaxHealth * SATURATION_PER_MAX_HEALTH);
    }

    public static float postureMultiplier(CTBSPlayerState.Posture posture) {
        if (posture == null) throw new IllegalArgumentException("Posture cannot be null");
        return switch (posture) {
            case SIT -> 1.2f;
            case LIE -> 1.4f;
            case NAP -> 1.6f;
            case STAND -> 1.0f;
        };
    }
}
