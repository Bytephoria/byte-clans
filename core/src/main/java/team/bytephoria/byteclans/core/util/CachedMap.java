package team.bytephoria.byteclans.core.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public interface CachedMap<K, V> {

    default Optional<V> getIfPresent(final @NotNull K key){
        return Optional.ofNullable(this.get(key));
    }

    V get(final @NotNull K key);

    V add(final @NotNull K key, final @NotNull V value);

    V update(final @NotNull K key, final @NotNull V value);

    V remove(final @NotNull K key);

    Map<K, V> asMap();

}
