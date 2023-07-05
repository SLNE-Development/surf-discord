package dev.slne.discord.whitelist;

import java.util.ArrayList;
import java.util.List;
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
    public UuidMinecraftName hitCache(Object uuidOrMinecraftName) {
        UuidMinecraftName uuidMinecraftName = null;

        if (uuidOrMinecraftName instanceof UUID uuid) {
            uuidMinecraftName = this.cache.stream()
                    .filter(uuidMinecraftNameItem -> uuidMinecraftNameItem.uuid().equals(uuid))
                    .findFirst().orElse(null);
        } else if (uuidOrMinecraftName instanceof String minecraftName) {
            uuidMinecraftName = this.cache.stream()
                    .filter(uuidMinecraftNameItem -> uuidMinecraftNameItem.minecraftName().equals(minecraftName))
                    .findFirst().orElse(null);
        }

        return uuidMinecraftName;
    }

    /**
     * Sets the UUID of a minecraft name.
     *
     * @param minecraftName The minecraft name.
     * @param uuid          The UUID.
     */
    public UuidMinecraftName setCache(String minecraftName, UUID uuid) {
        UuidMinecraftName uuidMinecraftName = this.hitCache(minecraftName);

        if (uuidMinecraftName != null) {
            this.cache.remove(uuidMinecraftName);
        }

        UuidMinecraftName newUuidMinecraftName = new UuidMinecraftName(uuid, minecraftName);
        this.cache.add(newUuidMinecraftName);

        return newUuidMinecraftName;
    }
}
