package com.example.ctbs.state;

/** Pure gameplay tuning rules, isolated from Minecraft runtime state for regression tests. */
public final class CTBSGameplayRules {
    private CTBSGameplayRules() {}

    public static float postureMultiplier(CTBSPlayerState.Posture posture) {
        return switch (posture) {
            case SIT -> 1.2f;
            case LIE -> 1.4f;
            case NAP -> 1.6f;
            case STAND -> 1.0f;
        };
    }
}
