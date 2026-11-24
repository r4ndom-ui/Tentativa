package com.example.ctbs.state;

import com.example.ctbs.CTBSMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Persistent player settings. The server is authoritative for all values. */
public final class CTBSPlayerState {
    public enum StomachMode { HOLD, HEAL, DIGEST }
    public enum Posture { STAND, SIT, LIE, NAP }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("ctbs-player-state.json");
    private static final Path BACKUP = FILE.resolveSibling(FILE.getFileName() + ".bak");
    private static final Map<UUID, PlayerData> PLAYERS = new HashMap<>();

    private CTBSPlayerState() {}

    public static synchronized void load() {
        try {
            Files.createDirectories(FILE.getParent());
            if (Files.exists(FILE)) {
                try {
                    readState(FILE);
                    return;
                } catch (Exception primaryFailure) {
                    CTBSMod.LOGGER.error("Primary CTBS state file is unreadable; trying backup", primaryFailure);
                }
            }
            if (Files.exists(BACKUP)) {
                readState(BACKUP);
                Files.copy(BACKUP, FILE, StandardCopyOption.REPLACE_EXISTING);
                CTBSMod.LOGGER.warn("Recovered CTBS player state from backup file");
            }
        } catch (Exception exception) {
            CTBSMod.LOGGER.error("Could not load CTBS player state or its backup", exception);
        }
    }

    private static void readState(Path source) throws Exception {
        Root root;
        try (Reader reader = Files.newBufferedReader(source)) {
            root = GSON.fromJson(reader, Root.class);
        }
        if (root == null || root.players == null) {
            throw new IllegalStateException("CTBS state file has no player data root");
        }
        Map<UUID, PlayerData> loaded = new HashMap<>();
        root.players.forEach((key, value) -> {
            try {
                UUID id = UUID.fromString(key);
                PlayerData data = value == null ? new PlayerData() : value;
                data.sanitize();
                loaded.put(id, data);
            } catch (IllegalArgumentException exception) {
                CTBSMod.LOGGER.warn("Ignoring invalid player UUID in CTBS state: {}", key);
            }
        });
        PLAYERS.clear();
        PLAYERS.putAll(loaded);
    }

    private static synchronized void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Root root = new Root();
            PLAYERS.forEach((id, data) -> root.players.put(id.toString(), data));
            Path temporary = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporary)) {
                GSON.toJson(root, writer);
            }
            if (Files.exists(FILE)) {
                Path backupTemporary = BACKUP.resolveSibling(BACKUP.getFileName() + ".tmp");
                Files.copy(FILE, backupTemporary, StandardCopyOption.REPLACE_EXISTING);
                try {
                    Files.move(backupTemporary, BACKUP, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException ignored) {
                    Files.move(backupTemporary, BACKUP, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            try {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception exception) {
            CTBSMod.LOGGER.error("Could not save CTBS player state", exception);
        }
    }

    public static synchronized PlayerData get(UUID playerId) {
        PlayerData data = PLAYERS.computeIfAbsent(playerId, ignored -> new PlayerData());
        data.sanitize();
        return data.copy();
    }

    public static synchronized int getCapacity(UUID playerId, String species) {
        PlayerData data = PLAYERS.get(playerId);
        String key = normalize(species);
        if (data != null && data.capacityOverrides.containsKey(key)) return data.capacityOverrides.get(key);
        return CTBSCapacityRules.baseCapacity(key);
    }

    public static synchronized boolean hasCapacityOverride(UUID playerId, String species) {
        PlayerData data = PLAYERS.get(playerId);
        return data != null && data.capacityOverrides.containsKey(normalize(species));
    }

    public static synchronized void resetCapacity(UUID playerId, String species) {
        PlayerData data = PLAYERS.get(playerId);
        if (data != null && data.capacityOverrides.remove(normalize(species)) != null) save();
    }

    public static synchronized int getBaseCapacity(String species) {
        return CTBSCapacityRules.baseCapacity(species);
    }

    public static synchronized void setCapacity(UUID playerId, String species, int capacity) {
        CTBSCapacityRules.validateOverride(capacity);
        PLAYERS.computeIfAbsent(playerId, ignored -> new PlayerData()).capacityOverrides.put(normalize(species), capacity);
        save();
    }

    public static synchronized void setMode(UUID playerId, StomachMode mode) {
        PLAYERS.computeIfAbsent(playerId, ignored -> new PlayerData()).mode = mode.name();
        save();
    }

    public static synchronized void setPosture(UUID playerId, Posture posture) {
        PLAYERS.computeIfAbsent(playerId, ignored -> new PlayerData()).posture = posture.name();
        save();
    }

    public static float digestionMultiplier(Posture posture) {
        return CTBSGameplayRules.postureMultiplier(posture);
    }

    private static String normalize(String species) {
        return CTBSCapacityRules.normalizeSpecies(species);
    }

    public static final class PlayerData {
        public String mode = StomachMode.HOLD.name();
        public String posture = Posture.STAND.name();
        public Map<String, Integer> capacityOverrides = new HashMap<>();

        private void sanitize() {
            try { StomachMode.valueOf(mode); } catch (Exception ignored) { mode = StomachMode.HOLD.name(); }
            try { Posture.valueOf(posture); } catch (Exception ignored) { posture = Posture.STAND.name(); }
            if (capacityOverrides == null) capacityOverrides = new HashMap<>();
            capacityOverrides.entrySet().removeIf(entry -> entry.getKey() == null || entry.getValue() == null || entry.getValue() < 0 || entry.getValue() > 100000);
        }

        private PlayerData copy() {
            PlayerData copy = new PlayerData();
            copy.mode = mode;
            copy.posture = posture;
            copy.capacityOverrides = new HashMap<>(capacityOverrides);
            return copy;
        }

        public StomachMode getMode() { sanitize(); return StomachMode.valueOf(mode); }
        public Posture getPosture() { sanitize(); return Posture.valueOf(posture); }
        public float getDigestionMultiplier() { return digestionMultiplier(getPosture()); }
    }

    private static final class Root {
        Map<String, PlayerData> players = new HashMap<>();
    }
}
