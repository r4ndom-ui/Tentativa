# CTBS — Cobblemon Transformation & Belly System

Experimental Fabric mod for Minecraft 1.21.1, Java 21 and Cobblemon 1.8.x.

## Build
GitHub Actions installs Java 21 and Gradle 8.12, then runs `gradle build` from this directory. The JAR is uploaded as the `ctbs-build` workflow artifact.

## Implemented in 1.5.0-alpha
- Fabric Loom build with split main/client source sets.
- Cobblemon 1.8.4 compile dependency; runtime metadata accepts Cobblemon >=1.8.0 and <1.9.0.
- Persistent per-player configuration stored in `config/ctbs-player-state.json`, with an automatic `.bak` recovery copy when the primary file is unreadable.
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
This alpha does not yet capture or store live entities/Pokémon, restore Cobblemon Pokémon data, run actual healing or digestion ticks, create reward drops, sync an entity stomach inventory, implement transformation support from Explorers of the Rift, or include belly/creature models and animations. These are explicitly unimplemented gameplay features, not hidden behind the current menu commands. The next implementation milestone must first add Cobblemon API-backed entity serialization and server-authoritative storage, then add safe release/rollback paths before enabling any digestion or reward behavior. The policy and posture settings are persistent selection/state only; their gameplay effects must still be connected.

The Cobblemon dependency is currently used for compile/runtime compatibility metadata; no Cobblemon-specific API calls are implemented yet.

## Test status
GitHub Actions run 45 completed successfully, including the JUnit 5 setup and capacity regression test task. Run 46 completed successfully and validates compilation, tests, packaging, and the new backup/recovery persistence change. Its `ctbs-build` artifact is artifact ID `14943613155` (32,636 bytes); see [workflow run 46](https://github.com/r4ndom-ui/Tentativa/actions/runs/45443134272). Run 39 also passed after removing duplicate capacity helper declarations. This validates compilation and packaging only; in-game behavior, multiplayer persistence, and Cobblemon entity handling still need manual testing. Back up worlds before testing experimental builds.
