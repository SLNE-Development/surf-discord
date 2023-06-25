package dev.slne.discord.whitelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UUIDCache {

    private List<UuidMinecraftName> cache;

    /**
     * Creates a new {@link UUIDCache}.
     */
    public UUIDCache() {
        this.cache = new ArrayList<>();
    }

    /**
     * Hits the cache.
     *
     * @param uuidOrMinecraftName The UUID or minecraft name.
     * @return The {@link UuidMinecraftName} if found.
     */
    public Optional<UuidMinecraftName> hitCache(Object uuidOrMinecraftName) {
        Optional<UuidMinecraftName> uuidMinecraftNameOptional = Optional.empty();

        if (uuidOrMinecraftName instanceof UUID uuid) {
            uuidMinecraftNameOptional = this.cache.stream()
                    .filter(uuidMinecraftName -> uuidMinecraftName.uuid().equals(uuid))
                    .findFirst();
        } else if (uuidOrMinecraftName instanceof String minecraftName) {
            uuidMinecraftNameOptional = this.cache.stream()
                    .filter(uuidMinecraftName -> uuidMinecraftName.minecraftName().equals(minecraftName))
                    .findFirst();
        }

        return uuidMinecraftNameOptional;
    }

    /**
     * Sets the UUID of a minecraft name.
     *
     * @param minecraftName The minecraft name.
     * @param uuid          The UUID.
     */
    public Optional<UuidMinecraftName> setCache(String minecraftName, UUID uuid) {
        Optional<UuidMinecraftName> uuidMinecraftNameOptional = this.hitCache(minecraftName);

        return Optional.of(uuidMinecraftNameOptional.orElseGet(() -> {
            UuidMinecraftName newUuidMinecraftName = new UuidMinecraftName(uuid, minecraftName);
            this.cache.add(newUuidMinecraftName);
            return newUuidMinecraftName;
        }));
    }
}
