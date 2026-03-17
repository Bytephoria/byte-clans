package team.bytephoria.byteclans.core.util;

import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.util.Identity;

import java.util.Map;
import java.util.UUID;

public final class IdentityCachedMap<V extends Identity> implements CachedMap<UUID, V> {

    private final Map<UUID, V> map;
    public IdentityCachedMap(final @NotNull Map<UUID, V> map) {
        this.map = map;
    }

    public V add(final @NotNull V value) {
        return this.map.put(value.uniqueId(), value);
    }

    public V get(final @NotNull Identity identity) {
        return this.map.get(identity.uniqueId());
    }

    public V remove(final @NotNull Identity identity) {
        return this.map.remove(identity.uniqueId());
    }

    @Override
    public V get(final @NotNull UUID key) {
        return this.map.get(key);
    }

    @Override
    public V add(final @NotNull UUID key, final @NotNull V value) {
        return this.map.put(key, value);
    }

    @Override
    public V update(final @NotNull UUID key, final @NotNull V value) {
        return this.map.replace(key, value);
    }

    @Override
    public V remove(final @NotNull UUID key) {
        return this.map.remove(key);
    }

    @Override
    public Map<UUID, V> asMap() {
        return this.map;
    }
}
