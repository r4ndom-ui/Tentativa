package com.example.ctbs.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CTBSGameplayRulesTest {
    @Test
    void digestionSaturationScalesWithTotalMaximumHealth() {
        assertEquals(5.0f, CTBSGameplayRules.digestionSaturation(10.0f), 0.0001f);
        assertEquals(10.0f, CTBSGameplayRules.digestionSaturation(20.0f), 0.0001f);
    }

    @Test
    void digestionSaturationNeverExceedsVanillaMaximum() {
        assertEquals(20.0f, CTBSGameplayRules.digestionSaturation(100.0f), 0.0001f);
    }

    @Test
    void invalidMaximumHealthGrantsNoSaturation() {
        assertEquals(0.0f, CTBSGameplayRules.digestionSaturation(0.0f));
        assertEquals(0.0f, CTBSGameplayRules.digestionSaturation(-5.0f));
        assertEquals(0.0f, CTBSGameplayRules.digestionSaturation(Float.NaN));
        assertEquals(0.0f, CTBSGameplayRules.digestionSaturation(Float.POSITIVE_INFINITY));
    }

    @Test
    void standingUsesBaseMultiplier() {
        assertEquals(1.0f, CTBSGameplayRules.postureMultiplier(CTBSPlayerState.Posture.STAND));
    }

    @Test
    void sittingUsesTwentyPercentMultiplier() {
        assertEquals(1.2f, CTBSGameplayRules.postureMultiplier(CTBSPlayerState.Posture.SIT), 0.0001f);
    }

    @Test
    void lyingUsesFortyPercentMultiplier() {
        assertEquals(1.4f, CTBSGameplayRules.postureMultiplier(CTBSPlayerState.Posture.LIE), 0.0001f);
    }

    @Test
    void rejectsNullPostureWithClearArgumentError() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
            () -> CTBSGameplayRules.postureMultiplier(null));
    }

    @Test
    void nappingUsesSixtyPercentMultiplier() {
        assertEquals(1.6f, CTBSGameplayRules.postureMultiplier(CTBSPlayerState.Posture.NAP), 0.0001f);
    }
}
