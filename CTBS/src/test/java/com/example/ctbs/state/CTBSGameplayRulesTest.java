package com.example.ctbs.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CTBSGameplayRulesTest {
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
    void nappingUsesSixtyPercentMultiplier() {
        assertEquals(1.6f, CTBSGameplayRules.postureMultiplier(CTBSPlayerState.Posture.NAP), 0.0001f);
    }
}
