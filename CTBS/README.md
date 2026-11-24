# CTBS — Cobblemon Transformation & Belly System

Experimental Fabric mod for Minecraft 1.21.1, Java 21 and Cobblemon 1.8.x.

## Build
GitHub Actions installs Java 21 and Gradle 8.12, then runs `gradle build` from this directory. The JAR is uploaded as the `ctbs-build` workflow artifact.

## Implemented in 1.5.0-alpha
- Fabric Loom build with split main/client source sets.
- Cobblemon 1.8.4 compile dependency; runtime metadata accepts Cobblemon >=1.8.0 and <1.9.0.
- Persistent per-player configuration stored in `config/ctbs-player-state.json`.
- Per-species capacity overrides editable in the in-game menu with a synchronized slider and exact numeric field (0–100000), plus a one-click reset to the species default.
- Conservative default capacities: Eevee 2, Gengar 7, Zoroark 5, Greninja 4, Scolipede/Snorlax 15, other species 3; Lucario uses the generic default unless customized.
- Persistent stomach policy selection: HOLD, HEAL, DIGEST.
- Persistent posture selection: STAND, SIT, LIE, NAP.
- Posture multiplier values exposed to future gameplay systems: 1.0x, 1.2x, 1.4x and 1.6x.
- Client keybinding (O by default), configurable in Minecraft Controls, and a dark-fantasy multi-tab screen: Creature, Stomach, Actions and Status.
- Stomach and posture tabs issue the existing server commands; Status displays current mode, posture and multiplier in chat.
- Creature tab supports exact numeric capacity entry synchronized with a slider.
- Capacity can be adjusted with a slider or entered as an exact number (0–100000); both controls stay synchronized.
- Brazilian Portuguese and English UI strings.

## Commands
- `/ctbs capacity get <species>`
- `/ctbs capacity set <species> <number>`
- `/ctbs capacity reset <species>` (removes the personal override and restores the species base value)
- `/ctbs stomach status`
- `/ctbs stomach mode hold|heal|digest`
- `/ctbs action stand|sit|lie|nap`

## Not implemented yet
This alpha does not yet capture or store live entities/Pokémon, restore Cobblemon Pokémon data, run actual healing or digestion ticks, create reward drops, sync an entity stomach inventory, implement transformation support from Explorers of the Rift, or include belly/creature models and animations. The policy and posture settings are persistent selection/state only; their gameplay effects must still be connected.

The Cobblemon dependency is currently used for compile/runtime compatibility metadata; no Cobblemon-specific API calls are implemented yet.

## Test status
The GitHub Actions build passed for the initial server-state and command milestones. The previous client screen and synchronized numeric/slider capacity control compiled successfully in GitHub Actions run 26. The new multi-tab screen is being validated in GitHub Actions run 29. The workflow artifact `ctbs-build` was produced; in-game behavior still needs manual testing. A successful Gradle build does not replace in-game client/server testing. Back up worlds before testing experimental builds.
