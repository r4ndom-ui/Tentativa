# CTBS — Cobblemon Transformation & Belly System

Experimental Fabric mod for Minecraft 1.21.1, Java 21 and Cobblemon 1.8.4 or newer within the 1.8.x line.

## Build
GitHub Actions installs Java 21 and Gradle 8.12, then runs `gradle build` from this directory. The JAR is uploaded as the `ctbs-build` workflow artifact.

## Implemented in 1.5.0-alpha
- Fabric Loom build with split main/client source sets.
- Cobblemon 1.8.4 compile dependency; runtime metadata requires Cobblemon >=1.8.4 and <1.9.0, matching the version used for the current build.
- Persistent per-player configuration and serialized-Pokémon ledger stored in `config/ctbs-player-state.json`, with an automatic `.bak` recovery copy when the primary file is unreadable.
- Server-side ledger primitives validate payload size, storage capacity, and duplicate Pokémon UUIDs before accepting a serialized record; stored records can be inspected and removed by UUID through the state API.
- Per-species capacity overrides editable in the in-game menu with a synchronized slider and exact numeric field (0–100000), plus a one-click reset to the species default.
- Conservative default capacities: Eevee 2, Gengar 7, Zoroark 5, Greninja 4, Scolipede/Snorlax 15, other species 3; Lucario uses the generic default unless customized.
- Persistent stomach policy selection: HOLD, HEAL, DIGEST. Settings now roll back in memory if disk persistence fails, and commands report save failures instead of claiming success.
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

The Cobblemon dependency is present for runtime compatibility, but no serialization bridge is currently included. Upstream Cobblemon source inspection confirms that `Pokemon` exposes registry-context-aware `saveToJSON` / `loadFromJSON` methods and a `CODEC`, and `PokemonEntity` exposes its `pokemon` property. The inspected upstream source uses Mojang-mapped `RegistryAccess`; this project uses Fabric/Yarn mappings, so the equivalent mapped type and the exact signatures in the pinned CurseMaven artifact still need to be verified before adding imports or enabling capture. A prior bridge using the Mojang class name directly failed under Yarn mappings and was removed to keep the baseline buildable. Do not remove a live entity until a serialized record is durably committed; release needs duplicate prevention and rollback.

## Test status
GitHub Actions runs 64–75 completed successfully. Runs 73 and 74 passed after aligning the declared runtime Cobblemon minimum to 1.8.4; run 75 also passed after the compatibility and documentation updates. The latest `ctbs-build` artifact was uploaded by [run 75](https://github.com/r4ndom-ui/Tentativa/actions/runs/45502774406). These jobs include compilation, JUnit tests and packaging. The new tests cover species-name normalization, invalid names, known base capacities and boundary validation. Earlier storage tests cover malformed JSON, empty/oversized payloads, duplicate UUIDs and capacity limits. These results validate compilation and automated rule tests only; in-game behavior, multiplayer persistence, and Cobblemon entity handling still require manual testing. Back up worlds before testing experimental builds.
